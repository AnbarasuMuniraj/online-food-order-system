package com.foodorder.delegate;

import com.foodorder.dto.KitchenRequest;
import com.foodorder.entity.Order;
import com.foodorder.repository.OrderRepository;
import com.foodorder.service.KitchenService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("kitchenServiceTaskDelegate")
public class KitchenServiceTaskDelegate implements JavaDelegate {

    private final KitchenService kitchenService;
    private final OrderRepository orderRepository;

    public KitchenServiceTaskDelegate(KitchenService kitchenService, OrderRepository orderRepository) {
        this.kitchenService = kitchenService;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public void execute(DelegateExecution execution) throws Exception {
        Long orderId = (Long) execution.getVariable("orderId");
        String item = (String) execution.getVariable("item");

        KitchenRequest request = KitchenRequest.builder()
                .orderId(orderId)
                .items(item)
                .build();

        kitchenService.prepareFood(request);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        order.setStatus("FOOD_READY");
        orderRepository.save(order);
    }
}
