<template>
  <div class="notices">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">活动通知</span>
          <el-badge v-if="unreadCount > 0" :value="unreadCount" class="unread-badge"></el-badge>
        </div>
      </template>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="银行活动" name="activities">
          <div v-if="activities.length > 0" class="activity-list">
            <div v-for="activity in activities" :key="activity.id" class="activity-card">
              <div class="activity-header">
                <div class="activity-icon">
                  <el-icon :size="28"><Present /></el-icon>
                </div>
                <div class="activity-info">
                  <div class="activity-title">{{ activity.name }}</div>
                  <div class="activity-time">
                    {{ activity.startTime?.split('T')[0] }} 至 {{ activity.endTime?.split('T')[0] }}
                  </div>
                </div>
                <el-tag type="success">进行中</el-tag>
              </div>
              <div class="activity-body">
                <p class="activity-desc">{{ activity.description }}</p>
                <div class="reward-info">
                  <el-row :gutter="10">
                    <el-col :span="12" v-if="['DEPOSIT_POINTS', 'DEPOSIT_GIFT'].includes(activity.activityType)">
                      <div class="reward-item">
                        <span class="label">最低存款：</span>
                        <span class="value">{{ activity.minDepositAmount }}元</span>
                      </div>
                    </el-col>
                    <el-col :span="12">
                      <div class="reward-item">
                        <span class="label">奖励：</span>
                        <span class="value highlight">
                          {{ activity.rewardPoints ? activity.rewardPoints + '积分' : (activity.rewardProductId ? '精美礼品' : '参与奖励') }}
                        </span>
                      </div>
                    </el-col>
                  </el-row>
                </div>
              </div>
              <div class="activity-actions">
                <el-button type="primary" @click="showParticipateDialog(activity)">立即参与</el-button>
                <el-button @click="viewDetails(activity)">查看详情</el-button>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无活动" />
        </el-tab-pane>
        
        <el-tab-pane label="我的参与" name="myParticipations">
          <div v-if="myActivities.length > 0">
            <el-table :data="myActivities" style="width: 100%">
              <el-table-column prop="activityName" label="活动名称" />
              <el-table-column prop="depositAmount" label="存款金额">
                <template #default="{ row }">
                {{ row.depositAmount }}元
              </template>
            </el-table-column>
              <el-table-column prop="rewardType" label="奖励类型" width="100">
              <template #default="{ row }">
                <el-tag :type="row.rewardType === 'POINTS' ? 'success' : 'primary'">
                  {{ row.rewardType === 'POINTS' ? '积分' : '礼品' }}
                </el-tag>
              </template>
            </el-table-column>
              <el-table-column prop="createTime" label="参与时间" width="180" />
              <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button type="danger" size="small" @click="cancelParticipation(row)">
                  取消参与
                </el-button>
              </template>
            </el-table-column>
            </el-table>
          </div>
          <el-empty v-else description="您还没有参与任何活动" />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="participateDialogVisible" title="参与活动" width="500px">
      <div v-if="selectedActivity">
        <el-alert
          :title="selectedActivity.name"
          :description="selectedActivity.description"
          type="info"
          :closable="false"
          show-icon
          style="margin-bottom: 20px"
        />
        
        <el-form :model="participateForm" label-width="100px">
          <el-form-item 
            v-if="['DEPOSIT_POINTS', 'DEPOSIT_GIFT'].includes(selectedActivity.activityType)" 
            label="存款金额"
          >
            <el-input-number 
              v-model="participateForm.depositAmount" 
              :min="selectedActivity.minDepositAmount || 0"
              :precision="2"
              style="width: 100%">
            </el-input-number>
          </el-form-item>
          <el-form-item v-if="selectedActivity.rewardPoints || selectedActivity.rewardProductId" label="奖励类型">
            <el-radio-group v-model="participateForm.rewardType">
              <el-radio v-if="selectedActivity.rewardPoints" label="POINTS">积分 {{ selectedActivity.rewardPoints }}</el-radio>
              <el-radio v-if="selectedActivity.rewardProductId" label="PRODUCT">礼品</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="participateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmParticipate" :loading="participateLoading">
          确认参与
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" :title="selectedActivity?.name || '活动详情'" width="600px">
      <div v-if="selectedActivity" class="activity-detail">
        <div class="detail-section">
          <h3>活动详情</h3>
          <p class="activity-description">{{ selectedActivity.description }}</p>
        </div>
        
        <div class="detail-section">
          <h3>活动时间</h3>
          <p>{{ selectedActivity.startTime?.split('T')[0] }} 至 {{ selectedActivity.endTime?.split('T')[0] }}</p>
        </div>
        
        <div class="detail-section">
          <h3>参与要求</h3>
          <ul class="requirement-list">
            <li>最低存款金额：{{ selectedActivity.minDepositAmount }}元</li>
            <li v-if="selectedActivity.rewardPoints">奖励积分：{{ selectedActivity.rewardPoints }}积分</li>
            <li v-if="selectedActivity.rewardProductId">奖励礼品：{{ selectedActivity.rewardProductName || '精美礼品' }}</li>
          </ul>
        </div>
        
        <div class="detail-section">
          <h3>参与方式</h3>
          <p>1. 点击"立即参与"按钮</p>
          <p>2. 输入存款金额（不低于最低要求）</p>
          <p>3. 选择奖励类型（积分或礼品）</p>
          <p>4. 确认参与</p>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="showParticipateDialog(selectedActivity)">立即参与</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getBankActivities, getUserActivities, participateActivity, cancelParticipation } from '@/api/user'
import { ElMessage } from 'element-plus'
import { Present } from '@element-plus/icons-vue'

export default {
  name: 'UserNotices',
  components: {
    Present
  },
  data() {
    return {
      user: JSON.parse(localStorage.getItem('user') || '{}'),
      activities: [],
      myActivities: [],
      activeTab: 'activities',
      unreadCount: 0,
      selectedActivity: null,
      participateDialogVisible: false,
      detailDialogVisible: false,
      participateForm: {
        depositAmount: 0,
        rewardType: 'POINTS'
      },
      participateLoading: false
    }
  },
  async mounted() {
    await this.loadActivities()
    await this.loadMyActivities()
  },
  methods: {
    async loadActivities() {
      try {
        const res = await getBankActivities()
        if (res.code === 200) {
          this.activities = res.data
          this.unreadCount = res.data.length
        }
      } catch (error) {
        console.error('加载活动失败', error)
      }
    },
    async loadMyActivities() {
      try {
        const res = await getUserActivities(this.user.id)
        if (res.code === 200) {
          this.myActivities = res.data
        }
      } catch (error) {
        console.error('加载我的活动失败', error)
      }
    },
    showParticipateDialog(activity) {
      this.selectedActivity = activity
      this.participateForm = {
        depositAmount: ['DEPOSIT_POINTS', 'DEPOSIT_GIFT'].includes(activity.activityType) ? activity.minDepositAmount || 0 : null,
        rewardType: activity.rewardPoints ? 'POINTS' : (activity.rewardProductId ? 'PRODUCT' : 'POINTS')
      }
      this.participateDialogVisible = true
    },
    async confirmParticipate() {
      if (['DEPOSIT_POINTS', 'DEPOSIT_GIFT'].includes(this.selectedActivity.activityType)) {
        if (this.participateForm.depositAmount < (this.selectedActivity.minDepositAmount || 0)) {
          ElMessage.warning('存款金额不满足活动最低要求')
          return
        }
      }
      this.participateLoading = true
      try {
        const res = await participateActivity(this.user.id, {
          activityId: this.selectedActivity.id,
          depositAmount: this.participateForm.depositAmount,
          rewardType: this.participateForm.rewardType
        })
        if (res.code === 200) {
          ElMessage.success('参与成功')
          this.participateDialogVisible = false
          await this.loadMyActivities()
        } else {
          ElMessage.error(res.message)
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '参与失败')
      } finally {
        this.participateLoading = false
      }
    },
    viewDetails(activity) {
      this.selectedActivity = activity
      this.detailDialogVisible = true
    },
    async cancelParticipation(row) {
      try {
        const res = await cancelParticipation(this.user.id, row.activityId)
        if (res.code === 200) {
          ElMessage.success('取消参与成功')
          await this.loadMyActivities()
        } else {
          ElMessage.error(res.message)
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '取消参与失败')
      }
    }
  }
}
</script>

<style scoped>
.notices {
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

.title {
  font-size: 18px;
  font-weight: bold;
}

.unread-badge {
  margin-left: 10px;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.activity-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 20px;
  background: #fff;
  transition: all 0.3s;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.activity-card:hover {
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.activity-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.activity-icon {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.activity-info {
  flex: 1;
}

.activity-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.activity-time {
  font-size: 12px;
  color: #909399;
}

.activity-body {
  margin-bottom: 15px;
}

.activity-desc {
  color: #606266;
  margin-bottom: 15px;
}

.reward-info {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 8px;
}

.reward-item {
  display: flex;
  align-items: center;
}

.reward-item .label {
  color: #909399;
  font-size: 14px;
}

.reward-item .value {
  font-weight: bold;
  color: #303133;
}

.reward-item .value.highlight {
  color: #f56c6c;
  font-size: 18px;
}

.activity-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.activity-detail {
  padding: 10px 0;
}

.activity-detail h3 {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 10px 0;
  padding-bottom: 5px;
  border-bottom: 1px solid #ebeef5;
}

.detail-section {
  margin-bottom: 20px;
}

.activity-description {
  line-height: 1.6;
  color: #606266;
}

.requirement-list {
  margin: 0;
  padding-left: 20px;
  color: #606266;
}

.requirement-list li {
  margin-bottom: 5px;
}
</style>
