package com.ducktel.controller.payment;

import com.ducktel.dto.PaymentRequestDTO;
import com.ducktel.dto.PaymentResponseDTO;
import com.ducktel.exception.GlobalExceptionHandler;
import com.ducktel.service.JwtService;
import com.ducktel.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(com.ducktel.controller.payment.PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createPayment_success() throws Exception {
        UUID uuid = UUID.fromString("c90c9ef9-5d3c-49f5-9a04-752cc06f5234");
        String token = "Bearer test.jwt.token";
        UUID userId = uuid;

        PaymentRequestDTO requestDTO = new PaymentRequestDTO();
        requestDTO.setAccommodationId(1L);
        requestDTO.setRoomId(101L);
        requestDTO.setName("홍길동");
        requestDTO.setPhoneNumber("01012345678");
        requestDTO.setCheckInDate(LocalDate.now().plusDays(1));
        requestDTO.setCheckOutDate(LocalDate.now().plusDays(2));
        requestDTO.setPaymentComplete(true);
        requestDTO.setAmount(new BigDecimal("150000"));
        requestDTO.setPaymentMethod("CARD");

        PaymentResponseDTO responseDTO = new PaymentResponseDTO();
        responseDTO.setMessage("결제 완료");
        responseDTO.setPgTransactionId("PG123456789");

        when(jwtService.getTokenFromHeader(token)).thenReturn("test.jwt.token");
        when(jwtService.getUserIdFromToken("test.jwt.token")).thenReturn(userId);
        when(paymentService.processPayment(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/payment/create")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.errorCode").doesNotExist())
                .andExpect(jsonPath("$.message").value("예약 성공"))
                .andExpect(jsonPath("$.data.pgTransactionId").value("PG123456789"));
    }

    @Test
    void createPayment_validationFail_dueToEmptyName() throws Exception {
        PaymentRequestDTO requestDTO = new PaymentRequestDTO();
        requestDTO.setAccommodationId(1L);
        requestDTO.setRoomId(101L);
        requestDTO.setName("a");
        requestDTO.setPhoneNumber("01012345678");
        requestDTO.setCheckInDate(LocalDate.now().plusDays(1));
        requestDTO.setCheckOutDate(LocalDate.now().plusDays(2));
        requestDTO.setPaymentComplete(true);
        requestDTO.setAmount(new BigDecimal("150000"));
        requestDTO.setPaymentMethod("CARD");

        mockMvc.perform(post("/api/payment/create")
                        .header("Authorization", "Bearer test.jwt.token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("이름은 2자 이상 8자 이아로 입력해주세요."))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void createPayment_validationFail_dueToInvalidPhoneNumber() throws Exception {
        PaymentRequestDTO requestDTO = new PaymentRequestDTO();
        requestDTO.setAccommodationId(1L);
        requestDTO.setRoomId(101L);
        requestDTO.setName("홍길동");
        requestDTO.setPhoneNumber("0101234");
        requestDTO.setCheckInDate(LocalDate.now().plusDays(1));
        requestDTO.setCheckOutDate(LocalDate.now().plusDays(2));
        requestDTO.setPaymentComplete(true);
        requestDTO.setAmount(new BigDecimal("150000"));
        requestDTO.setPaymentMethod("CARD");

        mockMvc.perform(post("/api/payment/create")
                        .header("Authorization", "Bearer test.jwt.token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("전화번호는 10자 이상 11자 이하로 입력해주세요."))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void createPayment_missingAuthorizationHeader() throws Exception {
        PaymentRequestDTO requestDTO = new PaymentRequestDTO();
        requestDTO.setAccommodationId(1L);
        requestDTO.setRoomId(101L);
        requestDTO.setName("홍길동");
        requestDTO.setPhoneNumber("01012345678");
        requestDTO.setCheckInDate(LocalDate.now().plusDays(1));
        requestDTO.setCheckOutDate(LocalDate.now().plusDays(2));
        requestDTO.setPaymentComplete(true);
        requestDTO.setAmount(new BigDecimal("150000"));
        requestDTO.setPaymentMethod("CARD");

        mockMvc.perform(post("/api/payment/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("AUTH_MISSING"))
                .andExpect(jsonPath("$.message").value("Authorization 헤더가 누락되었거나 유효하지 않습니다."))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
