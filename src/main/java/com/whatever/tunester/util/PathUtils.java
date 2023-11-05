package com.whatever.tunester.util;

import com.google.common.io.Files;

import java.nio.file.Path;

import static java.nio.file.Files.exists;

public class PathUtils {
    public static Path extendFilename(Path path, String prefix, String postfix) {
        String name = Files.getNameWithoutExtension(path.getFileName().toString());
        String extension = Files.getFileExtension(path.getFileName().toString());

        return Path.of(path.getParent().toString(), prefix + name + postfix + "." + extension);
    }

    public static Path getNextFreePath(Path path) {
        Path newPath;
        int i = 1;

        do {
            newPath = extendFilename(path, "", " (" + i++ + ")");
        } while (exists(newPath));

        return newPath;
    }
}
