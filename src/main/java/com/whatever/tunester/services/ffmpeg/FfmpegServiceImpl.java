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

import static com.whatever.tunester.services.ffmpeg.Util.getBitrate;
import static com.whatever.tunester.services.ffmpeg.Util.getFading;
import static com.whatever.tunester.util.PathUtils.extendFilename;
import static com.whatever.tunester.util.PathUtils.getNextFreePath;

@Service
@RequestScope
public class FfmpegServiceImpl implements FfmpegService {

    private final ProcessRunner processRunner = ProcessRunnerFactory.newProcessRunner();

    @Override
    public TrackMeta getTrackMeta(Path path) {
        try {
            String command = String.format(
                "ffprobe -show_format \"%s\" -v 0 -of json",
                path.toString().replace("%", "%%")
            );

            List<String> executionResult = processRunner.executeCommand(command, Files.exists(path));

            return new ObjectMapper().readValue(String.join("", executionResult), TrackMeta.class);
        } catch (UnsafeCommandException | JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void rateTrack(Path path, int rating) {
        try {
            TrackMeta trackMeta = getTrackMeta(path);
            TrackMetaComment trackMetaComment = (
                trackMeta == null || trackMeta.getTrackMetaComment() == null
                    ? new TrackMetaComment()
                    : trackMeta.getTrackMetaComment()
            ).setRating(rating).incrementedVersion();

            String comment = new ObjectMapper().writeValueAsString(trackMetaComment);
            Path tmpPath = extendFilename(path, "_tmp_" + UUID.randomUUID() + "_", "");

            String command = String.format(
                "ffmpeg -i \"%s\" -metadata comment=\"%s\" -codec copy \"%s\" 2>&1",
                path.toString().replace("%", "%%"),
                comment.replace("\"", "\\\\\""),
                tmpPath
            );

            executeFfmpegCommand(command, path, comment);

            renameNewVersion(path, tmpPath, trackMetaComment.getVersion());
        } catch (UnsafeCommandException | JsonProcessingException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public void cutTrack(Path path, TrackMetaCommentCut trackMetaCommentCut) {
        try {
            TrackMeta trackMeta = getTrackMeta(path);
            TrackMetaComment trackMetaComment = (
                trackMeta == null || trackMeta.getTrackMetaComment() == null
                    ? new TrackMetaComment()
                    : trackMeta.getTrackMetaComment()
            ).incrementedVersion();
            if (trackMetaComment.getTrackMetaCommentCut() == null) {
                trackMetaComment.setTrackMetaCommentCut(new TrackMetaCommentCut());
            }
            trackMetaComment.getTrackMetaCommentCut().update(trackMetaCommentCut);

            Path cutPath = getNextFreePath(extendFilename(path, "", "_cut"));
            String start = trackMetaCommentCut.getStart() != null ? "-ss " + trackMetaCommentCut.getStart() : "";
            String end = trackMetaCommentCut.getEnd() != null ? "-to " + trackMetaCommentCut.getEnd() : "";
            String input = path.toString().replace("%", "%%");
            String comment = new ObjectMapper().writeValueAsString(trackMetaComment);
            String fading = getFading(trackMetaCommentCut);
            String bitrate = String.valueOf(getBitrate(trackMeta));

            String command = String.format(
                "ffmpeg -copyts %s %s -i \"%s\" -vn -metadata comment=\"%s\" %s -b:a %s -c:a libmp3lame \"%s\" 2>&1",
                start,
                end,
                input,
                comment.replace("\"", "\\\\\""),
                fading,
                bitrate,
                cutPath
            );

            executeFfmpegCommand(command, path, comment);

//            return cutPath; // TODO: implement
        } catch (UnsafeCommandException | JsonProcessingException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    private void executeFfmpegCommand(String command, Path path, String comment) throws UnsafeCommandException {
        List<String> executionResult = processRunner.executeCommand(command, Files.exists(path));
        String[] splitResult = String.join("", executionResult).split("Output #0");

        if (splitResult.length < 2 || !splitResult[1].contains(comment)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during FFmpeg processing");
        }
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
