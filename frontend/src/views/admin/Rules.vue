<template>
  <div class="rules">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>积分规则管理</span>
          <el-button type="primary" @click="handleAdd">添加规则</el-button>
        </div>
      </template>
      <el-table :data="rules" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="规则名称" width="150" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="pointsPerYuan" label="每元积分" width="100" />
        <el-table-column prop="minAmount" label="最低消费" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="text" @click="handleEdit(row)">编辑</el-button>
            <el-button type="text" @click="handleToggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button type="text" style="color: #f56c6c" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="editDialogVisible" :title="isEdit ? '编辑规则' : '添加规则'" width="600px">
      <el-form :model="editForm" :rules="formRules" ref="editFormRef" label-width="100px">
        <el-form-item label="规则名称" prop="name">
          <el-input v-model="editForm.name"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="editForm.description" type="textarea" :rows="3"></el-input>
        </el-form-item>
        <el-form-item label="每元积分" prop="pointsPerYuan">
          <el-input-number v-model="editForm.pointsPerYuan" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="最低消费" prop="minAmount">
          <el-input-number v-model="editForm.minAmount" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="积分倍数" prop="multiplier">
          <el-input-number v-model="editForm.multiplier" :min="0" :step="0.1"></el-input-number>
        </el-form-item>
        <el-form-item label="过期天数" prop="expiryDays">
          <el-input-number v-model="editForm.expiryDays" :min="1"></el-input-number>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="editForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getAllRules, createRule, updateRule, deleteRule } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'AdminRules',
  data() {
    return {
      rules: [],
      editDialogVisible: false,
      isEdit: false,
      editForm: {
        id: null,
        name: '',
        description: '',
        pointsPerYuan: 0,
        minAmount: 0,
        multiplier: 1.0,
        expiryDays: 365,
        status: 1
      },
      formRules: {
        name: [
          { required: true, message: '请输入规则名称', trigger: 'blur' }
        ],
        pointsPerYuan: [
          { required: true, message: '请输入每元积分', trigger: 'blur' }
        ],
        minAmount: [
          { required: true, message: '请输入最低消费', trigger: 'blur' }
        ],
        multiplier: [
          { required: true, message: '请输入积分倍数', trigger: 'blur' }
        ],
        expiryDays: [
          { required: true, message: '请输入过期天数', trigger: 'blur' }
        ]
      },
      submitLoading: false
    }
  },
  async mounted() {
    await this.loadRules()
  },
  methods: {
    async loadRules() {
      try {
        const res = await getAllRules()
        if (res.code === 200) {
          this.rules = res.data
        }
      } catch (error) {
        console.error('加载规则失败', error)
      }
    },
    handleAdd() {
      this.isEdit = false
      this.editForm = {
        id: null,
        name: '',
        description: '',
        pointsPerYuan: 0,
        minAmount: 0,
        multiplier: 1.0,
        expiryDays: 365,
        status: 1
      }
      this.editDialogVisible = true
    },
    handleEdit(rule) {
      this.isEdit = true
      this.editForm = { ...rule }
      this.editDialogVisible = true
    },
    async handleSubmit() {
      this.$refs.editFormRef.validate(async (valid) => {
        if (valid) {
          this.submitLoading = true
          try {
            let res
            if (this.isEdit) {
              res = await updateRule(this.editForm)
            } else {
              res = await createRule(this.editForm)
            }
            if (res.code === 200) {
              ElMessage.success(this.isEdit ? '更新成功' : '添加成功')
              this.editDialogVisible = false
              await this.loadRules()
            } else {
              ElMessage.error(res.message)
            }
          } catch (error) {
            ElMessage.error(error.response?.data?.message || '操作失败')
          } finally {
            this.submitLoading = false
          }
        }
      })
    },
    async handleToggleStatus(rule) {
      try {
        const newStatus = rule.status === 1 ? 0 : 1
        const res = await updateRule({ ...rule, status: newStatus })
        if (res.code === 200) {
          ElMessage.success(newStatus === 1 ? '启用成功' : '禁用成功')
          await this.loadRules()
        } else {
          ElMessage.error(res.message)
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '操作失败')
      }
    },
    handleDelete(id) {
      ElMessageBox.confirm('确定要删除该规则吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await deleteRule(id)
          if (res.code === 200) {
            ElMessage.success('删除成功')
            await this.loadRules()
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
.rules {
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
</style>
