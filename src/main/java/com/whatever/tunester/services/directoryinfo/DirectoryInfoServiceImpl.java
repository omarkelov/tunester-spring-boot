package com.whatever.tunester.services.directoryinfo;

import com.whatever.tunester.constants.Mappings;
import com.whatever.tunester.database.entities.Track;
import com.whatever.tunester.database.entities.TrackMeta;
import com.whatever.tunester.database.repositories.TrackRepository;
import com.whatever.tunester.entities.DirectoryInfo;
import com.whatever.tunester.services.tracksmetascanner.TracksMetaScannerService;
import com.whatever.tunester.util.FileFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.whatever.tunester.constants.SystemProperties.ROOT_PATH_NAME;
import static com.whatever.tunester.util.TimestampUtils.getLastModifiedTimestamp;

@Service
public class DirectoryInfoServiceImpl implements DirectoryInfoService {

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private TracksMetaScannerService tracksMetaScannerService;

    @Override
    public DirectoryInfo getDirectoryInfo(String requestURI, int rating) {
        Path systemPath = getSystemPath(ROOT_PATH_NAME, requestURI);
        Path rootPath = Path.of(ROOT_PATH_NAME);

        List<Path> paths;
        try (Stream<Path> pathsStream = java.nio.file.Files.list(systemPath)) {
            paths = pathsStream.toList();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        List<String> directories = new ArrayList<>();
        List<Track> tracks = new ArrayList<>();

        for (Path path : paths) {
            Path relativePath = rootPath.relativize(path);

            if (Files.isDirectory(path)) {
                directories.add(relativePath.toString());
                continue;
            }

            if (FileFormatUtils.isAudioFile(relativePath)) {
                Timestamp lastModifiedTimestamp = getLastModifiedTimestamp(path);
                Track track = trackRepository.findByPath(relativePath.toString());

                if (track == null || !lastModifiedTimestamp.equals(track.getLastModified())) { // TODO: save to repository (in transaction)
                    TrackMeta trackMeta = tracksMetaScannerService.getTrackMeta(path.toString());
                    track = Track.builder()
                        .path(relativePath.toString())
                        .lastModified(lastModifiedTimestamp)
                        .trackMeta(trackMeta)
                        .build();
                }

                if (rating == 0 || (track.getTrackMeta().getRating() != null && track.getTrackMeta().getRating() >= rating)) {
                    tracks.add(track);
                }
            }
        }

        return new DirectoryInfo(directories, tracks);
    }

    private Path getSystemPath(String rootPathName, String requestURI) {
        String uri = UriUtils.decode(requestURI, StandardCharsets.UTF_8);
        String cutUri = uri.substring(uri.indexOf(Mappings.MUSIC) + Mappings.MUSIC.length());
        List<String> pathParts = Arrays.stream(cutUri.split("/")).filter(Predicate.not(String::isBlank)).toList();
        Path systemPath = Path.of(rootPathName).resolve(String.join(File.separator, pathParts)).normalize();

        if (!systemPath.startsWith(rootPathName)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return systemPath;
    }
}
