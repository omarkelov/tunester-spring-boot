package com.whatever.tunester.services.tracksmetascanner;

import jakarta.annotation.PreDestroy;

public class UnixTracksMetaScannerService extends AbstractTracksMetaScannerService {

    @Override
    protected String getProcessBuilderCommand() {
        return "sh";
    }

    @Override
    protected void writeInitConsoleCommand() {}

    @Override
    protected void writeGetTrackMetaJsonCommand(String ffmpegCommand) {
        writeCommand(ffmpegCommand);
    }

    @Override
    protected int getConsoleResultTrimFromIndex() {
        return 2;
    }

    @PreDestroy
    @Override
    public void close() {
        super.close();
    }
}
