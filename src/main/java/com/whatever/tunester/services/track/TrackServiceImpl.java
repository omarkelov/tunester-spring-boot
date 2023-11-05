package com.whatever.tunester.services.track;

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
    public void rateTrack(String requestURI, int rating) {
        Path systemPath = pathService.getSystemPath(ROOT_PATH_NAME, requestURI);

        ffmpegService.rateTrack(systemPath, rating);
    }
}
