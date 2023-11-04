package com.whatever.tunester.services.tracksmetascanner;

import static com.whatever.tunester.constants.SystemProperties.IS_WINDOWS;

public class TracksMetaScannerServiceFactory {
    public static TracksMetaScannerService newTracksMetaScannerService() {
        return IS_WINDOWS ? new WindowsTracksMetaScannerService() : new UnixTracksMetaScannerService();
    }
}
