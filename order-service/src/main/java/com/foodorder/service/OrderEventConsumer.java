package com.foodorder.service;

import com.foodorder.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final RuntimeService runtimeService;

    @JmsListener(destination = "order.created")
    public void receiveOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent from ActiveMQ for order ID: {}", event.getOrderId());

        Map<String, Object> variables = new HashMap<>();
        variables.put("orderId", event.getOrderId());
        variables.put("customerName", event.getCustomerName());
        variables.put("item", event.getItem());
        variables.put("amount", event.getAmount());

        runtimeService.startProcessInstanceByKey("food-order-process", String.valueOf(event.getOrderId()), variables);
        log.info("Successfully started Camunda process instance for order ID: {}", event.getOrderId());
    }
}
