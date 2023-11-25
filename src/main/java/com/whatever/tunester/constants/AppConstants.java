package com.whatever.tunester.constants;

public class AppConstants {
    public static final String TOKEN = "token";
    public static final int TOKEN_EXPIRATION_TIME_SECONDS = 180 * 24 * 60 * 60;
    public static final long TOKEN_EXPIRATION_TIME_MILLIS = TOKEN_EXPIRATION_TIME_SECONDS * 1000L;

    public static final String NOT_PERSISTED_PREFIX = "not_persisted_";
}
