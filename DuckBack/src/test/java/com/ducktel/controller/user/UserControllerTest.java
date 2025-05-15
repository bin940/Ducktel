package com.ducktel.controller.user;

import com.ducktel.dto.BookingDetailDTO;
import com.ducktel.dto.UserDTO;
import com.ducktel.exception.GlobalExceptionHandler;
import com.ducktel.service.BookingService;
import com.ducktel.service.JwtService;
import com.ducktel.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerUser_Success() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .username("testuser")
                .password("password123")
                .phoneNumber("01012345678")
                .email("test@example.com")
                .name("홍길동")
                .build();
        when(userService.registerUser(any(UserDTO.class))).thenReturn("testuser");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.errorCode").doesNotExist())
                .andExpect(jsonPath("$.message").value("회원가입 성공"))
                .andExpect(jsonPath("$.data").value("testuser"));
    }

    @Test
    void registerUser_ValidationFail() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .username("test")
                .password("password123")
                .phoneNumber("01012345678")
                .email("test@example.com")
                .name("홍길동")
                .build();

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("아이디는 6자 이상 16자 이하로 입력해주세요."))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getProfile_Success() throws Exception {
        UUID uuid = UUID.fromString("c90c9ef9-5d3c-49f5-9a04-752cc06f5234");
        UserDTO userDTO = new UserDTO("testuser", "test@example.com", "01012345678", "홍길동");
        when(jwtService.getTokenFromHeader("Bearer test-token")).thenReturn("test-token");
        when(jwtService.getUserIdFromToken("test-token")).thenReturn(uuid);
        when(userService.getProfile(uuid)).thenReturn(userDTO);

        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.errorCode").doesNotExist())
                .andExpect(jsonPath("$.message").value("유저정보 조회 성공"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void getProfile_AuthMissing() throws Exception {
        when(jwtService.getTokenFromHeader(null)).thenReturn(null);
        when(jwtService.getUserIdFromToken(null)).thenReturn(null);

        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("AUTH_MISSING"))
                .andExpect(jsonPath("$.message").value("Authorization 헤더가 누락되었거나 유효하지 않습니다"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void updateProfile_Success() throws Exception {
        UUID uuid = UUID.fromString("c90c9ef9-5d3c-49f5-9a04-752cc06f5234");
        UserDTO userDTO = UserDTO.builder()
                .username("testuser")
                .phoneNumber("01098765432")
                .email("new@example.com")
                .name("김길동")
                .build();
        when(jwtService.getTokenFromHeader("Bearer test-token")).thenReturn("test-token");
        when(jwtService.getUserIdFromToken("test-token")).thenReturn(uuid);
        when(userService.updateProfile(uuid, userDTO)).thenReturn(userDTO);

        mockMvc.perform(put("/api/users/profile")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.errorCode").doesNotExist())
                .andExpect(jsonPath("$.message").value("유저정보 변경 성공"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void deleteProfile_Success() throws Exception {
        UUID uuid = UUID.fromString("c90c9ef9-5d3c-49f5-9a04-752cc06f5234");
        when(userService.deleteProfile(uuid)).thenReturn("회원 탈퇴 성공");

        mockMvc.perform(delete("/api/users/profile/{userId}", uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("회원 탈퇴 성공"));
    }

    @Test
    void getBookingDetail_Success() throws Exception {
        UUID uuid = UUID.fromString("c90c9ef9-5d3c-49f5-9a04-752cc06f5234");
        BookingDetailDTO booking = BookingDetailDTO.builder()
                .bookingId(1L)
                .createdAt(LocalDateTime.now())
                .checkIn(LocalDate.now().plusDays(1))
                .checkOut(LocalDate.now().plusDays(2))
                .paymentCompleted(true)
                .build();
        when(jwtService.getTokenFromHeader("Bearer test-token")).thenReturn("test-token");
        when(jwtService.getUserIdFromToken("test-token")).thenReturn(uuid);
        when(bookingService.getBookingDetail(uuid)).thenReturn(Collections.singletonList(booking));

        mockMvc.perform(get("/api/users/book")
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.errorCode").doesNotExist())
                .andExpect(jsonPath("$.message").value("예약 조회 성공"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void updateBooking_Success() throws Exception {
        BookingDetailDTO bookingDTO = BookingDetailDTO.builder()
                .bookingId(1L)
                .checkIn(LocalDate.now().plusDays(3))
                .checkOut(LocalDate.now().plusDays(4))
                .paymentCompleted(true)
                .build();
        when(bookingService.updateBooking(bookingDTO)).thenReturn(bookingDTO);

        mockMvc.perform(put("/api/users/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.errorCode").doesNotExist())
                .andExpect(jsonPath("$.message").value("예약 변경 성공"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void deleteBooking_Success() throws Exception {
        UUID uuid = UUID.fromString("c90c9ef9-5d3c-49f5-9a04-752cc06f5234");
        Long bookingId = 1L;
        BookingDetailDTO booking = BookingDetailDTO.builder()
                .bookingId(bookingId)
                .checkIn(LocalDate.now().plusDays(1))
                .checkOut(LocalDate.now().plusDays(2))
                .build();
        when(jwtService.getTokenFromHeader("Bearer test-token")).thenReturn("test-token");
        when(jwtService.getUserIdFromToken("test-token")).thenReturn(uuid);
        when(bookingService.deleteBooking(uuid, bookingId)).thenReturn(Collections.singletonList(booking));

        mockMvc.perform(delete("/api/users/book/{bookingId}", bookingId)
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.errorCode").doesNotExist())
                .andExpect(jsonPath("$.message").value("예약 취소 성공"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void passWordReset_Success() throws Exception {
        UUID uuid = UUID.fromString("c90c9ef9-5d3c-49f5-9a04-752cc06f5234");

        String json = """
    {
      "password": "newpassword123"
    }
    """;

        when(jwtService.getTokenFromHeader("Bearer test-token")).thenReturn("test-token");
        when(jwtService.getUserIdFromToken("test-token")).thenReturn(uuid);
        when(userService.passWordReset(uuid, "newpassword123")).thenReturn("비밀번호 변경 성공");

        mockMvc.perform(post("/api/users/password-reset")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("비밀번호 변경 성공"));
    }
}