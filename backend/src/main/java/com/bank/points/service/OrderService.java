package com.bank.points.service;

import com.bank.points.dto.ExchangeRequest;
import com.bank.points.entity.MemberLevelConfig;
import com.bank.points.entity.Order;
import com.bank.points.entity.Product;
import com.bank.points.entity.User;
import com.bank.points.repository.MemberLevelConfigRepository;
import com.bank.points.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PointsService pointsService;

    @Autowired
    private MemberLevelConfigRepository memberLevelConfigRepository;

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    public Order getOrderByOrderNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo);
    }

    @Transactional
    public Order createOrder(Long userId, ExchangeRequest request) {
        Product product = productService.getProductById(request.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (product.getStatus() != 1) {
            throw new RuntimeException("商品已下架");
        }
        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("库存不足");
        }

        Integer originalPoints = product.getPoints() * request.getQuantity();
        Integer discountedPoints = productService.getDiscountedPoints(request.getProductId(), getMemberLevel(userId));

        String orderNo = generateOrderNo();

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setProductId(request.getProductId());
        order.setPoints(discountedPoints);
        order.setQuantity(request.getQuantity());
        order.setStatus("PENDING");
        order.setReceiverName(request.getContactName());
        order.setReceiverPhone(request.getContactPhone());
        order.setReceiverAddress(request.getShippingAddress());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        order = orderRepository.save(order);

        pointsService.deductPoints(userId, discountedPoints, "EXCHANGE", "兑换商品：" + product.getName() + (discountedPoints < originalPoints ? "（会员折扣）" : ""), order.getId());

        productService.updateStock(request.getProductId(), request.getQuantity());

        order.setStatus("COMPLETED");
        order.setUpdateTime(LocalDateTime.now());
        return orderRepository.save(order);
    }

    private Integer getMemberLevel(Long userId) {
        User user = pointsService.getUserById(userId);
        return user != null ? user.getMemberLevel() : 1;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public Order updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        order.setStatus(status);
        if ("SHIPPED".equals(status)) {
            order.setShipTime(LocalDateTime.now());
        }
        order.setUpdateTime(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Transactional
    public Order shipOrder(Long id, String logisticsCompany, String trackingNumber) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不允许发货");
        }
        order.setStatus("SHIPPED");
        order.setLogisticsCompany(logisticsCompany);
        order.setTrackingNumber(trackingNumber);
        order.setShipTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        return orderRepository.save(order);
    }

    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
