package com.ducktelback.service;

import com.ducktelback.dto.SignupRequest;
import com.ducktelback.entity.User;
import com.ducktelback.exception.CustomException;
import com.ducktelback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void signUp(SignupRequest signupRequest) {

        if(userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new CustomException("USERNAME_ALREADY_EXISTS", "이미 존재하는 사용자명입니다.");
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setEmail(signupRequest.getEmail());
        user.setPhoneNumber(signupRequest.getPhoneNumber());

    }

}

