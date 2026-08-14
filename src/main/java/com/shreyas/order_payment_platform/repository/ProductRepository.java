package com.shreyas.order_payment_platform.repository;

import com.shreyas.order_payment_platform.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  ProductRepository extends JpaRepository<Product,Integer> {
}
