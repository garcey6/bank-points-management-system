<template>
  <div class="user-layout">
    <el-container>
      <el-header class="header">
        <div class="header-content">
          <h1>银行消费积分管理系统</h1>
          <div class="user-info">
            <el-avatar :size="32" :src="user.avatar">
              {{ user.realName?.charAt(0) || 'U' }}
            </el-avatar>
            <span>欢迎，{{ user.realName }}</span>
            <el-button type="danger" plain @click="handleLogout">退出</el-button>
          </div>
        </div>
      </el-header>
      <el-container>
        <el-aside width="200px">
          <el-menu
            :default-active="activeMenu"
            router
            background-color="#545c64"
            text-color="#fff"
            active-text-color="#ffd04b">
            <el-menu-item index="/user/home">
              <el-icon><House /></el-icon>
              <span>首页</span>
            </el-menu-item>
            <el-menu-item index="/user/points">
              <el-icon><Coin /></el-icon>
              <span>我的积分</span>
            </el-menu-item>
            <el-menu-item index="/user/exchange">
              <el-icon><ShoppingCart /></el-icon>
              <span>积分兑换</span>
            </el-menu-item>
            <el-menu-item index="/user/orders">
              <el-icon><Document /></el-icon>
              <span>我的订单</span>
            </el-menu-item>
            <el-menu-item index="/user/benefits">
              <el-icon><Star /></el-icon>
              <span>会员权益</span>
            </el-menu-item>
            <el-menu-item index="/user/activities">
              <el-icon><TrendCharts /></el-icon>
              <span>积分活动</span>
            </el-menu-item>
            <el-menu-item index="/user/cart">
              <el-icon><Goods /></el-icon>
              <span>购物车</span>
            </el-menu-item>
            <el-menu-item index="/user/notices">
              <el-icon><Bell /></el-icon>
              <span>活动通知</span>
            </el-menu-item>
            <el-menu-item index="/user/messages">
              <el-icon><ChatDotRound /></el-icon>
              <span>留言反馈</span>
            </el-menu-item>
            <el-menu-item index="/user/profile">
              <el-icon><User /></el-icon>
              <span>个人中心</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus'
import { House, Coin, ShoppingCart, Document, Star, User, TrendCharts, Goods, Bell, ChatDotRound } from '@element-plus/icons-vue'

export default {
  name: 'UserIndex',
  components: {
    House,
    Coin,
    ShoppingCart,
    Document,
    Star,
    User,
    TrendCharts,
    Goods,
    Bell,
    ChatDotRound
  },
  data() {
    return {
      user: JSON.parse(localStorage.getItem('user') || '{}'),
      activeMenu: '/user/home'
    }
  },
  watch: {
    $route(to) {
      this.activeMenu = to.path
    }
  },
  methods: {
    handleLogout() {
      localStorage.removeItem('user')
      ElMessage.success('退出成功')
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.user-layout {
  height: 100vh;
}

.header {
  background-color: #409eff;
  color: white;
  display: flex;
  align-items: center;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content h1 {
  margin: 0;
  font-size: 24px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-info span {
  margin-right: 10px;
}

.el-aside {
  background-color: #545c64;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
