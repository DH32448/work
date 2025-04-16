<template>
  <div class="page-container">
    <!-- 顶部轮播图 -->
    <el-card class="banner-section" :body-style="{ padding: '0' }">
      <el-carousel height="300px">
        <el-carousel-item v-for="item in 4" :key="item">
          <div class="banner-item bg-color-{{ item }}">
            <h3>精选图书{{ item }}</h3>
          </div>
        </el-carousel-item>
      </el-carousel>
    </el-card>

    <!-- 分类导航 -->
    <el-card class="category-nav">
      <el-scrollbar>
        <el-row class="category-list">
          <el-col v-for="(category, i) in categories" :key="i" :span="2" :xs="4" :sm="3" :md="2">
            <div class="category-item">
              <el-icon><component :is="category.icon" /></el-icon>
              <span>{{ category.name }}</span>
            </div>
          </el-col>
        </el-row>
      </el-scrollbar>
    </el-card>

    <!-- 推荐书籍 -->
    <div class="section-container">
      <el-divider>
        <el-icon color="#409EFF"><Star /></el-icon>
        <span style="margin-left: 8px; font-size: 16px; font-weight: bold;">推荐书籍</span>
      </el-divider>
      
      <el-row :gutter="20">
        <el-col :xs="12" :sm="8" :md="6" v-for="i in 4" :key="i">
          <el-card class="book-card" shadow="hover" :body-style="{ padding: '0' }">
            <div class="book-cover">
              <div class="cover-placeholder bg-color-{{ i }}"></div>
              <el-badge value="热门" type="danger" class="book-badge" v-if="i === 1" />
              <el-badge value="新品" class="book-badge" v-if="i === 3" />
            </div>
            <div class="book-info">
              <h3 class="book-title">书籍标题 {{ i }}</h3>
              <p class="book-author">作者名称</p>
              <el-rate v-model="ratings[i-1]" disabled text-color="#ff9900" size="small"></el-rate>
              <div class="book-price">
                <span class="current-price">¥{{ 30 + i * 10 }}</span>
                <span class="original-price">¥{{ 50 + i * 10 }}</span>
              </div>
              <div class="book-actions">
                <el-tooltip content="加入购物车" placement="top" :show-after="500">
                  <el-button circle size="small">
                    <el-icon><ShoppingCart /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="收藏" placement="top" :show-after="500">
                  <el-button circle size="small">
                    <el-icon><Star /></el-icon>
                  </el-button>
                </el-tooltip>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 特惠活动 -->
    <div class="section-container">
      <el-divider>
        <el-icon color="#E6A23C"><Opportunity /></el-icon>
        <span style="margin-left: 8px; font-size: 16px; font-weight: bold;">特惠活动</span>
      </el-divider>
      
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" v-for="i in 2" :key="i">
          <el-card class="promo-card" shadow="hover" :body-style="{ padding: '0' }">
            <div class="promo-content bg-gradient-{{ i }}">
              <el-icon class="promo-icon"><Opportunity /></el-icon>
              <h3>限时特惠 {{ i }}</h3>
              <p>精选好书，低至5折</p>
              <div class="promo-time">
                <el-icon><Timer /></el-icon>
                <span>剩余 {{ 24 - i * 8 }}:{{ 59 - i * 15 }}</span>
              </div>
              <el-button type="primary">立即查看</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 新书上架 -->
    <div class="section-container">
      <el-divider>
        <el-icon color="#67C23A"><GoodsFilled /></el-icon>
        <span style="margin-left: 8px; font-size: 16px; font-weight: bold;">新书上架</span>
      </el-divider>
      
      <el-row :gutter="20">
        <el-col :xs="12" :sm="8" :md="4" v-for="i in 6" :key="i">
          <el-card class="book-card" shadow="hover" :body-style="{ padding: '0' }">
            <div class="book-cover">
              <div class="cover-placeholder bg-color-{{ (i % 4) + 1 }}"></div>
              <el-badge value="新上架" type="success" class="book-badge" />
            </div>
            <div class="book-info">
              <h3 class="book-title">新书标题 {{ i }}</h3>
              <p class="book-author">作者名称</p>
              <el-rate v-model="newBookRatings[i-1]" disabled text-color="#ff9900" size="small"></el-rate>
              <div class="book-price">
                <span class="current-price">¥{{ 40 + i * 5 }}</span>
              </div>
              <div class="book-actions">
                <el-tooltip content="加入购物车" placement="top" :show-after="500">
                  <el-button circle size="small">
                    <el-icon><ShoppingCart /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="收藏" placement="top" :show-after="500">
                  <el-button circle size="small">
                    <el-icon><Star /></el-icon>
                  </el-button>
                </el-tooltip>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { 
  Collection, Reading, User, Notification, 
  Document, Discount, CreditCard, Star, 
  ArrowRight, ShoppingCart, Timer, GoodsFilled,
  Opportunity
} from '@element-plus/icons-vue'

// 图书评分数据
const ratings = ref([4.5, 5, 4, 4.7])
const newBookRatings = ref([4.2, 3.8, 4.5, 4.3, 4.7, 3.9])

// 分类数据
const categories = [
  { name: '文学小说', icon: 'Reading' },
  { name: '经管励志', icon: 'User' },
  { name: '人文社科', icon: 'Document' },
  { name: '科技科普', icon: 'Collection' },
  { name: '生活艺术', icon: 'Opportunity' },
  { name: '少儿教育', icon: 'Reading' },
  { name: '限时特惠', icon: 'Discount' },
  { name: '积分兑换', icon: 'CreditCard' },
  { name: '新书上架', icon: 'GoodsFilled' },
  { name: '热门榜单', icon: 'Star' }
]
</script> 