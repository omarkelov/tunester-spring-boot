package com.whatever.tunester.services.ffmpeg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatever.tunester.database.entities.TrackMeta;
import com.whatever.tunester.util.processrunner.ProcessRunner;
import com.whatever.tunester.util.processrunner.ProcessRunnerFactory;
import com.whatever.tunester.util.processrunner.UnsafeCommandException;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@RequestScope
public class FfmpegServiceImpl implements FfmpegService {

    private final ProcessRunner processRunner = ProcessRunnerFactory.newProcessRunner();

    @Override
    public TrackMeta getTrackMeta(String absolutePathName) {
        try {
            String command = String.format(
                "ffprobe -show_format \"%s\" -v 0 -of json",
                absolutePathName.replaceAll("%", "%%")
            );

            List<String> ffmpegResult = processRunner.executeCommand(command, Files.exists(Path.of(absolutePathName)));

            return new ObjectMapper().readValue(String.join("", ffmpegResult), TrackMeta.class);
        } catch (UnsafeCommandException | JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PreDestroy
    @Override
    public void close() {
        try {
            processRunner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
