package com.ducktel.service;

import com.ducktel.exception.CustomException;
import com.ducktel.domain.entity.User;
import com.ducktel.domain.repository.UserRepository;
import com.ducktel.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
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

    @Override
    public UserDTO getProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("등록되지 않은 사용자입니다: " + userId));

        return user.getUser();
    }

    @Override
    public UserDTO updateProfile(String userId, UserDTO userData) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("등록되지 않은 사용자입니다: " + userId));
       user = userData.updateUser(user);

        User updatedUser = userRepository.save(user);

        return updatedUser.updateUser(userData);
    }
    //user return enum 상수로 관리!!
    @Override
    public String deleteProfile(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException("NOT FOUND", "유저를 찾을 수 없습니다. ID: " + userId);
        }
        userRepository.deleteById(userId);
        return "삭제되었습니다.";
    }

    @Override
    public String passWordReset(String userId, String newPassword) {
        int insert =userRepository.updatePassword(userId, passwordEncoder.encode(newPassword));
        log.info(insert + "개가 변경 되었습니다.");
        return "비밀번호가 변경 되었습니다.";
    }

}
