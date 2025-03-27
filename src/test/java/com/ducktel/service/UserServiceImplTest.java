package com.ducktel.service;

import com.ducktel.domain.entity.User;
import com.ducktel.domain.repository.UserRepository;
import com.ducktel.dto.UserDTO;
import com.ducktel.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private User user;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password123");
        userDTO.setName("홍길동");
        userDTO.setEmail("test@example.com");
        userDTO.setPhoneNumber("01012345678");

        user = new User();
        user.setUserId("user123");
        user.setUsername("testuser");
        user.setName("홍길동");
        user.setEmail("test@example.com");
        user.setPhoneNumber("01012345678");
        user.setPassword("encodedPassword");
    }

    @Test
    void registerUser_Success() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        String result = userService.registerUser(userDTO);

        assertThat(result).isEqualTo("홍길동");
    }

    @Test
    void registerUser_UsernameAlreadyExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(userDTO))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("이미 존재하는 사용자명입니다.");
    }

    @Test
    void getProfile_Success() {
        User user = User.builder()
                .userId("user123")
                .username("testuser")
                .email("test@example.com")
                .phoneNumber("01012345678")
                .name("홍길동")
                .role("ROLE_USER")
                .build();

        when(userRepository.findById("user123")).thenReturn(Optional.of(user));

        UserDTO result = userService.getProfile("user123");

        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void getProfile_NotFound() {
        when(userRepository.findById("user123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getProfile("user123"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void updateProfile_Success() {
        User user = new User();
        user.setUserId("user123");
        user.setName("기존이름");

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("new@example.com");
        userDTO.setPhoneNumber("01011112222");

        when(userRepository.findById("user123")).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        UserDTO result = userService.updateProfile("user123", userDTO);

        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getPhoneNumber()).isEqualTo("01011112222");
    }

    @Test
    void deleteProfile_Success() {
        when(userRepository.existsById("user123")).thenReturn(true);

        String result = userService.deleteProfile("user123");

        verify(userRepository).deleteById("user123");
        assertThat(result).isEqualTo("삭제되었습니다.");
    }

    @Test
    void deleteProfile_NotFound() {
        when(userRepository.existsById("user123")).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteProfile("user123"))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void passWordReset_Success() {
        when(passwordEncoder.encode("newpass")).thenReturn("encodedPass");
        when(userRepository.updatePassword("user123", "encodedPass")).thenReturn(1);

        String result = userService.passWordReset("user123", "newpass");

        assertThat(result).isEqualTo("비밀번호가 변경 되었습니다.");
    }
}
