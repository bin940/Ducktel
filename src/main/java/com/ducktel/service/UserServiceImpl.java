package com.ducktel.service;

import com.ducktel.exception.CustomException;
import com.ducktel.domain.entity.User;
import com.ducktel.domain.repository.UserRepository;
import com.ducktel.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public String registerUser(UserDTO userDTO) {
        if(userRepository.existsByUsername(userDTO.getUsername())) {
            throw new CustomException("USERNAME_ALREADY_EXISTS", "이미 존재하는 사용자명입니다.");
        }
        User user = userDTO.createUser(passwordEncoder);

        User result= userRepository.save(user);
        return result.getName();
    }
}
