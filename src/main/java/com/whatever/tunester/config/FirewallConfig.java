package com.whatever.tunester.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

@Configuration
public class FirewallConfig {
    @Bean
    public HttpFirewall allowEncodedParamsFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();

        firewall.setAllowUrlEncodedPercent(true);

        return firewall;
    }
}
