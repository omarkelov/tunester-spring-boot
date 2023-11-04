package com.whatever.tunester.util.processrunner;

import static com.whatever.tunester.constants.SystemProperties.IS_WINDOWS;

public class ProcessRunnerFactory {
    public static ProcessRunner newProcessRunner() {
        return IS_WINDOWS ? new WindowsProcessRunner() : new UnixProcessRunner();
    }
}
