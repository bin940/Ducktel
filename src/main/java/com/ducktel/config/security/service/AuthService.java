package com.ducktel.config.security.service;

import com.ducktel.config.security.jwt.JwtUtils;
import com.ducktel.dto.PrincipalDetailDTO;
import com.ducktel.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;

    public Map<String, String> login(String username, String password) {
        try {
            // Spring Security를 이용해 사용자 인증
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // 인증된 사용자 정보 가져오기
            PrincipalDetailDTO principal = (PrincipalDetailDTO) authentication.getPrincipal();

            // SecurityContext에 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);


            //JWT 토큰 생성
            String accessToken = JwtUtils.generateToken(
                    Map.of(
                            "userId", principal.getUser().getUserId(),
                            "username", principal.getUsername(),
                            "roles", String.join(",", principal.getAuthorities().stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .toList()) // 권한을 문자열로 변환
                    ),
                    60 // 유효 시간: 60분
            );

            String refreshToken = JwtUtils.generateToken(
                    Map.of(
                            "userId", principal.getUser().getUserId(),
                            "username", principal.getUsername(),
                            "type", "refresh"
                    ),
                    60 * 24 * 7 // 7일
            );

            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);

        } catch (BadCredentialsException e) {
            log.error("BadCredentialsException 발생 - 아이디 또는 비밀번호가 틀림!");
            throw new CustomException("LOGIN_FAILED", "아이디 또는 비밀번호가 잘못되었습니다.");
        } catch (AuthenticationException e) {
            log.error("AuthenticationException 발생 - " + e.getMessage());
            throw new CustomException("LOGIN_FAILED", "로그인에 실패하였습니다.");
        }
    }
}
