package com.bank.points.service;

import com.bank.points.entity.PointsActivity;
import com.bank.points.repository.PointsActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PointsActivityService {

    @Autowired
    private PointsActivityRepository pointsActivityRepository;

    public List<PointsActivity> getAllActivities() {
        return pointsActivityRepository.findByStatusOrderByCreateTimeDesc(1);
    }

    public PointsActivity getActivityById(Long id) {
        return pointsActivityRepository.findById(id).orElse(null);
    }

    public List<PointsActivity> getActiveActivities() {
        return pointsActivityRepository.findActiveActivities(LocalDateTime.now(), 1);
    }

    @Transactional
    public PointsActivity createActivity(PointsActivity activity) {
        activity.setStatus(1);
        activity.setCreateTime(LocalDateTime.now());
        activity.setUpdateTime(LocalDateTime.now());
        return pointsActivityRepository.save(activity);
    }

    @Transactional
    public PointsActivity updateActivity(PointsActivity activity) {
        activity.setUpdateTime(LocalDateTime.now());
        return pointsActivityRepository.save(activity);
    }

    @Transactional
    public void deleteActivity(Long id) {
        pointsActivityRepository.deleteById(id);
    }
}
