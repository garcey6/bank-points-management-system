<template>
  <div class="home">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #409eff">
              <el-icon :size="30"><Coin /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">总积分</div>
              <div class="stat-value">{{ pointsAccount?.totalPoints || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #67c23a">
              <el-icon :size="30"><Wallet /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">可用积分</div>
              <div class="stat-value">{{ pointsAccount?.availablePoints || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #e6a23c">
              <el-icon :size="30"><Star /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">会员等级</div>
              <div class="stat-value">{{ memberLevelText }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="section-card" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span class="section-title">推荐商品</span>
          <el-button type="text" @click="$router.push('/user/exchange')">查看更多</el-button>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="6" v-for="product in recommendProducts" :key="product.id">
          <el-card class="product-card" shadow="hover" @click="goToProduct(product)">
            <div class="product-image">
              <el-image :src="getProductImageUrl(product)" fit="cover">
                <template #error>
                  <div class="image-slot">
                    <el-icon :size="30"><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
            </div>
            <div class="product-info">
              <div class="product-name">{{ product.name }}</div>
              <div class="product-footer">
                <span class="product-points">{{ product.points }}积分</span>
                <el-button type="primary" size="small" @click.stop="addToCartHandler(product)">加入购物车</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card class="section-card">
          <template #header>
            <div class="card-header">
              <span class="section-title">最近积分记录</span>
              <el-button type="text" @click="$router.push('/user/points')">查看更多</el-button>
            </div>
          </template>
          <el-table :data="recentRecords" stripe style="width: 100%">
            <el-table-column prop="type" label="类型" width="100">
              <template #default="{ row }">
                <el-tag :type="row.points > 0 ? 'success' : 'danger'">{{ row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="points" label="积分" width="100">
              <template #default="{ row }">
                <span :style="{ color: row.points > 0 ? '#67c23a' : '#f56c6c' }">
                  {{ row.points > 0 ? '+' : '' }}{{ row.points }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="section-card">
          <template #header>
            <div class="card-header">
              <span class="section-title">活动通知</span>
              <el-button type="text" @click="$router.push('/user/notices')">查看更多</el-button>
            </div>
          </template>
          <div v-if="activities.length > 0" class="activity-list">
            <div v-for="activity in activities" :key="activity.id" class="activity-item" @click="goToNotice(activity)">
              <div class="activity-icon">
                <el-icon :size="24"><Present /></el-icon>
              </div>
              <div class="activity-content">
                <div class="activity-title">{{ activity.name }}</div>
                <div class="activity-desc">{{ activity.description }}</div>
              </div>
              <el-tag type="success" size="small">进行中</el-tag>
            </div>
          </div>
          <el-empty v-else description="暂无活动" :image-size="60" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getPointsAccount, getPointsRecords, getProducts, addToCart, getBankActivities } from '@/api/user'
import { ElMessage } from 'element-plus'
import { Coin, Wallet, Star, Picture, Present } from '@element-plus/icons-vue'

export default {
  name: 'UserHome',
  components: {
    Coin,
    Wallet,
    Star,
    Picture,
    Present
  },
  data() {
    return {
      user: JSON.parse(localStorage.getItem('user') || '{}'),
      pointsAccount: null,
      recentRecords: [],
      recommendProducts: [],
      activities: []
    }
  },
  computed: {
    memberLevelText() {
      const levels = ['', '普通会员', '银卡会员', '金卡会员', '白金会员']
      return levels[this.user.memberLevel] || '普通会员'
    }
  },
  async mounted() {
    await this.loadPointsAccount()
    await this.loadRecentRecords()
    await this.loadRecommendProducts()
    await this.loadActivities()
  },
  methods: {

    
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
    async loadRecentRecords() {
      try {
        const res = await getPointsRecords(this.user.id)
        if (res.code === 200) {
          this.recentRecords = res.data.slice(0, 5)
        }
      } catch (error) {
        console.error('加载积分记录失败', error)
      }
    },
    async loadRecommendProducts() {
      try {
        const res = await getProducts()
        if (res.code === 200) {
          const products = res.data.filter(p => p.status === 1)
          this.recommendProducts = this.shuffleArray(products).slice(0, 4)
        }
      } catch (error) {
        console.error('加载商品失败', error)
      }
    },
    async loadActivities() {
      try {
        const res = await getBankActivities()
        if (res.code === 200) {
          this.activities = res.data.slice(0, 3)
        }
      } catch (error) {
        console.error('加载活动失败', error)
      }
    },
    shuffleArray(array) {
      const arr = [...array]
      for (let i = arr.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [arr[i], arr[j]] = [arr[j], arr[i]]
      }
      return arr
    },
    goToProduct(product) {
      this.$router.push(`/user/exchange?id=${product.id}`)
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
    goToNotice(activity) {
      this.$router.push('/user/notices')
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
.home {
  flex: 1;
  padding: 20px;
  min-height: calc(100vh - 40px);
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.stat-card {
  margin-bottom: 20px;
  border-radius: 12px !important;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.stat-card:hover {
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.stat-icon:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  background: linear-gradient(90deg, #409eff, #67c23a);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
  border-radius: 12px 12px 0 0;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  background: linear-gradient(90deg, #409eff, #67c23a);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.product-card {
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 20px;
  border-radius: 12px !important;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
}

.product-image {
  width: 100%;
  height: 150px;
  overflow: hidden;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}

.product-image :deep(.el-image) {
  width: 100%;
  height: 100%;
}

.product-image :deep(.el-image__inner) {
  width: 100% !important;
  height: 100% !important;
  object-fit: contain !important;
}

.product-image :deep(.el-image__wrapper) {
  width: 100% !important;
  height: 100% !important;
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
  font-size: 14px;
  color: #303133;
  margin-bottom: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-points {
  font-size: 16px;
  color: #f56c6c;
  font-weight: bold;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.activity-item:hover {
  background-color: #e6f7ff;
}

.activity-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.activity-content {
  flex: 1;
  min-width: 0;
}

.activity-title {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.activity-desc {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
