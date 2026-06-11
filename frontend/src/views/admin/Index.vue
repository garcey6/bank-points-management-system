<template>
  <div class="admin-layout">
    <el-container>
      <el-header class="header">
        <div class="header-content">
          <h1>银行消费积分管理系统 - 管理员端</h1>
          <div class="user-info">
            <span>管理员</span>
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
            <el-menu-item index="/admin/dashboard">
              <el-icon><DataLine /></el-icon>
              <span>数据概览</span>
            </el-menu-item>
            <el-menu-item index="/admin/users">
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/products">
              <el-icon><Box /></el-icon>
              <span>商品管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/rules">
              <el-icon><Setting /></el-icon>
              <span>积分规则</span>
            </el-menu-item>
            <el-menu-item index="/admin/orders">
              <el-icon><Document /></el-icon>
              <span>订单管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/benefits">
              <el-icon><Star /></el-icon>
              <span>会员权益</span>
            </el-menu-item>
            <el-menu-item index="/admin/rewards">
              <el-icon><Medal /></el-icon>
              <span>积分奖励</span>
            </el-menu-item>
            <el-menu-item index="/admin/activities">
              <el-icon><Calendar /></el-icon>
              <span>活动管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/messages">
              <el-icon><ChatDotRound /></el-icon>
              <span>留言管理</span>
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
import { DataLine, User, Box, Setting, Document, Star, Medal, Calendar, ChatDotRound } from '@element-plus/icons-vue'

export default {
  name: 'AdminIndex',
  components: {
    DataLine,
    User,
    Box,
    Setting,
    Document,
    Star,
    Medal,
    Calendar,
    ChatDotRound
  },
  data() {
    return {
      activeMenu: '/admin/dashboard'
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
.admin-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #409eff;
  color: white;
  display: flex;
  align-items: center;
  min-height: 60px;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-content h1 {
  margin: 0;
  font-size: 24px;
  background: linear-gradient(90deg, #ffffff, #e6f7ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.el-aside {
  background-color: #545c64;
  min-height: calc(100vh - 60px);
}

.el-main {
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
  min-height: calc(100vh - 60px);
  display: flex;
  flex-direction: column;
}
</style>
