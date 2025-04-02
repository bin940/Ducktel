package com.ducktel.service;

import com.ducktel.config.security.jwt.JwtUtils;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    public String getTokenFromHeader(String header) {
        return JwtUtils.getTokenFromHeader(header);
    }

    public String getUserIdFromToken(String token) {
        return JwtUtils.getUserIdFromToken(token);
    }
}
