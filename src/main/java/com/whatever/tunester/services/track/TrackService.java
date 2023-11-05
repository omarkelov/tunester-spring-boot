package com.whatever.tunester.services.track;

import com.whatever.tunester.database.entities.TrackMetaCommentCut;

public interface TrackService {
    void rateTrack(String trackRelativePath, int rating);
    void cutTrack(String trackRelativePath, TrackMetaCommentCut trackMetaCommentCut);
}
