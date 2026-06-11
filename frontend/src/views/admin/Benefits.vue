<template>
  <div class="benefits">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>会员权益管理</span>
          <el-button type="primary" @click="handleAdd">添加权益</el-button>
        </div>
      </template>
      <el-table :data="benefits" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="memberLevel" label="会员等级" width="100">
          <template #default="{ row }">
            <el-tag>{{ getMemberLevelText(row.memberLevel) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="benefitName" label="权益名称" width="150" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
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

    <el-dialog v-model="editDialogVisible" :title="isEdit ? '编辑权益' : '添加权益'" width="600px">
      <el-form :model="editForm" :rules="rules" ref="editFormRef" label-width="100px">
        <el-form-item label="会员等级" prop="memberLevel">
          <el-select v-model="editForm.memberLevel" placeholder="请选择">
            <el-option label="普通会员" :value="1"></el-option>
            <el-option label="银卡会员" :value="2"></el-option>
            <el-option label="金卡会员" :value="3"></el-option>
            <el-option label="白金会员" :value="4"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="权益名称" prop="benefitName">
          <el-input v-model="editForm.benefitName"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="editForm.description" type="textarea" :rows="3"></el-input>
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
import { getAllBenefits, createBenefit, updateBenefit, deleteBenefit } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'AdminBenefits',
  data() {
    return {
      benefits: [],
      editDialogVisible: false,
      isEdit: false,
      editForm: {
        id: null,
        memberLevel: 1,
        benefitName: '',
        description: '',
        status: 1
      },
      rules: {
        memberLevel: [
          { required: true, message: '请选择会员等级', trigger: 'change' }
        ],
        benefitName: [
          { required: true, message: '请输入权益名称', trigger: 'blur' }
        ]
      },
      submitLoading: false
    }
  },
  async mounted() {
    await this.loadBenefits()
  },
  methods: {
    async loadBenefits() {
      try {
        const res = await getAllBenefits()
        if (res.code === 200) {
          this.benefits = res.data
        }
      } catch (error) {
        console.error('加载权益失败', error)
      }
    },
    getMemberLevelText(level) {
      const levels = ['', '普通会员', '银卡会员', '金卡会员', '白金会员']
      return levels[level] || '普通会员'
    },
    handleAdd() {
      this.isEdit = false
      this.editForm = {
        id: null,
        memberLevel: 1,
        benefitName: '',
        description: '',
        status: 1
      }
      this.editDialogVisible = true
    },
    handleEdit(benefit) {
      this.isEdit = true
      this.editForm = { ...benefit }
      this.editDialogVisible = true
    },
    async handleSubmit() {
      this.$refs.editFormRef.validate(async (valid) => {
        if (valid) {
          this.submitLoading = true
          try {
            let res
            if (this.isEdit) {
              res = await updateBenefit(this.editForm)
            } else {
              res = await createBenefit(this.editForm)
            }
            if (res.code === 200) {
              ElMessage.success(this.isEdit ? '更新成功' : '添加成功')
              this.editDialogVisible = false
              await this.loadBenefits()
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
    async handleToggleStatus(benefit) {
      try {
        const newStatus = benefit.status === 1 ? 0 : 1
        const res = await updateBenefit({ ...benefit, status: newStatus })
        if (res.code === 200) {
          ElMessage.success(newStatus === 1 ? '启用成功' : '禁用成功')
          await this.loadBenefits()
        } else {
          ElMessage.error(res.message)
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '操作失败')
      }
    },
    handleDelete(id) {
      ElMessageBox.confirm('确定要删除该权益吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await deleteBenefit(id)
          if (res.code === 200) {
            ElMessage.success('删除成功')
            await this.loadBenefits()
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
</style>
