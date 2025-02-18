package com.ducktel.config.security.service;

import com.ducktel.domain.entity.User;
import com.ducktel.domain.repository.UserRepository;
import com.ducktel.dto.PrincipalDetailDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("등록되지 않은 사용자입니다: " + username));

        return new PrincipalDetailDTO(user,
                user.getAuthorities());
    }


}