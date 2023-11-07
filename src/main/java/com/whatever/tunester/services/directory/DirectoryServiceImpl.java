package com.whatever.tunester.services.directory;

import com.whatever.tunester.database.entities.Directory;
import com.whatever.tunester.database.repositories.DirectoryRepository;
import com.whatever.tunester.database.repositories.TrackRepository;
import com.whatever.tunester.services.path.PathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

import static com.whatever.tunester.constants.SystemProperties.ROOT_PATH_NAME;

@Service
public class DirectoryServiceImpl implements DirectoryService {

    @Autowired
    private PathService pathService;

    @Autowired
    private DirectoryRepository directoryRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Override
    public Directory getDirectory(String directoryRelativePath, int rating) { // TODO: use rating
        Path directorySystemPath = pathService.getSystemPath(ROOT_PATH_NAME, directoryRelativePath);
        Path rootPath = Path.of(ROOT_PATH_NAME);

        String relativePath = rootPath.relativize(directorySystemPath).toString().replace('\\', '/');
        if (relativePath.isEmpty()) {
            relativePath = ".";
        }

        Directory directory = directoryRepository.findByPath(relativePath);

        directory.setDirectories(
            directory
                .getDirectoriesFileNames()
                .stream()
                .map(directoryName -> directoryRepository.findByPath(getFullPath(directory.getPath(), directoryName)))
                .toList()
        );

        directory.setTracks(
            directory
                .getTracksFileNames()
                .stream()
                .map(trackName -> trackRepository.findByPath(getFullPath(directory.getPath(), trackName)))
                .toList()
        );

        return directory;
    }

    private String getFullPath(String path, String filename) {
        return path.equals(".")
            ? filename
            : path + "/" + filename;
    }
}
