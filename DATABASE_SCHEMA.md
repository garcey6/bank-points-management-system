# 银行消费积分管理系统 - 数据库表结构

## 1. 数据库信息

| 配置项 | 值 |
|-------|-----|
| 数据库类型 | MySQL |
| 版本 | 8.0.33 |
| 数据库名称 | bank_points_db |
| 主机地址 | localhost |
| 端口 | 3306 |
| 用户名 | root |
| 密码 | 123456 |
| 字符集 | utf8 |
| 时区 | Asia/Shanghai |
| 连接 URL | jdbc:mysql://localhost:3306/bank_points_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true |

## 2. 表结构

### 2.1 users 表（用户表）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 用户ID |
| username | VARCHAR(50) | NOT NULL, UNIQUE | 用户名 |
| password | VARCHAR(100) | NOT NULL | 密码 |
| salt | VARCHAR(50) | NOT NULL | 密码盐值 |
| real_name | VARCHAR(50) | NOT NULL | 真实姓名 |
| phone | VARCHAR(20) | NOT NULL, UNIQUE | 手机号码 |
| email | VARCHAR(100) | NOT NULL, UNIQUE | 邮箱 |
| bank_card | VARCHAR(100) | | 银行卡号 |
| member_level | INTEGER | NOT NULL | 会员等级 |
| status | INTEGER | NOT NULL | 状态 |
| create_time | DATETIME(6) | NOT NULL | 创建时间 |
| update_time | DATETIME(6) | | 更新时间 |
| avatar | LONGTEXT | | 头像（Base64编码） |

### 2.2 orders 表（订单表）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 订单ID |
| order_no | VARCHAR(50) | NOT NULL, UNIQUE | 订单号 |
| user_id | BIGINT | NOT NULL | 用户ID |
| product_id | BIGINT | NOT NULL | 商品ID |
| points | INTEGER | NOT NULL | 消耗积分 |
| quantity | INTEGER | NOT NULL | 数量 |
| status | VARCHAR(20) | NOT NULL | 状态 |
| receiver_name | VARCHAR(50) | | 收货人姓名 |
| receiver_phone | VARCHAR(20) | | 收货人电话 |
| receiver_address | VARCHAR(200) | | 收货地址 |
| logistics_company | VARCHAR(50) | | 物流公司 |
| tracking_number | VARCHAR(50) | | 物流单号 |
| ship_time | DATETIME(6) | | 发货时间 |
| create_time | DATETIME(6) | NOT NULL | 创建时间 |
| update_time | DATETIME(6) | | 更新时间 |

### 2.3 bank_activity 表（银行活动表）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 活动ID |
| activity_no | VARCHAR(50) | | 活动编号 |
| name | VARCHAR(100) | NOT NULL | 活动名称 |
| description | VARCHAR(500) | | 活动描述 |
| min_deposit_amount | DECIMAL(10,2) | | 最低存款金额 |
| reward_points | INTEGER | | 奖励积分 |
| reward_product_id | BIGINT | | 奖励商品ID |
| activity_type | VARCHAR(50) | NOT NULL, DEFAULT 'DEPOSIT_POINTS' | 活动类型 |
| start_time | DATETIME(6) | | 开始时间 |
| end_time | DATETIME(6) | | 结束时间 |
| status | INTEGER | NOT NULL | 状态 |
| create_time | DATETIME(6) | NOT NULL | 创建时间 |
| update_time | DATETIME(6) | | 更新时间 |

### 2.4 points_rule 表（积分规则表）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 规则ID |
| name | VARCHAR(100) | NOT NULL | 规则名称 |
| description | VARCHAR(500) | | 规则描述 |
| points_per_yuan | INTEGER | NOT NULL | 每元获得积分 |
| min_amount | INTEGER | NOT NULL | 最低金额 |
| multiplier | DECIMAL(3,2) | NOT NULL | 倍数 |
| expiry_days | INTEGER | NOT NULL | 过期天数 |
| status | INTEGER | NOT NULL | 状态 |
| create_time | DATETIME(6) | NOT NULL | 创建时间 |
| update_time | DATETIME(6) | | 更新时间 |

### 2.5 points_reward 表（积分奖励表）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 奖励ID |
| reward_no | VARCHAR(50) | | 奖励编号 |
| user_id | BIGINT | NOT NULL | 用户ID |
| reward_reason | VARCHAR(200) | | 奖励原因 |
| points | INTEGER | NOT NULL | 奖励积分 |
| status | INTEGER | NOT NULL | 状态 |
| create_time | DATETIME(6) | NOT NULL | 创建时间 |
| update_time | DATETIME(6) | | 更新时间 |

### 2.6 message 表（留言表）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 留言ID |
| user_id | BIGINT | NOT NULL | 用户ID |
| content | VARCHAR(500) | | 留言内容 |
| reply_content | VARCHAR(500) | | 回复内容 |
| reply_time | DATETIME(6) | | 回复时间 |
| status | INTEGER | NOT NULL | 状态 |
| create_time | DATETIME(6) | NOT NULL | 创建时间 |
| update_time | DATETIME(6) | | 更新时间 |

### 2.7 product 表（商品表）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 商品ID |
| name | VARCHAR(100) | NOT NULL | 商品名称 |
| description | VARCHAR(500) | | 商品描述 |
| category | VARCHAR(50) | | 商品分类 |
| points | INTEGER | NOT NULL | 兑换积分 |
| stock | INTEGER | NOT NULL | 库存 |
| image_url | VARCHAR(200) | | 图片URL |
| discount | DECIMAL(3,2) | | 折扣 |
| status | INTEGER | NOT NULL | 状态 |
| create_time | DATETIME(6) | NOT NULL | 创建时间 |
| update_time | DATETIME(6) | | 更新时间 |

### 2.8 cart 表（购物车表）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 购物车ID |
| user_id | BIGINT | NOT NULL | 用户ID |
| product_id | BIGINT | NOT NULL | 商品ID |
| quantity | INTEGER | NOT NULL | 数量 |
| create_time | DATETIME(6) | NOT NULL | 创建时间 |
| update_time | DATETIME(6) | | 更新时间 |

### 2.9 member_benefit 表（会员权益表）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 权益ID |
| member_level | INTEGER | NOT NULL | 会员等级 |
| benefit_name | VARCHAR(100) | NOT NULL | 权益名称 |
| description | VARCHAR(500) | | 权益描述 |
| status | INTEGER | NOT NULL | 状态 |
| create_time | DATETIME(6) | NOT NULL | 创建时间 |
| update_time | DATETIME(6) | | 更新时间 |

### 2.10 points_account 表（积分账户表）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 账户ID |
| user_id | BIGINT | NOT NULL | 用户ID |
| total_points | INTEGER | NOT NULL | 总积分 |
| available_points | INTEGER | NOT NULL | 可用积分 |
| frozen_points | INTEGER | NOT NULL | 冻结积分 |
| expired_points | INTEGER | | 过期积分 |
| expiry_date | DATETIME(6) | | 过期日期 |
| total_deposit_amount | DOUBLE | | 总存款金额 |
| create_time | DATETIME(6) | NOT NULL | 创建时间 |
| update_time | DATETIME(6) | | 更新时间 |

### 2.11 points_record 表（积分记录表）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 记录ID |
| user_id | BIGINT | NOT NULL | 用户ID |
| points | INTEGER | NOT NULL | 积分数量 |
| type | VARCHAR(20) | NOT NULL | 类型（INCOME/EXPENSE） |
| description | VARCHAR(500) | | 描述 |
| order_id | BIGINT | | 订单ID |
| expiry_date | DATETIME(6) | | 过期日期 |
| is_expired | BOOLEAN | | 是否已过期 |
| create_time | DATETIME(6) | NOT NULL | 创建时间 |

### 2.12 user_activity 表（用户活动参与表）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 参与ID |
| user_id | BIGINT | NOT NULL | 用户ID |
| activity_id | BIGINT | NOT NULL | 活动ID |
| deposit_amount | DECIMAL(10,2) | | 存款金额 |
| reward_type | VARCHAR(20) | | 奖励类型 |
| status | INTEGER | NOT NULL | 状态 |
| create_time | DATETIME(6) | NOT NULL | 创建时间 |
| update_time | DATETIME(6) | | 更新时间 |

### 2.13 product_review 表（商品评价表）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 评价ID |
| user_id | BIGINT | NOT NULL | 用户ID |
| product_id | BIGINT | NOT NULL | 商品ID |
| order_id | BIGINT | | 订单ID |
| rating | INTEGER | NOT NULL | 评分 |
| content | VARCHAR(500) | | 评价内容 |
| images | VARCHAR(200) | | 评价图片 |
| status | INTEGER | NOT NULL | 状态 |
| create_time | DATETIME(6) | NOT NULL | 创建时间 |
| update_time | DATETIME(6) | | 更新时间 |

### 2.14 member_level_config 表（会员等级配置表）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 配置ID |
| level | INTEGER | NOT NULL | 会员等级 |
| level_name | VARCHAR(50) | NOT NULL | 等级名称 |
| min_points | INTEGER | NOT NULL | 最低积分 |
| min_deposit_amount | DECIMAL(10,2) | NOT NULL | 最低存款金额 |
| discount_rate | DECIMAL(3,2) | NOT NULL | 折扣率 |
| status | INTEGER | NOT NULL | 状态 |
| create_time | DATETIME(6) | NOT NULL | 创建时间 |
| update_time | DATETIME(6) | | 更新时间 |

### 2.15 points_activity 表（积分活动表）
| 字段名 | 数据类型 | 约束 | 描述 |
|-------|---------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 活动ID |
| name | VARCHAR(100) | NOT NULL | 活动名称 |
| description | VARCHAR(500) | | 活动描述 |
| start_time | DATETIME(6) | NOT NULL | 开始时间 |
| end_time | DATETIME(6) | NOT NULL | 结束时间 |
| min_points | INTEGER | NOT NULL | 最低积分 |
| max_points | INTEGER | | 最高积分 |
| multiplier | DECIMAL(3,2) | NOT NULL | 倍数 |
| status | INTEGER | NOT NULL | 状态 |
| create_time | DATETIME(6) | NOT NULL | 创建时间 |
| update_time | DATETIME(6) | | 更新时间 |

## 3. 表关系

### 3.1 主要表关系

| 表名 | 关联表 | 关联字段 | 关系类型 |
|------|-------|----------|----------|
| users | points_account | user_id | 1:1 |
| users | orders | user_id | 1:N |
| users | points_record | user_id | 1:N |
| users | points_reward | user_id | 1:N |
| users | message | user_id | 1:N |
| users | cart | user_id | 1:N |
| users | user_activity | user_id | 1:N |
| product | orders | product_id | 1:N |
| product | cart | product_id | 1:N |
| product | product_review | product_id | 1:N |
| bank_activity | user_activity | id (activity_id) | 1:N |
| orders | points_record | id (order_id) | 1:N |
| orders | product_review | id (order_id) | 1:N |

### 3.2 会员等级与权益关系

| 会员等级 | 等级名称 | 对应权益表 |
|---------|---------|------------|
| 1 | 普通会员 | member_benefit (member_level=1) |
| 2 | 银卡会员 | member_benefit (member_level=2) |
| 3 | 金卡会员 | member_benefit (member_level=3) |
| 4 | 白金会员 | member_benefit (member_level=4) |

## 4. 数据初始化

系统启动时会自动初始化以下数据：

1. **用户数据**：
   - 管理员账号：admin / 123456
   - 测试用户账号：user1 / 123456

2. **商品数据**：
   - 虚拟卡券：话费充值50元、话费充值100元、京东E卡50元、京东E卡100元
   - 服务权益：星巴克咖啡券、电影票
   - 实物商品：精美礼品盒、品牌保温杯

3. **积分规则数据**：
   - 消费积分规则：每消费1元获得1积分
   - 存款积分规则：每存款100元获得1积分
   - 活动积分规则：参与活动获得额外积分

4. **订单数据**：
   - 兑换话费充值50元
   - 兑换星巴克咖啡券
   - 兑换品牌保温杯

5. **积分奖励数据**：
   - 注册奖励：1000积分
   - 生日奖励：500积分
   - 活动奖励：200积分

6. **留言数据**：
   - 咨询积分规则
   - 咨询兑换流程（已回复）
   - 投诉问题（未回复）

7. **会员权益数据**：
   - 普通会员、银卡会员、金卡会员、白金会员的权益

8. **活动数据**：
   - 存款送积分活动
   - 存款送礼品活动
   - 积分兑换优惠活动
   - 会员专享活动

## 5. 数据库索引

| 表名 | 索引名称 | 索引字段 | 类型 |
|------|---------|----------|------|
| users | UK_6dotkott2kjsp8vw4d0m25fb7 | email | UNIQUE |
| users | UK_du5v5sr43g5bfnji4vb8hg5s3 | phone | UNIQUE |
| users | UK_r43af9ap4edm43mmtq01oddj6 | username | UNIQUE |
| orders | UK_g8pohnngqi5x1nask7nff2u7w | order_no | UNIQUE |
