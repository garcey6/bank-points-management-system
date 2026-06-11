<template>
  <div class="rewards">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>积分奖励管理</span>
          <el-button type="primary" @click="showAddDialog">添加奖励</el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="奖励记录" name="list">
          <el-table :data="rewards" stripe style="width: 100%">
            <el-table-column prop="rewardNo" label="奖励编号" width="180" />
            <el-table-column prop="username" label="用户名" width="120" />
            <el-table-column prop="realName" label="姓名" width="100" />
            <el-table-column prop="phone" label="联系方式" width="130" />
            <el-table-column prop="rewardReason" label="奖励原因" />
            <el-table-column prop="points" label="奖励积分" width="100">
              <template #default="{ row }">
                <span class="points">+{{ row.points }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="添加时间" width="180" />
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="text" @click="handleEdit(row)">编辑</el-button>
                <el-button type="text" style="color: #f56c6c" @click="handleDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="批量添加" name="add">
          <div class="add-section">
            <el-form :model="addForm" label-width="120px" style="max-width: 600px">
              <el-form-item label="选择用户">
                <el-select 
                  v-model="addForm.userId" 
                  placeholder="请选择用户" 
                  filterable
                  style="width: 100%">
                  <el-option
                    v-for="user in users"
                    :key="user.id"
                    :label="`${user.realName} (${user.username})`"
                    :value="user.id">
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="奖励积分">
                <el-input-number v-model="addForm.points" :min="1" style="width: 100%"></el-input-number>
              </el-form-item>
              <el-form-item label="奖励原因">
                <el-input v-model="addForm.rewardReason" placeholder="请输入奖励原因"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleAdd" :loading="addLoading">添加</el-button>
                <el-button @click="addForm = { userId: null, points: 100, rewardReason: '' }">重置</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="editDialogVisible" title="编辑奖励积分" width="500px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="奖励编号">
          <span>{{ editForm.rewardNo }}</span>
        </el-form-item>
        <el-form-item label="用户名">
          <span>{{ editForm.username }}</span>
        </el-form-item>
        <el-form-item label="奖励积分">
          <el-input-number v-model="editForm.points" :min="1" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="奖励原因">
          <span>{{ editForm.rewardReason }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmEdit" :loading="editLoading">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getAllRewards, createReward, updateReward, deleteReward, getAllUsers } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'AdminRewards',
  data() {
    return {
      rewards: [],
      users: [],
      activeTab: 'list',
      addForm: {
        userId: null,
        points: 100,
        rewardReason: ''
      },
      addLoading: false,
      editDialogVisible: false,
      editForm: {},
      editLoading: false
    }
  },
  async mounted() {
    await this.loadRewards()
    await this.loadUsers()
  },
  methods: {
    async loadRewards() {
      try {
        const res = await getAllRewards()
        if (res.code === 200) {
          this.rewards = res.data
        }
      } catch (error) {
        console.error('加载奖励记录失败', error)
      }
    },
    async loadUsers() {
      try {
        const res = await getAllUsers()
        if (res.code === 200) {
          this.users = res.data.filter(u => u.status === 1)
        }
      } catch (error) {
        console.error('加载用户失败', error)
      }
    },
    showAddDialog() {
      this.activeTab = 'add'
    },
    async handleAdd() {
      if (!this.addForm.userId) {
        ElMessage.warning('请选择用户')
        return
      }
      if (!this.addForm.rewardReason) {
        ElMessage.warning('请输入奖励原因')
        return
      }
      this.addLoading = true
      try {
        const res = await createReward(this.addForm)
        if (res.code === 200) {
          ElMessage.success('添加成功')
          this.addForm = { userId: null, points: 100, rewardReason: '' }
          await this.loadRewards()
          this.activeTab = 'list'
        } else {
          ElMessage.error(res.message)
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '添加失败')
      } finally {
        this.addLoading = false
      }
    },
    handleEdit(reward) {
      this.editForm = { ...reward }
      this.editDialogVisible = true
    },
    async confirmEdit() {
      try {
        const res = await updateReward(this.editForm.id, { points: this.editForm.points })
        if (res.code === 200) {
          ElMessage.success('更新成功')
          this.editDialogVisible = false
          await this.loadRewards()
        } else {
          ElMessage.error(res.message)
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '更新失败')
      } finally {
        this.editLoading = false
      }
    },
    handleDelete(reward) {
      ElMessageBox.confirm('确定要删除这条奖励记录吗？删除后将扣回用户积分', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await deleteReward(reward.id)
          if (res.code === 200) {
            ElMessage.success('删除成功')
            await this.loadRewards()
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

.points {
  color: #67c23a;
  font-weight: bold;
}

.add-section {
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}
</style>
