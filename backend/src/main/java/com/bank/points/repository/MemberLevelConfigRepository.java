package com.bank.points.repository;

import com.bank.points.entity.MemberLevelConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberLevelConfigRepository extends JpaRepository<MemberLevelConfig, Long> {

    List<MemberLevelConfig> findByStatusOrderByLevelAsc(Integer status);

    MemberLevelConfig findByLevel(Integer level);
}
