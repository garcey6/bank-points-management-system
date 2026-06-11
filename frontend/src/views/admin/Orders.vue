<template>
  <div class="orders">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>订单管理</span>
          <el-select v-model="statusFilter" placeholder="订单状态" clearable style="width: 150px" @change="loadOrders">
            <el-option label="全部" value=""></el-option>
            <el-option label="待处理" value="PENDING"></el-option>
            <el-option label="已完成" value="COMPLETED"></el-option>
            <el-option label="已取消" value="CANCELLED"></el-option>
          </el-select>
        </div>
      </template>
      <el-table :data="orders" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="productId" label="商品ID" width="80" />
        <el-table-column prop="points" label="积分" width="100" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="logisticsCompany" label="物流公司" width="120" />
        <el-table-column prop="trackingNumber" label="物流单号" width="150" />
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="text" @click="handleEdit(row)">编辑</el-button>
            <el-button type="text" @click="handleShip(row)" v-if="row.status === 'PENDING'">发货</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="editDialogVisible" title="编辑订单" width="500px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="订单号">
          <el-input v-model="editForm.orderNo" disabled></el-input>
        </el-form-item>
        <el-form-item label="用户ID">
          <el-input v-model="editForm.userId" disabled></el-input>
        </el-form-item>
        <el-form-item label="商品ID">
          <el-input v-model="editForm.productId" disabled></el-input>
        </el-form-item>
        <el-form-item label="积分">
          <el-input v-model="editForm.points" disabled></el-input>
        </el-form-item>
        <el-form-item label="数量">
          <el-input v-model="editForm.quantity" disabled></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status" placeholder="请选择">
            <el-option label="待处理" value="PENDING"></el-option>
            <el-option label="已发货" value="SHIPPED"></el-option>
            <el-option label="已完成" value="COMPLETED"></el-option>
            <el-option label="已取消" value="CANCELLED"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpdate" :loading="updateLoading">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="shipDialogVisible" title="订单发货" width="500px">
      <el-form :model="shipForm" :rules="shipRules" ref="shipFormRef" label-width="100px">
        <el-form-item label="订单号">
          <el-input v-model="shipForm.orderNo" disabled></el-input>
        </el-form-item>
        <el-form-item label="物流公司" prop="logisticsCompany">
          <el-select v-model="shipForm.logisticsCompany" placeholder="请选择物流公司">
            <el-option label="顺丰速运" value="顺丰速运"></el-option>
            <el-option label="中通快递" value="中通快递"></el-option>
            <el-option label="圆通速递" value="圆通速递"></el-option>
            <el-option label="申通快递" value="申通快递"></el-option>
            <el-option label="韵达速递" value="韵达速递"></el-option>
            <el-option label="邮政EMS" value="邮政EMS"></el-option>
            <el-option label="京东物流" value="京东物流"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="物流单号" prop="trackingNumber">
          <el-input v-model="shipForm.trackingNumber" placeholder="请输入物流单号"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmShip" :loading="shipLoading">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getAllOrders, updateOrderStatus, shipOrder } from '@/api/admin'
import { ElMessage } from 'element-plus'

export default {
  name: 'AdminOrders',
  data() {
    return {
      orders: [],
      statusFilter: '',
      editDialogVisible: false,
      shipDialogVisible: false,
      editForm: {
        id: null,
        orderNo: '',
        userId: null,
        productId: null,
        points: 0,
        quantity: 0,
        status: ''
      },
      shipForm: {
        orderId: null,
        orderNo: '',
        logisticsCompany: '',
        trackingNumber: ''
      },
      shipRules: {
        logisticsCompany: [
          { required: true, message: '请选择物流公司', trigger: 'change' }
        ],
        trackingNumber: [
          { required: true, message: '请输入物流单号', trigger: 'blur' }
        ]
      },
      updateLoading: false,
      shipLoading: false
    }
  },
  async mounted() {
    await this.loadOrders()
  },
  methods: {
    async loadOrders() {
      try {
        const res = await getAllOrders()
        if (res.code === 200) {
          if (this.statusFilter) {
            this.orders = res.data.filter(order => order.status === this.statusFilter)
          } else {
            this.orders = res.data
          }
        }
      } catch (error) {
        console.error('加载订单失败', error)
      }
    },
    getStatusType(status) {
      const types = {
        'PENDING': 'warning',
        'SHIPPED': 'primary',
        'COMPLETED': 'success',
        'CANCELLED': 'info'
      }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = {
        'PENDING': '待处理',
        'SHIPPED': '已发货',
        'COMPLETED': '已完成',
        'CANCELLED': '已取消'
      }
      return texts[status] || status
    },
    handleEdit(order) {
      this.editForm = { ...order }
      this.editDialogVisible = true
    },
    async handleUpdate() {
      this.updateLoading = true
      try {
        const res = await updateOrderStatus(this.editForm)
        if (res.code === 200) {
          ElMessage.success('更新成功')
          this.editDialogVisible = false
          await this.loadOrders()
        } else {
          ElMessage.error(res.message)
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '更新失败')
      } finally {
        this.updateLoading = false
      }
    },
    handleShip(order) {
      this.shipForm = {
        orderId: order.id,
        orderNo: order.orderNo,
        logisticsCompany: '',
        trackingNumber: ''
      }
      this.shipDialogVisible = true
    },
    async handleConfirmShip() {
      this.$refs.shipFormRef.validate(async (valid) => {
        if (valid) {
          this.shipLoading = true
          try {
            const res = await shipOrder(this.shipForm)
            if (res.code === 200) {
              ElMessage.success('发货成功')
              this.shipDialogVisible = false
              await this.loadOrders()
            } else {
              ElMessage.error(res.message)
            }
          } catch (error) {
            ElMessage.error(error.response?.data?.message || '发货失败')
          } finally {
            this.shipLoading = false
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.orders {
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
