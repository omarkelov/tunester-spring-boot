package com.whatever.tunester.services.tracksmetascanner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatever.tunester.database.entities.TrackMeta;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class AbstractTracksMetaScannerService implements TracksMetaScannerService {

    private static final String COMMANDS_DELIMITER = "__COMMANDS_DELIMITER__";
    private Process process;
    private BufferedWriter stdin;
    private Scanner scanner;

    public AbstractTracksMetaScannerService() {
        try {
            ProcessBuilder builder = new ProcessBuilder(getProcessBuilderCommand());
            process = builder.start();
            stdin = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8));
            writeInitConsoleCommand();
            stdin.write(String.format("echo %s", COMMANDS_DELIMITER));
            stdin.newLine();
            stdin.flush();

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

    @Override
    public TrackMeta getTrackMeta(String absolutePathName) {
        try {
            return new ObjectMapper().readValue(getTrackMetaJson(absolutePathName), TrackMeta.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void writeCommand(String command) {
        try {
            stdin.write(command);
            stdin.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract String getProcessBuilderCommand();

    protected abstract void writeInitConsoleCommand();

    protected abstract void writeGetTrackMetaJsonCommand(String ffmpegCommand) throws IOException;

    protected abstract int getConsoleResultTrimFromIndex();

    private String getTrackMetaJson(String absolutePathName) {
        try {
            String command = String.format(
                "ffprobe -show_format \"%s\" -v 0 -of json",
                absolutePathName.replaceAll("%", "%%")
            );

            writeGetTrackMetaJsonCommand(command);
            stdin.write(String.format("echo %s", COMMANDS_DELIMITER));
            stdin.newLine();
            stdin.flush();

            List<String> lines = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String nextLine = scanner.nextLine();
                lines.add(nextLine);
                if (nextLine.equals(COMMANDS_DELIMITER)) {
                    break;
                }
            }

            return String.join("", lines.stream().toList().subList(getConsoleResultTrimFromIndex(), lines.size() - 3));
        } catch (IOException | IndexOutOfBoundsException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    @Override
    public void close() {
        try {
            if (stdin != null) {
                stdin.write("exit");
                stdin.newLine();
                stdin.flush();
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
