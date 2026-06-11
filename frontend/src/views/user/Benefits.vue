<template>
  <div class="benefits">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>会员权益</span>
        </div>
      </template>
      <div class="member-info">
        <div class="member-level">
          <div class="level-badge" :class="`level-${user.memberLevel}`">
            {{ memberLevelText }}
          </div>
        </div>
        <div class="member-desc">您当前的会员等级，享受以下权益</div>
      </div>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>会员权益列表</span>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="8" v-for="benefit in benefits" :key="benefit.id">
          <el-card class="benefit-card" shadow="hover">
            <div class="benefit-icon">
              <el-icon :size="40"><Star /></el-icon>
            </div>
            <div class="benefit-name">{{ benefit.benefitName }}</div>
            <div class="benefit-description">{{ benefit.description }}</div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <el-card style="margin-top: 20px" v-if="nextLevel">
      <template #header>
        <div class="card-header">
          <span>升级进度</span>
        </div>
      </template>
      <div class="upgrade-progress">
        <div class="current-level">
          <span class="label">当前等级：</span>
          <el-tag type="success">{{ memberLevelText }}</el-tag>
        </div>
        <div class="next-level">
          <span class="label">下一等级：</span>
          <el-tag type="warning">{{ nextLevel.levelName }}</el-tag>
        </div>
        <div class="progress-section">
          <div class="progress-item">
            <div class="progress-label">
              <span>积分进度</span>
              <span class="progress-value">{{ pointsAccount?.totalPoints || 0 }} / {{ nextLevel.minPoints }}</span>
            </div>
            <el-progress 
              :percentage="pointsProgress" 
              :color="progressColor"
              :stroke-width="20"
              :text-inside="true">
            </el-progress>
          </div>
          <div class="progress-item">
            <div class="progress-label">
              <span>存款金额</span>
              <span class="progress-value">¥{{ pointsAccount?.totalDepositAmount || 0 }} / ¥{{ nextLevel.minDepositAmount }}</span>
            </div>
            <el-progress 
              :percentage="depositProgress" 
              :color="progressColor"
              :stroke-width="20"
              :text-inside="true">
            </el-progress>
          </div>
        </div>
        <el-alert type="info" :closable="false" style="margin-top: 15px">
          <template #title>
            升级条件：总积分达到 {{ nextLevel.minPoints }} 且存款金额达到 ¥{{ nextLevel.minDepositAmount }}
          </template>
        </el-alert>
      </div>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>会员等级说明</span>
        </div>
      </template>
      <el-steps :active="user.memberLevel - 1" align-center>
        <el-step title="普通会员" description="基础会员权益"></el-step>
        <el-step title="银卡会员" description="享受1.1倍积分"></el-step>
        <el-step title="金卡会员" description="享受1.2倍积分"></el-step>
        <el-step title="白金会员" description="享受1.5倍积分"></el-step>
      </el-steps>
    </el-card>
  </div>
</template>

<script>
import { getBenefits, getPointsAccount } from '@/api/user'
import { Star } from '@element-plus/icons-vue'
import { getNextLevel } from '@/api/user'

export default {
  name: 'UserBenefits',
  components: {
    Star
  },
  data() {
    return {
      user: JSON.parse(localStorage.getItem('user') || '{}'),
      benefits: [],
      nextLevel: null,
      pointsAccount: null
    }
  },
  computed: {
    memberLevelText() {
      const levels = ['', '普通会员', '银卡会员', '金卡会员', '白金会员']
      return levels[this.user.memberLevel] || '普通会员'
    },
    pointsProgress() {
      if (!this.nextLevel || !this.pointsAccount) return 0
      const progress = ((this.pointsAccount.totalPoints || 0) / this.nextLevel.minPoints) * 100
      return Math.min(Math.round(progress), 100)
    },
    depositProgress() {
      if (!this.nextLevel || !this.pointsAccount) return 0
      const progress = ((this.pointsAccount.totalDepositAmount || 0) / this.nextLevel.minDepositAmount) * 100
      return Math.min(Math.round(progress), 100)
    },
    progressColor() {
      const progress = Math.min(this.pointsProgress, this.depositProgress)
      if (progress < 30) return '#f56c6c'
      if (progress < 70) return '#e6a23c'
      return '#67c23a'
    }
  },
  async mounted() {
    await this.loadBenefits()
    await this.loadPointsAccount()
    await this.loadNextLevel()
  },
  methods: {
    async loadBenefits() {
      try {
        const res = await getBenefits(this.user.memberLevel)
        if (res.code === 200) {
          this.benefits = res.data
        }
      } catch (error) {
        console.error('加载会员权益失败', error)
      }
    },
    async loadPointsAccount() {
      try {
        const res = await getPointsAccount(this.user.id)
        if (res.code === 200) {
          this.pointsAccount = res.data
        }
      } catch (error) {
        console.error('加载积分账户失败', error)
      }
    },
    async loadNextLevel() {
      try {
        const res = await getNextLevel(this.user.id)
        if (res.code === 200) {
          this.nextLevel = res.data
        }
      } catch (error) {
        console.error('加载下一等级信息失败', error)
      }
    }
  }
}
</script>

<style scoped>
.benefits {
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

.member-info {
  text-align: center;
  padding: 20px 0;
}

.member-level {
  margin-bottom: 10px;
}

.level-badge {
  display: inline-block;
  padding: 10px 30px;
  border-radius: 20px;
  font-size: 18px;
  font-weight: bold;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: all 0.3s;
}

.level-badge:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
}

.level-1 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.level-2 {
  background: linear-gradient(135deg, #c0c0c0 0%, #e8e8e8 100%);
  color: #333;
}

.level-3 {
  background: linear-gradient(135deg, #ffd700 0%, #ffec8b 100%);
  color: #333;
}

.level-4 {
  background: linear-gradient(135deg, #e5e4e2 0%, #f5f5f5 100%);
  color: #333;
}

.member-desc {
  color: #909399;
  font-size: 14px;
}

.benefit-card {
  text-align: center;
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.benefit-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
}

.benefit-icon {
  color: #e6a23c;
  margin-bottom: 10px;
}

.benefit-name {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.benefit-description {
  font-size: 14px;
  color: #909399;
}

.upgrade-progress {
  padding: 20px 0;
}

.current-level, .next-level {
  margin-bottom: 20px;
}

.current-level .label, .next-level .label {
  margin-right: 10px;
  font-weight: bold;
}

.progress-section {
  margin: 20px 0;
}

.progress-item {
  margin-bottom: 20px;
}

.progress-label {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-weight: bold;
}

.progress-value {
  color: #409eff;
}
</style>
