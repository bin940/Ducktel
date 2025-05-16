package com.ducktel.dto;

import com.ducktel.validation.CreateUser;
import com.ducktel.validation.UpdateUser;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDTO {
    private UUID userId;
    private Long accommodationId;
    private Long roomId;
    @NotBlank(message = "이름을 입력해주세요.")
    @Size(min =2, max =8, message= "이름은 2자 이상 8자 이아로 입력해주세요.")
    private String name;
    @NotBlank(message = "전화번호를 입력해주세요.")
    @Size(min = 10, max = 11, message = "전화번호는 10자 이상 11자 이하로 입력해주세요.")
    private String phoneNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private boolean paymentComplete;
    private BigDecimal amount;
    private String paymentMethod;
}
