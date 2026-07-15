package com.foodorder.service;

import com.foodorder.dto.DeliveryRequest;
import com.foodorder.dto.DeliveryResponse;
import com.foodorder.entity.Delivery;
import com.foodorder.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    @Transactional
    public DeliveryResponse deliverOrder(DeliveryRequest request) {
        log.info("Receiving delivery request for order ID: {}, address: {}", request.getOrderId(), request.getDeliveryAddress());

        // Simulate driver assignment
        String courierName = "Swift Courier - Driver Dave";
        String status = "DELIVERED";

        Delivery delivery = Delivery.builder()
                .orderId(request.getOrderId())
                .courierName(courierName)
                .deliveryAddress(request.getDeliveryAddress())
                .status(status)
                .build();

        Delivery savedDelivery = deliveryRepository.save(delivery);
        log.info("Delivery record saved successfully. Delivery ID: {}, Status: {}", savedDelivery.getId(), savedDelivery.getStatus());

        return mapToDeliveryResponse(savedDelivery);
    }

    private DeliveryResponse mapToDeliveryResponse(Delivery delivery) {
        return DeliveryResponse.builder()
                .id(delivery.getId())
                .orderId(delivery.getOrderId())
                .courierName(delivery.getCourierName())
                .deliveryAddress(delivery.getDeliveryAddress())
                .status(delivery.getStatus())
                .createdAt(delivery.getCreatedAt())
                .build();
    }
}
