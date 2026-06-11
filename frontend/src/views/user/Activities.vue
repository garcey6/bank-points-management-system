<template>
  <div class="activities">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>积分活动</span>
        </div>
      </template>
      <el-empty v-if="activities.length === 0 && myActivities.length === 0" description="暂无活动"></el-empty>
      
      <!-- 进行中的活动 -->
      <div v-if="activities.length > 0">
        <h3 class="section-title">进行中的活动</h3>
        <el-row :gutter="20">
          <el-col :span="8" v-for="activity in activities" :key="activity.id">
            <el-card class="activity-card" shadow="hover">
              <div class="activity-header">
                <el-tag type="success" size="large">进行中</el-tag>
                <div class="activity-multiplier">{{ activity.multiplier }}倍积分</div>
              </div>
              <div class="activity-name">{{ activity.name }}</div>
              <div class="activity-description">{{ activity.description }}</div>
              <div class="activity-info">
                <div class="info-item">
                  <el-icon><Calendar /></el-icon>
                  <span>活动时间：{{ formatDate(activity.startTime) }} - {{ formatDate(activity.endTime) }}</span>
                </div>
                <div class="info-item">
                  <el-icon><Coin /></el-icon>
                  <span>最低积分：{{ activity.minPoints }}积分</span>
                </div>
                <div class="info-item" v-if="activity.maxPoints">
                  <el-icon><TrendCharts /></el-icon>
                  <span>最高积分：{{ activity.maxPoints }}积分</span>
                </div>
              </div>
              <div class="activity-footer">
                <el-alert type="info" :closable="false">
                  <template #title>
                    活动期间消费或存款可获得{{ activity.multiplier }}倍积分奖励
                  </template>
                </el-alert>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      
      <!-- 我参与的活动 -->
      <div v-if="myActivities.length > 0">
        <h3 class="section-title">我参与的活动</h3>
        <el-row :gutter="20">
          <el-col :span="8" v-for="activity in myActivities" :key="activity.activityId">
            <el-card class="activity-card participated" shadow="hover">
              <div class="activity-header">
                <el-tag type="primary" size="large">已参与</el-tag>
              </div>
              <div class="activity-name">{{ activity.activityName }}</div>
              <div class="activity-info">
                <div class="info-item">
                  <el-icon><Calendar /></el-icon>
                  <span>参与时间：{{ formatDate(activity.createTime) }}</span>
                </div>
                <div class="info-item">
                  <el-icon><Coin /></el-icon>
                  <span>存款金额：{{ activity.depositAmount }}元</span>
                </div>
                <div class="info-item">
                  <el-icon><Present /></el-icon>
                  <span>奖励类型：{{ activity.rewardType === 'POINTS' ? '积分' : '礼品' }}</span>
                </div>
              </div>
              <div class="activity-footer">
                <el-alert type="success" :closable="false">
                  <template #title>
                    您已成功参与此活动
                  </template>
                </el-alert>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getActivities, getUserActivities } from '@/api/user'
import { Calendar, Coin, TrendCharts, Present } from '@element-plus/icons-vue'

export default {
  name: 'UserActivities',
  components: {
    Calendar,
    Coin,
    TrendCharts,
    Present
  },
  data() {
    return {
      activities: [],
      myActivities: [],
      user: JSON.parse(localStorage.getItem('user') || '{}')
    }
  },
  async mounted() {
    await this.loadActivities()
    await this.loadMyActivities()
  },
  methods: {
    async loadActivities() {
      try {
        const res = await getActivities()
        if (res.code === 200) {
          this.activities = res.data
        }
      } catch (error) {
        console.error('加载活动失败', error)
      }
    },
    async loadMyActivities() {
      if (!this.user.id) return
      try {
        const res = await getUserActivities(this.user.id)
        if (res.code === 200) {
          this.myActivities = res.data
        }
      } catch (error) {
        console.error('加载我的活动失败', error)
      }
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hour = String(date.getHours()).padStart(2, '0')
      const minute = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hour}:${minute}`
    }
  }
}
</script>

<style scoped>
.activities {
  flex: 1;
  padding: 20px;
  min-height: calc(100vh - 40px);
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin: 20px 0 10px 0;
  padding-bottom: 5px;
  border-bottom: 1px solid #ebeef5;
}

.activity-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.activity-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
}

.activity-card.participated {
  border: 2px solid #67c23a;
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.activity-multiplier {
  font-size: 24px;
  font-weight: bold;
  color: #f56c6c;
}

.activity-name {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
}

.activity-description {
  font-size: 14px;
  color: #909399;
  margin-bottom: 15px;
  min-height: 40px;
}

.activity-info {
  margin-bottom: 15px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 14px;
  color: #606266;
}

.activity-footer {
  margin-top: 10px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin: 20px 0 10px 0;
  padding-bottom: 5px;
  border-bottom: 1px solid #ebeef5;
}

.activity-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.activity-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
}

.activity-card.participated {
  border: 2px solid #67c23a;
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.activity-multiplier {
  font-size: 24px;
  font-weight: bold;
  color: #f56c6c;
}

.activity-name {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
}

.activity-description {
  font-size: 14px;
  color: #909399;
  margin-bottom: 15px;
  min-height: 40px;
}

.activity-info {
  margin-bottom: 15px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 14px;
  color: #606266;
}

.activity-footer {
  margin-top: 10px;
}
</style>
