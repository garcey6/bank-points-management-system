package com.bank.points.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "points_account")
public class PointsAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer totalPoints;

    @Column(nullable = false)
    private Integer availablePoints;

    @Column(nullable = false)
    private Integer frozenPoints;

    @Column(name = "expired_points")
    private Integer expiredPoints;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "total_deposit_amount")
    private Double totalDepositAmount;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
