<template>
  <div class="page-container">
    <!-- 顶部轮播图 -->
    <el-card class="banner-section" :body-style="{ padding: '0' }">
      <el-carousel height="400px" :interval="5000" arrow="always" indicator-position="outside">
        <el-carousel-item v-for="item in carouselItems" :key="item.id">
          <div class="banner-item">
            <div class="banner-content">
              <h2>{{ item.title }}</h2>
              <p>{{ item.description }}</p>
              <el-button type="primary" @click="$router.push(item.link)">查看详情</el-button>
            </div>
            <div class="banner-image-container">
              <el-image class="carousel-image" :src="item.imageUrl">
                <template #error>
                  <div class="image-placeholder">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </el-card>

    <!-- 特色分类快捷方式 -->
    <el-row :gutter="20" class="category-shortcuts">
      <el-col :xs="12" :sm="6" v-for="(shortcut, index) in shortcuts" :key="index">
        <el-card shadow="hover" class="shortcut-card" @click="$router.push(shortcut.link)">
          <el-icon class="shortcut-icon"><component :is="shortcut.icon" /></el-icon>
          <div class="shortcut-text">{{ shortcut.title }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 热门分类和应用介绍 -->
    <el-divider>
      <el-icon color="#409EFF"><Service /></el-icon>
      <span style="margin-left: 8px; font-size: 16px; font-weight: bold;">平台特色</span>
    </el-divider>
    
    <el-row :gutter="20">
      <el-col :xs="24" :sm="12" :md="6" v-for="(feature, index) in features" :key="index">
        <el-card class="feature-card" shadow="hover">
          <el-icon class="feature-icon" :size="50">
            <component :is="feature.icon"></component>
          </el-icon>
          <h3>{{ feature.title }}</h3>
          <p>{{ feature.description }}</p>
        </el-card>
      </el-col>
    </el-row>

    <!-- 今日推荐 -->
    <el-divider>
      <el-icon color="#E6A23C"><Star /></el-icon>
      <span style="margin-left: 8px; font-size: 16px; font-weight: bold;">今日推荐</span>
    </el-divider>
    
    <el-card class="today-book">
      <el-row :gutter="30">
        <el-col :xs="24" :sm="6">
          <div class="book-cover">
            <div class="cover-placeholder bg-color-featured"></div>
            <el-badge value="编辑推荐" type="warning" class="book-badge"></el-badge>
          </div>
        </el-col>
        <el-col :xs="24" :sm="18">
          <div class="book-details">
            <h2 class="book-title">精选推荐：《编程的艺术》</h2>
            <div class="book-meta">
              <el-tag size="small">计算机</el-tag>
              <el-tag size="small" type="success">程序设计</el-tag>
              <el-tag size="small" type="warning">畅销榜</el-tag>
            </div>
            <div class="book-rating">
              <el-rate v-model="bookRating" disabled show-score text-color="#ff9900"></el-rate>
              <span>(2543条评价)</span>
            </div>
            <p class="book-intro">这本书深入浅出地讲解了编程思维与技巧，适合各个层次的读者学习参考。涵盖了从基础算法到高级设计模式的全面内容，是程序员进阶的必读之选。</p>
            <div class="book-price-section">
              <div class="book-price">
                <span class="current-price">¥68</span>
                <span class="original-price">¥128</span>
                <span class="discount">5.3折</span>
              </div>
              <div class="book-actions">
                <el-tooltip content="加入购物车" placement="top" :show-after="500">
                  <el-button type="danger" round>
                    <el-icon class="el-icon--left"><ShoppingCart /></el-icon>
                    立即购买
                  </el-button>
                </el-tooltip>
                <el-tooltip content="收藏" placement="top" :show-after="500">
                  <el-button circle>
                    <el-icon><Star /></el-icon>
                  </el-button>
                </el-tooltip>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 热门推荐 -->
    <el-divider>
      <el-icon color="#F56C6C"><DataLine /></el-icon>
      <span style="margin-left: 8px; font-size: 16px; font-weight: bold;">热门榜单</span>
    </el-divider>
    
    <el-tabs type="border-card">
      <el-tab-pane>
        <template #label>
          <el-icon><TrendCharts /></el-icon>
          <span>畅销榜</span>
        </template>
        <el-table :data="topSales" stripe style="width: 100%">
          <el-table-column width="60">
            <template #default="scope">
              <div class="rank-number" :class="scope.row.id <= 3 ? 'top-three' : ''">{{ scope.row.id }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="title" label="图书"></el-table-column>
          <el-table-column prop="author" label="作者" width="120"></el-table-column>
          <el-table-column prop="sales" label="销量" width="120"></el-table-column>
          <el-table-column prop="price" label="价格" width="100">
            <template #default="scope">
              <span class="rank-price">¥{{ scope.row.price }}</span>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane>
        <template #label>
          <el-icon><StarFilled /></el-icon>
          <span>好评榜</span>
        </template>
        <el-empty description="敬请期待"></el-empty>
      </el-tab-pane>
      <el-tab-pane>
        <template #label>
          <el-icon><Timer /></el-icon>
          <span>新书榜</span>
        </template>
        <el-empty description="敬请期待"></el-empty>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { useCounterStore } from '@/stores/counter'
import { ref } from 'vue'
import { 
  Reading, ShoppingCart, StarFilled, Opportunity, Star, Service,
  Picture, Document, DataLine, TrendCharts, Timer, Collection, Grid
} from '@element-plus/icons-vue'

const store = useCounterStore()
const bookRating = ref(4.7)

// 轮播图数据
const carouselItems = [
  {
    id: 1,
    title: "畅销新书推荐",
    description: "发现本月最受欢迎的新书籍",
    imageUrl: "https://img.freepik.com/free-photo/book-composition-with-open-book_23-2147690555.jpg",
    link: "/bookstore"
  },
  {
    id: 2,
    title: "限时特惠活动",
    description: "精选好书，低至5折起",
    imageUrl: "https://img.freepik.com/free-photo/front-view-stacked-books-graduation-cap-education-day_23-2149241011.jpg",
    link: "/bookstore"
  },
  {
    id: 3,
    title: "经典文学系列",
    description: "珍藏版世界名著，重温经典",
    imageUrl: "https://img.freepik.com/free-photo/stack-books-table_23-2147845973.jpg",
    link: "/bookstore"
  },
  {
    id: 4,
    title: "科技图书专区",
    description: "探索前沿科技，引领未来趋势",
    imageUrl: "https://img.freepik.com/free-photo/digital-tablet-cozy-home-desk-remote-lifestyle_53876-147137.jpg",
    link: "/bookstore"
  }
]

// 快捷分类按钮
const shortcuts = [
  {
    title: '文学小说',
    icon: Reading,
    link: '/bookstore'
  },
  {
    title: '热门榜单',
    icon: Star,
    link: '/bookstore'
  },
  {
    title: '特惠专区',
    icon: Opportunity,
    link: '/bookstore'
  },
  {
    title: '全部分类',
    icon: Grid,
    link: '/bookstore'
  }
]

// 功能特点数据
const features = [
  {
    icon: Reading,
    title: '海量图书',
    description: '超过100万本电子图书，涵盖各类题材与领域'
  },
  {
    icon: StarFilled,
    title: '个性推荐',
    description: '基于您的阅读习惯，智能推荐您可能喜欢的书籍'
  },
  {
    icon: Opportunity,
    title: '限时特惠',
    description: '每日更新特惠活动，好书低至3折起'
  },
  {
    icon: ShoppingCart,
    title: '便捷购买',
    description: '支持多种支付方式，购买后即可阅读'
  }
]

// 畅销榜数据
const topSales = [
  { id: 1, title: '热门书籍标题 1', author: '知名作家', sales: 9000, price: 55 },
  { id: 2, title: '热门书籍标题 2', author: '知名作家', sales: 8000, price: 60 },
  { id: 3, title: '热门书籍标题 3', author: '知名作家', sales: 7000, price: 65 },
  { id: 4, title: '热门书籍标题 4', author: '知名作家', sales: 6000, price: 70 },
  { id: 5, title: '热门书籍标题 5', author: '知名作家', sales: 5000, price: 75 }
]
</script> 