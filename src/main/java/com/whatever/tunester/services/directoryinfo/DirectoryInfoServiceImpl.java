package com.whatever.tunester.services.directoryinfo;

import com.whatever.tunester.database.entities.Track;
import com.whatever.tunester.database.entities.TrackMeta;
import com.whatever.tunester.database.repositories.TrackRepository;
import com.whatever.tunester.entities.DirectoryInfo;
import com.whatever.tunester.services.ffmpeg.FfmpegService;
import com.whatever.tunester.services.path.PathService;
import com.whatever.tunester.util.FileFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.whatever.tunester.constants.SystemProperties.ROOT_PATH_NAME;
import static com.whatever.tunester.util.TimestampUtils.getLastModifiedTimestamp;

@Service
public class DirectoryInfoServiceImpl implements DirectoryInfoService {

    @Autowired
    private PathService pathService;

    @Autowired
    private FfmpegService ffmpegService;

    @Autowired
    private TrackRepository trackRepository;

    @Override
    public DirectoryInfo getDirectoryInfo(String directoryRelativePath, int rating) {
        Path directorySystemPath = pathService.getSystemPath(ROOT_PATH_NAME, directoryRelativePath);
        Path rootPath = Path.of(ROOT_PATH_NAME);

        List<Path> paths;
        try (Stream<Path> pathsStream = java.nio.file.Files.list(directorySystemPath)) {
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
                    TrackMeta trackMeta = ffmpegService.getTrackMeta(path);
                    track = Track.builder()
                        .path(relativePath.toString().replace('\\', '/'))
                        .lastModified(lastModifiedTimestamp)
                        .trackMeta(trackMeta)
                        .build();
                }

                if (rating == 0 || track.getRating() >= rating) {
                    tracks.add(track);
                }
            }
        }

        return new DirectoryInfo(directories, tracks);
    }
}
