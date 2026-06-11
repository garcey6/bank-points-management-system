package com.bank.points.service;

import com.bank.points.entity.PointsReward;
import com.bank.points.entity.User;
import com.bank.points.repository.PointsRewardRepository;
import com.bank.points.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PointsRewardService {

    @Autowired
    private PointsRewardRepository pointsRewardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointsService pointsService;

    public List<PointsReward> getAllRewardsForAdmin() {
        List<PointsReward> rewards = pointsRewardRepository.findAll();
        for (PointsReward reward : rewards) {
            User user = userRepository.findById(reward.getUserId()).orElse(null);
            if (user != null) {
                reward.setUsername(user.getUsername());
                reward.setRealName(user.getRealName());
                reward.setPhone(user.getPhone());
            }
        }
        return rewards;
    }

    public List<PointsReward> getRewardsByUserId(Long userId) {
        return pointsRewardRepository.findByUserId(userId);
    }

    @Transactional
    public PointsReward addReward(PointsReward reward) {
        User user = userRepository.findById(reward.getUserId()).orElse(null);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        String rewardNo = "RWD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        reward.setRewardNo(rewardNo);
        reward.setStatus(1);
        reward.setCreateTime(LocalDateTime.now());
        reward.setUpdateTime(LocalDateTime.now());

        PointsReward savedReward = pointsRewardRepository.save(reward);

        pointsService.addPoints(reward.getUserId(), reward.getPoints(), "INCOME", reward.getRewardReason(), reward.getId(), null);

        return savedReward;
    }

    @Transactional
    public PointsReward updateReward(Long id, Integer points) {
        PointsReward reward = pointsRewardRepository.findById(id).orElse(null);
        if (reward == null) {
            throw new RuntimeException("奖励记录不存在");
        }

        int diff = points - reward.getPoints();
        reward.setPoints(points);
        reward.setUpdateTime(LocalDateTime.now());
        PointsReward updatedReward = pointsRewardRepository.save(reward);

        if (diff > 0) {
            pointsService.addPoints(reward.getUserId(), diff, "INCOME", "调整积分奖励", reward.getId(), null);
        } else if (diff < 0) {
            pointsService.deductPoints(reward.getUserId(), -diff, "EXPENSE", "调整积分奖励", reward.getId());
        }

        return updatedReward;
    }

    @Transactional
    public void deleteReward(Long id) {
        PointsReward reward = pointsRewardRepository.findById(id).orElse(null);
        if (reward != null) {
            pointsService.deductPoints(reward.getUserId(), reward.getPoints(), "EXPENSE", "删除奖励", reward.getId());
        }
        pointsRewardRepository.deleteById(id);
    }
}
