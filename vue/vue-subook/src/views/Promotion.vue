<template>
  <div class="promotion-container">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>促销活动</el-breadcrumb-item>
      <el-breadcrumb-item>{{ promotionInfo.title }}</el-breadcrumb-item>
    </el-breadcrumb>
    
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="5" animated />
    </div>
    
    <div v-else-if="!promotionInfo.id" class="empty-result">
      <el-empty description="未找到活动信息" />
    </div>
    
    <div v-else>
      <div class="promotion-header" :style="{ backgroundImage: `url(${promotionInfo.bannerImage})` }">
        <div class="promotion-header-content">
          <h1>{{ promotionInfo.title }}</h1>
          <div class="promotion-time">{{ promotionInfo.startDate }} - {{ promotionInfo.endDate }}</div>
          <div class="promotion-description">{{ promotionInfo.description }}</div>
        </div>
      </div>
      
      <div class="promotion-content">
        <div class="promotion-info">
          <el-alert
            v-if="promotionInfo.rules"
            :title="'活动规则'"
            :description="promotionInfo.rules"
            type="info"
            :closable="false"
            show-icon
          />
          
          <el-alert
            v-if="isExpired"
            title="活动已结束"
            type="warning"
            :closable="false"
            show-icon
          />
          
          <el-alert
            v-else-if="isNotStarted"
            title="活动未开始"
            type="warning"
            :closable="false"
            show-icon
          />
        </div>
        
        <div class="section-title">
          <h2>活动图书</h2>
          <div v-if="promotionInfo.discount" class="discount-tag">
            {{ promotionInfo.discount }}
          </div>
        </div>
        
        <div v-if="promotionBooks.length === 0" class="empty-books">
          <el-empty description="暂无促销图书" />
        </div>
        
        <div v-else class="books-grid">
          <el-row :gutter="20">
            <el-col :xs="12" :sm="8" :md="6" :lg="4" v-for="book in promotionBooks" :key="book.id">
              <el-card shadow="hover" class="book-card" @click="goToBookDetail(book.id)">
                <div class="discount-tag">{{ promotionInfo.discount }}</div>
                <el-image :src="book.cover" fit="cover" class="book-cover" loading="lazy" />
                <div class="book-title">{{ book.title }}</div>
                <div class="book-author">{{ book.author }}</div>
                <div class="book-price">
                  <span class="current-price">¥{{ book.discountPrice.toFixed(2) }}</span>
                  <span class="original-price">¥{{ book.price.toFixed(2) }}</span>
                </div>
                <div class="book-actions">
                  <el-button type="primary" size="small" @click.stop="addToCart(book)">加入购物车</el-button>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const promotionId = computed(() => Number(route.params.id))
const loading = ref(true)

// 模拟促销活动数据
const promotions = [
  {
    id: 1,
    title: '双11图书大促',
    description: '双11特惠活动，精选图书低至5折起，更有满减优惠等你来！',
    startDate: '2023-11-01',
    endDate: '2023-11-15',
    discount: '5折起',
    rules: '1. 活动期间，特定图书享受折扣\n2. 满199减50，满299减100\n3. 每个用户限购10本\n4. 活动最终解释权归书城所有',
    bannerImage: 'https://img.alicdn.com/imgextra/i1/O1CN01TvW0i51lkIr5QTZlw_!!6000000004857-0-tps-1190-400.jpg'
  },
  {
    id: 2,
    title: '开学季读书节',
    description: '开学季特惠，文学、科普、教辅等类别图书特惠，助力新学期！',
    startDate: '2023-08-20',
    endDate: '2023-09-10',
    discount: '7折起',
    rules: '1. 活动期间，教辅类图书享7折优惠\n2. 文学类图书满3本额外95折\n3. 活动最终解释权归书城所有',
    bannerImage: 'https://img.alicdn.com/imgextra/i3/O1CN01Rn79Xu1VVEBUpUQGq_!!6000000002657-0-tps-1190-400.jpg'
  },
  {
    id: 3,
    title: '年中大促',
    description: '年中图书盛典，多种品类联合促销，错过等半年！',
    startDate: '2023-06-01',
    endDate: '2023-06-30',
    discount: '6折起',
    rules: '1. 活动期间，特定图书享受折扣\n2. 满99减20，满199减50\n3. 每个用户限购5本特价书\n4. 活动最终解释权归书城所有',
    bannerImage: 'https://img.alicdn.com/imgextra/i3/O1CN01zHYUPV1MlKDtJvjmv_!!6000000001477-0-tps-1190-400.jpg'
  }
]

// 模拟促销图书数据
const allPromotionBooks = [
  // 促销活动1的图书
  {
    id: 101,
    promotionId: 1,
    title: '克拉拉与太阳',
    author: '[英] 石黑一雄',
    cover: 'https://img2.doubanio.com/view/subject/s/public/s33821754.jpg',
    price: 59.00,
    discountPrice: 29.50,
    category: '文学小说'
  },
  {
    id: 102,
    promotionId: 1,
    title: '活着',
    author: '余华',
    cover: 'https://img1.doubanio.com/view/subject/s/public/s33859154.jpg',
    price: 39.80,
    discountPrice: 19.90,
    category: '文学小说'
  },
  {
    id: 103,
    promotionId: 1,
    title: '人类简史',
    author: '[以色列] 尤瓦尔·赫拉利',
    cover: 'https://img1.doubanio.com/view/subject/s/public/s27814883.jpg',
    price: 68.00,
    discountPrice: 33.90,
    category: '人文社科'
  },
  {
    id: 104,
    promotionId: 1,
    title: '三体',
    author: '刘慈欣',
    cover: 'https://img9.doubanio.com/view/subject/s/public/s2768378.jpg',
    price: 23.00,
    discountPrice: 11.50,
    category: '科幻小说'
  },
  {
    id: 105,
    promotionId: 1,
    title: '认知觉醒',
    author: '周岭',
    cover: 'https://img2.doubanio.com/view/subject/s/public/s33956854.jpg',
    price: 59.00,
    discountPrice: 29.50,
    category: '经管励志'
  },
  {
    id: 106,
    promotionId: 1,
    title: '解忧杂货店',
    author: '[日] 东野圭吾',
    cover: 'https://img2.doubanio.com/view/subject/s/public/s27264181.jpg',
    price: 39.50,
    discountPrice: 19.80,
    category: '文学小说'
  },
  
  // 促销活动2的图书
  {
    id: 201,
    promotionId: 2,
    title: '窗边的小豆豆',
    author: '[日] 黑柳彻子',
    cover: 'https://img9.doubanio.com/view/subject/s/public/s1067911.jpg',
    price: 39.80,
    discountPrice: 27.90,
    category: '少儿读物'
  },
  {
    id: 202,
    promotionId: 2,
    title: '高中数学知识大全',
    author: '张三',
    cover: 'https://img9.doubanio.com/view/subject/s/public/s33922951.jpg',
    price: 68.00,
    discountPrice: 47.60,
    category: '教育考试'
  },
  {
    id: 203,
    promotionId: 2,
    title: '小学语文阅读理解训练',
    author: '李四',
    cover: 'https://img9.doubanio.com/view/subject/s/public/s33921966.jpg',
    price: 45.00,
    discountPrice: 31.50,
    category: '教育考试'
  },
  {
    id: 204,
    promotionId: 2,
    title: '格林童话',
    author: '[德] 格林兄弟',
    cover: 'https://img9.doubanio.com/view/subject/s/public/s27179473.jpg',
    price: 36.00,
    discountPrice: 25.20,
    category: '少儿读物'
  },
  
  // 促销活动3的图书
  {
    id: 301,
    promotionId: 3,
    title: '百年孤独',
    author: '[哥伦比亚] 加西亚·马尔克斯',
    cover: 'https://img2.doubanio.com/view/subject/s/public/s27237850.jpg',
    price: 55.00,
    discountPrice: 33.00,
    category: '文学小说'
  },
  {
    id: 302,
    promotionId: 3,
    title: '红楼梦',
    author: '曹雪芹',
    cover: 'https://img2.doubanio.com/view/subject/s/public/s1070959.jpg',
    price: 59.70,
    discountPrice: 35.80,
    category: '文学名著'
  },
  {
    id: 303,
    promotionId: 3,
    title: '被讨厌的勇气',
    author: '[日] 岸见一郎 / 古贺史健',
    cover: 'https://img9.doubanio.com/view/subject/s/public/s29237648.jpg',
    price: 39.80,
    discountPrice: 23.90,
    category: '经管励志'
  },
  {
    id: 304,
    promotionId: 3,
    title: '时间简史',
    author: '[英] 史蒂芬·霍金',
    cover: 'https://img3.doubanio.com/view/subject/s/public/s29958602.jpg',
    price: 45.00,
    discountPrice: 27.00,
    category: '科学技术'
  },
  {
    id: 305,
    promotionId: 3,
    title: '围城',
    author: '钱钟书',
    cover: 'https://img1.doubanio.com/view/subject/s/public/s1070222.jpg',
    price: 39.00,
    discountPrice: 23.40,
    category: '文学名著'
  }
]

// 获取当前促销活动信息
const promotionInfo = ref({})
const promotionBooks = ref([])

// 判断活动是否已结束
const isExpired = computed(() => {
  if (!promotionInfo.value.endDate) return false
  const endDate = new Date(promotionInfo.value.endDate)
  const today = new Date()
  return endDate < today
})

// 判断活动是否未开始
const isNotStarted = computed(() => {
  if (!promotionInfo.value.startDate) return false
  const startDate = new Date(promotionInfo.value.startDate)
  const today = new Date()
  return startDate > today
})

// 获取促销活动数据
const fetchPromotionData = () => {
  loading.value = true
  
  // 模拟API请求延迟
  setTimeout(() => {
    // 获取促销活动信息
    const promotion = promotions.find(item => item.id === promotionId.value)
    if (promotion) {
      promotionInfo.value = promotion
      
      // 获取促销图书
      promotionBooks.value = allPromotionBooks.filter(book => book.promotionId === promotionId.value)
    } else {
      promotionInfo.value = {}
      promotionBooks.value = []
    }
    
    loading.value = false
  }, 500)
}

// 查看图书详情
const goToBookDetail = (id) => {
  router.push(`/book/${id}`)
}

// 加入购物车
const addToCart = (book) => {
  if (isExpired.value) {
    ElMessage.warning('活动已结束，无法加入购物车')
    return
  }
  
  if (isNotStarted.value) {
    ElMessage.warning('活动未开始，无法加入购物车')
    return
  }
  
  ElMessage.success(`已将《${book.title}》加入购物车`)
}

// 初始加载
onMounted(() => {
  fetchPromotionData()
})
</script>

<style scoped>
.promotion-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.loading-container {
  padding: 40px 0;
}

.empty-result {
  padding: 60px 0;
}

.promotion-header {
  height: 260px;
  border-radius: 8px;
  background-size: cover;
  background-position: center;
  margin: 20px 0;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: flex-end;
}

.promotion-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(0,0,0,0) 0%, rgba(0,0,0,0.7) 100%);
}

.promotion-header-content {
  position: relative;
  padding: 20px;
  color: #fff;
  width: 100%;
}

.promotion-header-content h1 {
  font-size: 28px;
  margin: 0 0 10px 0;
}

.promotion-time {
  font-size: 14px;
  margin-bottom: 10px;
  opacity: 0.8;
}

.promotion-description {
  font-size: 16px;
  max-width: 80%;
}

.promotion-content {
  margin-top: 30px;
}

.promotion-info {
  margin-bottom: 30px;
}

.section-title {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.section-title h2 {
  font-size: 22px;
  margin: 0;
  position: relative;
  padding-left: 15px;
}

.section-title h2::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 22px;
  background-color: #E6A23C;
}

.section-title .discount-tag {
  margin-left: 15px;
  background-color: #E6A23C;
  color: #fff;
  padding: 2px 10px;
  border-radius: 4px;
  font-size: 14px;
}

.empty-books {
  padding: 40px 0;
}

.books-grid {
  margin-top: 20px;
}

.book-card {
  margin-bottom: 20px;
  position: relative;
  transition: all 0.3s;
  overflow: hidden;
  cursor: pointer;
}

.book-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.discount-tag {
  position: absolute;
  top: 10px;
  right: 10px;
  background-color: #E6A23C;
  color: #fff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  z-index: 1;
}

.book-cover {
  height: 200px;
  width: 100%;
  display: block;
  margin-bottom: 10px;
}

.book-title {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.book-author {
  font-size: 12px;
  color: #909399;
  margin-bottom: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.book-price {
  margin-bottom: 10px;
}

.current-price {
  color: #ff6700;
  font-weight: bold;
  font-size: 16px;
}

.original-price {
  color: #909399;
  font-size: 12px;
  text-decoration: line-through;
  margin-left: 5px;
}

.book-actions {
  display: flex;
  justify-content: center;
}
</style> 