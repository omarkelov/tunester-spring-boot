package com.whatever.tunester.services.ffmpeg;

import com.whatever.tunester.database.entities.TrackMeta;
import com.whatever.tunester.database.entities.TrackMetaCommentCut;

public interface FfmpegService extends AutoCloseable {
    TrackMeta getTrackMeta(String absolutePathName);
    boolean updateTrackRating(int rating);
    boolean cutTrack(TrackMetaCommentCut trackMetaCommentCut);
    void close();
}
