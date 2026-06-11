<template>
  <div class="exchange">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>积分兑换</span>
          <div class="header-actions">
            <el-select v-model="selectedCategory" placeholder="选择分类" clearable @change="filterByCategory" style="width: 150px; margin-right: 10px">
              <el-option label="全部分类" value="" />
              <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
            </el-select>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索商品名称或描述"
              style="width: 250px"
              clearable
              @clear="loadProducts"
              @input="debouncedSearch"
              @keyup.enter="searchProducts">
              <template #prefix>
                <el-icon :size="16" v-if="searchLoading"><Loading /></el-icon>
                <el-icon :size="16" v-else><Search /></el-icon>
              </template>
              <template #append>
                <el-button type="primary" :loading="searchLoading" @click="searchProducts">
                  <el-icon v-if="searchLoading"><Loading /></el-icon>
                  <el-icon v-else><Search /></el-icon>
                  <span>搜索</span>
                </el-button>
              </template>
            </el-input>
          </div>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="6" v-for="product in products" :key="product.id">
          <el-card class="product-card" shadow="hover" @click="showProductDetail(product)">
            <div class="product-image">
              <el-image :src="getProductImageUrl(product)" fit="contain" @error="handleImageError">
                <template #error>
                  <div class="image-slot">
                    <el-icon :size="30"><Picture /></el-icon>
                    <div style="font-size: 12px; margin-top: 5px;">图片加载失败</div>
                  </div>
                </template>
              </el-image>
            </div>
            <div class="product-info">
              <div class="product-name">{{ product.name }}</div>
              <div class="product-description">{{ product.description }}</div>
              <div class="product-footer">
                <span class="product-points">{{ product.points }}积分</span>
                <div class="product-actions">
                  <el-button type="primary" size="small" @click.stop="showExchangeDialog(product)">兑换</el-button>
                  <el-button type="default" size="small" @click.stop="addToCartHandler(product)">加入购物车</el-button>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <el-dialog v-model="detailDialogVisible" title="商品详情" width="700px">
      <div v-if="selectedProduct" class="product-detail">
        <el-row :gutter="20">
          <el-col :span="10">
            <el-image 
              :src="getProductImageUrl(selectedProduct)" 
              fit="contain"
              style="width: 100%; border-radius: 8px; height: 300px; display: flex; align-items: center; justify-content: center; background: #f5f7fa;">
            </el-image>
          </el-col>
          <el-col :span="14">
            <h2>{{ selectedProduct.name }}</h2>
            <p class="product-desc">{{ selectedProduct.description }}</p>
            <div class="product-meta">
              <div class="points-info">
                <span class="label">所需积分：</span>
                <span class="points-value">{{ selectedProduct.points }}积分</span>
              </div>
              <div class="stock-info">
                <span class="label">库存数量：</span>
                <span>{{ selectedProduct.stock }}</span>
              </div>
              <div class="category-info">
                <span class="label">商品分类：</span>
                <el-tag size="small">{{ selectedProduct.category || '未分类' }}</el-tag>
              </div>
            </div>
            <div class="detail-actions">
              <el-button type="primary" size="large" @click="showExchangeDialog(selectedProduct)">立即兑换</el-button>
              <el-button size="large" @click="addToCartHandler(selectedProduct)">加入购物车</el-button>
            </div>
          </el-col>
        </el-row>

        <el-divider>用户评价</el-divider>
        
        <div class="reviews-section">
          <el-button type="primary" size="small" style="margin-bottom: 15px" @click="showReviewDialog">
            写评价
          </el-button>
          
          <div v-if="reviews.length > 0" class="reviews-list">
            <div v-for="review in reviews" :key="review.id" class="review-item">
              <div class="review-header">
                <div class="review-user">
                  <el-avatar :size="32">{{ review.realName?.charAt(0) || 'U' }}</el-avatar>
                  <span class="username">{{ review.realName || review.username }}</span>
                </div>
                <div class="review-rating">
                  <el-rate v-model="review.rating" disabled :max="5" />
                </div>
              </div>
              <div class="review-content">{{ review.content }}</div>
              <div class="review-time">{{ review.createTime }}</div>
            </div>
          </div>
          <el-empty v-else description="暂无评价" :image-size="60" />
        </div>
      </div>
    </el-dialog>

    <el-dialog v-model="exchangeDialogVisible" title="确认兑换" width="400px">
      <el-form :model="exchangeForm" label-width="80px">
        <el-form-item label="商品名称">
          <span>{{ selectedProduct?.name }}</span>
        </el-form-item>
        <el-form-item label="所需积分">
          <span>{{ selectedProduct?.points }}积分</span>
        </el-form-item>
        <el-form-item label="兑换数量">
          <el-input-number v-model="exchangeForm.quantity" :min="1" :max="selectedProduct?.stock"></el-input-number>
        </el-form-item>
        <el-form-item label="总积分">
          <span class="total-points">{{ totalPoints }}积分</span>
        </el-form-item>
        <el-form-item label="可用积分">
          <span>{{ pointsAccount?.availablePoints || 0 }}积分</span>
        </el-form-item>
        <el-form-item label="收货地址">
          <el-input v-model="exchangeForm.shippingAddress" type="textarea" :rows="2" placeholder="请输入收货地址"></el-input>
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="exchangeForm.contactName" placeholder="请输入联系人"></el-input>
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="exchangeForm.contactPhone" placeholder="请输入联系电话"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="exchangeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleExchange" :loading="exchangeLoading">确认兑换</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="reviewDialogVisible" title="写评价" width="500px">
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.rating" :max="5" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input 
            v-model="reviewForm.content" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入您的评价">
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReview" :loading="reviewLoading">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getProducts, searchProducts, exchangeProduct, getPointsAccount, getProductsByCategory, addToCart, getProductReviews, addReview } from '@/api/user'
import { ElMessage } from 'element-plus'
import { Picture, Search, Loading } from '@element-plus/icons-vue'

export default {
  name: 'UserExchange',
  components: {
    Picture,
    Search,
    Loading
  },
  data() {
    return {
      user: JSON.parse(localStorage.getItem('user') || '{}'),
      products: [],
      originalProducts: [], // 存储原始商品数据
      categories: [],
      reviews: [],
      searchKeyword: '',
      selectedCategory: '',
      pointsAccount: null,
      detailDialogVisible: false,
      exchangeDialogVisible: false,
      reviewDialogVisible: false,
      selectedProduct: null,
      exchangeForm: {
        quantity: 1,
        shippingAddress: '',
        contactName: '',
        contactPhone: ''
      },
      reviewForm: {
        rating: 5,
        content: ''
      },
      exchangeLoading: false,
      reviewLoading: false,
      searchLoading: false,
      searchTimeout: null
    }
  },
  computed: {
    totalPoints() {
      return this.selectedProduct ? this.selectedProduct.points * this.exchangeForm.quantity : 0
    }
  },
  async mounted() {
    await this.loadProducts()
    await this.loadPointsAccount()
    if (this.$route.query.id) {
      const product = this.products.find(p => p.id == this.$route.query.id)
      if (product) {
        this.showProductDetail(product)
      }
    }
  },
  methods: {
    async loadProducts() {
      try {
        const res = await getProducts()
        if (res.code === 200) {
          console.log('原始商品数据:', res.data)
          this.originalProducts = res.data.filter(p => p.status === 1)
          this.products = [...this.originalProducts]
          console.log('过滤后商品数据:', this.products)
          const catSet = new Set(this.originalProducts.map(p => p.category).filter(c => c))
          this.categories = Array.from(catSet)
        }
      } catch (error) {
        console.error('加载商品失败', error)
      }
    },
    debouncedSearch() {
      if (this.searchTimeout) {
        clearTimeout(this.searchTimeout)
      }
      this.searchTimeout = setTimeout(() => {
        this.searchProducts()
      }, 300)
    },
    async searchProducts() {
      if (!this.searchKeyword) {
        await this.loadProducts()
        return
      }
      this.searchLoading = true
      try {
        const res = await searchProducts(this.searchKeyword)
        if (res.code === 200) {
          this.products = res.data
          if (this.products.length === 0) {
            ElMessage.info('未找到匹配的商品，请尝试其他关键词')
          }
        }
      } catch (error) {
        console.error('搜索商品失败', error)
        ElMessage.error('搜索失败，请稍后重试')
      } finally {
        this.searchLoading = false
      }
    },
    filterByCategory() {
      if (!this.selectedCategory) {
        this.products = [...this.originalProducts]
        return
      }
      // 基于原始商品数据进行分类筛选
      this.products = this.originalProducts.filter(p => p.category === this.selectedCategory)
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
    async showProductDetail(product) {
      console.log('selectedProduct:', product)
      this.selectedProduct = product
      this.detailDialogVisible = true
      await this.loadReviews()
    },
    async loadReviews() {
      try {
        const res = await getProductReviews(this.selectedProduct.id)
        if (res.code === 200) {
          this.reviews = res.data
        }
      } catch (error) {
        console.error('加载评价失败', error)
      }
    },
    showExchangeDialog(product) {
      this.selectedProduct = product
      this.exchangeForm.quantity = 1
      this.exchangeDialogVisible = true
    },
    showReviewDialog() {
      this.reviewForm = { rating: 5, content: '' }
      this.reviewDialogVisible = true
    },
    async handleExchange() {
      if (this.totalPoints > (this.pointsAccount?.availablePoints || 0)) {
        ElMessage.error('可用积分不足')
        return
      }
      if (!this.exchangeForm.shippingAddress || !this.exchangeForm.contactName || !this.exchangeForm.contactPhone) {
        ElMessage.warning('请填写完整收货信息')
        return
      }
      this.exchangeLoading = true
      try {
        const res = await exchangeProduct(this.user.id, {
          productId: this.selectedProduct.id,
          quantity: this.exchangeForm.quantity,
          shippingAddress: this.exchangeForm.shippingAddress,
          contactName: this.exchangeForm.contactName,
          contactPhone: this.exchangeForm.contactPhone
        })
        if (res.code === 200) {
          ElMessage.success('兑换成功')
          this.exchangeDialogVisible = false
          await this.loadProducts()
          await this.loadPointsAccount()
        } else {
          ElMessage.error(res.message)
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '兑换失败')
      } finally {
        this.exchangeLoading = false
      }
    },
    async addToCartHandler(product) {
      try {
        const res = await addToCart(this.user.id, {
          productId: product.id,
          quantity: 1
        })
        if (res.code === 200) {
          ElMessage.success('添加到购物车成功')
        } else {
          ElMessage.error(res.message || '添加失败')
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '添加失败')
      }
    },
    async submitReview() {
      if (!this.reviewForm.content) {
        ElMessage.warning('请输入评价内容')
        return
      }
      this.reviewLoading = true
      try {
        const res = await addReview({
          productId: this.selectedProduct.id,
          userId: this.user.id,
          rating: this.reviewForm.rating,
          content: this.reviewForm.content
        })
        if (res.code === 200) {
          ElMessage.success('评价成功')
          this.reviewDialogVisible = false
          await this.loadReviews()
        } else {
          ElMessage.error(res.message)
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '评价失败')
      } finally {
        this.reviewLoading = false
      }
    },
    handleImageError(err) {
      console.error('图片加载失败:', err)
      console.log('图片路径:', err.target.src)
    },
    getProductImageUrl(product) {
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
      return imageMap[product.name] || 'https://via.placeholder.com/200x200'
    }
  }
}
</script>

<style scoped>
.exchange {
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

.header-actions {
  display: flex;
  align-items: center;
}

.product-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
}

.product-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  border-radius: 4px 4px 0 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}

.product-image :deep(.el-image) {
  width: 100%;
  height: 100%;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background-color: #f5f7fa;
  color: #909399;
}

.product-info {
  padding: 10px 0;
}

.product-name {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-description {
  font-size: 14px;
  color: #909399;
  margin-bottom: 12px;
  height: 40px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-points {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
}

.product-actions {
  display: flex;
  gap: 5px;
}

.total-points {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
}

.product-detail h2 {
  margin: 0 0 15px 0;
  font-size: 24px;
  color: #303133;
}

.product-desc {
  color: #606266;
  margin-bottom: 20px;
}

.product-meta {
  margin-bottom: 20px;
}

.product-meta > div {
  margin-bottom: 10px;
}

.product-meta .label {
  color: #909399;
  margin-right: 10px;
}

.product-meta .points-value {
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
}

.detail-actions {
  display: flex;
  gap: 10px;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.review-item {
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.review-user {
  display: flex;
  align-items: center;
  gap: 10px;
}

.review-user .username {
  font-weight: 500;
  color: #303133;
}

.review-content {
  color: #606266;
  margin-bottom: 8px;
}

.review-time {
  font-size: 12px;
  color: #909399;
}
</style>
