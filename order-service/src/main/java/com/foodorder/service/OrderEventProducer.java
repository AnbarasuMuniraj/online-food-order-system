package com.foodorder.service;

import com.foodorder.dto.OrderCreatedEvent;
import jakarta.jms.Queue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final JmsTemplate jmsTemplate;
    private final Queue orderCreatedQueue;

    public void sendOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Sending OrderCreatedEvent to queue: {} for order ID: {}", orderCreatedQueue, event.getOrderId());
        jmsTemplate.convertAndSend(orderCreatedQueue, event);
    }
}
