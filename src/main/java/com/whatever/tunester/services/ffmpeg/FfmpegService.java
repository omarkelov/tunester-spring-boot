package com.whatever.tunester.services.ffmpeg;

import com.whatever.tunester.database.entities.TrackMeta;
import com.whatever.tunester.database.entities.TrackMetaCommentCut;

import java.nio.file.Path;

public interface FfmpegService extends AutoCloseable {
    TrackMeta getTrackMeta(Path path);
    void rateTrack(Path path, int rating);
    void cutTrack(Path path, TrackMetaCommentCut trackMetaCommentCut);
    void close();
}
