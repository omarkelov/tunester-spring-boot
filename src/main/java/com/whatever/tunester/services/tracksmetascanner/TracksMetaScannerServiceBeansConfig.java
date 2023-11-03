package com.whatever.tunester.services.tracksmetascanner;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.annotation.RequestScope;

import static com.whatever.tunester.constants.SystemProperties.IS_WINDOWS;

@Configuration
public class TracksMetaScannerServiceBeansConfig {
    @Bean
    @RequestScope
    @Primary
    public TracksMetaScannerService getTracksMetaScannerService() {
        return getBean();
    }

    @Bean(name = "prototype")
    @Scope("prototype")
    public TracksMetaScannerService getTracksMetaScannerServicePrototype() {
        return getBean();
    }

    private TracksMetaScannerService getBean() {
        return IS_WINDOWS ? new WindowsTracksMetaScannerService() : new UnixTracksMetaScannerService();
    }
}
