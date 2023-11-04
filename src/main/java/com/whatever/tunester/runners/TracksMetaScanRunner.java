package com.whatever.tunester.runners;

import com.whatever.tunester.database.entities.Track;
import com.whatever.tunester.database.entities.TrackMeta;
import com.whatever.tunester.database.repositories.TrackRepository;
import com.whatever.tunester.services.ffmpeg.pool.FfmpegServicePool;
import com.whatever.tunester.util.FileFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
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

import static com.whatever.tunester.constants.SystemProperties.N_THREADS_OPTIMAL;
import static com.whatever.tunester.constants.SystemProperties.ROOT_PATH_NAME;
import static com.whatever.tunester.util.TimestampUtils.getLastModifiedTimestamp;

@Component
public class TracksMetaScanRunner implements ApplicationRunner {

    @Autowired
    private TrackRepository trackRepository;

    @Override
    public void run(ApplicationArguments args) {
        Path rootPath = Path.of(ROOT_PATH_NAME);
        int nThreads = N_THREADS_OPTIMAL;

        List<Path> paths;
        try (Stream<Path> pathStream = Files.walk(rootPath)) {
            paths = pathStream
                .filter(Files::isRegularFile)
                .filter(FileFormatUtils::isAudioFile)
                .toList();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        trackRepository.removeNonPresent(paths.stream().map(rootPath::relativize).map(Path::toString).toList());

        FfmpegServicePool ffmpegServicePool = FfmpegServicePool.newGenericPool(nThreads);
        ExecutorService threadPool = Executors.newFixedThreadPool(nThreads);

        CompletableFuture[] futures = paths
            .stream()
            .map(path -> CompletableFuture.runAsync(
                () -> scanTracksMeta(ffmpegServicePool, path, rootPath.relativize(path)),
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
        Timestamp lastModifiedTimestamp = getLastModifiedTimestamp(path);
        Track track = trackRepository.findByPath(relativePath.toString());

        if (track != null) {
            if (lastModifiedTimestamp.equals(track.getLastModified())) {
                return;
            }

            trackRepository.delete(track);
        }

        TrackMeta trackMeta = ffmpegServicePool.getTrackMeta(path.toString());
        track = Track.builder()
            .path(relativePath.toString())
            .lastModified(lastModifiedTimestamp)
            .trackMeta(trackMeta)
            .build();

        trackRepository.save(track);
    }
}
