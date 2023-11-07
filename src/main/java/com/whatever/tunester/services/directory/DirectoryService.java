package com.whatever.tunester.services.directory;

import com.whatever.tunester.database.entities.Directory;

public interface DirectoryService {
    Directory getDirectory(String directoryRelativePath, int rating);
}
