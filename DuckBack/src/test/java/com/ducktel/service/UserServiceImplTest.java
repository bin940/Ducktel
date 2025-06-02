package com.ducktel.service;

import com.ducktel.domain.entity.User;
import com.ducktel.domain.repository.UserRepository;
import com.ducktel.dto.UserDTO;
import com.ducktel.exception.CustomException;
import com.ducktel.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

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
        UUID uuid = UUID.fromString("c90c9ef9-5d3c-49f5-9a04-752cc06f5234");
        userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password123");
        userDTO.setName("홍길동");
        userDTO.setEmail("test@example.com");
        userDTO.setPhoneNumber("01012345678");

        user = new User();
        user.setUserId(uuid);
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
        UUID uuid = UUID.fromString("c90c9ef9-5d3c-49f5-9a04-752cc06f5234");
        User user = User.builder()
                .userId(uuid)
                .username("testuser")
                .email("test@example.com")
                .phoneNumber("01012345678")
                .name("홍길동")
                .role("ROLE_USER")
                .build();

        when(userRepository.findById(uuid)).thenReturn(Optional.of(user));

        UserDTO result = userService.getProfile(uuid);

        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void getProfile_NotFound() {
        UUID uuid = UUID.fromString("c90c9ef9-5d3c-49f5-9a04-752cc06f5234");
        when(userRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getProfile(uuid))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void updateProfile_Success() {
        UUID uuid = UUID.fromString("c90c9ef9-5d3c-49f5-9a04-752cc06f5234");
        User user = new User();
        user.setUserId(uuid);
        user.setName("기존이름");

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("new@example.com");
        userDTO.setPhoneNumber("01011112222");

        when(userRepository.findById(uuid)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        UserDTO result = userService.updateProfile(uuid, userDTO);

        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getPhoneNumber()).isEqualTo("01011112222");
    }

    @Test
    void deleteProfile_Success() {
        UUID uuid = UUID.fromString("c90c9ef9-5d3c-49f5-9a04-752cc06f5234");
        when(userRepository.existsById(uuid)).thenReturn(true);

        String result = userService.deleteProfile(uuid);

        verify(userRepository).deleteById(uuid);
        assertThat(result).isEqualTo("삭제되었습니다.");
    }

    @Test
    void deleteProfile_NotFound() {
        UUID uuid = UUID.fromString("c90c9ef9-5d3c-49f5-9a04-752cc06f5234");
        when(userRepository.existsById(uuid)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteProfile(uuid))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void passWordReset_Success() {
        UUID uuid = UUID.fromString("c90c9ef9-5d3c-49f5-9a04-752cc06f5234");
        when(passwordEncoder.encode("newpass")).thenReturn("encodedPass");
        when(userRepository.updatePassword(uuid, "encodedPass")).thenReturn(1);

        String result = userService.passWordReset(uuid, "newpass");

        assertThat(result).isEqualTo("비밀번호가 변경 되었습니다.");
    }
}
