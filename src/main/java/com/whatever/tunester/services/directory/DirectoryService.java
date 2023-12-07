package com.whatever.tunester.services.directory;

import com.whatever.tunester.database.entities.Directory;

import java.nio.file.Path;

public interface DirectoryService {
    Directory getDirectory(Path rootPath, Path directoryPath, int rating);
    Directory getPreviousDirectory(String directoryRelativePath, String rootPath, int rating);
    Directory getNextDirectory(String directoryRelativePath, String rootPath, int rating);
}
