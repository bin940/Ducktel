package com.ducktel.service;

import com.ducktel.dto.UserDTO;

public interface UserService {
    String registerUser(UserDTO userDTO);
    UserDTO getProfile(String userId);
    UserDTO updateProfile(String userId, UserDTO userData);
    String deleteProfile(String userId);
    String passWordReset(String userId, String newPassword);
}
