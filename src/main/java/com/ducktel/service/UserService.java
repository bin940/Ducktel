package com.ducktel.service;

import com.ducktel.dto.UserDTO;

public interface UserService {
    String registerUser(UserDTO userDTO);
    UserDTO getProfile(Long userId);
    UserDTO updateProfile(Long userId, UserDTO userData);
    String deleteProfile(Long userId);
}
