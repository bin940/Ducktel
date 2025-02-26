package com.ducktel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDTO {
    private Long userId;
    private Long accommodationId;
    private Long roomId;
    private String name;
    private String phoneNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private boolean paymentComplete;
    private BigDecimal amount;
    private String paymentMethod;
}
