package com.ducktel.controller.place;

import com.ducktel.dto.PlacesDTO;
import com.ducktel.exception.CustomException;
import com.ducktel.exception.GlobalExceptionHandler;
import com.ducktel.service.PlacesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PlacesController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class PlacesControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlacesService placesService;

    @Test
    void getPlaces_Success() throws Exception {
        Long accommodationId = 1L;
        LocalDate checkInDate = LocalDate.now().plusDays(1);
        LocalDate checkOutDate = LocalDate.now().plusDays(2);
        PlacesDTO mockResponse = new PlacesDTO();
        when(placesService.getPlaces(accommodationId, checkInDate, checkOutDate)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/places/{accommodationId}/{checkInData}/{checkOutData}",
                        accommodationId, checkInDate, checkOutDate)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.errorCode").doesNotExist())
                .andExpect(jsonPath("$.message").value("Place 조회 성공"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void getPlaces_WithInvalidDateRange() throws Exception {
        Long accommodationId = 1L;
        LocalDate checkInDate = LocalDate.now().plusDays(2);
        LocalDate checkOutDate = LocalDate.now().plusDays(1);

        when(placesService.getPlaces(accommodationId, checkInDate, checkOutDate))
                .thenThrow(new CustomException("INVALID_DATE_RANGE", "체크아웃 날짜는 체크인 날짜보다 늦어야 합니다"));

        mockMvc.perform(get("/api/places/{accommodationId}/{checkInData}/{checkOutData}",
                        accommodationId, checkInDate, checkOutDate)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("INVALID_DATE_RANGE"))
                .andExpect(jsonPath("$.message").value("체크아웃 날짜는 체크인 날짜보다 늦어야 합니다"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}