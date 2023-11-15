package com.whatever.tunester.services.track;

import com.whatever.tunester.database.entities.TrackMetaCommentCut;
import org.springframework.core.io.FileSystemResource;

import java.nio.file.Path;

public interface TrackService {
    FileSystemResource getTrackResource(Path trackPath);
    void rateTrack(Path trackPath, int rating);
    void cutTrack(Path trackPath, TrackMetaCommentCut trackMetaCommentCut);
}
