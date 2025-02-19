package com.ducktel.service;

import com.ducktel.dto.UserDTO;
import org.springframework.http.ResponseEntity;

public interface UserService {
    String registerUser(UserDTO userDTO);
    UserDTO getProfile(Long userId);
}
