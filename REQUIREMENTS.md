# 银行消费积分管理系统 - 依赖需求文档

## 1. 项目概述

银行消费积分管理系统是一个完整的前后端分离应用，用于管理用户积分、积分兑换、活动参与等功能。系统分为前端和后端两部分，前端负责用户界面展示，后端负责业务逻辑处理和数据存储。

## 2. 技术栈

### 前端技术栈
- Vue 3
- Element Plus
- Ant Design Vue
- Axios
- Vue Router
- Vue CLI

### 后端技术栈
- Spring Boot 2.7.14
- Spring Data JPA
- MySQL 8.0.33
- Quartz 定时任务
- Fastjson
- Lombok

## 3. 前端依赖

### 核心依赖
| 依赖名称 | 版本 | 用途 |
|---------|------|------|
| vue | ^3.2.47 | 前端框架 |
| element-plus | ^2.3.3 | UI组件库 |
| ant-design-vue | ^3.2.20 | UI组件库 |
| axios | ^1.3.4 | HTTP请求库 |
| vue-router | ^4.1.6 | 路由管理 |

### 开发依赖
| 依赖名称 | 版本 | 用途 |
|---------|------|------|
| @vue/cli-service | ^5.0.8 | Vue CLI服务 |
| @vue/compiler-sfc | ^3.2.47 | Vue SFC编译器 |

## 4. 后端依赖

### 核心依赖
| 依赖名称 | 版本 | 用途 |
|---------|------|------|
| spring-boot-starter-web | 2.7.14 | Web服务 |
| spring-boot-starter-data-jpa | 2.7.14 | 数据访问 |
| mysql-connector-java | 8.0.33 | MySQL驱动 |
| spring-boot-starter-validation | 2.7.14 | 数据验证 |
| fastjson | 1.2.83 | JSON处理 |
| spring-boot-starter-quartz | 2.7.14 | 定时任务 |
| h2 | runtime | 嵌入式数据库（备用） |

### 工具依赖
| 依赖名称 | 版本 | 用途 |
|---------|------|------|
| lombok | optional | 代码简化 |
| spring-boot-starter-test | test | 测试框架 |

## 5. 环境要求

### 前端环境
- Node.js >= 14.0.0
- npm >= 6.0.0

### 后端环境
- JDK >= 1.8
- Maven >= 3.6.0
- MySQL >= 8.0

## 6. 安装说明

### 方法一：一键安装（推荐）
1. 在项目根目录下找到 `install.bat` 文件

2. 双击运行 `install.bat` 脚本

3. 脚本会自动：
   - 检查环境依赖（Node.js、npm、Java、Maven）
   - 安装前端依赖
   - 构建后端项目
   - 显示安装结果和运行说明

### 方法二：手动安装

#### 前端安装
1. 进入前端目录
   ```bash
   cd frontend
   ```

2. 安装依赖
   ```bash
   npm install
   ```

#### 后端安装
1. 进入后端目录
   ```bash
   cd backend
   ```

2. 构建项目
   ```bash
   mvn clean install
   ```

## 7. 运行说明

### 前端运行
1. 启动开发服务器
   ```bash
   npm run serve
   ```

2. 访问地址
   ```
   http://localhost:8083
   ```

### 后端运行
1. 启动Spring Boot应用
   ```bash
   mvn spring-boot:run
   ```

2. 服务地址
   ```
   http://localhost:8081
   ```

## 8. 数据库配置

### 默认配置 (MySQL数据库)
系统默认使用MySQL数据库，配置如下：

| 配置项 | 值 | 说明 |
|-------|-----|------|
| 数据库类型 | MySQL | 关系型数据库 |
| 版本 | 8.0.33 | MySQL Community Edition |
| 数据库名称 | bank_points_db | 系统使用的数据库名称 |
| 主机地址 | localhost | 数据库服务器地址 |
| 端口 | 3306 | MySQL默认端口 |
| 用户名 | root | 数据库登录用户名 |
| 密码 | 123456 | 数据库登录密码 |
| 字符集 | utf8 | 数据库字符集 |
| 时区 | Asia/Shanghai | 数据库时区设置 |
| 连接URL | jdbc:mysql://localhost:3306/bank_points_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true | 完整的数据库连接URL |

### 配置文件
数据库配置位于 `backend/src/main/resources/application.properties` 文件：

```properties
# 数据库驱动
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
# 数据库连接URL
spring.datasource.url=jdbc:mysql://localhost:3306/bank_points_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
# 数据库用户名
spring.datasource.username=root
# 数据库密码
spring.datasource.password=123456

# JPA配置
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQL57Dialect
```

## 9. 项目结构

### 前端结构
```
frontend/
├── public/              # 静态资源
│   └── images/          # 图片资源
├── src/
│   ├── api/             # API调用
│   ├── components/      # 组件
│   ├── views/           # 页面
│   │   ├── admin/       # 管理端页面
│   │   └── user/        # 用户端页面
│   ├── utils/           # 工具类
│   ├── router/          # 路由配置
│   ├── App.vue          # 根组件
│   └── main.js          # 入口文件
├── package.json         # 依赖配置
└── vue.config.js        # Vue配置
```

### 后端结构
```
backend/
├── src/main/java/com/bank/points/
│   ├── config/          # 配置类
│   ├── controller/      # 控制器
│   ├── entity/          # 实体类
│   ├── repository/      # 数据访问
│   ├── service/         # 业务逻辑
│   └── PointsManagementApplication.java  # 应用入口
├── src/main/resources/
│   └── application.properties  # 应用配置
└── pom.xml              # Maven依赖
```

## 10. 主要功能模块

### 用户端功能
- 首页商品推荐
- 积分兑换页面
- 购物车管理
- 活动通知与参与
- 积分记录查询
- 个人信息管理

### 管理端功能
- 用户管理
- 商品管理
- 积分规则管理
- 活动管理
- 积分奖励管理
- 订单管理

## 11. 注意事项

1. **端口冲突**：确保8081(后端)和8083(前端)端口未被占用
2. **数据库初始化**：首次启动时会自动初始化测试数据
3. **环境变量**：生产环境部署时请修改相关配置
4. **依赖版本**：建议使用文档中指定的依赖版本，避免版本冲突

## 12. 故障排除

### 常见问题

1. **后端启动失败**
   - 检查端口8081是否被占用
   - 检查JDK版本是否为1.8+

2. **前端启动失败**
   - 检查端口8083是否被占用
   - 检查Node.js版本是否满足要求
   - 重新安装依赖：`npm install`

3. **数据库连接失败**
   - 检查MySQL服务是否启动
   - 检查数据库配置是否正确

4. **API调用失败**
   - 检查后端服务是否正常运行
   - 检查前端API地址配置是否正确
