<template>
  <div class="users">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索用户"
            style="width: 200px"
            clearable>
            <template #append>
              <el-button icon="Search"></el-button>
            </template>
          </el-input>
        </div>
      </template>
      <el-table :data="filteredUsers" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column label="可用积分" width="120">
          <template #default="{ row }">
            <span class="points-text">{{ getAvailablePoints(row.id) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="memberLevel" label="会员等级" width="100">
          <template #default="{ row }">
            <el-tag>{{ getMemberLevelText(row.memberLevel) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="text" @click="handleEdit(row)">编辑</el-button>
            <el-button type="text" @click="handleEditPoints(row)">编辑积分</el-button>
            <el-button type="text" @click="handleViewRecords(row)">积分记录</el-button>
            <el-button type="text" style="color: #f56c6c" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="recordsDialogVisible" title="用户积分记录" width="80%">
      <el-table :data="userPointsRecords" stripe>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.points > 0 ? 'success' : 'danger'">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="points" label="积分" width="100">
          <template #default="{ row }">
            <span :style="{ color: row.points > 0 ? '#67c23a' : '#f56c6c', fontWeight: 'bold' }">
              {{ row.points > 0 ? '+' : '' }}{{ row.points }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="createTime" label="时间" width="180" />
      </el-table>
      <template #footer>
        <el-button @click="recordsDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="pointsDialogVisible" title="编辑用户积分" width="500px">
      <div v-if="selectedUser">
        <el-alert
          :title="`当前用户：${selectedUser.realName}（${selectedUser.username}）`"
          :description="`当前可用积分：${currentAvailablePoints}积分`"
          type="info"
          :closable="false"
          show-icon
          style="margin-bottom: 20px"
        />
        <el-form :model="pointsForm" label-width="100px">
          <el-form-item label="操作类型">
            <el-radio-group v-model="pointsForm.type">
              <el-radio label="ADD">增加积分</el-radio>
              <el-radio label="REDUCE">扣减积分</el-radio>
              <el-radio label="SET">设置积分</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="积分数量">
            <el-input-number v-model="pointsForm.points" :min="0" style="width: 100%"></el-input-number>
          </el-form-item>
          <el-form-item label="操作说明">
            <el-input v-model="pointsForm.description" placeholder="请输入操作说明"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="pointsDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmUpdatePoints" :loading="pointsLoading">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getAllUsers, updateUserStatus, deleteUser, getUserPointsRecords, updateUserPoints } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'AdminUsers',
  data() {
    return {
      users: [],
      pointsAccounts: {},
      searchKeyword: '',
      editDialogVisible: false,
      recordsDialogVisible: false,
      pointsDialogVisible: false,
      selectedUser: null,
      currentAvailablePoints: 0,
      editForm: {
        id: null,
        username: '',
        realName: '',
        phone: '',
        email: '',
        memberLevel: 1,
        status: 1
      },
      pointsForm: {
        type: 'ADD',
        points: 0,
        description: ''
      },
      rules: {
        realName: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' }
        ]
      },
      updateLoading: false,
      pointsLoading: false,
      userPointsRecords: []
    }
  },
  computed: {
    filteredUsers() {
      if (!this.searchKeyword) return this.users
      const keyword = this.searchKeyword.toLowerCase()
      return this.users.filter(u => 
        u.username.toLowerCase().includes(keyword) ||
        u.realName.toLowerCase().includes(keyword) ||
        u.phone.includes(keyword)
      )
    }
  },
  async mounted() {
    await this.loadUsers()
  },
  methods: {
    async loadUsers() {
      try {
        const res = await getAllUsers()
        if (res.code === 200) {
          this.users = res.data
        }
      } catch (error) {
        console.error('加载用户失败', error)
      }
    },
    getMemberLevelText(level) {
      const levels = ['', '普通会员', '银卡会员', '金卡会员', '白金会员']
      return levels[level] || '普通会员'
    },
    getAvailablePoints(userId) {
      return this.pointsAccounts[userId] || 0
    },
    handleEdit(user) {
      this.editForm = { ...user }
      this.editDialogVisible = true
    },
    async handleUpdate() {
      this.updateLoading = true
      try {
        const res = await updateUserStatus(this.editForm)
        if (res.code === 200) {
          ElMessage.success('更新成功')
          this.editDialogVisible = false
          await this.loadUsers()
        } else {
          ElMessage.error(res.message)
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '更新失败')
      } finally {
        this.updateLoading = false
      }
    },
    handleEditPoints(user) {
      this.selectedUser = user
      this.currentAvailablePoints = this.pointsAccounts[user.id] || 0
      this.pointsForm = {
        type: 'ADD',
        points: 0,
        description: ''
      }
      this.pointsDialogVisible = true
    },
    async confirmUpdatePoints() {
      if (this.pointsForm.points <= 0) {
        ElMessage.warning('请输入有效积分数量')
        return
      }
      this.pointsLoading = true
      try {
        const res = await updateUserPoints(this.selectedUser.id, {
          points: this.pointsForm.points,
          type: this.pointsForm.type,
          description: this.pointsForm.description || '管理员调整'
        })
        if (res.code === 200) {
          ElMessage.success('更新成功')
          this.pointsDialogVisible = false
        } else {
          ElMessage.error(res.message)
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '更新失败')
      } finally {
        this.pointsLoading = false
      }
    },
    handleDelete(id) {
      ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await deleteUser(id)
          if (res.code === 200) {
            ElMessage.success('删除成功')
            await this.loadUsers()
          } else {
            ElMessage.error(res.message)
          }
        } catch (error) {
          ElMessage.error(error.response?.data?.message || '删除失败')
        }
      }).catch(() => {})
    },
    async handleViewRecords(user) {
      try {
        const res = await getUserPointsRecords(user.id)
        if (res.code === 200) {
          this.userPointsRecords = res.data
          this.recordsDialogVisible = true
        }
      } catch (error) {
        console.error('加载积分记录失败', error)
        ElMessage.error('加载积分记录失败')
      }
    }
  }
}
</script>

<style scoped>
.users {
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

.points-text {
  color: #f56c6c;
  font-weight: bold;
}
</style>
