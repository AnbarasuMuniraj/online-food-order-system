package com.foodorder.delegate;

import com.foodorder.dto.DeliveryRequest;
import com.foodorder.entity.Order;
import com.foodorder.repository.OrderRepository;
import com.foodorder.service.DeliveryService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("deliveryServiceTaskDelegate")
public class DeliveryServiceTaskDelegate implements JavaDelegate {

    private final DeliveryService deliveryService;
    private final OrderRepository orderRepository;

    public DeliveryServiceTaskDelegate(DeliveryService deliveryService, OrderRepository orderRepository) {
        this.deliveryService = deliveryService;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public void execute(DelegateExecution execution) throws Exception {
        Long orderId = (Long) execution.getVariable("orderId");

        // Simulate delivery address
        String deliveryAddress = "Simulated Customer Home Address";

        DeliveryRequest request = DeliveryRequest.builder()
                .orderId(orderId)
                .deliveryAddress(deliveryAddress)
                .build();

        deliveryService.deliverOrder(request);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        order.setStatus("DELIVERED");
        orderRepository.save(order);
    }
}
