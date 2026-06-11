package com.bank.points.repository;

import com.bank.points.entity.PointsRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointsRecordRepository extends JpaRepository<PointsRecord, Long> {

    List<PointsRecord> findByUserIdOrderByCreateTimeDesc(Long userId);

    List<PointsRecord> findByUserIdAndTypeOrderByCreateTimeDesc(Long userId, String type);
}
