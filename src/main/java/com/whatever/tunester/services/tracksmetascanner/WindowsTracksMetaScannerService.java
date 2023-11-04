package com.whatever.tunester.services.tracksmetascanner;

import jakarta.annotation.PreDestroy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static com.whatever.tunester.constants.SystemProperties.APP_PATH;

public class WindowsTracksMetaScannerService extends AbstractTracksMetaScannerService {

    private Path tmpBatFilePath;

    public WindowsTracksMetaScannerService() {
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
    protected void writeGetTrackMetaJsonCommand(String ffmpegCommand) throws IOException {
        Files.writeString(tmpBatFilePath, ffmpegCommand);
        writeCommand(tmpBatFilePath.toString());
    }

    @Override
    protected int getConsoleResultTrimFromIndex() {
        return 4;
    }

    @PreDestroy
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
