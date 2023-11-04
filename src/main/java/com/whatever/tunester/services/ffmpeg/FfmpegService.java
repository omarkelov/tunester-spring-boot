package com.whatever.tunester.services.ffmpeg;

import com.whatever.tunester.database.entities.TrackMeta;

public interface FfmpegService extends AutoCloseable {
    TrackMeta getTrackMeta(String absolutePathName);
    void close();
}
