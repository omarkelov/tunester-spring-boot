package com.whatever.tunester.services.token;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@PropertySource("classpath:security.properties")
public class JwtTokenService implements TokenService {

    private static final long EXPIRATION_TIME_MILLIS = 180 * 24 * 60 * 60 * 1000L;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public String generateToken(String subject) {
        return Jwts.builder()
            .subject(subject)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MILLIS))
            .signWith(getSecretKey())
            .compact();
    }

    @Override
    public String getSubject(String token) {
        if (token == null) {
            return null;
        }

        try {
            return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
