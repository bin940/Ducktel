package com.ducktel.config.security.cookie.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.Optional;

public class CookieUtils {


    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return Optional.empty();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return Optional.of(cookie);
            }
        }

        return Optional.empty();
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(true); // HTTPS 환경에서만 전송되도록
        cookie.setDomain("ducktel.uk");
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        getCookie(request, name).ifPresent(cookie -> {
            cookie.setValue("");
            cookie.setPath("/");
            cookie.setMaxAge(0);
            cookie.setDomain("ducktel.uk");
            response.addCookie(cookie);
        });
    }

    public static String serialize(Object object) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getUrlDecoder().decode(cookie.getValue()));
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return cls.cast(objectInputStream.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalArgumentException("Deserialization failed", e);
        }
    }
}
