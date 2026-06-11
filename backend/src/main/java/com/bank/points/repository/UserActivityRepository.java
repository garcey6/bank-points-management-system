package com.bank.points.repository;

import com.bank.points.entity.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    List<UserActivity> findByUserId(Long userId);
    List<UserActivity> findByActivityId(Long activityId);
    UserActivity findByUserIdAndActivityId(Long userId, Long activityId);
}
