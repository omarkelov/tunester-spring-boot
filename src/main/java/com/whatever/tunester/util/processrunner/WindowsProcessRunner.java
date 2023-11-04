package com.whatever.tunester.util.processrunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static com.whatever.tunester.constants.SystemProperties.APP_PATH;

public class WindowsProcessRunner extends AbstractProcessRunner {

    private Path tmpBatFilePath;

    public WindowsProcessRunner() {
        try {
            Files.createDirectories(APP_PATH);
            tmpBatFilePath = APP_PATH.resolve("tmp_" + UUID.randomUUID() + ".bat");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getProcessBuilderCommand() {
        return "cmd.exe";
    }

    @Override
    protected void writeInitConsoleCommand() {
        writeCommand("chcp 65001");
    }

    @Override
    protected void execute(String command) throws IOException {
        Files.writeString(tmpBatFilePath, command);
        writeCommand(tmpBatFilePath.toString());
    }

    @Override
    protected int getResultTrimFromIndex() {
        return 4;
    }

    @Override
    public void close() {
        super.close();

        try {
            Files.deleteIfExists(tmpBatFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
