package com.bank.points.controller;

import com.bank.points.dto.Result;
import com.bank.points.entity.*;
import com.bank.points.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private PointsRuleService pointsRuleService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MemberBenefitService memberBenefitService;

    @Autowired
    private PointsActivityService pointsActivityService;

    @Autowired
    private PointsService pointsService;

    @GetMapping("/users")
    public Result<List<User>> getAllUsers() {
        return Result.success(userService.getAllUsers());
    }

    @GetMapping("/users/search")
    public Result<List<User>> searchUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer memberLevel,
            @RequestParam(required = false) Integer status) {
        return Result.success(userService.searchUsers(username, phone, memberLevel, status));
    }

    @GetMapping("/users/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    @PutMapping("/users/status")
    public Result<User> updateUserStatus(@RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(user);
            return Result.success("更新成功", updatedUser);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/users/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/rules")
    public Result<List<PointsRule>> getAllRules() {
        return Result.success(pointsRuleService.getAllRulesForAdmin());
    }

    @GetMapping("/rules/{id}")
    public Result<PointsRule> getRule(@PathVariable Long id) {
        return Result.success(pointsRuleService.getRuleById(id));
    }

    @PostMapping("/rules")
    public Result<PointsRule> createRule(@RequestBody PointsRule rule) {
        try {
            return Result.success("创建成功", pointsRuleService.createRule(rule));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/rules")
    public Result<PointsRule> updateRule(@RequestBody PointsRule rule) {
        try {
            return Result.success("更新成功", pointsRuleService.updateRule(rule));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/rules/{id}")
    public Result<Void> deleteRule(@PathVariable Long id) {
        try {
            pointsRuleService.deleteRule(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/products")
    public Result<List<Product>> getAllProducts() {
        return Result.success(productService.getAllProductsForAdmin());
    }

    @GetMapping("/products/{id}")
    public Result<Product> getProduct(@PathVariable Long id) {
        return Result.success(productService.getProductById(id));
    }

    @PostMapping("/products")
    public Result<Product> createProduct(@RequestBody Product product) {
        try {
            return Result.success("创建成功", productService.createProduct(product));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/products")
    public Result<Product> updateProduct(@RequestBody Product product) {
        try {
            return Result.success("更新成功", productService.updateProduct(product));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/products/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/orders")
    public Result<List<Order>> getAllOrders() {
        return Result.success(orderService.getAllOrders());
    }

    @GetMapping("/orders/{id}")
    public Result<Order> getOrder(@PathVariable Long id) {
        return Result.success(orderService.getOrderByOrderNo(id.toString()));
    }

    @PutMapping("/orders/status")
    public Result<Order> updateOrderStatus(@RequestBody Order order) {
        try {
            return Result.success("更新成功", orderService.updateOrderStatus(order.getId(), order.getStatus()));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/orders/ship")
    public Result<Order> shipOrder(@RequestBody Map<String, Object> request) {
        try {
            Long orderId = Long.parseLong(request.get("orderId").toString());
            String logisticsCompany = (String) request.get("logisticsCompany");
            String trackingNumber = (String) request.get("trackingNumber");
            return Result.success("发货成功", orderService.shipOrder(orderId, logisticsCompany, trackingNumber));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/benefits")
    public Result<List<MemberBenefit>> getAllBenefits() {
        return Result.success(memberBenefitService.getAllBenefitsForAdmin());
    }

    @GetMapping("/benefits/{id}")
    public Result<MemberBenefit> getBenefit(@PathVariable Long id) {
        return Result.success(memberBenefitService.getBenefitById(id));
    }

    @PostMapping("/benefits")
    public Result<MemberBenefit> createBenefit(@RequestBody MemberBenefit benefit) {
        try {
            return Result.success("创建成功", memberBenefitService.createBenefit(benefit));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/benefits")
    public Result<MemberBenefit> updateBenefit(@RequestBody MemberBenefit benefit) {
        try {
            return Result.success("更新成功", memberBenefitService.updateBenefit(benefit));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/benefits/{id}")
    public Result<Void> deleteBenefit(@PathVariable Long id) {
        try {
            memberBenefitService.deleteBenefit(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/activities")
    public Result<List<PointsActivity>> getAllActivities() {
        return Result.success(pointsActivityService.getAllActivities());
    }

    @GetMapping("/activities/{id}")
    public Result<PointsActivity> getActivity(@PathVariable Long id) {
        return Result.success(pointsActivityService.getActivityById(id));
    }

    @PostMapping("/activities")
    public Result<PointsActivity> createActivity(@RequestBody PointsActivity activity) {
        try {
            return Result.success("创建成功", pointsActivityService.createActivity(activity));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/activities")
    public Result<PointsActivity> updateActivity(@RequestBody PointsActivity activity) {
        try {
            return Result.success("更新成功", pointsActivityService.updateActivity(activity));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/activities/{id}")
    public Result<Void> deleteActivity(@PathVariable Long id) {
        try {
            pointsActivityService.deleteActivity(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userService.getAllUsers().size());
        stats.put("productCount", productService.getAllProductsForAdmin().size());
        stats.put("orderCount", orderService.getAllOrders().size());
        
        List<Order> orders = orderService.getAllOrders();
        int totalPoints = orders.stream().mapToInt(Order::getPoints).sum();
        stats.put("totalPoints", totalPoints);
        
        Map<Integer, Long> levelDistribution = userService.getAllUsers().stream()
            .collect(java.util.stream.Collectors.groupingBy(User::getMemberLevel, java.util.stream.Collectors.counting()));
        stats.put("levelDistribution", levelDistribution);
        
        return Result.success(stats);
    }

    @GetMapping("/users/{userId}/points-records")
    public Result<List<?>> getUserPointsRecords(@PathVariable Long userId) {
        return Result.success(pointsService.getPointsRecords(userId));
    }

    @PutMapping("/users/{userId}/points")
    public Result<Void> updateUserPoints(@PathVariable Long userId, @RequestBody Map<String, Object> request) {
        try {
            Integer points = Integer.parseInt(request.get("points").toString());
            String type = (String) request.get("type");
            String description = (String) request.getOrDefault("description", "管理员调整");
            
            User user = userService.getUserById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            if ("ADD".equals(type)) {
                pointsService.addPoints(userId, points, "INCOME", description, null, null);
            } else if ("REDUCE".equals(type)) {
                pointsService.addPoints(userId, -points, "EXPENSE", description, null, null);
            } else if ("SET".equals(type)) {
                com.bank.points.entity.PointsAccount account = pointsService.getPointsAccount(userId);
                if (account != null) {
                    int diff = points - account.getAvailablePoints();
                    if (diff > 0) {
                        pointsService.addPoints(userId, diff, "INCOME", description, null, null);
                    } else if (diff < 0) {
                        pointsService.addPoints(userId, diff, "EXPENSE", description, null, null);
                    }
                }
            }
            return Result.success("调整成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Autowired
    private com.bank.points.service.PointsRewardService pointsRewardService;

    @GetMapping("/rewards")
    public Result<List<?>> getAllRewards() {
        return Result.success(pointsRewardService.getAllRewardsForAdmin());
    }

    @PostMapping("/rewards")
    public Result<?> createReward(@RequestBody com.bank.points.entity.PointsReward reward) {
        try {
            return Result.success("添加成功", pointsRewardService.addReward(reward));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/rewards/{id}")
    public Result<?> updateReward(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Integer points = Integer.parseInt(request.get("points").toString());
            return Result.success("更新成功", pointsRewardService.updateReward(id, points));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/rewards/{id}")
    public Result<Void> deleteReward(@PathVariable Long id) {
        try {
            pointsRewardService.deleteReward(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Autowired
    private com.bank.points.service.BankActivityService bankActivityService;

    @GetMapping("/bank-activities")
    public Result<List<?>> getAllBankActivities() {
        return Result.success(bankActivityService.getAllActivitiesForAdmin());
    }

    @GetMapping("/bank-activities/{id}")
    public Result<?> getBankActivity(@PathVariable Long id) {
        return Result.success(bankActivityService.getActivityById(id));
    }

    @PostMapping("/bank-activities")
    public Result<?> createBankActivity(@RequestBody com.bank.points.entity.BankActivity activity) {
        try {
            return Result.success("创建成功", bankActivityService.createActivity(activity));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/bank-activities")
    public Result<?> updateBankActivity(@RequestBody com.bank.points.entity.BankActivity activity) {
        try {
            return Result.success("更新成功", bankActivityService.updateActivity(activity));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/bank-activities/{id}")
    public Result<Void> deleteBankActivity(@PathVariable Long id) {
        try {
            bankActivityService.deleteActivity(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/bank-activities/{id}/participants")
    public Result<List<?>> getActivityParticipants(@PathVariable Long id) {
        return Result.success(bankActivityService.getActivityParticipants(id));
    }

    @Autowired
    private com.bank.points.service.MessageService messageService;

    @GetMapping("/messages")
    public Result<List<?>> getAllMessages() {
        return Result.success(messageService.getAllMessagesForAdmin());
    }

    @GetMapping("/messages/unreplied")
    public Result<List<?>> getUnrepliedMessages() {
        return Result.success(messageService.getUnrepliedMessages());
    }

    @PutMapping("/messages/{id}/reply")
    public Result<?> replyMessage(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            String replyContent = (String) request.get("replyContent");
            return Result.success("回复成功", messageService.replyMessage(id, replyContent));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/messages/{id}")
    public Result<Void> deleteMessage(@PathVariable Long id) {
        try {
            messageService.deleteMessage(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Autowired
    private com.bank.points.service.ProductReviewService productReviewService;

    @GetMapping("/reviews")
    public Result<List<?>> getAllReviews() {
        return Result.success(productReviewService.getAllReviewsForAdmin());
    }

    @PutMapping("/reviews/{id}/status")
    public Result<?> updateReviewStatus(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Integer status = Integer.parseInt(request.get("status").toString());
            return Result.success("更新成功", productReviewService.updateReviewStatus(id, status));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/reviews/{id}")
    public Result<Void> deleteReview(@PathVariable Long id) {
        try {
            productReviewService.deleteReview(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
