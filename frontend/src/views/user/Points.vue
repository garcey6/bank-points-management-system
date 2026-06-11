<template>
  <div class="points">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的积分</span>
        </div>
      </template>
      <div class="points-summary">
        <div class="summary-item">
          <div class="label">总积分</div>
          <div class="value">{{ pointsAccount?.totalPoints || 0 }}</div>
        </div>
        <div class="summary-item">
          <div class="label">可用积分</div>
          <div class="value available">{{ pointsAccount?.availablePoints || 0 }}</div>
        </div>
        <div class="summary-item">
          <div class="label">冻结积分</div>
          <div class="value frozen">{{ pointsAccount?.frozenPoints || 0 }}</div>
        </div>
        <div class="summary-item">
          <div class="label">过期积分</div>
          <div class="value expired">{{ pointsAccount?.expiredPoints || 0 }}</div>
        </div>
      </div>
      <div v-if="pointsAccount?.expiryDate" class="expiry-info">
        <el-alert type="warning" :closable="false">
          <template #title>
            积分有效期：{{ formatDate(pointsAccount.expiryDate) }}
          </template>
        </el-alert>
      </div>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>积分记录</span>
          <el-radio-group v-model="recordType" size="small" @change="loadRecords">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="INCOME">获得</el-radio-button>
            <el-radio-button label="EXPENSE">兑换</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <el-table :data="records" stripe>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.points > 0 ? 'success' : 'danger'">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="points" label="积分" width="120">
          <template #default="{ row }">
            <span :style="{ color: row.points > 0 ? '#67c23a' : '#f56c6c', fontWeight: 'bold' }">
              {{ row.points > 0 ? '+' : '' }}{{ row.points }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column label="时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { getPointsAccount, getPointsRecords, getPointsRecordsByType } from '@/api/user'

export default {
  name: 'UserPoints',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('user') || '{}'),
      pointsAccount: null,
      records: [],
      recordType: ''
    }
  },
  async mounted() {
    await this.loadPointsAccount()
    await this.loadRecords()
  },
  methods: {
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
    async loadRecords() {
      try {
        let res
        if (this.recordType) {
          res = await getPointsRecordsByType(this.user.id, this.recordType)
        } else {
          res = await getPointsRecords(this.user.id)
        }
        if (res.code === 200) {
          this.records = res.data
        }
      } catch (error) {
        console.error('加载积分记录失败', error)
      }
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    formatDateTime(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}`
    }
  }
}
</script>

<style scoped>
.points {
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

.points-summary {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
}

.summary-item {
  text-align: center;
}

.summary-item .label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.summary-item .value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
}

.summary-item .value.available {
  color: #67c23a;
}

.summary-item .value.frozen {
  color: #e6a23c;
}

.summary-item .value.expired {
  color: #f56c6c;
}

.expiry-info {
  margin-top: 15px;
}
</style>
