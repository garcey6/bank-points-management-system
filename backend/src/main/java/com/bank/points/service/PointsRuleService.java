package com.bank.points.service;

import com.bank.points.entity.PointsRule;
import com.bank.points.repository.PointsRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PointsRuleService {

    @Autowired
    private PointsRuleRepository pointsRuleRepository;

    public List<PointsRule> getAllRules() {
        return pointsRuleRepository.findByStatusOrderByCreateTimeDesc(1);
    }

    public List<PointsRule> getAllRulesForAdmin() {
        return pointsRuleRepository.findAll();
    }

    public PointsRule getRuleById(Long id) {
        return pointsRuleRepository.findById(id).orElse(null);
    }

    public PointsRule getActiveRule() {
        return pointsRuleRepository.findFirstByStatusOrderByCreateTimeDesc(1);
    }

    @Transactional
    public PointsRule createRule(PointsRule rule) {
        rule.setStatus(1);
        rule.setCreateTime(LocalDateTime.now());
        rule.setUpdateTime(LocalDateTime.now());
        return pointsRuleRepository.save(rule);
    }

    @Transactional
    public PointsRule updateRule(PointsRule rule) {
        rule.setUpdateTime(LocalDateTime.now());
        return pointsRuleRepository.save(rule);
    }

    @Transactional
    public void deleteRule(Long id) {
        pointsRuleRepository.deleteById(id);
    }
}
