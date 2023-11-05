package com.whatever.tunester.util;

import com.google.common.io.Files;

import java.nio.file.Path;

public class PathUtils {
    public static Path extendFilename(Path path, String prefix, String postfix) {
        String name = Files.getNameWithoutExtension(path.getFileName().toString());
        String extension = Files.getFileExtension(path.getFileName().toString());

        return Path.of(path.getParent().toString(), prefix + name + postfix + "." + extension);
    }
}
