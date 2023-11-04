package com.whatever.tunester.util.processrunner;

import java.util.List;

public interface ProcessRunner extends AutoCloseable {
    List<String> executeCommand(String command);
}
