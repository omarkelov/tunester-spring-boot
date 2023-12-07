package com.whatever.tunester.services.directory;

import com.whatever.tunester.database.entities.Directory;
import com.whatever.tunester.database.repositories.DirectoryRepository;
import com.whatever.tunester.database.repositories.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

import static com.whatever.tunester.util.ListUtils.getPreviousAndNextItems;

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

        return fillWithDirectoriesAndTracks(directoryRepository.findByPath(relativePath));
    }

    @Override
    public Directory getPreviousDirectory(String directoryRelativePath, String rootPath, int rating) {
        // TODO: Use rating
        // TODO: Store absolute path in db for getting only directories available for user
        // TODO: Implement it in SQL with LEAD/LAG after switch to modern version db

        return fillWithDirectoriesAndTracks(getPreviousAndNextDirectories(directoryRelativePath, rootPath, rating).get(0));
    }

    @Override
    public Directory getNextDirectory(String directoryRelativePath, String rootPath, int rating) {
        // TODO: Use rating
        // TODO: Store absolute path in db for getting only directories available for user
        // TODO: Implement it in SQL with LEAD/LAG after switch to modern version db

        return fillWithDirectoriesAndTracks(getPreviousAndNextDirectories(directoryRelativePath, rootPath, rating).get(1));
    }

    private Directory fillWithDirectoriesAndTracks(Directory directory) {
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

    private List<Directory> getPreviousAndNextDirectories(String directoryRelativePath, String rootPath, int rating) {
        List<Directory> directories = directoryRepository.findAllByOrderByPath();

        return getPreviousAndNextItems(directories, i -> directories.get(i).getPath().equals(directoryRelativePath));
    }
}
