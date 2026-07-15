package com.foodorder.repository;

import com.foodorder.entity.KitchenTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KitchenRepository extends JpaRepository<KitchenTicket, Long> {
}
