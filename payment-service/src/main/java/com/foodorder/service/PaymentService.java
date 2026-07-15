package com.foodorder.service;

import com.foodorder.dto.PaymentRequest;
import com.foodorder.dto.PaymentResponse;
import com.foodorder.entity.Payment;
import com.foodorder.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Processing payment for order ID: {}, amount: {}", request.getOrderId(), request.getAmount());

        // Simulate success/failure logic: Fail if amount equals 99.99
        String status = "COMPLETED";
        if (request.getAmount() != null && request.getAmount().compareTo(new BigDecimal("99.99")) == 0) {
            log.warn("Payment simulation trigger: failing payment for order ID: {}", request.getOrderId());
            status = "FAILED";
        }

        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .status(status)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        return mapToPaymentResponse(savedPayment);
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
