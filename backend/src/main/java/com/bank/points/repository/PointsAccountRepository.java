package com.bank.points.repository;

import com.bank.points.entity.PointsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointsAccountRepository extends JpaRepository<PointsAccount, Long> {

    PointsAccount findByUserId(Long userId);
}
