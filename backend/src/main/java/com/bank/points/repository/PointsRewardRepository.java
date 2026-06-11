package com.bank.points.repository;

import com.bank.points.entity.PointsReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PointsRewardRepository extends JpaRepository<PointsReward, Long> {
    List<PointsReward> findByUserId(Long userId);
    PointsReward findByRewardNo(String rewardNo);
}
