package com.bank.points.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bank_activity")
public class BankActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activity_no", length = 50)
    private String activityNo;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "min_deposit_amount", precision = 10, scale = 2)
    private BigDecimal minDepositAmount;

    @Column(name = "reward_points")
    private Integer rewardPoints;

    @Column(name = "reward_product_id")
    private Long rewardProductId;

    @Column(name = "activity_type", length = 50, nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'DEPOSIT_POINTS'")
    private String activityType;

    @Column(name = "start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Integer status;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Transient
    private String rewardProductName;
}
