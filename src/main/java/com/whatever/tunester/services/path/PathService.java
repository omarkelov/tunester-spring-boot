package com.whatever.tunester.services.path;

import java.nio.file.Path;

public interface PathService {
    Path getSystemPath(String rootPathName, String requestURI);
}
