package com.whatever.tunester.constants;

import java.nio.file.Path;

public class SystemProperties {
    private static final String USER_HOME_DIRECTORY = System.getProperty("user.home");
    private static final String WINDOWS_LOCAL_DIRECTORY = System.getenv("LOCALAPPDATA");
    private static final String APP_DIRECTORY_NAME = "tunester";

    public static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().startsWith("windows");
    public static final Path APP_PATH = Path.of(IS_WINDOWS ? WINDOWS_LOCAL_DIRECTORY : USER_HOME_DIRECTORY, APP_DIRECTORY_NAME);

    private static final int N_CORES = Runtime.getRuntime().availableProcessors();
    public static final int N_THREADS_OPTIMAL;
    static {
        if (N_CORES < 2) {
            N_THREADS_OPTIMAL = 1;
        } else if (N_CORES < 5) {
            N_THREADS_OPTIMAL = N_CORES;
        } else {
            N_THREADS_OPTIMAL = N_CORES - 1;
        }
    }
}
