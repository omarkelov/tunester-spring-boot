package com.whatever.tunester.services.track;

import com.whatever.tunester.database.entities.TrackMetaCommentCut;
import com.whatever.tunester.services.ffmpeg.FfmpegService;
import com.whatever.tunester.services.path.PathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Path;

import static com.whatever.tunester.util.FileFormatUtils.isAudioFile;

@Service
public class TrackServiceImpl implements TrackService {

    @Autowired
    private PathService pathService;

    @Autowired
    private FfmpegService ffmpegService;

    @Override
    public FileSystemResource getTrackResource(String trackRelativePath, String rootPath) {
        Path trackSystemPath = pathService.getSystemPath(rootPath, trackRelativePath);

        validate(trackSystemPath);

        return new FileSystemResource(trackSystemPath);
    }

    @Override
    public void rateTrack(String trackRelativePath, String rootPath, int rating) {
        Path trackSystemPath = pathService.getSystemPath(rootPath, trackRelativePath);

        validate(trackSystemPath);

        ffmpegService.rateTrack(trackSystemPath, rating);
    }

    @Override
    public void cutTrack(String trackRelativePath, String rootPath, TrackMetaCommentCut trackMetaCommentCut) {
        Path trackSystemPath = pathService.getSystemPath(rootPath, trackRelativePath);

        validate(trackSystemPath);

        ffmpegService.cutTrack(trackSystemPath, trackMetaCommentCut);
    }

    private void validate(Path path) {
        if (!isAudioFile(path)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
