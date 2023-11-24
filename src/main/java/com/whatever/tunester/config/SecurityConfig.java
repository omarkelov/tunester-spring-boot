package com.whatever.tunester.config;

import com.whatever.tunester.constants.Mappings;
import com.whatever.tunester.database.entities.User;
import com.whatever.tunester.services.token.TokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String OPTIONS_MAX_AGE_SECONDS = String.valueOf(30 * 60);

    @Autowired
    private TokenFilter tokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults()) // TODO: is it needed?
            .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // TODO: is it needed?
            .authorizeHttpRequests(matcherRegistry -> matcherRegistry
                .requestMatchers(Mappings.API + Mappings.LOGIN + "/**").anonymous()
                .requestMatchers(HttpMethod.HEAD).hasAnyAuthority(User.Role.ADMIN.toString(), User.Role.USER.toString())
                .requestMatchers(HttpMethod.GET).hasAnyAuthority(User.Role.ADMIN.toString(), User.Role.USER.toString())
                .requestMatchers(HttpMethod.POST).hasAuthority(User.Role.ADMIN.toString())
                .requestMatchers(HttpMethod.PUT).hasAuthority(User.Role.ADMIN.toString())
                .requestMatchers(HttpMethod.PATCH).hasAuthority(User.Role.ADMIN.toString())
                .requestMatchers(HttpMethod.OPTIONS).hasAuthority(User.Role.ADMIN.toString())
                .requestMatchers(HttpMethod.DELETE).hasAuthority(User.Role.ADMIN.toString())
                .requestMatchers(HttpMethod.TRACE).hasAuthority(User.Role.ADMIN.toString())
                .anyRequest().authenticated()
            )
            .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class) // TODO: why use addFilterBefore?
            .headers(httpSecurityHeadersConfigurer ->
                httpSecurityHeadersConfigurer.addHeaderWriter((request, response) -> {
                    if (request.getMethod().equals(HttpMethod.OPTIONS.toString())) {
                        response.setHeader("Access-Control-Max-Age", OPTIONS_MAX_AGE_SECONDS);
                    }
                })
            )
            .build();
    }
}
