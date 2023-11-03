package com.whatever.tunester.services.directoryinfo;

import com.whatever.tunester.entities.DirectoryInfo;

public interface DirectoryInfoService {
    DirectoryInfo getDirectoryInfo(String requestURI, int rating);
}
