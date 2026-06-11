package com.bank.points.repository;

import com.bank.points.entity.MemberBenefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberBenefitRepository extends JpaRepository<MemberBenefit, Long> {

    List<MemberBenefit> findByMemberLevelAndStatusOrderByCreateTimeDesc(Integer memberLevel, Integer status);

    List<MemberBenefit> findByStatusOrderByCreateTimeDesc(Integer status);
}
