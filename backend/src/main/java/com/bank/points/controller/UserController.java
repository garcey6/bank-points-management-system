package com.bank.points.controller;

import com.bank.points.dto.LoginRequest;
import com.bank.points.dto.RegisterRequest;
import com.bank.points.dto.Result;
import com.bank.points.entity.PointsAccount;
import com.bank.points.entity.PointsRecord;
import com.bank.points.entity.User;
import com.bank.points.service.MemberBenefitService;
import com.bank.points.service.OrderService;
import com.bank.points.service.PointsService;
import com.bank.points.service.ProductService;
import com.bank.points.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PointsService pointsService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MemberBenefitService memberBenefitService;

    @PostMapping("/register")
    public Result<User> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request);
            return Result.success("注册成功", user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.login(request);
            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            return Result.success("登录成功", data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/info/{id}")
    public Result<User> getUserInfo(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    @PutMapping("/update")
    public Result<User> updateUser(@RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(user);
            return Result.success("更新成功", updatedUser);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/points/{userId}")
    public Result<PointsAccount> getPointsAccount(@PathVariable Long userId) {
        PointsAccount account = pointsService.getPointsAccount(userId);
        if (account == null) {
            return Result.error("积分账户不存在");
        }
        return Result.success(account);
    }

    @GetMapping("/points/records/{userId}")
    public Result<List<PointsRecord>> getPointsRecords(@PathVariable Long userId) {
        List<PointsRecord> records = pointsService.getPointsRecords(userId);
        return Result.success(records);
    }

    @GetMapping("/points/records/{userId}/{type}")
    public Result<List<PointsRecord>> getPointsRecordsByType(@PathVariable Long userId, @PathVariable String type) {
        List<PointsRecord> records = pointsService.getPointsRecordsByType(userId, type);
        return Result.success(records);
    }

    @GetMapping("/products")
    public Result<List<?>> getProducts() {
        return Result.success(productService.getAllProducts());
    }

    @GetMapping("/products/{id}")
    public Result<?> getProduct(@PathVariable Long id) {
        return Result.success(productService.getProductById(id));
    }

    @GetMapping("/products/search")
    public Result<List<?>> searchProducts(@RequestParam String name) {
        return Result.success(productService.searchProducts(name));
    }

    @GetMapping("/products/filter")
    public Result<List<?>> filterProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer minPoints,
            @RequestParam(required = false) Integer maxPoints) {
        return Result.success(productService.filterProducts(category, minPoints, maxPoints));
    }

    @GetMapping("/products/category/{category}")
    public Result<List<?>> getProductsByCategory(@PathVariable String category) {
        return Result.success(productService.getProductsByCategory(category));
    }

    @PostMapping("/exchange/{userId}")
    public Result<?> exchangeProduct(@PathVariable Long userId, @RequestBody com.bank.points.dto.ExchangeRequest request) {
        try {
            return Result.success("兑换成功", orderService.createOrder(userId, request));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/orders/{userId}")
    public Result<List<?>> getOrders(@PathVariable Long userId) {
        return Result.success(orderService.getUserOrders(userId));
    }

    @GetMapping("/benefits/{memberLevel}")
    public Result<List<?>> getBenefits(@PathVariable Integer memberLevel) {
        return Result.success(memberBenefitService.getBenefitsByMemberLevel(memberLevel));
    }

    @PostMapping("/deposit/{userId}")
    public Result<?> addDepositPoints(@PathVariable Long userId, @RequestBody Map<String, Object> request) {
        try {
            Double amount = Double.parseDouble(request.get("amount").toString());
            userService.addDepositPoints(userId, java.math.BigDecimal.valueOf(amount));
            return Result.success("存款成功，积分已到账");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/consumption/{userId}")
    public Result<?> addConsumptionPoints(@PathVariable Long userId, @RequestBody Map<String, Object> request) {
        try {
            Double amount = Double.parseDouble(request.get("amount").toString());
            userService.addConsumptionPoints(userId, java.math.BigDecimal.valueOf(amount));
            return Result.success("消费成功，积分已到账");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/member/levels")
    public Result<List<?>> getAllMemberLevels() {
        return Result.success(userService.getAllMemberLevels());
    }

    @GetMapping("/member/next-level/{userId}")
    public Result<?> getNextLevel(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            com.bank.points.entity.MemberLevelConfig nextLevel = userService.getNextLevelConfig(user.getMemberLevel());
            return Result.success(nextLevel);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Autowired
    private com.bank.points.service.PointsActivityService pointsActivityService;

    @GetMapping("/activities")
    public Result<List<?>> getActiveActivities() {
        return Result.success(pointsActivityService.getActiveActivities());
    }

    @Autowired
    private com.bank.points.service.CartService cartService;

    @GetMapping("/cart/{userId}")
    public Result<List<?>> getUserCart(@PathVariable Long userId) {
        return Result.success(cartService.getUserCartWithProducts(userId));
    }

    @PostMapping("/cart/{userId}")
    public Result<?> addToCart(@PathVariable Long userId, @RequestBody Map<String, Object> request) {
        try {
            Long productId = Long.parseLong(request.get("productId").toString());
            Integer quantity = Integer.parseInt(request.get("quantity").toString());
            return Result.success("添加成功", cartService.addToCart(userId, productId, quantity));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/cart/{cartId}")
    public Result<?> updateCartQuantity(@PathVariable Long cartId, @RequestBody Map<String, Object> request) {
        try {
            Integer quantity = Integer.parseInt(request.get("quantity").toString());
            return Result.success("更新成功", cartService.updateCartQuantity(cartId, quantity));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/cart/{cartId}")
    public Result<Void> removeFromCart(@PathVariable Long cartId) {
        try {
            cartService.removeFromCart(cartId);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/cart/clear/{userId}")
    public Result<Void> clearCart(@PathVariable Long userId) {
        try {
            cartService.clearCart(userId);
            return Result.success("清空成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Autowired
    private com.bank.points.service.ProductReviewService productReviewService;

    @GetMapping("/reviews/product/{productId}")
    public Result<List<?>> getProductReviews(@PathVariable Long productId) {
        return Result.success(productReviewService.getReviewsByProductId(productId));
    }

    @GetMapping("/reviews/user/{userId}")
    public Result<List<?>> getUserReviews(@PathVariable Long userId) {
        return Result.success(productReviewService.getReviewsByUserId(userId));
    }

    @PostMapping("/reviews")
    public Result<?> addReview(@RequestBody com.bank.points.entity.ProductReview review) {
        try {
            return Result.success("评价成功", productReviewService.addReview(review));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Autowired
    private com.bank.points.service.BankActivityService bankActivityService;

    @GetMapping("/bank-activities")
    public Result<List<?>> getActiveBankActivities() {
        return Result.success(bankActivityService.getActiveActivities());
    }

    @GetMapping("/bank-activities/{id}")
    public Result<?> getBankActivity(@PathVariable Long id) {
        return Result.success(bankActivityService.getActivityById(id));
    }

    @GetMapping("/user-activities/{userId}")
    public Result<List<?>> getUserActivities(@PathVariable Long userId) {
        return Result.success(bankActivityService.getUserActivities(userId));
    }

    @PostMapping("/user-activities/{userId}")
    public Result<?> participateActivity(@PathVariable Long userId, @RequestBody Map<String, Object> request) {
        try {
            Long activityId = Long.parseLong(request.get("activityId").toString());
            java.math.BigDecimal depositAmount = null;
            if (request.get("depositAmount") != null) {
                Double amount = Double.parseDouble(request.get("depositAmount").toString());
                depositAmount = java.math.BigDecimal.valueOf(amount);
            }
            String rewardType = (String) request.get("rewardType");
            return Result.success("参与成功", bankActivityService.participateActivity(userId, activityId, depositAmount, rewardType));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/user-activities/{userId}/{activityId}")
    public Result<Void> cancelParticipation(@PathVariable Long userId, @PathVariable Long activityId) {
        try {
            bankActivityService.cancelParticipation(userId, activityId);
            return Result.success("取消参与成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Autowired
    private com.bank.points.service.MessageService messageService;

    @GetMapping("/messages/{userId}")
    public Result<List<?>> getUserMessages(@PathVariable Long userId) {
        return Result.success(messageService.getMessagesByUserId(userId));
    }

    @PostMapping("/messages")
    public Result<?> addMessage(@RequestBody com.bank.points.entity.Message message) {
        try {
            return Result.success("留言成功", messageService.addMessage(message));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
