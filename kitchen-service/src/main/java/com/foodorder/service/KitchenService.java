package com.foodorder.service;

import com.foodorder.dto.KitchenRequest;
import com.foodorder.dto.KitchenResponse;
import com.foodorder.entity.KitchenTicket;
import com.foodorder.repository.KitchenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class KitchenService {

    private final KitchenRepository kitchenRepository;

    @Transactional
    public KitchenResponse prepareFood(KitchenRequest request) {
        log.info("Receiving order for food preparation. Order ID: {}, Items: {}", request.getOrderId(), request.getItems());

        // Simulate food preparation status: COMPLETED
        String status = "COMPLETED";

        KitchenTicket ticket = KitchenTicket.builder()
                .orderId(request.getOrderId())
                .items(request.getItems())
                .status(status)
                .build();

        KitchenTicket savedTicket = kitchenRepository.save(ticket);
        log.info("Kitchen ticket saved successfully. Ticket ID: {}, Status: {}", savedTicket.getId(), savedTicket.getStatus());

        return mapToKitchenResponse(savedTicket);
    }

    private KitchenResponse mapToKitchenResponse(KitchenTicket ticket) {
        return KitchenResponse.builder()
                .id(ticket.getId())
                .orderId(ticket.getOrderId())
                .items(ticket.getItems())
                .status(ticket.getStatus())
                .createdAt(ticket.getCreatedAt())
                .build();
    }
}
