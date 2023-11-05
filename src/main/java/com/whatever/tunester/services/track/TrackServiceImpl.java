package com.whatever.tunester.services.track;

import com.whatever.tunester.database.entities.TrackMetaCommentCut;
import com.whatever.tunester.services.ffmpeg.FfmpegService;
import com.whatever.tunester.services.path.PathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

import static com.whatever.tunester.constants.SystemProperties.ROOT_PATH_NAME;

@Service
public class TrackServiceImpl implements TrackService {

    @Autowired
    private PathService pathService;

    @Autowired
    private FfmpegService ffmpegService;

    @Override
    public void rateTrack(String trackRelativePath, int rating) {
        Path systemPath = pathService.getSystemPath(ROOT_PATH_NAME, trackRelativePath);

        ffmpegService.rateTrack(systemPath, rating);
    }

    @Override
    public void cutTrack(String trackRelativePath, TrackMetaCommentCut trackMetaCommentCut) {
        Path systemPath = pathService.getSystemPath(ROOT_PATH_NAME, trackRelativePath);

        ffmpegService.cutTrack(systemPath, trackMetaCommentCut);
    }
}
