package com.bank.points.service;

import com.bank.points.entity.BankActivity;
import com.bank.points.entity.Product;
import com.bank.points.entity.UserActivity;
import com.bank.points.entity.User;
import com.bank.points.entity.PointsAccount;
import com.bank.points.repository.BankActivityRepository;
import com.bank.points.repository.UserActivityRepository;
import com.bank.points.repository.ProductRepository;
import com.bank.points.repository.UserRepository;
import com.bank.points.repository.PointsAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BankActivityService {

    @Autowired
    private BankActivityRepository bankActivityRepository;

    @Autowired
    private UserActivityRepository userActivityRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointsAccountRepository pointsAccountRepository;

    @Autowired
    private PointsService pointsService;

    public List<BankActivity> getAllActivitiesForAdmin() {
        List<BankActivity> activities = bankActivityRepository.findAll();
        for (BankActivity activity : activities) {
            if (activity.getRewardProductId() != null) {
                Product product = productRepository.findById(activity.getRewardProductId()).orElse(null);
                if (product != null) {
                    activity.setRewardProductName(product.getName());
                }
            }
        }
        return activities;
    }

    public List<BankActivity> getActiveActivities() {
        LocalDateTime now = LocalDateTime.now();
        List<BankActivity> activities = bankActivityRepository.findByStartTimeBeforeAndEndTimeAfterAndStatus(now, now, 1);
        for (BankActivity activity : activities) {
            if (activity.getRewardProductId() != null) {
                Product product = productRepository.findById(activity.getRewardProductId()).orElse(null);
                if (product != null) {
                    activity.setRewardProductName(product.getName());
                }
            }
        }
        return activities;
    }

    public BankActivity getActivityById(Long id) {
        return bankActivityRepository.findById(id).orElse(null);
    }

    @Transactional
    public BankActivity createActivity(BankActivity activity) {
        String activityNo = "ACT" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        activity.setActivityNo(activityNo);
        activity.setStatus(1);
        activity.setCreateTime(LocalDateTime.now());
        activity.setUpdateTime(LocalDateTime.now());
        return bankActivityRepository.save(activity);
    }

    @Transactional
    public BankActivity updateActivity(BankActivity activity) {
        BankActivity existing = bankActivityRepository.findById(activity.getId()).orElse(null);
        if (existing == null) {
            throw new RuntimeException("活动不存在");
        }
        activity.setActivityNo(existing.getActivityNo());
        activity.setCreateTime(existing.getCreateTime());
        activity.setUpdateTime(LocalDateTime.now());
        return bankActivityRepository.save(activity);
    }

    @Transactional
    public void deleteActivity(Long id) {
        bankActivityRepository.deleteById(id);
    }

    public List<UserActivity> getUserActivities(Long userId) {
        List<UserActivity> userActivities = userActivityRepository.findByUserId(userId);
        for (UserActivity ua : userActivities) {
            BankActivity activity = bankActivityRepository.findById(ua.getActivityId()).orElse(null);
            if (activity != null) {
                ua.setActivityName(activity.getName());
            }
        }
        return userActivities;
    }

    public List<UserActivity> getActivityParticipants(Long activityId) {
        List<UserActivity> userActivities = userActivityRepository.findByActivityId(activityId);
        for (UserActivity ua : userActivities) {
            User user = userRepository.findById(ua.getUserId()).orElse(null);
            if (user != null) {
                ua.setUsername(user.getUsername());
                ua.setRealName(user.getRealName());
            }
            BankActivity activity = bankActivityRepository.findById(ua.getActivityId()).orElse(null);
            if (activity != null) {
                ua.setActivityName(activity.getName());
            }
        }
        return userActivities;
    }

    @Transactional
    public UserActivity participateActivity(Long userId, Long activityId, BigDecimal depositAmount, String rewardType) {
        BankActivity activity = bankActivityRepository.findById(activityId).orElse(null);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        if (activity.getStatus() != 1) {
            throw new RuntimeException("活动已结束");
        }

        UserActivity existing = userActivityRepository.findByUserIdAndActivityId(userId, activityId);
        if (existing != null) {
            throw new RuntimeException("您已参与过此活动");
        }

        PointsAccount account = pointsAccountRepository.findByUserId(userId);
        if (account == null) {
            account = new PointsAccount();
            account.setUserId(userId);
            account.setTotalPoints(0);
            account.setAvailablePoints(0);
            account.setFrozenPoints(0);
            account.setExpiredPoints(0);
            account.setTotalDepositAmount(0.0);
            account.setCreateTime(LocalDateTime.now());
            account.setUpdateTime(LocalDateTime.now());
            pointsAccountRepository.save(account);
        }

        String activityType = activity.getActivityType();
        if ("DEPOSIT_POINTS".equals(activityType) || "DEPOSIT_GIFT".equals(activityType)) {
            if (depositAmount == null || depositAmount.compareTo(activity.getMinDepositAmount()) < 0) {
                throw new RuntimeException("存款金额不满足活动要求");
            }
            account.setTotalDepositAmount(account.getTotalDepositAmount() + depositAmount.doubleValue());
            pointsAccountRepository.save(account);
        }

        if ("POINTS".equals(rewardType) && activity.getRewardPoints() != null) {
            pointsService.addPoints(userId, activity.getRewardPoints(), "INCOME", "活动奖励：" + activity.getName(), null, null);
        }

        UserActivity userActivity = new UserActivity();
        userActivity.setUserId(userId);
        userActivity.setActivityId(activityId);
        userActivity.setDepositAmount(depositAmount);
        userActivity.setRewardType(rewardType);
        userActivity.setStatus(1);
        userActivity.setCreateTime(LocalDateTime.now());
        userActivity.setUpdateTime(LocalDateTime.now());

        return userActivityRepository.save(userActivity);
    }

    @Transactional
    public void cancelParticipation(Long userId, Long activityId) {
        UserActivity userActivity = userActivityRepository.findByUserIdAndActivityId(userId, activityId);
        if (userActivity == null) {
            throw new RuntimeException("您未参与过此活动");
        }

        // 可以在这里添加额外的逻辑，比如退还积分等
        
        userActivityRepository.delete(userActivity);
    }
}
