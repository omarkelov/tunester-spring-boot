package com.whatever.tunester.runners;

import com.google.common.collect.Streams;
import com.whatever.tunester.database.entities.Directory;
import com.whatever.tunester.database.entities.Track;
import com.whatever.tunester.database.entities.TrackMeta;
import com.whatever.tunester.database.repositories.DirectoryRepository;
import com.whatever.tunester.database.repositories.TrackRepository;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.whatever.tunester.constants.Precedence.THIRD_PRIORITY;
import static com.whatever.tunester.util.TimestampUtils.getLastUpdatedTimestamp;

@Component
@Order(THIRD_PRIORITY)
public class DirectoriesScanRunner implements ApplicationRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private DirectoryRepository directoryRepository;

    @Override
    public void run(ApplicationArguments args) {
        String rootPathName = userService.getUserRootPath("admin");

        if (rootPathName == null) {
            return;
        }

        Path rootPath = Path.of(rootPathName);

        directoryRepository.deleteAll();

        List<Path> directoriesPaths;
        try (Stream<Path> pathStream = Files.walk(rootPath)) {
            directoriesPaths = new ArrayList<>(
                pathStream
                    .filter(Files::isDirectory)
                    .toList()
            );
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Collections.reverse(directoriesPaths);

        directoriesPaths.forEach(directoryPath -> {
            try (Stream<Path> childrenPathsStream = Files.list(directoryPath)) {
                String relativePath = rootPath.relativize(directoryPath).toString().replace('\\', '/');
                if (relativePath.isEmpty()) {
                    relativePath = ".";
                }

                Map<Boolean, List<Path>> groupedPaths = childrenPathsStream
                    .collect(Collectors.groupingBy(Files::isDirectory));

                List<String> directoriesPathsNames = groupedPaths
                    .getOrDefault(true, Collections.emptyList())
                    .stream()
                    .map(path -> rootPath.relativize(path).toString().replace('\\', '/'))
                    .toList();

                List<String> tracksPathsNames = groupedPaths
                    .getOrDefault(false, Collections.emptyList())
                    .stream()
                    .filter(FileFormatUtils::isAudioFile)
                    .map(path -> rootPath.relativize(path).toString().replace('\\', '/'))
                    .toList();

                List<Track> tracks = tracksPathsNames
                    .stream()
                    .map(trackRepository::findByPath)
                    .filter(Objects::nonNull)
                    .toList();

                List<Directory> directories = directoriesPathsNames
                    .stream()
                    .map(directoryRepository::findByPath)
                    .filter(Objects::nonNull)
                    .toList();

                Timestamp lastUpdatedTimestamp = new Timestamp(
                    Streams
                        .concat(
                            Stream.of(getLastUpdatedTimestamp(directoryPath)),
                            tracks
                                .stream()
                                .map(Track::getLastUpdated),
                            directories
                                .stream()
                                .map(Directory::getLastUpdated)
                        )
                        .filter(Objects::nonNull)
                        .map(Timestamp::getTime)
                        .mapToLong(Number::longValue)
                        .max().orElse(0)
                );

                Long size = Streams
                    .concat(
                        tracks
                            .stream()
                            .map(Track::getTrackMeta)
                            .filter(Objects::nonNull)
                            .map(TrackMeta::getSize),
                        directories
                            .stream()
                            .map(Directory::getSize)
                    )
                    .filter(Objects::nonNull)
                    .mapToLong(Number::longValue)
                    .sum();

                Map<String, Long> ratingCountByRating = new HashMap<>();
                Streams
                    .concat(
                        Stream.of(
                            tracks
                                .stream()
                                .map(Track::getRating)
                                .map(String::valueOf)
                                .collect(Collectors.groupingBy(v -> v, Collectors.counting()))
                        ),
                        directories
                            .stream()
                            .map(Directory::getRatingCountByRating)
                    )
                    .forEach(map -> map.forEach(
                        (key, value) -> ratingCountByRating.compute(
                            key,
                            (resKey, resValue) -> resValue == null
                                ? value
                                : resValue + value
                        )
                    ));

                List<String> directoriesFileNames = directoriesPathsNames
                    .stream()
                    .map(Path::of)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .toList();

                List<String> tracksFileNames = tracksPathsNames
                    .stream()
                    .map(Path::of)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .toList();

                Directory directory = Directory.builder()
                    .path(relativePath)
                    .lastUpdated(lastUpdatedTimestamp)
                    .size(size)
                    .ratingCountByRating(ratingCountByRating)
                    .directoriesFileNames(directoriesFileNames)
                    .tracksFileNames(tracksFileNames)
                    .build();

                directoryRepository.save(directory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
