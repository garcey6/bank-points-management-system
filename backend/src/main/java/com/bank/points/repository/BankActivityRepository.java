package com.bank.points.repository;

import com.bank.points.entity.BankActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BankActivityRepository extends JpaRepository<BankActivity, Long> {
    List<BankActivity> findByStatus(Integer status);
    List<BankActivity> findByStartTimeBeforeAndEndTimeAfterAndStatus(LocalDateTime time1, LocalDateTime time2, Integer status);
}
