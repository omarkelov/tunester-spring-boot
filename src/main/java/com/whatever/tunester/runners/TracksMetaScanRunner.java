package com.whatever.tunester.runners;

import com.whatever.tunester.database.entities.Track;
import com.whatever.tunester.database.entities.TrackMeta;
import com.whatever.tunester.database.repositories.TrackRepository;
import com.whatever.tunester.services.tracksmetascanner.TracksMetaScannerService;
import com.whatever.tunester.util.FileFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

import static com.whatever.tunester.constants.SystemProperties.N_THREADS_OPTIMAL;
import static com.whatever.tunester.constants.SystemProperties.ROOT_PATH_NAME;
import static com.whatever.tunester.util.TimestampUtils.getLastModifiedTimestamp;

@Component
public class TracksMetaScanRunner implements ApplicationRunner {

    @Autowired
    ApplicationContext context;

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

        trackRepository.removeNonPresent(paths.stream().map(path -> rootPath.relativize(path).toString()).toList());

        BlockingQueue<Path> pathsQueue = new ArrayBlockingQueue<>(paths.size(), false, paths);
        CountDownLatch latch = new CountDownLatch(paths.size());
        ArrayList<Thread> threads = new ArrayList<>(nThreads);

        for (int i = 0; i < nThreads; i++) {
            Thread thread = new Thread(() -> {
                try (TracksMetaScannerService scanner = context.getBean("prototype", TracksMetaScannerService.class)) {
                    for (;;) {
                        Path path = pathsQueue.take();
                        scanTracksMeta(scanner, path, rootPath.relativize(path), latch);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            threads.add(thread);
            thread.start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        threads.forEach(Thread::interrupt);
    }

    private void scanTracksMeta(
        TracksMetaScannerService tracksMetaScannerService,
        Path path,
        Path relativePath,
        CountDownLatch latch
    ) { // TODO: run in transaction
        Timestamp lastModifiedTimestamp = getLastModifiedTimestamp(path);
        Track track = trackRepository.findByPath(relativePath.toString());

        if (track != null) {
            if (lastModifiedTimestamp.equals(track.getLastModified())) {
                latch.countDown();
                return;
            }

            trackRepository.delete(track);
        }

        TrackMeta trackMeta = tracksMetaScannerService.getTrackMeta(path.toString());
        track = Track.builder()
            .path(relativePath.toString())
            .lastModified(lastModifiedTimestamp)
            .trackMeta(trackMeta)
            .build();

        trackRepository.save(track);

        latch.countDown();
    }
}
