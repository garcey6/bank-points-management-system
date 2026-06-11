<template>
  <div class="products">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>商品管理</span>
          <el-button type="primary" @click="handleAdd">添加商品</el-button>
        </div>
      </template>
      <el-table :data="products" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="商品名称" width="150" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="points" label="积分" width="100" />
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="text" @click="handleEdit(row)">编辑</el-button>
            <el-button type="text" @click="handleToggleStatus(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button type="text" style="color: #f56c6c" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="editDialogVisible" :title="isEdit ? '编辑商品' : '添加商品'" width="600px">
      <el-form :model="editForm" :rules="rules" ref="editFormRef" label-width="100px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="editForm.name"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="editForm.description" type="textarea" :rows="3"></el-input>
        </el-form-item>
        <el-form-item label="积分" prop="points">
          <el-input-number v-model="editForm.points" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="editForm.stock" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="图片URL">
          <el-input v-model="editForm.imageUrl"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="editForm.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
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
import { getAllProducts, createProduct, updateProduct, deleteProduct } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'AdminProducts',
  data() {
    return {
      products: [],
      editDialogVisible: false,
      isEdit: false,
      editForm: {
        id: null,
        name: '',
        description: '',
        points: 0,
        stock: 0,
        imageUrl: '',
        status: 1
      },
      rules: {
        name: [
          { required: true, message: '请输入商品名称', trigger: 'blur' }
        ],
        points: [
          { required: true, message: '请输入积分', trigger: 'blur' }
        ],
        stock: [
          { required: true, message: '请输入库存', trigger: 'blur' }
        ]
      },
      submitLoading: false
    }
  },
  async mounted() {
    await this.loadProducts()
  },
  methods: {
    async loadProducts() {
      try {
        const res = await getAllProducts()
        if (res.code === 200) {
          this.products = res.data
        }
      } catch (error) {
        console.error('加载商品失败', error)
      }
    },
    handleAdd() {
      this.isEdit = false
      this.editForm = {
        id: null,
        name: '',
        description: '',
        points: 0,
        stock: 0,
        imageUrl: '',
        status: 1
      }
      this.editDialogVisible = true
    },
    handleEdit(product) {
      this.isEdit = true
      this.editForm = { ...product }
      this.editDialogVisible = true
    },
    async handleSubmit() {
      this.$refs.editFormRef.validate(async (valid) => {
        if (valid) {
          this.submitLoading = true
          try {
            let res
            if (this.isEdit) {
              res = await updateProduct(this.editForm)
            } else {
              res = await createProduct(this.editForm)
            }
            if (res.code === 200) {
              ElMessage.success(this.isEdit ? '更新成功' : '添加成功')
              this.editDialogVisible = false
              await this.loadProducts()
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
    async handleToggleStatus(product) {
      try {
        const newStatus = product.status === 1 ? 0 : 1
        const res = await updateProduct({ ...product, status: newStatus })
        if (res.code === 200) {
          ElMessage.success(newStatus === 1 ? '上架成功' : '下架成功')
          await this.loadProducts()
        } else {
          ElMessage.error(res.message)
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '操作失败')
      }
    },
    handleDelete(id) {
      ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await deleteProduct(id)
          if (res.code === 200) {
            ElMessage.success('删除成功')
            await this.loadProducts()
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
.products {
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
