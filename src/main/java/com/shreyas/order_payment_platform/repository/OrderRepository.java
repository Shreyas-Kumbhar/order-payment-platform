package com.shreyas.order_payment_platform.repository;

import com.shreyas.order_payment_platform.entity.Order;
import com.shreyas.order_payment_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Integer> {
    List<Order> findByUser(User user);
}
