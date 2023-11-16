package com.whatever.tunester.util;

import jakarta.servlet.http.HttpServletRequest;

import java.nio.charset.StandardCharsets;

public class UriUtils {
    public static String getPathAfterSubstring(HttpServletRequest request, String substring) {
        String uri = org.springframework.web.util.UriUtils.decode(request.getRequestURI(), StandardCharsets.UTF_8);
        return uri.substring(uri.indexOf(substring) + substring.length());
    }
}
