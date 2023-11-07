package com.whatever.tunester.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.sql.Timestamp;

public class TimestampUtils {
    public static Timestamp getLastUpdatedTimestamp(Path path) {
        try {
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            FileTime creationTime = attrs.creationTime();
            FileTime lastModifiedTime = attrs.lastModifiedTime();

            return new Timestamp(Math.max(creationTime.toMillis(), lastModifiedTime.toMillis()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Timestamp(0);
    }
}
