package com.whatever.tunester.util;

import com.google.common.io.Files;

import java.nio.file.Path;
import java.util.Set;

public class FileFormatUtils {
    private static final Set<String> AUDIO_FORMATS = Set.of(
        "3gp",
        "8svx",
        "aa",
        "aac",
        "aax",
        "act",
        "aiff",
        "alac",
        "amr",
        "ape",
        "au",
        "awb",
        "cda",
        "dss",
        "dvf",
        "flac",
        "gsm",
        "iklax",
        "ivs",
        "m4a",
        "m4b",
        "m4p",
        "mmf",
        "mogg",
        "movpkg",
        "mp3",
        "mpc",
        "msv",
        "nmf",
        "oga",
        "ogg",
        "opus",
        "ra",
        "raw",
        "rf64",
        "rm",
        "sln",
        "tta",
        "voc",
        "vox",
        "wav",
        "webm",
        "wma",
        "wv"
    );

    public static boolean isAudioFile(Path path) {
        return AUDIO_FORMATS.contains(Files.getFileExtension(path.getFileName().toString()));
    }
}
