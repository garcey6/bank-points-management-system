package com.bank.points.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "points_reward")
public class PointsReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reward_no", length = 50)
    private String rewardNo;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "reward_reason", length = 200)
    private String rewardReason;

    @Column(nullable = false)
    private Integer points;

    @Column(nullable = false)
    private Integer status;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Transient
    private String username;

    @Transient
    private String realName;

    @Transient
    private String phone;
}
