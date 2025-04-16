<template>
  <div class="home-container">
    <!-- 轮播图区域 -->
    <div class="banner-section">
      <el-carousel height="400px">
        <el-carousel-item v-for="(item, index) in banners" :key="index">
          <el-image :src="item.image" fit="cover" class="carousel-image" />
          <div class="carousel-content">
            <h2>{{ item.title }}</h2>
            <p>{{ item.description }}</p>
            <el-button type="primary" @click="goToBookDetail(item.id)">立即查看</el-button>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 分类快捷导航 -->
    <div class="category-section">
      <el-row :gutter="20">
        <el-col :span="4" v-for="category in categories" :key="category.id">
          <el-card shadow="hover" class="category-card" @click="filterByCategory(category.id)">
            <div class="category-icon">
              <el-icon><component :is="category.icon" /></el-icon>
            </div>
            <div class="category-name">{{ category.name }}</div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 新书上架 -->
    <div class="new-books-section">
      <div class="section-header">
        <h2>新书上架</h2>
        <el-button type="text" @click="goToMore('new')">查看更多 <el-icon><ArrowRight /></el-icon></el-button>
      </div>
      <el-row :gutter="20">
        <el-col :span="4" v-for="book in newBooks" :key="book.id">
          <el-card shadow="hover" class="book-card" @click="goToBookDetail(book.id)">
            <el-image :src="book.cover" fit="cover" class="book-cover" loading="lazy" />
            <div class="book-title">{{ book.title }}</div>
            <div class="book-author">{{ book.author }}</div>
            <div class="book-price">
              <span class="current-price">¥{{ book.price.toFixed(2) }}</span>
              <span v-if="book.originalPrice" class="original-price">¥{{ book.originalPrice.toFixed(2) }}</span>
            </div>
            <div class="book-actions">
              <el-button type="primary" size="small" @click.stop="addToCart(book)">加入购物车</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 热门推荐 -->
    <div class="hot-books-section">
      <div class="section-header">
        <h2>热门推荐</h2>
        <el-button type="text" @click="goToMore('hot')">查看更多 <el-icon><ArrowRight /></el-icon></el-button>
      </div>
      <el-row :gutter="20">
        <el-col :span="4" v-for="book in hotBooks" :key="book.id">
          <el-card shadow="hover" class="book-card" @click="goToBookDetail(book.id)">
            <div v-if="book.discount" class="discount-tag">{{ book.discount }}</div>
            <el-image :src="book.cover" fit="cover" class="book-cover" loading="lazy" />
            <div class="book-title">{{ book.title }}</div>
            <div class="book-author">{{ book.author }}</div>
            <div class="book-price">
              <span class="current-price">¥{{ book.price.toFixed(2) }}</span>
              <span v-if="book.originalPrice" class="original-price">¥{{ book.originalPrice.toFixed(2) }}</span>
            </div>
            <div class="book-actions">
              <el-button type="primary" size="small" @click.stop="addToCart(book)">加入购物车</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 书城活动 -->
    <div class="promotion-section">
      <el-row :gutter="20">
        <el-col :span="8" v-for="promo in promotions" :key="promo.id">
          <el-card shadow="hover" class="promotion-card" @click="goToPromotion(promo.id)">
            <el-image :src="promo.image" fit="cover" class="promotion-image" />
            <div class="promotion-content">
              <h3>{{ promo.title }}</h3>
              <p>{{ promo.description }}</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Reading, Collection, Star, Notebook, Calendar, ArrowRight,
  GoodsFilled, TrendCharts, PriceTag, Medal
} from '@element-plus/icons-vue'

const router = useRouter()

// 轮播图数据
const banners = [
  {
    id: 1,
    title: '2023年度好书榜单',
    description: '发现阅读的无限可能',
    image: 'https://img3.doubanio.com/view/subject/l/public/s34227181.jpg'
  },
  {
    id: 2,
    title: '文学经典限时特惠',
    description: '品味经典，阅读人生',
    image: 'https://img2.doubanio.com/view/subject/l/public/s34069289.jpg'
  },
  {
    id: 3,
    title: '儿童读物专场',
    description: '点亮孩子的想象力',
    image: 'https://img1.doubanio.com/view/subject/l/public/s34095207.jpg'
  }
]

// 分类数据
const categories = [
  { id: 1, name: '文学小说', icon: 'Reading' },
  { id: 2, name: '经管励志', icon: 'TrendCharts' },
  { id: 3, name: '人文社科', icon: 'Notebook' },
  { id: 4, name: '科技科普', icon: 'GoodsFilled' },
  { id: 5, name: '生活艺术', icon: 'PriceTag' },
  { id: 6, name: '童书', icon: 'Star' }
]

// 新书数据
const newBooks = [
  {
    id: 101,
    title: '克拉拉与太阳',
    author: '[英] 石黑一雄',
    cover: 'https://img2.doubanio.com/view/subject/s/public/s33821754.jpg',
    price: 59.00,
    originalPrice: 69.00
  },
  {
    id: 102,
    title: '我们生活在巨大的差距里',
    author: '梁鸿',
    cover: 'https://img9.doubanio.com/view/subject/s/public/s33956867.jpg',
    price: 49.80,
    originalPrice: null
  },
  {
    id: 103,
    title: '蛤蟆先生去看心理医生',
    author: '[英] 罗伯特·戴博德',
    cover: 'https://img1.doubanio.com/view/subject/s/public/s29934533.jpg',
    price: 38.00,
    originalPrice: 45.00
  },
  {
    id: 104,
    title: '一句顶一万句',
    author: '刘震云',
    cover: 'https://img2.doubanio.com/view/subject/s/public/s4599550.jpg',
    price: 29.80,
    originalPrice: null
  },
  {
    id: 105,
    title: '生活蒙太奇',
    author: '范晓萱',
    cover: 'https://img1.doubanio.com/view/subject/s/public/s33633191.jpg',
    price: 42.00,
    originalPrice: 52.80
  },
  {
    id: 106,
    title: '被讨厌的勇气',
    author: '[日] 岸见一郎 / 古贺史健',
    cover: 'https://img9.doubanio.com/view/subject/s/public/s29237648.jpg',
    price: 35.00,
    originalPrice: 39.80
  }
]

// 热门图书数据
const hotBooks = [
  {
    id: 201,
    title: '活着',
    author: '余华',
    cover: 'https://img1.doubanio.com/view/subject/s/public/s33859154.jpg',
    price: 39.80,
    originalPrice: 45.00,
    discount: '8.8折'
  },
  {
    id: 202,
    title: '红楼梦',
    author: '曹雪芹',
    cover: 'https://img2.doubanio.com/view/subject/s/public/s1070959.jpg',
    price: 59.70,
    originalPrice: 99.50,
    discount: '6折'
  },
  {
    id: 203,
    title: '三体',
    author: '刘慈欣',
    cover: 'https://img9.doubanio.com/view/subject/s/public/s2768378.jpg',
    price: 23.00,
    originalPrice: 29.00,
    discount: '7.9折'
  },
  {
    id: 204,
    title: '解忧杂货店',
    author: '[日] 东野圭吾',
    cover: 'https://img9.doubanio.com/view/subject/s/public/s27264181.jpg',
    price: 35.00,
    originalPrice: 39.50,
    discount: '8.9折'
  },
  {
    id: 205,
    title: '百年孤独',
    author: '[哥伦比亚] 加西亚·马尔克斯',
    cover: 'https://img9.doubanio.com/view/subject/s/public/s27237850.jpg',
    price: 39.50,
    originalPrice: 55.00,
    discount: '7.2折'
  },
  {
    id: 206,
    title: '人类简史',
    author: '[以色列] 尤瓦尔·赫拉利',
    cover: 'https://img1.doubanio.com/view/subject/s/public/s27814883.jpg',
    price: 58.00,
    originalPrice: 68.00,
    discount: '8.5折'
  }
]

// 促销活动数据
const promotions = [
  {
    id: 1,
    title: '新用户专享优惠',
    description: '新用户注册即送100元优惠券',
    image: 'https://img9.doubanio.com/view/photo/l/public/p2549536410.jpg'
  },
  {
    id: 2,
    title: '618年中大促',
    description: '全场图书低至5折，满199减100',
    image: 'https://img1.doubanio.com/view/photo/l/public/p2550851501.jpg'
  },
  {
    id: 3,
    title: '儿童读物专场',
    description: '少儿图书，满300减150',
    image: 'https://img3.doubanio.com/view/photo/l/public/p2554525534.jpg'
  }
]

// 查看图书详情
const goToBookDetail = (id) => {
  router.push(`/book/${id}`)
}

// 按分类筛选
const filterByCategory = (categoryId) => {
  router.push(`/category/${categoryId}`)
}

// 查看更多
const goToMore = (type) => {
  if (type === 'new') {
    router.push('/new-books')
  } else if (type === 'hot') {
    router.push('/hot-books')
  }
}

// 查看促销活动
const goToPromotion = (id) => {
  router.push(`/promotion/${id}`)
}

// 加入购物车
const addToCart = (book) => {
  ElMessage.success(`已将《${book.title}》加入购物车`)
}
</script>

<style scoped>
.home-container {
  padding: 0 0 40px 0;
}

/* 轮播图样式 */
.banner-section {
  margin-bottom: 30px;
}

.carousel-image {
  width: 100%;
  height: 100%;
}

.carousel-content {
  position: absolute;
  top: 50%;
  left: 10%;
  transform: translateY(-50%);
  color: #fff;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
  max-width: 500px;
}

.carousel-content h2 {
  font-size: 36px;
  margin-bottom: 10px;
}

.carousel-content p {
  font-size: 18px;
  margin-bottom: 20px;
}

/* 分类导航样式 */
.category-section {
  margin-bottom: 40px;
}

.category-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  height: 120px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.category-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.category-icon {
  font-size: 30px;
  color: #409EFF;
  margin-bottom: 10px;
}

.category-name {
  font-size: 14px;
}

/* 图书区块样式 */
.new-books-section,
.hot-books-section {
  margin-bottom: 40px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  font-size: 22px;
  font-weight: bold;
  position: relative;
  padding-left: 15px;
}

.section-header h2::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 20px;
  background-color: #409EFF;
}

/* 图书卡片样式 */
.book-card {
  margin-bottom: 20px;
  position: relative;
  transition: all 0.3s;
  overflow: hidden;
}

.book-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.discount-tag {
  position: absolute;
  top: 10px;
  right: 10px;
  background-color: #ff6700;
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

/* 促销活动区样式 */
.promotion-section {
  margin-bottom: 40px;
}

.promotion-card {
  position: relative;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.promotion-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.promotion-image {
  height: 180px;
  width: 100%;
}

.promotion-content {
  padding: 15px;
}

.promotion-content h3 {
  font-size: 18px;
  margin-bottom: 5px;
}

.promotion-content p {
  font-size: 14px;
  color: #606266;
}
</style> 