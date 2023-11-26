package com.whatever.tunester.services.track;

import com.whatever.tunester.database.entities.TrackMetaCommentCut;
import org.springframework.core.io.FileSystemResource;

public interface TrackService {
    FileSystemResource getTrackResource(String trackRelativePath, String rootPath);
    void rateTrack(String trackRelativePath, String rootPath, int rating);
    void cutTrack(String trackRelativePath, String rootPath, TrackMetaCommentCut trackMetaCommentCut);
}
