package com.bank.points.service;

import com.bank.points.entity.MemberBenefit;
import com.bank.points.repository.MemberBenefitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MemberBenefitService {

    @Autowired
    private MemberBenefitRepository memberBenefitRepository;

    public List<MemberBenefit> getBenefitsByMemberLevel(Integer memberLevel) {
        return memberBenefitRepository.findByMemberLevelAndStatusOrderByCreateTimeDesc(memberLevel, 1);
    }

    public List<MemberBenefit> getAllBenefits() {
        return memberBenefitRepository.findByStatusOrderByCreateTimeDesc(1);
    }

    public List<MemberBenefit> getAllBenefitsForAdmin() {
        return memberBenefitRepository.findAll();
    }

    public MemberBenefit getBenefitById(Long id) {
        return memberBenefitRepository.findById(id).orElse(null);
    }

    @Transactional
    public MemberBenefit createBenefit(MemberBenefit benefit) {
        benefit.setStatus(1);
        benefit.setCreateTime(LocalDateTime.now());
        benefit.setUpdateTime(LocalDateTime.now());
        return memberBenefitRepository.save(benefit);
    }

    @Transactional
    public MemberBenefit updateBenefit(MemberBenefit benefit) {
        benefit.setUpdateTime(LocalDateTime.now());
        return memberBenefitRepository.save(benefit);
    }

    @Transactional
    public void deleteBenefit(Long id) {
        memberBenefitRepository.deleteById(id);
    }
}
