package com.whatever.tunester.services.track;

import com.whatever.tunester.database.entities.Track;
import com.whatever.tunester.database.entities.TrackMetaCommentCut;
import org.springframework.core.io.FileSystemResource;

public interface TrackService {
    FileSystemResource getTrackResource(String trackRelativePath, String rootPath);
    Track getPreviousRandomTrack(String trackRelativePath, String rootPath, int rating, int seed);
    Track getNextRandomTrack(String trackRelativePath, String rootPath, int rating, int seed);
    void rateTrack(String trackRelativePath, String rootPath, int rating);
    void cutTrack(String trackRelativePath, String rootPath, TrackMetaCommentCut trackMetaCommentCut);
}
