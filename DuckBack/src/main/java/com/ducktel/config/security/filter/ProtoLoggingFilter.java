package com.ducktel.config.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ProtoLoggingFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(ProtoLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String proto = request.getHeader("x-forwarded-proto");
        log.info("x-forwarded-proto: {}", proto);
        filterChain.doFilter(request, response);
    }
}