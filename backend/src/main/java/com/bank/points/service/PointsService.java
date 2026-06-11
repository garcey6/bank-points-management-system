package com.bank.points.service;

import com.bank.points.entity.MemberLevelConfig;
import com.bank.points.entity.PointsAccount;
import com.bank.points.entity.PointsRecord;
import com.bank.points.repository.MemberLevelConfigRepository;
import com.bank.points.repository.PointsAccountRepository;
import com.bank.points.repository.PointsRecordRepository;
import com.bank.points.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PointsService {

    @Autowired
    private PointsAccountRepository pointsAccountRepository;

    @Autowired
    private PointsRecordRepository pointsRecordRepository;

    @Autowired
    private MemberLevelConfigRepository memberLevelConfigRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.bank.points.service.UserService userService;

    public PointsAccount getPointsAccount(Long userId) {
        return pointsAccountRepository.findByUserId(userId);
    }

    @Transactional
    public PointsAccount createPointsAccount(Long userId) {
        PointsAccount account = new PointsAccount();
        account.setUserId(userId);
        account.setTotalPoints(0);
        account.setAvailablePoints(0);
        account.setFrozenPoints(0);
        account.setExpiredPoints(0);
        account.setExpiryDate(LocalDateTime.now().plusYears(1)); // 设置默认过期时间为1年后
        account.setTotalDepositAmount(0.0);
        account.setCreateTime(LocalDateTime.now());
        account.setUpdateTime(LocalDateTime.now());
        return pointsAccountRepository.save(account);
    }

    @Transactional
    public void addPoints(Long userId, Integer points, String type, String description, Long orderId, LocalDateTime expiryDate) {
        PointsAccount account = pointsAccountRepository.findByUserId(userId);
        if (account == null) {
            account = createPointsAccount(userId);
        }

        account.setTotalPoints(account.getTotalPoints() + points);
        account.setAvailablePoints(account.getAvailablePoints() + points);
        if (expiryDate != null) {
            account.setExpiryDate(expiryDate);
        }
        account.setUpdateTime(LocalDateTime.now());
        pointsAccountRepository.save(account);

        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setPoints(points);
        record.setType(type);
        record.setDescription(description);
        record.setOrderId(orderId);
        record.setExpiryDate(expiryDate);
        record.setIsExpired(false);
        record.setCreateTime(LocalDateTime.now());
        pointsRecordRepository.save(record);

        userService.updateMemberLevel(userId);
    }

    @Transactional
    public void deductPoints(Long userId, Integer points, String type, String description, Long orderId) {
        PointsAccount account = pointsAccountRepository.findByUserId(userId);
        if (account == null) {
            throw new RuntimeException("积分账户不存在");
        }

        if (account.getAvailablePoints() < points) {
            throw new RuntimeException("可用积分不足");
        }

        account.setAvailablePoints(account.getAvailablePoints() - points);
        account.setUpdateTime(LocalDateTime.now());
        pointsAccountRepository.save(account);

        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setPoints(-points);
        record.setType(type);
        record.setDescription(description);
        record.setOrderId(orderId);
        record.setCreateTime(LocalDateTime.now());
        pointsRecordRepository.save(record);
    }

    @Transactional
    public void updateDepositAmount(Long userId, Double amount) {
        PointsAccount account = pointsAccountRepository.findByUserId(userId);
        if (account == null) {
            throw new RuntimeException("积分账户不存在");
        }

        account.setTotalDepositAmount(account.getTotalDepositAmount() + amount);
        account.setUpdateTime(LocalDateTime.now());
        pointsAccountRepository.save(account);
    }

    public List<PointsRecord> getPointsRecords(Long userId) {
        return pointsRecordRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }

    public List<PointsRecord> getPointsRecordsByType(Long userId, String type) {
        return pointsRecordRepository.findByUserIdAndTypeOrderByCreateTimeDesc(userId, type);
    }

    public com.bank.points.entity.User getUserById(Long userId) {
        PointsAccount account = pointsAccountRepository.findByUserId(userId);
        if (account == null) {
            return null;
        }
        com.bank.points.entity.User user = new com.bank.points.entity.User();
        user.setId(userId);
        user.setMemberLevel(calculateMemberLevel(account));
        return user;
    }

    private Integer calculateMemberLevel(PointsAccount account) {
        List<MemberLevelConfig> levels = memberLevelConfigRepository.findByStatusOrderByLevelAsc(1);
        if (levels.isEmpty()) {
            return 1;
        }
        for (MemberLevelConfig level : levels) {
            if (account.getTotalPoints() >= level.getMinPoints() && account.getTotalDepositAmount() >= level.getMinDepositAmount().doubleValue()) {
                return level.getLevel();
            }
        }
        return 1;
    }

    @Transactional
    public void checkExpiredPoints() {
        LocalDateTime now = LocalDateTime.now();
        List<PointsRecord> expiredRecords = pointsRecordRepository.findAll().stream()
            .filter(record -> record.getExpiryDate() != null 
                && record.getExpiryDate().isBefore(now) 
                && !record.getIsExpired())
            .collect(java.util.stream.Collectors.toList());

        for (PointsRecord record : expiredRecords) {
            record.setIsExpired(true);
            pointsRecordRepository.save(record);

            PointsAccount account = pointsAccountRepository.findByUserId(record.getUserId());
            if (account != null && record.getPoints() > 0) {
                account.setExpiredPoints(account.getExpiredPoints() + record.getPoints());
                account.setAvailablePoints(Math.max(0, account.getAvailablePoints() - record.getPoints()));
                account.setUpdateTime(LocalDateTime.now());
                pointsAccountRepository.save(account);
            }
        }
    }

    public Double getMemberDiscountRate(Integer memberLevel) {
        MemberLevelConfig level = memberLevelConfigRepository.findByLevel(memberLevel);
        if (level == null) {
            return 1.0;
        }
        return level.getDiscountRate().doubleValue();
    }
}
