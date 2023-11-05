package com.whatever.tunester.services.ffmpeg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatever.tunester.database.entities.TrackMeta;
import com.whatever.tunester.database.entities.TrackMetaComment;
import com.whatever.tunester.database.entities.TrackMetaCommentCut;
import com.whatever.tunester.util.processrunner.ProcessRunner;
import com.whatever.tunester.util.processrunner.ProcessRunnerFactory;
import com.whatever.tunester.util.processrunner.UnsafeCommandException;
import jakarta.annotation.PreDestroy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static com.whatever.tunester.util.PathUtils.extendFilename;

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

    @Override
    public void rateTrack(Path path, int rating) {
        try {
            TrackMeta trackMeta = getTrackMeta(path.toString());
            TrackMetaComment trackMetaComment = (
                trackMeta == null || trackMeta.getTrackMetaComment() == null
                    ? new TrackMetaComment()
                    : trackMeta.getTrackMetaComment()
            ).setRating(rating).incrementedVersion();

            Path tmpPath = extendFilename(path, "_tmp_" + UUID.randomUUID() + "_", "");

            String command = String.format(
                "ffmpeg -i \"%s\" -metadata comment=\"%s\" -codec copy \"%s\" 2>&1",
                path.toString().replaceAll("%", "%%"),
                new ObjectMapper().writeValueAsString(trackMetaComment).replaceAll("\"", "\\\\\""),
                tmpPath
            );

            List<String> executionResult = processRunner.executeCommand(command, Files.exists(path));
            String[] splitResult = String.join("", executionResult).split("Output #0");

            if (splitResult.length < 2 || !splitResult[1].contains(String.format("\"rating\":%d", trackMetaComment.getRating()))) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during FFmpeg processing");
            }

            renameNewVersion(path, tmpPath, trackMetaComment.getVersion());
        } catch (UnsafeCommandException | JsonProcessingException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public void cutTrack(Path path, TrackMetaCommentCut trackMetaCommentCut) {

    }

    private void renameNewVersion(Path path, Path tmpPath, int version) {
        Path versionedPath = extendFilename(path, "_", "_v" + (version - 1));

        try {
            Files.move(path, versionedPath);
        } catch (IOException e) {
            try {
                Files.deleteIfExists(tmpPath);
            } catch (IOException ex) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error during original file renaming (also could not delete temporary file)");
            }

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error during original file renaming");
        }

        try {
            Files.move(tmpPath, path);
        } catch (IOException e) {
            try {
                Files.move(versionedPath, path);
            } catch (IOException ex) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error during temporary file renaming (also could not rename original file back)");
            }

            try {
                Files.deleteIfExists(tmpPath);
            } catch (IOException ex) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error during temporary file renaming (also could not delete temporary file)");
            }

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error during temporary file renaming");
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
