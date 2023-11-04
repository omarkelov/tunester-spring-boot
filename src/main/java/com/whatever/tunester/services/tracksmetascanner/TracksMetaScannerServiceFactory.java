package com.whatever.tunester.services.tracksmetascanner;

public class TracksMetaScannerServiceFactory {
    public static TracksMetaScannerService newTracksMetaScannerService() {
        return new TracksMetaScannerServiceImpl();
    }
}
