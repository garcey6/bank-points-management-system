<template>
  <div class="cart">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">我的购物车</span>
          <el-button 
            v-if="cartItems.length > 0" 
            type="danger" 
            size="small" 
            @click="handleClearCart">
            清空购物车
          </el-button>
        </div>
      </template>
      
      <div v-if="cartItems.length > 0">
        <el-table :data="cartItems" style="width: 100%">
          <el-table-column label="商品信息">
            <template #default="{ row }">
              <div class="product-cell">
                <el-image 
                  :src="getProductImageUrl(row.productName)" 
                  fit="cover"
                  style="width: 80px; height: 80px; border-radius: 4px; margin-right: 12px;">
                </el-image>
                <div class="product-info">
                  <div class="product-name">{{ row.productName }}</div>
                  <div class="product-stock">库存: {{ row.productStock }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="所需积分" width="120">
            <template #default="{ row }">
              <span class="points">{{ row.productPoints }}积分</span>
            </template>
          </el-table-column>
          <el-table-column label="数量" width="150">
            <template #default="{ row }">
              <el-input-number 
                v-model="row.quantity" 
                :min="1" 
                :max="row.productStock"
                size="small"
                @change="handleQuantityChange(row)">
              </el-input-number>
            </template>
          </el-table-column>
          <el-table-column label="小计" width="120">
            <template #default="{ row }">
              <span class="total-points">{{ row.totalPoints }}积分</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button type="danger" size="small" link @click="handleRemove(row.id)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="cart-footer">
          <div class="cart-summary">
            <span>共 {{ cartItems.length }} 件商品</span>
            <span class="total">合计: <em>{{ totalPoints }}</em> 积分</span>
          </div>
          <el-button type="primary" size="large" @click="handleExchangeAll" :disabled="!canExchange">
            一键兑换
          </el-button>
        </div>
      </div>

      <el-empty v-else description="购物车是空的">
        <el-button type="primary" @click="$router.push('/user/exchange')">去逛逛</el-button>
      </el-empty>
    </el-card>

    <el-dialog v-model="exchangeDialogVisible" title="确认兑换" width="500px">
      <el-alert
        title="兑换确认"
        :description="`您确定要兑换这些商品吗？将扣除 ${totalPoints} 积分。`"
        type="info"
        :closable="false"
        show-icon
        style="margin-bottom: 20px"
      />
      <el-form label-width="80px">
        <el-form-item label="可用积分">
          <span>{{ pointsAccount?.availablePoints || 0 }}积分</span>
        </el-form-item>
        <el-form-item label="收货地址">
          <el-input 
            v-model="shippingForm.address" 
            placeholder="请输入收货地址"
            type="textarea"
            :rows="3">
          </el-input>
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="shippingForm.contactName" placeholder="请输入联系人姓名"></el-input>
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="shippingForm.contactPhone" placeholder="请输入联系电话"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="exchangeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmExchange" :loading="exchangeLoading">
          确认兑换
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getCart, updateCartItem, removeFromCart, clearCart, getPointsAccount, exchangeProduct } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'UserCart',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('user') || '{}'),
      cartItems: [],
      pointsAccount: null,
      exchangeDialogVisible: false,
      exchangeLoading: false,
      shippingForm: {
        address: '',
        contactName: '',
        contactPhone: ''
      }
    }
  },
  computed: {
    totalPoints() {
      return this.cartItems.reduce((sum, item) => sum + item.totalPoints, 0)
    },
    canExchange() {
      return this.cartItems.length > 0 && 
             this.pointsAccount && 
             this.totalPoints <= (this.pointsAccount.availablePoints || 0)
    }
  },
  async mounted() {
    await this.loadCart()
    await this.loadPointsAccount()
  },
  methods: {
    async loadCart() {
      try {
        const res = await getCart(this.user.id)
        if (res.code === 200) {
          this.cartItems = res.data || []
        }
      } catch (error) {
        console.error('加载购物车失败', error)
      }
    },
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
    async handleQuantityChange(row) {
      try {
        const res = await updateCartItem(row.id, { quantity: row.quantity })
        if (res.code === 200) {
          await this.loadCart()
        }
      } catch (error) {
        ElMessage.error('更新失败')
        console.error(error)
      }
    },
    async handleRemove(cartId) {
      try {
        await ElMessageBox.confirm('确定要删除这个商品吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        const res = await removeFromCart(cartId)
        if (res.code === 200) {
          ElMessage.success('删除成功')
          await this.loadCart()
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    },
    async handleClearCart() {
      try {
        await ElMessageBox.confirm('确定要清空购物车吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        const res = await clearCart(this.user.id)
        if (res.code === 200) {
          ElMessage.success('清空成功')
          await this.loadCart()
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('操作失败')
        }
      }
    },
    handleExchangeAll() {
      this.exchangeDialogVisible = true
    },
    async confirmExchange() {
      if (!this.shippingForm.address || !this.shippingForm.contactName || !this.shippingForm.contactPhone) {
        ElMessage.warning('请填写完整的收货信息')
        return
      }
      
      this.exchangeLoading = true
      try {
        for (const item of this.cartItems) {
          await exchangeProduct(this.user.id, {
            productId: item.productId,
            quantity: item.quantity,
            shippingAddress: this.shippingForm.address,
            contactName: this.shippingForm.contactName,
            contactPhone: this.shippingForm.contactPhone
          })
        }
        ElMessage.success('兑换成功')
        this.exchangeDialogVisible = false
        await this.loadCart()
        await this.loadPointsAccount()
        this.$router.push('/user/orders')
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '兑换失败')
      } finally {
        this.exchangeLoading = false
      }
    },
    getProductImageUrl(productName) {
      // 根据商品名称生成图片路径
      const imageMap = {
        '话费充值50元': '/images/products/huafei50.jpg',
        '话费充值100元': '/images/products/huafei100.jpg',
        '京东E卡50元': '/images/products/JDEcard50.jpg',
        '京东E卡100元': '/images/products/JDEcard100.jpg',
        '星巴克咖啡券': '/images/products/Starbucks.jpg',
        '电影票': '/images/products/dianying.jpg',
        '精美礼品盒': '/images/products/gifthe.jpg',
        '品牌保温杯': '/images/products/baowenbei.jpg'
      }
      return imageMap[productName] || 'https://via.placeholder.com/80x80'
    }
  }
}
</script>

<style scoped>
.cart {
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

.product-cell {
  display: flex;
  align-items: center;
}

.product-info {
  flex: 1;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.product-stock {
  font-size: 12px;
  color: #909399;
}

.points {
  color: #f56c6c;
  font-weight: bold;
}

.total-points {
  color: #f56c6c;
  font-weight: bold;
  font-size: 16px;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-top: 20px;
}

.cart-summary {
  font-size: 14px;
  color: #606266;
}

.cart-summary .total {
  margin-left: 20px;
  font-size: 16px;
}

.cart-summary .total em {
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
  font-style: normal;
}
</style>
