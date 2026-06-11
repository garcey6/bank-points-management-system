<template>
  <div class="activities">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>活动管理</span>
          <el-button type="primary" @click="showAddDialog">添加活动</el-button>
        </div>
      </template>

      <el-table :data="activities" stripe style="width: 100%">
        <el-table-column prop="activityNo" label="活动编号" width="150" />
        <el-table-column prop="name" label="活动名称" width="200" />
        <el-table-column prop="description" label="活动内容" />
        <el-table-column label="活动时间" width="220">
          <template #default="{ row }">
            {{ row.startTime?.split('T')[0] }} 至 {{ row.endTime?.split('T')[0] }}
          </template>
        </el-table-column>
        <el-table-column prop="minDepositAmount" label="最低存款" width="120">
          <template #default="{ row }">
            {{ row.minDepositAmount }}元
          </template>
        </el-table-column>
        <el-table-column prop="rewardPoints" label="奖励积分" width="100">
          <template #default="{ row }">
            {{ row.rewardPoints }}积分
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="添加时间" width="180" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '进行中' : '已结束' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="text" @click="handleEdit(row)">编辑</el-button>
            <el-button type="text" @click="viewParticipants(row)">参与记录</el-button>
            <el-button type="text" style="color: #f56c6c" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="addDialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="activityForm" label-width="120px">
        <el-form-item label="活动名称">
          <el-input v-model="activityForm.name" placeholder="请输入活动名称"></el-input>
        </el-form-item>
        <el-form-item label="活动内容">
          <el-input
            v-model="activityForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入活动内容"></el-input>
        </el-form-item>
        <el-form-item label="活动时间">
          <el-date-picker
            v-model="activityForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            :unlink-panels="true"
            style="width: 100%">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="最低存款">
          <el-input-number v-model="activityForm.minDepositAmount" :min="0" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="奖励积分">
          <el-input-number v-model="activityForm.rewardPoints" :min="0" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="奖励产品ID（可选）">
          <el-select v-model="activityForm.rewardProductId" placeholder="选择礼品（可选）" filterable clearable style="width: 100%">
            <el-option
              v-for="product in products"
              :key="product.id"
              :label="product.name"
              :value="product.id">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSubmit" :loading="submitLoading">确认</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="participantsDialogVisible" title="活动参与记录" width="80%">
      <el-table :data="participants" stripe>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="depositAmount" label="存款金额" width="120">
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
      </el-table>
      <template #footer>
        <el-button @click="participantsDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getAllBankActivities, createBankActivity, updateBankActivity, deleteBankActivity, getActivityParticipants, getAllProducts } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'AdminActivities',
  data() {
    return {
      activities: [],
      products: [],
      participants: [],
      addDialogVisible: false,
      participantsDialogVisible: false,
      dialogTitle: '添加活动',
      activityForm: {
        name: '',
        description: '',
        dateRange: [],
        minDepositAmount: 0,
        rewardPoints: 0,
        rewardProductId: null
      },
      submitLoading: false,
      isEdit: false,
      editId: null
    }
  },
  async mounted() {
    await this.loadActivities()
    await this.loadProducts()
  },
  methods: {
    async loadActivities() {
      try {
        const res = await getAllBankActivities()
        if (res.code === 200) {
          this.activities = res.data
        }
      } catch (error) {
        console.error('加载活动失败', error)
      }
    },
    async loadProducts() {
      try {
        const res = await getAllProducts()
        if (res.code === 200) {
          this.products = res.data.filter(p => p.status === 1)
        }
      } catch (error) {
        console.error('加载商品失败', error)
      }
    },
    showAddDialog() {
      this.dialogTitle = '添加活动'
      this.isEdit = false
      this.editId = null
      this.activityForm = {
        name: '',
        description: '',
        dateRange: [],
        minDepositAmount: 0,
        rewardPoints: 0,
        rewardProductId: null
      }
      this.addDialogVisible = true
    },
    handleEdit(activity) {
      this.dialogTitle = '编辑活动'
      this.isEdit = true
      this.editId = activity.id
      this.activityForm = {
        name: activity.name,
        description: activity.description,
        dateRange: [activity.startTime?.split(' ')[0], activity.endTime?.split(' ')[0]],
        minDepositAmount: activity.minDepositAmount,
        rewardPoints: activity.rewardPoints,
        rewardProductId: activity.rewardProductId
      }
      this.addDialogVisible = true
    },
    async confirmSubmit() {
      if (!this.activityForm.name) {
        ElMessage.warning('请输入活动名称')
        return
      }
      if (this.activityForm.dateRange.length !== 2) {
        ElMessage.warning('请选择活动时间')
        return
      }
      this.submitLoading = true
      let data = null
      try {
        data = {
          name: this.activityForm.name,
          description: this.activityForm.description,
          startTime: this.activityForm.dateRange[0] + ' 00:00:00',
          endTime: this.activityForm.dateRange[1] + ' 23:59:59',
          minDepositAmount: this.activityForm.minDepositAmount,
          rewardPoints: this.activityForm.rewardPoints,
          rewardProductId: this.activityForm.rewardProductId || null,
          activityType: 'DEPOSIT_POINTS',
          status: 1 // 添加状态字段
        }
        let res
        if (this.isEdit) {
          res = await updateBankActivity({ id: this.editId, ...data })
        } else {
          res = await createBankActivity(data)
        }
        if (res.code === 200) {
          ElMessage.success(this.isEdit ? '更新成功' : '添加成功')
          this.addDialogVisible = false
          await this.loadActivities()
        } else {
          ElMessage.error(res.message)
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '操作失败')
        console.error('提交活动失败:', error)
        console.error('错误响应:', error.response)
        console.error('请求数据:', data)
      } finally {
        this.submitLoading = false
      }
    },
    async viewParticipants(activity) {
      try {
        const res = await getActivityParticipants(activity.id)
        if (res.code === 200) {
          this.participants = res.data
          this.participantsDialogVisible = true
        }
      } catch (error) {
        console.error('加载参与记录失败', error)
      }
    },
    handleDelete(activity) {
      ElMessageBox.confirm('确定要删除这个活动吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await deleteBankActivity(activity.id)
          if (res.code === 200) {
            ElMessage.success('删除成功')
            await this.loadActivities()
          } else {
            ElMessage.error(res.message)
          }
        } catch (error) {
          ElMessage.error(error.response?.data?.message || '删除失败')
        }
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
