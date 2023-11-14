package com.whatever.tunester.services.track;

import com.whatever.tunester.database.entities.TrackMetaCommentCut;
import org.springframework.core.io.FileSystemResource;

public interface TrackService {
    FileSystemResource getTrackResource(String trackRelativePath);
    void rateTrack(String trackRelativePath, int rating);
    void cutTrack(String trackRelativePath, TrackMetaCommentCut trackMetaCommentCut);
}
