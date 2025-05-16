package com.ducktel.config.security.service;

import com.ducktel.domain.entity.User;
import com.ducktel.domain.repository.UserRepository;
import com.ducktel.dto.PrincipalDetailDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("사용자 정보 로드 요청: username={}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("사용자 정보 로드 실패 - 등록되지 않은 사용자: username={}", username);
                    return new UsernameNotFoundException("등록되지 않은 사용자입니다: " + username);
                });

        log.info("사용자 정보 로드 성공: username={}, role={}", user.getUsername(), user.getRole());

        return new PrincipalDetailDTO(user,
                user.getAuthorities());
    }


}