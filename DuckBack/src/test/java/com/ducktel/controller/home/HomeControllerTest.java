package com.ducktel.controller.home;

import com.ducktel.dto.HomeResponseDTO;
import com.ducktel.exception.CustomException;
import com.ducktel.exception.GlobalExceptionHandler;
import com.ducktel.service.HomeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(HomeController.class)
@Import(GlobalExceptionHandler.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HomeService homeService;


    @Test
    void getHomeData_Success() throws Exception {
        HomeResponseDTO mockResponse = new HomeResponseDTO();
        when(homeService.getHomeData()).thenReturn(mockResponse);

        mockMvc.perform(get("/api/home")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.errorCode").doesNotExist())
                .andExpect(jsonPath("$.message").value("Home Data 조회 성공"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void getSubHomeData_Success() throws Exception {
        String category = "호텔";
        HomeResponseDTO mockResponse = new HomeResponseDTO();
        when(homeService.getSubHomeData(category)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/sub-home/{category}", category)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.errorCode").doesNotExist())
                .andExpect(jsonPath("$.message").value("Sub-Home Data 조회 성공"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void getLocationHomeData_Success() throws Exception {
        String category = "리조트";
        String location = "제주";
        HomeResponseDTO mockResponse = new HomeResponseDTO();
        when(homeService.getLocationHomeData(category, location)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/sub-home/{category}/{location}", category, location)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.errorCode").doesNotExist())
                .andExpect(jsonPath("$.message").value("Location Home Data 조회 성공"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void getSubHomeData_WithInvalidCategory() throws Exception {
        String category = "unknown";

        when(homeService.getSubHomeData(category))
                .thenThrow(new CustomException(400,"INVALID_CATEGORY", "존재하지 않는 카테고리"));

        mockMvc.perform(get("/api/sub-home/{category}", category))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("INVALID_CATEGORY"))
                .andExpect(jsonPath("$.message").value("존재하지 않는 카테고리"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getLocationHomeData_WithInvalidLocation() throws Exception {
        String category = "리조트";
        String location = " ";

        when(homeService.getLocationHomeData(category, location))
                .thenThrow(new CustomException(400,"INVALID_LOCATION", "위치 값이 유효하지 않습니다"));

        mockMvc.perform(get("/api/sub-home/{category}/{location}", category, location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("INVALID_LOCATION"))
                .andExpect(jsonPath("$.message").value("위치 값이 유효하지 않습니다"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
