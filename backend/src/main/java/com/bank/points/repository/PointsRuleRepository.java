package com.bank.points.repository;

import com.bank.points.entity.PointsRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointsRuleRepository extends JpaRepository<PointsRule, Long> {

    List<PointsRule> findByStatusOrderByCreateTimeDesc(Integer status);

    PointsRule findFirstByStatusOrderByCreateTimeDesc(Integer status);
}
