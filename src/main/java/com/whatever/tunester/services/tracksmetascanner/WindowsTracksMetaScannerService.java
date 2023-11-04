package com.whatever.tunester.services.tracksmetascanner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatever.tunester.database.entities.TrackMeta;
import jakarta.annotation.PreDestroy;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import static com.whatever.tunester.constants.SystemProperties.APP_PATH;

public class WindowsTracksMetaScannerService implements TracksMetaScannerService {

    private static final String COMMANDS_DELIMITER = "__COMMANDS_DELIMITER__";

    private Path tmpBatFilePath;
    private Process process;
    private BufferedWriter stdin;
    private Scanner scanner;

    public WindowsTracksMetaScannerService() {
        ProcessBuilder builder = new ProcessBuilder("cmd.exe");
        try {
            Files.createDirectories(APP_PATH);
            tmpBatFilePath = APP_PATH.resolve("tmp_" + UUID.randomUUID() + ".bat");

            process = builder.start();
            stdin = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8));
            stdin.write("chcp 65001");
            stdin.newLine();
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

    @PreDestroy
    @Override
    public void close() {
        try {
            Files.deleteIfExists(tmpBatFilePath);

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

    public TrackMeta getTrackMeta(String absolutePathName) {
        try {
            return new ObjectMapper().readValue(getTrackMetaJson(absolutePathName), TrackMeta.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getTrackMetaJson(String absolutePathName) {
        try {
            String command = String.format(
                "ffprobe -show_format \"%s\" -v 0 -of json",
                absolutePathName.replaceAll("%", "%%")
            );

            Files.writeString(tmpBatFilePath, command);

            stdin.write(tmpBatFilePath.toString());
            stdin.newLine();
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

            return String.join("", lines.stream().filter(line -> !line.isBlank()).toList().subList(2, lines.size() - 3));
        } catch (IOException | IndexOutOfBoundsException e) {
            e.printStackTrace();
            return "{}";
        }
    }
}
