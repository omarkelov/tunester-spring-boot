package com.whatever.tunester.services.token;

public interface TokenService {
    String generateToken(String subject);
    String getSubject(String token);
}
