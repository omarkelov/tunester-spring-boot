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
    public FileSystemResource getTrackResource(Path trackPath) {
        validate(trackPath);

        return new FileSystemResource(trackPath);
    }

    @Override
    public void rateTrack(Path trackPath, int rating) {
        validate(trackPath);

        ffmpegService.rateTrack(trackPath, rating);
    }

    @Override
    public void cutTrack(Path trackPath, TrackMetaCommentCut trackMetaCommentCut) {
        validate(trackPath);

        ffmpegService.cutTrack(trackPath, trackMetaCommentCut);
    }

    private void validate(Path trackSystemPath) {
        if (!isAudioFile(trackSystemPath)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
