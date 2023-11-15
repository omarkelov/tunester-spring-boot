package com.whatever.tunester.services.directory;

import com.whatever.tunester.database.entities.Directory;
import com.whatever.tunester.database.repositories.DirectoryRepository;
import com.whatever.tunester.database.repositories.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class DirectoryServiceImpl implements DirectoryService {

    @Autowired
    private DirectoryRepository directoryRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Override
    public Directory getDirectory(Path rootPath, Path directoryPath, int rating) { // TODO: use rating
        String relativePath = rootPath.relativize(directoryPath).toString().replace('\\', '/');
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
