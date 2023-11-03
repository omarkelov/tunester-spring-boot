package com.whatever.tunester.services.tracksmetascanner;

import com.whatever.tunester.database.entities.TrackMeta;

public interface TracksMetaScannerService extends AutoCloseable {
    TrackMeta getTrackMeta(String absolutePathName);
    void close();
}
