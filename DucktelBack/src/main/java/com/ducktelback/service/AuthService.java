package com.ducktelback.service;

import com.ducktelback.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final AuthenticationManager authenticationManager;


    public String login(String username, String password) {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return "로그인 성공";
        } catch (AuthenticationException e) {
            throw new CustomException("LOGIN_FAILED","로그인 실패" + e.getMessage());
        }

    }
}

