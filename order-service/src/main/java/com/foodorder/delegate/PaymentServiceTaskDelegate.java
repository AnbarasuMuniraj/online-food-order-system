package com.foodorder.delegate;

import com.foodorder.dto.PaymentRequest;
import com.foodorder.dto.PaymentResponse;
import com.foodorder.entity.Order;
import com.foodorder.repository.OrderRepository;
import com.foodorder.service.PaymentService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component("paymentServiceTaskDelegate")
public class PaymentServiceTaskDelegate implements JavaDelegate {

    private final PaymentService paymentService;
    private final OrderRepository orderRepository;

    public PaymentServiceTaskDelegate(PaymentService paymentService, OrderRepository orderRepository) {
        this.paymentService = paymentService;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public void execute(DelegateExecution execution) throws Exception {
        Long orderId = (Long) execution.getVariable("orderId");
        BigDecimal amount = (BigDecimal) execution.getVariable("amount");

        PaymentRequest request = PaymentRequest.builder()
                .orderId(orderId)
                .amount(amount)
                .build();

        PaymentResponse response = paymentService.processPayment(request);

        boolean paymentSuccess = "COMPLETED".equals(response.getStatus());
        execution.setVariable("paymentSuccess", paymentSuccess);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (paymentSuccess) {
            order.setStatus("PAYMENT_COMPLETED");
        } else {
            order.setStatus("CANCELLED");
        }
        orderRepository.save(order);
    }
}
