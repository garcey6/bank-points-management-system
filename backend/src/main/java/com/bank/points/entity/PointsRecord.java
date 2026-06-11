package com.bank.points.entity;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "points_record")
public class PointsRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer points;

    @Column(nullable = false, length = 20)
    private String type;

    @Column(length = 500)
    private String description;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "is_expired")
    private Boolean isExpired;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;
}
