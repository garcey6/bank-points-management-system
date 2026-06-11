<template>
  <div class="orders">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的订单</span>
        </div>
      </template>
      <el-table :data="orders" stripe>
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="points" label="积分" width="120">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold">{{ row.points }}积分</span>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="下单时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { getUserOrders } from '@/api/user'

export default {
  name: 'UserOrders',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('user') || '{}'),
      orders: []
    }
  },
  async mounted() {
    await this.loadOrders()
  },
  methods: {
    async loadOrders() {
      try {
        const res = await getUserOrders(this.user.id)
        if (res.code === 200) {
          this.orders = res.data
        }
      } catch (error) {
        console.error('加载订单失败', error)
      }
    },
    getStatusType(status) {
      const types = {
        'PENDING': 'warning',
        'COMPLETED': 'success',
        'CANCELLED': 'info'
      }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = {
        'PENDING': '待处理',
        'COMPLETED': '已完成',
        'CANCELLED': '已取消'
      }
      return texts[status] || status
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
