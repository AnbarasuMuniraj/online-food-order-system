package com.foodorder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KitchenResponse {
    private Long id;
    private Long orderId;
    private String items;
    private String status;
    private LocalDateTime createdAt;
}
