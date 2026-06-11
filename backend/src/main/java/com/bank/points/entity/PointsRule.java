package com.bank.points.entity;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "points_rule")
public class PointsRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Integer pointsPerYuan;

    @Column(nullable = false)
    private Integer minAmount;

    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal multiplier;

    @Column(nullable = false)
    private Integer expiryDays;

    @Column(nullable = false)
    private Integer status;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
