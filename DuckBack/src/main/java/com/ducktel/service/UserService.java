package com.ducktel.service;

import com.ducktel.dto.UserDTO;

import java.util.UUID;

public interface UserService {
    String registerUser(UserDTO userDTO);
    UserDTO getProfile(UUID userId);
    UserDTO updateProfile(UUID userId, UserDTO userData);
    String deleteProfile(UUID userId);
    String passWordReset(UUID userId, String newPassword);
    String toggleLike (UUID userId, Long accommodationId);
}
