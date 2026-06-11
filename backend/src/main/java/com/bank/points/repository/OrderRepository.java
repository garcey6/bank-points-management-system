package com.bank.points.repository;

import com.bank.points.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserIdOrderByCreateTimeDesc(Long userId);

    Order findByOrderNo(String orderNo);

    List<Order> findByStatus(String status);
}
