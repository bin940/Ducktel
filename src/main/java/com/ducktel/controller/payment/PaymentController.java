package com.ducktel.controller.payment;

import com.ducktel.dto.PaymentRequestDTO;
import com.ducktel.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody PaymentRequestDTO paymentRequestDTO) {

        return ResponseEntity.ok(paymentService.processPayment(paymentRequestDTO));

    }
}
