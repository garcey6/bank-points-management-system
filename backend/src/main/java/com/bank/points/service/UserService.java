package com.bank.points.service;

import com.bank.points.dto.LoginRequest;
import com.bank.points.dto.RegisterRequest;
import com.bank.points.entity.MemberLevelConfig;
import com.bank.points.entity.PointsAccount;
import com.bank.points.entity.User;
import com.bank.points.repository.MemberLevelConfigRepository;
import com.bank.points.repository.PointsAccountRepository;
import com.bank.points.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointsAccountRepository pointsAccountRepository;

    @Autowired
    private MemberLevelConfigRepository memberLevelConfigRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> searchUsers(String username, String phone, Integer memberLevel, Integer status) {
        if (username != null && !username.isEmpty()) {
            return userRepository.findByUsernameContaining(username);
        }
        if (phone != null && !phone.isEmpty()) {
            return userRepository.findByPhoneContaining(phone);
        }
        if (memberLevel != null) {
            return userRepository.findByMemberLevel(memberLevel);
        }
        if (status != null) {
            return userRepository.findByStatus(status);
        }
        return userRepository.findAll();
    }

    public User login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if (user != null && user.getPassword().equals(request.getPassword())) {
            if (user.getStatus() == 0) {
                throw new RuntimeException("账号已被禁用");
            }
            return user;
        }
        throw new RuntimeException("用户名或密码错误");
    }

    public User register(RegisterRequest request) {
        try {
            System.out.println("Starting registration for user: " + request.getUsername());
            
            // 检查用户名是否已存在
            System.out.println("Checking if username exists: " + request.getUsername());
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new RuntimeException("用户名已存在");
            }
            
            // 检查手机号是否已被注册
            System.out.println("Checking if phone exists: " + request.getPhone());
            if (userRepository.existsByPhone(request.getPhone())) {
                throw new RuntimeException("手机号已被注册");
            }
            
            // 检查邮箱是否已被注册
            System.out.println("Checking if email exists: " + request.getEmail());
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("邮箱已被注册");
            }

            // 创建用户对象
            System.out.println("Creating user object");
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            user.setSalt("default_salt"); // 设置默认salt值
            user.setRealName(request.getRealName());
            user.setPhone(request.getPhone());
            user.setEmail(request.getEmail());
            user.setBankCard(request.getBankCard());
            user.setMemberLevel(1);
            user.setStatus(1);
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());

            // 保存用户
            System.out.println("Saving user: " + user);
            try {
                user = userRepository.save(user);
                System.out.println("User saved successfully: " + user.getId());
            } catch (Exception e) {
                System.err.println("Error saving user: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }

            // 创建积分账户
            System.out.println("Creating points account for user: " + user.getId());
            PointsAccount account = new PointsAccount();
            account.setUserId(user.getId());
            account.setTotalPoints(0);
            account.setAvailablePoints(0);
            account.setFrozenPoints(0);
            account.setExpiredPoints(0);
            account.setExpiryDate(LocalDateTime.now().plusYears(1)); // 设置默认过期时间为1年后
            account.setTotalDepositAmount(0.0);
            account.setCreateTime(LocalDateTime.now());
            account.setUpdateTime(LocalDateTime.now());
            
            // 保存积分账户
            System.out.println("Saving points account: " + account);
            try {
                pointsAccountRepository.save(account);
                System.out.println("Points account saved successfully");
            } catch (Exception e) {
                System.err.println("Error saving points account: " + e.getMessage());
                e.printStackTrace();
                // 积分账户创建失败，不影响用户注册
                System.out.println("Continuing registration without points account");
            }

            System.out.println("Registration completed successfully for user: " + user.getUsername());
            return user;
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public User updateUser(User user) {
        // 获取当前用户信息
        User currentUser = userRepository.findById(user.getId()).orElse(null);
        if (currentUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 检查用户名是否已被其他用户使用
        if (!currentUser.getUsername().equals(user.getUsername())) {
            User existingUserByUsername = userRepository.findByUsername(user.getUsername());
            if (existingUserByUsername != null) {
                throw new RuntimeException("用户名已被其他用户使用");
            }
        }
        
        // 检查手机号是否已被其他用户使用
        if (!currentUser.getPhone().equals(user.getPhone())) {
            User existingUserByPhone = userRepository.findByPhone(user.getPhone());
            if (existingUserByPhone != null) {
                throw new RuntimeException("手机号已被其他用户使用");
            }
        }
        
        // 检查邮箱是否已被其他用户使用
        if (!currentUser.getEmail().equals(user.getEmail())) {
            User existingUserByEmail = userRepository.findByEmail(user.getEmail());
            if (existingUserByEmail != null) {
                throw new RuntimeException("邮箱已被其他用户使用");
            }
        }
        
        // 确保密码和salt不为空
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(currentUser.getPassword());
        }
        if (user.getSalt() == null || user.getSalt().isEmpty()) {
            user.setSalt(currentUser.getSalt());
        }
        // 确保createTime不为空，使用当前用户的createTime
        if (user.getCreateTime() == null) {
            user.setCreateTime(currentUser.getCreateTime());
        }
        
        user.setUpdateTime(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public void addDepositPoints(Long userId, java.math.BigDecimal amount) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        PointsAccount account = pointsAccountRepository.findByUserId(userId);
        if (account == null) {
            account = new PointsAccount();
            account.setUserId(userId);
            account.setTotalPoints(0);
            account.setAvailablePoints(0);
            account.setFrozenPoints(0);
            account.setExpiredPoints(0);
            account.setExpiryDate(LocalDateTime.now().plusYears(1)); // 设置默认过期时间为1年后
            account.setTotalDepositAmount(0.0);
            account.setCreateTime(LocalDateTime.now());
            account.setUpdateTime(LocalDateTime.now());
            pointsAccountRepository.save(account);
        }

        account.setTotalDepositAmount(account.getTotalDepositAmount() + amount.doubleValue());
        account.setUpdateTime(LocalDateTime.now());
        pointsAccountRepository.save(account);

        updateMemberLevel(userId);
    }

    @Transactional
    public void addConsumptionPoints(Long userId, java.math.BigDecimal amount) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        PointsAccount account = pointsAccountRepository.findByUserId(userId);
        if (account == null) {
            account = new PointsAccount();
            account.setUserId(userId);
            account.setTotalPoints(0);
            account.setAvailablePoints(0);
            account.setFrozenPoints(0);
            account.setExpiredPoints(0);
            account.setExpiryDate(LocalDateTime.now().plusYears(1)); // 设置默认过期时间为1年后
            account.setTotalDepositAmount(0.0);
            account.setCreateTime(LocalDateTime.now());
            account.setUpdateTime(LocalDateTime.now());
            pointsAccountRepository.save(account);
        }

        updateMemberLevel(userId);
    }

    @Transactional
    public void updateMemberLevel(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return;
        }

        PointsAccount account = pointsAccountRepository.findByUserId(userId);
        if (account == null) {
            return;
        }

        Integer newLevel = calculateMemberLevel(account);
        if (!newLevel.equals(user.getMemberLevel())) {
            user.setMemberLevel(newLevel);
            user.setUpdateTime(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    private Integer calculateMemberLevel(PointsAccount account) {
        List<MemberLevelConfig> levels = memberLevelConfigRepository.findByStatusOrderByLevelAsc(1);
        if (levels.isEmpty()) {
            return 1;
        }
        Integer maxLevel = 1;
        for (MemberLevelConfig level : levels) {
            if (account.getTotalPoints() >= level.getMinPoints() 
                && account.getTotalDepositAmount().doubleValue() >= level.getMinDepositAmount().doubleValue()) {
                maxLevel = Math.max(maxLevel, level.getLevel());
            }
        }
        return maxLevel;
    }

    public MemberLevelConfig getNextLevelConfig(Integer currentLevel) {
        List<MemberLevelConfig> levels = memberLevelConfigRepository.findByStatusOrderByLevelAsc(1);
        for (MemberLevelConfig level : levels) {
            if (level.getLevel() > currentLevel) {
                return level;
            }
        }
        return null;
    }

    public List<MemberLevelConfig> getAllMemberLevels() {
        return memberLevelConfigRepository.findByStatusOrderByLevelAsc(1);
    }

    @Transactional
    public User createUser(User user) {
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        return userRepository.save(user);
    }
}
