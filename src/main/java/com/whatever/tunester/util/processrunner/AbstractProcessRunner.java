package com.whatever.tunester.util.processrunner;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class AbstractProcessRunner implements ProcessRunner {

    private static final String COMMANDS_DELIMITER = "__COMMANDS_DELIMITER__";
    private Process process;
    private BufferedWriter stdin;
    private Scanner scanner;

    public AbstractProcessRunner() {
        try {
            ProcessBuilder builder = new ProcessBuilder(getProcessBuilderCommand());
            process = builder.start();
            stdin = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8));
            writeInitConsoleCommand();
            writeCommand(String.format("echo %s", COMMANDS_DELIMITER));

            scanner = new Scanner(process.getInputStream(), StandardCharsets.UTF_8);
            while (scanner.hasNextLine()) {
                String nextLine = scanner.nextLine();
                if (nextLine.equals(COMMANDS_DELIMITER)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> executeCommand(String command, boolean isCommandSafe) throws UnsafeCommandException {
        if (!isCommandSafe) {
            throw new UnsafeCommandException();
        }

        try {
            execute(command);
            // TODO: fix (the leading symbols of my command are cut during ffmpeg execution,
            //       since ffmpeg rewrites the last line when writing the progress)
            writeCommand(" ".repeat(512) + " echo " + COMMANDS_DELIMITER);

            List<String> lines = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String nextLine = scanner.nextLine();
                lines.add(nextLine);
                if (nextLine.equals(COMMANDS_DELIMITER)) {
                    break;
                }
            }

            return lines.stream().toList().subList(getResultTrimFromIndex(), lines.size() - 3);
        } catch (IOException | IndexOutOfBoundsException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    protected void writeCommand(String command) {
        try {
            stdin.write(command);
            stdin.newLine();
            stdin.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract String getProcessBuilderCommand();

    protected abstract void writeInitConsoleCommand();

    protected abstract void execute(String command) throws IOException;

    protected abstract int getResultTrimFromIndex();

    @Override
    public void close() {
        try {
            if (stdin != null) {
                writeCommand("exit");
                stdin.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (scanner != null) {
            scanner.close();
        }

        if (process != null) {
            process.destroy();
        }
    }
}
