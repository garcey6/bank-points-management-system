package com.bank.points.config;

import com.bank.points.entity.Product;
import com.bank.points.entity.User;
import com.bank.points.entity.BankActivity;
import com.bank.points.entity.MemberBenefit;
import com.bank.points.entity.PointsRule;
import com.bank.points.entity.Order;
import com.bank.points.entity.PointsReward;
import com.bank.points.entity.Message;
import com.bank.points.service.ProductService;
import com.bank.points.service.UserService;
import com.bank.points.service.PointsService;
import com.bank.points.service.BankActivityService;
import com.bank.points.service.MemberBenefitService;
import com.bank.points.service.PointsRuleService;
import com.bank.points.service.OrderService;
import com.bank.points.service.PointsRewardService;
import com.bank.points.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private PointsService pointsService;

    @Autowired
    private BankActivityService bankActivityService;

    @Autowired
    private MemberBenefitService memberBenefitService;

    @Autowired
    private PointsRuleService pointsRuleService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PointsRewardService pointsRewardService;

    @Autowired
    private MessageService messageService;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    @Override
    public void run(String... args) throws Exception {
        // 创建数据库（如果不存在）
        createDatabaseIfNotExists();
        
        // 初始化用户数据
        if (userService.getAllUsers().isEmpty()) {
            // 创建测试用户
            User user1 = new User();
            user1.setUsername("user1");
            user1.setPassword("123456");
            user1.setSalt("test_salt");
            user1.setRealName("测试用户");
            user1.setPhone("13800138000");
            user1.setEmail("test@example.com");
            user1.setMemberLevel(2);
            user1.setStatus(1);
            user1.setCreateTime(LocalDateTime.now());
            user1.setUpdateTime(LocalDateTime.now());
            userService.createUser(user1);

            // 创建管理员用户
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("123456");
            admin.setSalt("admin_salt");
            admin.setRealName("管理员");
            admin.setPhone("13900139000");
            admin.setEmail("admin@example.com");
            admin.setMemberLevel(4);
            admin.setStatus(1);
            admin.setCreateTime(LocalDateTime.now());
            admin.setUpdateTime(LocalDateTime.now());
            userService.createUser(admin);

            System.out.println("初始化用户数据完成");
        }

        // 初始化商品数据
        List<Product> existingProducts = productService.getAllProductsForAdmin();
        boolean hasInvalidProducts = false;
        boolean hasMissingProducts = false;
        
        // 检查是否有异常商品
        for (Product product : existingProducts) {
            if ("123".equals(product.getName()) || product.getCategory() == null) {
                hasInvalidProducts = true;
                break;
            }
        }
        
        // 检查是否缺少必要的商品
        List<String> requiredProductNames = new java.util.ArrayList<>(java.util.Arrays.asList(
            "话费充值50元", "话费充值100元", "京东E卡50元", "京东E卡100元", 
            "星巴克咖啡券", "电影票", "精美礼品盒", "品牌保温杯"
        ));
        
        for (Product product : existingProducts) {
            requiredProductNames.remove(product.getName());
        }
        
        if (!requiredProductNames.isEmpty()) {
            hasMissingProducts = true;
        }
        
        // 如果有异常商品或缺少商品，重新初始化
        if (existingProducts.isEmpty() || hasInvalidProducts || hasMissingProducts) {
            // 删除所有商品
            for (Product product : existingProducts) {
                productService.deleteProduct(product.getId());
            }
            
            System.out.println("清理商品数据完成");
            
            // 插入示例商品数据
            Product product1 = new Product();
            product1.setName("话费充值50元");
            product1.setDescription("移动/联通/电信50元话费充值");
            product1.setCategory("虚拟卡券");
            product1.setPoints(500);
            product1.setStock(100);
            product1.setImageUrl("/images/products/huafei50.jpg");
            product1.setDiscount(1.0);
            product1.setStatus(1);
            product1.setCreateTime(LocalDateTime.now());
            product1.setUpdateTime(LocalDateTime.now());
            productService.createProduct(product1);

            Product product2 = new Product();
            product2.setName("话费充值100元");
            product2.setDescription("移动/联通/电信100元话费充值");
            product2.setCategory("虚拟卡券");
            product2.setPoints(1000);
            product2.setStock(100);
            product2.setImageUrl("/images/products/huafei100.jpg");
            product2.setDiscount(1.0);
            product2.setStatus(1);
            product2.setCreateTime(LocalDateTime.now());
            product2.setUpdateTime(LocalDateTime.now());
            productService.createProduct(product2);

            Product product3 = new Product();
            product3.setName("京东E卡50元");
            product3.setDescription("京东购物卡50元面值");
            product3.setCategory("虚拟卡券");
            product3.setPoints(500);
            product3.setStock(50);
            product3.setImageUrl("/images/products/JDEcard50.jpg");
            product3.setDiscount(1.0);
            product3.setStatus(1);
            product3.setCreateTime(LocalDateTime.now());
            product3.setUpdateTime(LocalDateTime.now());
            productService.createProduct(product3);

            Product product4 = new Product();
            product4.setName("京东E卡100元");
            product4.setDescription("京东购物卡100元面值");
            product4.setCategory("虚拟卡券");
            product4.setPoints(1000);
            product4.setStock(50);
            product4.setImageUrl("/images/products/JDEcard100.jpg");
            product4.setDiscount(1.0);
            product4.setStatus(1);
            product4.setCreateTime(LocalDateTime.now());
            product4.setUpdateTime(LocalDateTime.now());
            productService.createProduct(product4);

            Product product5 = new Product();
            product5.setName("星巴克咖啡券");
            product5.setDescription("星巴克中杯饮品兑换券");
            product5.setCategory("服务权益");
            product5.setPoints(300);
            product5.setStock(200);
            product5.setImageUrl("/images/products/Starbucks.jpg");
            product5.setDiscount(1.0);
            product5.setStatus(1);
            product5.setCreateTime(LocalDateTime.now());
            product5.setUpdateTime(LocalDateTime.now());
            productService.createProduct(product5);

            Product product6 = new Product();
            product6.setName("电影票");
            product6.setDescription("全国通用电影票一张");
            product6.setCategory("服务权益");
            product6.setPoints(400);
            product6.setStock(150);
            product6.setImageUrl("/images/products/dianying.jpg");
            product6.setDiscount(1.0);
            product6.setStatus(1);
            product6.setCreateTime(LocalDateTime.now());
            product6.setUpdateTime(LocalDateTime.now());
            productService.createProduct(product6);

            Product product7 = new Product();
            product7.setName("精美礼品盒");
            product7.setDescription("精选精美礼品盒");
            product7.setCategory("实物商品");
            product7.setPoints(800);
            product7.setStock(50);
            product7.setImageUrl("/images/products/gifthe.jpg");
            product7.setDiscount(1.0);
            product7.setStatus(1);
            product7.setCreateTime(LocalDateTime.now());
            product7.setUpdateTime(LocalDateTime.now());
            productService.createProduct(product7);

            Product product8 = new Product();
            product8.setName("品牌保温杯");
            product8.setDescription("高品质品牌保温杯");
            product8.setCategory("实物商品");
            product8.setPoints(1200);
            product8.setStock(30);
            product8.setImageUrl("/images/products/baowenbei.jpg");
            product8.setDiscount(1.0);
            product8.setStatus(1);
            product8.setCreateTime(LocalDateTime.now());
            product8.setUpdateTime(LocalDateTime.now());
            productService.createProduct(product8);

            System.out.println("初始化商品数据完成");
        }

        // 为测试用户添加积分记录
        if (userService.getAllUsers().size() > 0) {
            User testUser = userService.getAllUsers().get(0);
            // 确保积分账户存在
            if (pointsService.getPointsAccount(testUser.getId()) == null) {
                pointsService.createPointsAccount(testUser.getId());
            }
            
            // 检查是否已有积分记录
            if (pointsService.getPointsRecords(testUser.getId()).isEmpty()) {
                // 添加积分记录
                pointsService.addPoints(testUser.getId(), 1000, "INCOME", "注册奖励", null, LocalDateTime.now().plusDays(365));
                pointsService.addPoints(testUser.getId(), 500, "INCOME", "消费返利", null, LocalDateTime.now().plusDays(365));
                pointsService.addPoints(testUser.getId(), 200, "INCOME", "活动奖励", null, LocalDateTime.now().plusDays(365));
                pointsService.addPoints(testUser.getId(), -300, "EXPENSE", "兑换商品", null, null);

                System.out.println("初始化积分数据完成");
            }
        }

        // 初始化活动数据
        if (bankActivityService.getActiveActivities().isEmpty()) {
            // 创建存款送积分活动
            BankActivity activity1 = new BankActivity();
            activity1.setActivityNo("ACT" + System.currentTimeMillis());
            activity1.setName("存款送积分活动");
            activity1.setDescription("新用户首次存款满1000元，送500积分；存款满5000元，送2000积分；存款满10000元，送5000积分。活动期间存款还可享受积分翻倍优惠！");
            activity1.setActivityType("DEPOSIT_POINTS");
            activity1.setStartTime(LocalDateTime.now());
            activity1.setEndTime(LocalDateTime.now().plusMonths(3));
            activity1.setMinDepositAmount(java.math.BigDecimal.valueOf(1000));
            activity1.setRewardPoints(500);
            activity1.setStatus(1);
            activity1.setCreateTime(LocalDateTime.now());
            activity1.setUpdateTime(LocalDateTime.now());
            bankActivityService.createActivity(activity1);

            // 创建存款送礼品活动
            BankActivity activity2 = new BankActivity();
            activity2.setActivityNo("ACT" + (System.currentTimeMillis() + 1));
            activity2.setName("存款送礼品活动");
            activity2.setDescription("存款满5000元送精美礼品盒，存款满10000元送品牌保温杯。数量有限，先到先得！");
            activity2.setActivityType("DEPOSIT_GIFT");
            activity2.setStartTime(LocalDateTime.now());
            activity2.setEndTime(LocalDateTime.now().plusMonths(2));
            activity2.setMinDepositAmount(java.math.BigDecimal.valueOf(5000));
            activity2.setRewardProductId(7L); // 精美礼品盒
            activity2.setStatus(1);
            activity2.setCreateTime(LocalDateTime.now());
            activity2.setUpdateTime(LocalDateTime.now());
            bankActivityService.createActivity(activity2);

            // 创建积分兑换优惠活动
            BankActivity activity3 = new BankActivity();
            activity3.setActivityNo("ACT" + (System.currentTimeMillis() + 2));
            activity3.setName("积分兑换优惠活动");
            activity3.setDescription("活动期间，使用积分兑换指定商品可享受8折优惠！包括话费充值、京东E卡、星巴克咖啡券等热门商品。机会难得，不容错过！");
            activity3.setActivityType("EXCHANGE_DISCOUNT");
            activity3.setStartTime(LocalDateTime.now());
            activity3.setEndTime(LocalDateTime.now().plusMonths(2));
            activity3.setRewardPoints(0);
            activity3.setStatus(1);
            activity3.setCreateTime(LocalDateTime.now());
            activity3.setUpdateTime(LocalDateTime.now());
            bankActivityService.createActivity(activity3);

            // 创建会员专享活动
            BankActivity activity4 = new BankActivity();
            activity4.setActivityNo("ACT" + (System.currentTimeMillis() + 3));
            activity4.setName("会员专享活动");
            activity4.setDescription("银卡及以上会员专享：每月可领取100积分福利，生日当月额外赠送500积分。参与活动还可获得精美礼品一份！");
            activity4.setActivityType("MEMBER_EXCLUSIVE");
            activity4.setStartTime(LocalDateTime.now());
            activity4.setEndTime(LocalDateTime.now().plusMonths(6));
            activity4.setRewardPoints(100);
            activity4.setStatus(1);
            activity4.setCreateTime(LocalDateTime.now());
            activity4.setUpdateTime(LocalDateTime.now());
            bankActivityService.createActivity(activity4);

            System.out.println("初始化活动数据完成");
        }

        // 初始化会员权益数据
        if (memberBenefitService.getAllBenefits().isEmpty()) {
            // 普通会员（等级1）权益
            MemberBenefit benefit1 = new MemberBenefit();
            benefit1.setMemberLevel(1);
            benefit1.setBenefitName("基础积分累计");
            benefit1.setDescription("消费可获得基础积分，1元=1积分");
            benefit1.setStatus(1);
            benefit1.setCreateTime(LocalDateTime.now());
            benefit1.setUpdateTime(LocalDateTime.now());
            memberBenefitService.createBenefit(benefit1);

            MemberBenefit benefit2 = new MemberBenefit();
            benefit2.setMemberLevel(1);
            benefit2.setBenefitName("积分兑换");
            benefit2.setDescription("可使用积分兑换指定商品和服务");
            benefit2.setStatus(1);
            benefit2.setCreateTime(LocalDateTime.now());
            benefit2.setUpdateTime(LocalDateTime.now());
            memberBenefitService.createBenefit(benefit2);

            MemberBenefit benefit3 = new MemberBenefit();
            benefit3.setMemberLevel(1);
            benefit3.setBenefitName("活动参与");
            benefit3.setDescription("可参与银行举办的基础活动");
            benefit3.setStatus(1);
            benefit3.setCreateTime(LocalDateTime.now());
            benefit3.setUpdateTime(LocalDateTime.now());
            memberBenefitService.createBenefit(benefit3);

            // 银卡会员（等级2）权益
            MemberBenefit benefit4 = new MemberBenefit();
            benefit4.setMemberLevel(2);
            benefit4.setBenefitName("积分加速");
            benefit4.setDescription("消费可获得1.1倍积分，1元=1.1积分");
            benefit4.setStatus(1);
            benefit4.setCreateTime(LocalDateTime.now());
            benefit4.setUpdateTime(LocalDateTime.now());
            memberBenefitService.createBenefit(benefit4);

            MemberBenefit benefit5 = new MemberBenefit();
            benefit5.setMemberLevel(2);
            benefit5.setBenefitName("生日礼遇");
            benefit5.setDescription("生日当月可领取100积分奖励");
            benefit5.setStatus(1);
            benefit5.setCreateTime(LocalDateTime.now());
            benefit5.setUpdateTime(LocalDateTime.now());
            memberBenefitService.createBenefit(benefit5);

            MemberBenefit benefit6 = new MemberBenefit();
            benefit6.setMemberLevel(2);
            benefit6.setBenefitName("专属活动");
            benefit6.setDescription("可参与银卡会员专属活动");
            benefit6.setStatus(1);
            benefit6.setCreateTime(LocalDateTime.now());
            benefit6.setUpdateTime(LocalDateTime.now());
            memberBenefitService.createBenefit(benefit6);

            MemberBenefit benefit7 = new MemberBenefit();
            benefit7.setMemberLevel(2);
            benefit7.setBenefitName("优先服务");
            benefit7.setDescription("银行柜台优先办理业务");
            benefit7.setStatus(1);
            benefit7.setCreateTime(LocalDateTime.now());
            benefit7.setUpdateTime(LocalDateTime.now());
            memberBenefitService.createBenefit(benefit7);

            // 金卡会员（等级3）权益
            MemberBenefit benefit8 = new MemberBenefit();
            benefit8.setMemberLevel(3);
            benefit8.setBenefitName("积分倍增");
            benefit8.setDescription("消费可获得1.2倍积分，1元=1.2积分");
            benefit8.setStatus(1);
            benefit8.setCreateTime(LocalDateTime.now());
            benefit8.setUpdateTime(LocalDateTime.now());
            memberBenefitService.createBenefit(benefit8);

            MemberBenefit benefit9 = new MemberBenefit();
            benefit9.setMemberLevel(3);
            benefit9.setBenefitName("生日豪礼");
            benefit9.setDescription("生日当月可领取300积分奖励和精美礼品");
            benefit9.setStatus(1);
            benefit9.setCreateTime(LocalDateTime.now());
            benefit9.setUpdateTime(LocalDateTime.now());
            memberBenefitService.createBenefit(benefit9);

            MemberBenefit benefit10 = new MemberBenefit();
            benefit10.setMemberLevel(3);
            benefit10.setBenefitName("VIP活动");
            benefit10.setDescription("可参与金卡会员VIP专属活动");
            benefit10.setStatus(1);
            benefit10.setCreateTime(LocalDateTime.now());
            benefit10.setUpdateTime(LocalDateTime.now());
            memberBenefitService.createBenefit(benefit10);

            MemberBenefit benefit11 = new MemberBenefit();
            benefit11.setMemberLevel(3);
            benefit11.setBenefitName("专属客服");
            benefit11.setDescription("配备专属客户经理，提供一对一服务");
            benefit11.setStatus(1);
            benefit11.setCreateTime(LocalDateTime.now());
            benefit11.setUpdateTime(LocalDateTime.now());
            memberBenefitService.createBenefit(benefit11);

            MemberBenefit benefit12 = new MemberBenefit();
            benefit12.setMemberLevel(3);
            benefit12.setBenefitName("费用减免");
            benefit12.setDescription("享受部分银行手续费减免");
            benefit12.setStatus(1);
            benefit12.setCreateTime(LocalDateTime.now());
            benefit12.setUpdateTime(LocalDateTime.now());
            memberBenefitService.createBenefit(benefit12);

            // 白金会员（等级4）权益
            MemberBenefit benefit13 = new MemberBenefit();
            benefit13.setMemberLevel(4);
            benefit13.setBenefitName("积分尊享");
            benefit13.setDescription("消费可获得1.5倍积分，1元=1.5积分");
            benefit13.setStatus(1);
            benefit13.setCreateTime(LocalDateTime.now());
            benefit13.setUpdateTime(LocalDateTime.now());
            memberBenefitService.createBenefit(benefit13);

            MemberBenefit benefit14 = new MemberBenefit();
            benefit14.setMemberLevel(4);
            benefit14.setBenefitName("生日尊享");
            benefit14.setDescription("生日当月可领取500积分奖励和豪华礼品");
            benefit14.setStatus(1);
            benefit14.setCreateTime(LocalDateTime.now());
            benefit14.setUpdateTime(LocalDateTime.now());
            memberBenefitService.createBenefit(benefit14);

            MemberBenefit benefit15 = new MemberBenefit();
            benefit15.setMemberLevel(4);
            benefit15.setBenefitName("尊享活动");
            benefit15.setDescription("可参与白金会员尊享专属活动，包括高端沙龙、品鉴会等");
            benefit15.setStatus(1);
            benefit15.setCreateTime(LocalDateTime.now());
            benefit15.setUpdateTime(LocalDateTime.now());
            memberBenefitService.createBenefit(benefit15);

            MemberBenefit benefit16 = new MemberBenefit();
            benefit16.setMemberLevel(4);
            benefit16.setBenefitName("私人银行服务");
            benefit16.setDescription("享受私人银行级别的专属服务");
            benefit16.setStatus(1);
            benefit16.setCreateTime(LocalDateTime.now());
            benefit16.setUpdateTime(LocalDateTime.now());
            memberBenefitService.createBenefit(benefit16);

            MemberBenefit benefit17 = new MemberBenefit();
            benefit17.setMemberLevel(4);
            benefit17.setBenefitName("全面费用减免");
            benefit17.setDescription("享受大部分银行手续费全免");
            benefit17.setStatus(1);
            benefit17.setCreateTime(LocalDateTime.now());
            benefit17.setUpdateTime(LocalDateTime.now());
            memberBenefitService.createBenefit(benefit17);

            MemberBenefit benefit18 = new MemberBenefit();
            benefit18.setMemberLevel(4);
            benefit18.setBenefitName("机场贵宾厅");
            benefit18.setDescription("可使用机场贵宾厅服务");
            benefit18.setStatus(1);
            benefit18.setCreateTime(LocalDateTime.now());
            benefit18.setUpdateTime(LocalDateTime.now());
            memberBenefitService.createBenefit(benefit18);

            System.out.println("初始化会员权益数据完成");
        }

        // 初始化积分规则数据
        if (pointsRuleService.getAllRules().isEmpty()) {
            // 创建消费积分规则
            PointsRule rule1 = new PointsRule();
            rule1.setName("消费积分规则");
            rule1.setDescription("每消费1元获得1积分");
            rule1.setPointsPerYuan(1);
            rule1.setMinAmount(1);
            rule1.setMultiplier(java.math.BigDecimal.valueOf(1.0));
            rule1.setExpiryDays(365);
            rule1.setStatus(1);
            rule1.setCreateTime(LocalDateTime.now());
            rule1.setUpdateTime(LocalDateTime.now());
            pointsRuleService.createRule(rule1);

            // 创建存款积分规则
            PointsRule rule2 = new PointsRule();
            rule2.setName("存款积分规则");
            rule2.setDescription("每存款100元获得1积分");
            rule2.setPointsPerYuan(0);
            rule2.setMinAmount(100);
            rule2.setMultiplier(java.math.BigDecimal.valueOf(1.0));
            rule2.setExpiryDays(365);
            rule2.setStatus(1);
            rule2.setCreateTime(LocalDateTime.now());
            rule2.setUpdateTime(LocalDateTime.now());
            pointsRuleService.createRule(rule2);

            // 创建活动积分规则
            PointsRule rule3 = new PointsRule();
            rule3.setName("活动积分规则");
            rule3.setDescription("参与活动获得额外积分");
            rule3.setPointsPerYuan(0);
            rule3.setMinAmount(0);
            rule3.setMultiplier(java.math.BigDecimal.valueOf(2.0));
            rule3.setExpiryDays(180);
            rule3.setStatus(1);
            rule3.setCreateTime(LocalDateTime.now());
            rule3.setUpdateTime(LocalDateTime.now());
            pointsRuleService.createRule(rule3);

            System.out.println("初始化积分规则数据完成");
        }

        // 初始化订单数据
        if (userService.getAllUsers().size() > 0 && productService.getAllProducts().size() > 0) {
            User testUser = userService.getAllUsers().get(0);
            List<Product> products = productService.getAllProducts();
            
            // 检查是否已有订单记录
            if (orderService.getUserOrders(testUser.getId()).isEmpty()) {
                // 确保用户有足够的积分
                if (pointsService.getPointsAccount(testUser.getId()) == null) {
                    pointsService.createPointsAccount(testUser.getId());
                }
                pointsService.addPoints(testUser.getId(), 5000, "INCOME", "初始化积分", null, LocalDateTime.now().plusDays(365));
                
                // 创建订单1：兑换话费充值50元
                com.bank.points.dto.ExchangeRequest request1 = new com.bank.points.dto.ExchangeRequest();
                request1.setProductId(products.get(0).getId());
                request1.setQuantity(1);
                orderService.createOrder(testUser.getId(), request1);

                // 创建订单2：兑换星巴克咖啡券
                com.bank.points.dto.ExchangeRequest request2 = new com.bank.points.dto.ExchangeRequest();
                request2.setProductId(products.get(4).getId());
                request2.setQuantity(2);
                orderService.createOrder(testUser.getId(), request2);

                // 创建订单3：兑换品牌保温杯
                com.bank.points.dto.ExchangeRequest request3 = new com.bank.points.dto.ExchangeRequest();
                request3.setProductId(products.get(7).getId());
                request3.setQuantity(1);
                orderService.createOrder(testUser.getId(), request3);

                System.out.println("初始化订单数据完成");
            }
        }

        // 初始化积分奖励数据
        if (userService.getAllUsers().size() > 0) {
            User testUser = userService.getAllUsers().get(0);
            
            // 检查是否已有积分奖励记录
            if (pointsRewardService.getRewardsByUserId(testUser.getId()).isEmpty()) {
                // 创建积分奖励1：注册奖励
                PointsReward reward1 = new PointsReward();
                reward1.setUserId(testUser.getId());
                reward1.setPoints(1000);
                reward1.setRewardReason("注册奖励");
                pointsRewardService.addReward(reward1);

                // 创建积分奖励2：生日奖励
                PointsReward reward2 = new PointsReward();
                reward2.setUserId(testUser.getId());
                reward2.setPoints(500);
                reward2.setRewardReason("生日奖励");
                pointsRewardService.addReward(reward2);

                // 创建积分奖励3：活动奖励
                PointsReward reward3 = new PointsReward();
                reward3.setUserId(testUser.getId());
                reward3.setPoints(200);
                reward3.setRewardReason("活动奖励");
                pointsRewardService.addReward(reward3);

                System.out.println("初始化积分奖励数据完成");
            }
        }

        // 初始化留言数据
        if (userService.getAllUsers().size() > 0) {
            User testUser = userService.getAllUsers().get(0);
            
            // 检查是否已有留言记录
            if (messageService.getMessagesByUserId(testUser.getId()).isEmpty()) {
                // 创建留言1：咨询积分规则
                Message message1 = new Message();
                message1.setUserId(testUser.getId());
                message1.setContent("请问积分规则是怎样的？消费1元可以获得多少积分？");
                message1.setStatus(1);
                message1.setCreateTime(LocalDateTime.now().minusDays(5));
                message1.setUpdateTime(LocalDateTime.now().minusDays(5));
                messageService.addMessage(message1);

                // 创建留言2：咨询兑换流程
                Message message2 = new Message();
                message2.setUserId(testUser.getId());
                message2.setContent("积分兑换商品的流程是怎样的？需要多长时间才能收到商品？");
                message2.setStatus(2);
                message2.setReplyContent("积分兑换流程：1. 登录系统 2. 进入积分兑换页面 3. 选择商品 4. 确认兑换 5. 等待审核。实物商品一般3-5个工作日内发货，虚拟商品即时到账。");
                message2.setReplyTime(LocalDateTime.now().minusDays(4));
                message2.setCreateTime(LocalDateTime.now().minusDays(5));
                message2.setUpdateTime(LocalDateTime.now().minusDays(4));
                messageService.addMessage(message2);

                // 创建留言3：投诉问题
                Message message3 = new Message();
                message3.setUserId(testUser.getId());
                message3.setContent("我上周兑换的商品至今还没有收到，请问是什么原因？");
                message3.setStatus(0);
                message3.setCreateTime(LocalDateTime.now().minusDays(1));
                message3.setUpdateTime(LocalDateTime.now().minusDays(1));
                messageService.addMessage(message3);

                System.out.println("初始化留言数据完成");
            }
        }
    }
    
    /**
     * 创建数据库（如果不存在）
     */
    private void createDatabaseIfNotExists() throws Exception {
        // 从数据源URL中提取数据库名称
        String databaseName = extractDatabaseName(datasourceUrl);
        if (databaseName == null) {
            System.out.println("无法从数据源URL中提取数据库名称");
            return;
        }
        
        // 构建连接到MySQL服务器的URL（不包含数据库名称）
        String serverUrl = datasourceUrl.split("/" + databaseName)[0] + "/";
        
        // 加载MySQL驱动
        Class.forName("com.mysql.cj.jdbc.Driver");
        
        // 连接到MySQL服务器
        try (Connection conn = DriverManager.getConnection(serverUrl, datasourceUsername, datasourcePassword);
             Statement stmt = conn.createStatement()) {
            // 创建数据库（如果不存在）
            String sql = "CREATE DATABASE IF NOT EXISTS " + databaseName + " CHARACTER SET utf8 COLLATE utf8_general_ci";
            stmt.executeUpdate(sql);
            System.out.println("数据库 " + databaseName + " 检查/创建完成");
        }
    }
    
    /**
     * 从数据源URL中提取数据库名称
     */
    private String extractDatabaseName(String url) {
        // 解析URL，提取数据库名称
        // 格式：jdbc:mysql://localhost:3306/bank_points_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
        int startIndex = url.lastIndexOf("/") + 1;
        int endIndex = url.indexOf("?", startIndex);
        if (endIndex == -1) {
            endIndex = url.length();
        }
        return url.substring(startIndex, endIndex);
    }
}
