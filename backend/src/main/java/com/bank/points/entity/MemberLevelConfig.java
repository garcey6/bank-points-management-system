package com.bank.points.entity;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "member_level_config")
public class MemberLevelConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer level;

    @Column(nullable = false, length = 50)
    private String levelName;

    @Column(nullable = false)
    private Integer minPoints;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal minDepositAmount;

    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal discountRate;

    @Column(nullable = false)
    private Integer status;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
