package com.ducktel.controller.auth;

import com.ducktel.config.security.service.AuthService;
import com.ducktel.exception.CustomException;
import com.ducktel.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AuthController.class)
@Import({GlobalExceptionHandler.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;



    @Test
    void refresh_Success() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("refreshToken", "validRefreshToken");

        Map<String, String> mockResponse = new HashMap<>();
        mockResponse.put("accessToken", "newAccessToken");
        when(authService.refresh("validRefreshToken")).thenReturn(mockResponse);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("토큰 갱신 성공"))
                .andExpect(jsonPath("$.data.accessToken").value("newAccessToken"));
    }

    @Test
    void refresh_WithInvalidToken() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("refreshToken", "invalidRefreshToken");

        when(authService.refresh("invalidRefreshToken"))
                .thenThrow(new CustomException("INVALID_REFRESH_TOKEN", "유효하지 않은 리프레시 토큰"));

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("INVALID_REFRESH_TOKEN"))
                .andExpect(jsonPath("$.message").value("유효하지 않은 리프레시 토큰"));


    }


    @Test
    void logout_WithLocalLogin() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("refreshToken", "validRefreshToken");
        request.put("loginType", "LOCAL");


        doNothing().when(authService).logout("validRefreshToken");

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("로그아웃 성공"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void logout_WithSocialLogin() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("refreshToken", "validRefreshToken");
        request.put("loginType", "GOOGLE");

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("소셜 로그아웃 URL 반환"))
                .andExpect(jsonPath("$.data").value("https://accounts.google.com/logout"));
    }

    @Test
    void logout_WithInvalidRequest() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("loginType", "LOCAL");

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("잘못된 요청"));
    }
}