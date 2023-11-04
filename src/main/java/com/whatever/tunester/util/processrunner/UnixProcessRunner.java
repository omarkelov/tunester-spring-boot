package com.whatever.tunester.util.processrunner;

public class UnixProcessRunner extends AbstractProcessRunner {
    @Override
    protected String getProcessBuilderCommand() {
        return "sh";
    }

    @Override
    protected void writeInitConsoleCommand() {}

    @Override
    protected void execute(String command) {
        writeCommand(command);
    }

    @Override
    protected int getResultTrimFromIndex() {
        return 2;
    }
}
