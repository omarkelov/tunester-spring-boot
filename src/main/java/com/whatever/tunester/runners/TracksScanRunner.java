package com.whatever.tunester.runners;

import com.whatever.tunester.database.entities.Track;
import com.whatever.tunester.database.entities.TrackMeta;
import com.whatever.tunester.database.repositories.TrackRepository;
import com.whatever.tunester.services.ffmpeg.pool.FfmpegServicePool;
import com.whatever.tunester.services.user.UserService;
import com.whatever.tunester.util.FileFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static com.whatever.tunester.constants.Precedence.SECOND_PRIORITY;
import static com.whatever.tunester.constants.SystemProperties.N_THREADS_OPTIMAL;
import static com.whatever.tunester.util.TimestampUtils.getLastUpdatedTimestamp;

@Component
@Order(SECOND_PRIORITY)
public class TracksScanRunner implements ApplicationRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private TrackRepository trackRepository;

    @Override
    public void run(ApplicationArguments args) {
        String rootPathName = userService.getUserRootPath("admin");

        if (rootPathName == null) {
            return;
        }

        Path rootPath = Path.of(rootPathName);
        int nThreads = N_THREADS_OPTIMAL;

        List<Path> tracksPaths;
        try (Stream<Path> pathStream = Files.walk(rootPath)) {
            tracksPaths = pathStream
                .filter(Files::isRegularFile)
                .filter(FileFormatUtils::isAudioFile)
                .toList();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        trackRepository.removeNonExistent(tracksPaths.stream().map(rootPath::relativize).map(Path::toString).toList());

        FfmpegServicePool ffmpegServicePool = FfmpegServicePool.newGenericPool(nThreads);
        ExecutorService threadPool = Executors.newFixedThreadPool(nThreads);

        CompletableFuture[] futures = tracksPaths
            .stream()
            .map(trackPath -> CompletableFuture.runAsync(
                () -> scanTracksMeta(ffmpegServicePool, trackPath, rootPath.relativize(trackPath)),
                threadPool
            ))
            .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).join();

        ffmpegServicePool.close();
        threadPool.shutdown();
    }

    private void scanTracksMeta(
        FfmpegServicePool ffmpegServicePool,
        Path path,
        Path relativePath
    ) { // TODO: run in transaction
        Timestamp lastUpdatedTimestamp = getLastUpdatedTimestamp(path);
        Track track = trackRepository.findByPath(relativePath.toString());

        if (track != null && track.getLastUpdated() != null) {
            if (lastUpdatedTimestamp.equals(track.getLastUpdated())) {
                return;
            }

            trackRepository.delete(track);
        }

        TrackMeta trackMeta = ffmpegServicePool
            .useFfmpegService(ffmpegService -> ffmpegService.getTrackMeta(path));
        track = Track.builder()
            .path(relativePath.toString().replace('\\', '/'))
            .lastUpdated(lastUpdatedTimestamp)
            .trackMeta(trackMeta)
            .build();

        trackRepository.save(track);
    }
}
