<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #409eff">
              <el-icon :size="30"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">用户总数</div>
              <div class="stat-value">{{ stats.userCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #67c23a">
              <el-icon :size="30"><Coin /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">商品总数</div>
              <div class="stat-value">{{ stats.productCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #e6a23c">
              <el-icon :size="30"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">订单总数</div>
              <div class="stat-value">{{ stats.orderCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #f56c6c">
              <el-icon :size="30"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">总积分发放</div>
              <div class="stat-value">{{ stats.totalPoints }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最新用户</span>
            </div>
          </template>
          <el-table :data="recentUsers" stripe>
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="realName" label="真实姓名" />
            <el-table-column prop="phone" label="手机号" />
            <el-table-column prop="memberLevel" label="会员等级" width="100">
              <template #default="{ row }">
                <el-tag>{{ getMemberLevelText(row.memberLevel) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="注册时间" width="180" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最新订单</span>
            </div>
          </template>
          <el-table :data="recentOrders" stripe>
            <el-table-column prop="orderNo" label="订单号" width="150" />
            <el-table-column prop="userId" label="用户ID" width="80" />
            <el-table-column prop="points" label="积分" width="100" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="下单时间" width="180" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>会员等级分布</span>
            </div>
          </template>
          <div class="level-distribution">
            <div v-for="(count, level) in stats.levelDistribution" :key="level" class="level-item">
              <div class="level-name">{{ getMemberLevelText(parseInt(level)) }}</div>
              <el-progress :percentage="getLevelPercentage(count)" :color="getLevelColor(parseInt(level))"></el-progress>
              <div class="level-count">{{ count }}人</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getAllUsers, getAllOrders, getStatistics } from '@/api/admin'
import { User, Coin, Document, TrendCharts } from '@element-plus/icons-vue'

export default {
  name: 'AdminDashboard',
  components: {
    User,
    Coin,
    Document,
    TrendCharts
  },
  data() {
    return {
      stats: {
        userCount: 0,
        productCount: 0,
        orderCount: 0,
        totalPoints: 0,
        levelDistribution: {}
      },
      recentUsers: [],
      recentOrders: []
    }
  },
  async mounted() {
    await this.loadStats()
  },
  methods: {
    async loadStats() {
      try {
        const [usersRes, ordersRes, statsRes] = await Promise.all([getAllUsers(), getAllOrders(), getStatistics()])
        if (usersRes.code === 200) {
          this.stats.userCount = usersRes.data.length
          this.recentUsers = usersRes.data.slice(0, 5)
        }
        if (ordersRes.code === 200) {
          this.stats.orderCount = ordersRes.data.length
          this.recentOrders = ordersRes.data.slice(0, 5)
        }
        if (statsRes.code === 200) {
          this.stats = { ...this.stats, ...statsRes.data }
        }
      } catch (error) {
        console.error('加载数据失败', error)
      }
    },
    getStatusType(status) {
      const types = {
        'PENDING': 'warning',
        'SHIPPED': 'primary',
        'COMPLETED': 'success',
        'CANCELLED': 'info'
      }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = {
        'PENDING': '待处理',
        'SHIPPED': '已发货',
        'COMPLETED': '已完成',
        'CANCELLED': '已取消'
      }
      return texts[status] || status
    },
    getMemberLevelText(level) {
      const levels = ['', '普通会员', '银卡会员', '金卡会员', '白金会员']
      return levels[level] || '普通会员'
    },
    getLevelPercentage(count) {
      const total = Object.values(this.stats.levelDistribution).reduce((sum, val) => sum + val, 0)
      return total > 0 ? Math.round((count / total) * 100) : 0
    },
    getLevelColor(level) {
      const colors = {
        1: '#909399',
        2: '#409eff',
        3: '#e6a23c',
        4: '#f56c6c'
      }
      return colors[level] || '#909399'
    }
  }
}
</script>

<style scoped>
.dashboard {
  flex: 1;
  padding: 20px;
  min-height: calc(100vh - 40px);
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.stat-card {
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.level-distribution {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  padding: 20px 0;
}

.level-item {
  text-align: center;
}

.level-name {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
}

.level-count {
  margin-top: 8px;
  font-size: 14px;
  color: #909399;
}
</style>
