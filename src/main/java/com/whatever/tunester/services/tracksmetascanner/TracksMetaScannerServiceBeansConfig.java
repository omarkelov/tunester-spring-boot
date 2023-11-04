package com.whatever.tunester.services.tracksmetascanner;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class TracksMetaScannerServiceBeansConfig {
    @Bean
    @RequestScope
    public TracksMetaScannerService getTracksMetaScannerService() {
        return TracksMetaScannerServiceFactory.newTracksMetaScannerService();
    }
}
