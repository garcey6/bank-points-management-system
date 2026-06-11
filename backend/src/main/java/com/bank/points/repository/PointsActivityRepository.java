package com.bank.points.repository;

import com.bank.points.entity.PointsActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PointsActivityRepository extends JpaRepository<PointsActivity, Long> {

    List<PointsActivity> findByStatusOrderByCreateTimeDesc(Integer status);

    @Query("SELECT pa FROM PointsActivity pa WHERE pa.status = :status AND pa.startTime <= :currentTime AND pa.endTime >= :currentTime")
    List<PointsActivity> findActiveActivities(@Param("currentTime") LocalDateTime currentTime, @Param("status") Integer status);

    PointsActivity findFirstByStatusOrderByCreateTimeDesc(Integer status);
}
