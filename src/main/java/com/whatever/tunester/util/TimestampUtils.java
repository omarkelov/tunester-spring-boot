package com.whatever.tunester.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.sql.Timestamp;

public class TimestampUtils {
    public static Timestamp getLastModifiedTimestamp(Path path) {
        try {
            FileTime lastModifiedTime = Files.readAttributes(path, BasicFileAttributes.class).lastModifiedTime();

            return new Timestamp(lastModifiedTime.toMillis());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Timestamp(0);
    }
}
