<template>
  <div class="book-detail-container">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: `/category/${book.categoryId}` }">{{ getCategoryName(book.categoryId) }}</el-breadcrumb-item>
      <el-breadcrumb-item>{{ book.title }}</el-breadcrumb-item>
    </el-breadcrumb>
    
    <el-card class="book-detail-card">
      <div class="book-detail-content">
        <div class="book-image-section">
          <el-image :src="book.cover" fit="contain" class="book-cover" />
        </div>
        
        <div class="book-info-section">
          <h1>{{ book.title }}</h1>
          <div class="book-meta">
            <span class="author">作者：{{ book.author }}</span>
            <el-divider direction="vertical"></el-divider>
            <span class="publisher">出版社：{{ book.publisher }}</span>
            <el-divider direction="vertical"></el-divider>
            <span class="pubdate">出版日期：{{ book.pubdate }}</span>
          </div>
          
          <div class="book-rating">
            <span>评分：</span>
            <el-rate v-model="book.rating" disabled text-color="#ff9900" show-score />
            <span class="rating-count">({{ book.ratingCount }}人评价)</span>
          </div>
          
          <el-divider></el-divider>
          
          <div class="price-section">
            <div class="current-price">¥{{ book.price.toFixed(2) }}</div>
            <div v-if="book.originalPrice" class="original-price">定价：<s>¥{{ book.originalPrice.toFixed(2) }}</s></div>
            <div v-if="book.discount" class="discount">{{ book.discount }}</div>
          </div>
          
          <div class="quantity-section">
            <span class="label">数量：</span>
            <el-input-number v-model="quantity" :min="1" :max="10" />
            <span class="stock-info">库存{{ book.stock }}件</span>
          </div>
          
          <div class="action-section">
            <el-button type="primary" size="large" @click="addToCart">加入购物车</el-button>
            <el-button type="danger" size="large" @click="buyNow">立即购买</el-button>
            <el-button size="large" @click="addToFavorite">
              <el-icon><Star /></el-icon>收藏
            </el-button>
          </div>
          
          <div class="service-section">
            <h4>服务承诺</h4>
            <ul class="service-list">
              <li><el-icon><Check /></el-icon>正品保障</li>
              <li><el-icon><Check /></el-icon>急速配送</li>
              <li><el-icon><Check /></el-icon>无忧退换</li>
              <li><el-icon><Check /></el-icon>休闲食品</li>
            </ul>
          </div>
        </div>
      </div>
    </el-card>
    
    <el-card class="book-detail-tabs">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="详情信息" name="details">
          <div class="book-details">
            <h3>图书详情</h3>
            <el-divider></el-divider>
            
            <el-descriptions :column="2" border>
              <el-descriptions-item label="ISBN">{{ book.isbn }}</el-descriptions-item>
              <el-descriptions-item label="页数">{{ book.pages }}页</el-descriptions-item>
              <el-descriptions-item label="装帧">{{ book.binding }}</el-descriptions-item>
              <el-descriptions-item label="开本">{{ book.format }}</el-descriptions-item>
              <el-descriptions-item label="版次">{{ book.edition }}</el-descriptions-item>
              <el-descriptions-item label="印次">{{ book.impression }}</el-descriptions-item>
              <el-descriptions-item label="纸张">{{ book.paper }}</el-descriptions-item>
              <el-descriptions-item label="分类">{{ getCategoryName(book.categoryId) }}</el-descriptions-item>
            </el-descriptions>
            
            <h3>内容简介</h3>
            <el-divider></el-divider>
            <div class="book-description">
              <p v-for="(paragraph, index) in book.description.split('\n')" :key="index">
                {{ paragraph }}
              </p>
            </div>
            
            <h3>作者简介</h3>
            <el-divider></el-divider>
            <div class="author-description">
              <p v-for="(paragraph, index) in book.authorDescription.split('\n')" :key="index">
                {{ paragraph }}
              </p>
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="目录" name="catalog">
          <div class="book-catalog">
            <div v-if="book.catalog" class="catalog-content">
              <p v-for="(line, index) in book.catalog.split('\n')" :key="index" class="catalog-line">
                {{ line }}
              </p>
            </div>
            <el-empty v-else description="暂无目录信息"></el-empty>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="评价({{ book.reviews.length }})" name="reviews">
          <div class="book-reviews">
            <div v-if="book.reviews.length > 0">
              <div v-for="review in book.reviews" :key="review.id" class="review-item">
                <div class="review-header">
                  <el-avatar :size="40">{{ review.user.charAt(0) }}</el-avatar>
                  <div class="review-meta">
                    <div class="user-name">{{ review.user }}</div>
                    <div class="review-rating">
                      <el-rate v-model="review.rating" disabled text-color="#ff9900" />
                      <span class="review-date">{{ review.date }}</span>
                    </div>
                  </div>
                </div>
                <div class="review-content">{{ review.content }}</div>
              </div>
            </div>
            <el-empty v-else description="暂无评价信息"></el-empty>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
    
    <el-card class="recommend-books">
      <div class="section-header">
        <h2>猜你喜欢</h2>
      </div>
      <el-row :gutter="20">
        <el-col :span="4" v-for="item in relatedBooks" :key="item.id">
          <el-card shadow="hover" class="book-card" @click="goToBookDetail(item.id)">
            <el-image :src="item.cover" fit="cover" class="book-cover-small" lazy></el-image>
            <div class="book-title">{{ item.title }}</div>
            <div class="book-price">¥{{ item.price.toFixed(2) }}</div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, Check } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const bookId = computed(() => Number(route.params.id))
const quantity = ref(1)
const activeTab = ref('details')

// 分类数据
const categories = [
  { id: 1, name: '文学小说' },
  { id: 2, name: '经管励志' },
  { id: 3, name: '人文社科' },
  { id: 4, name: '科技科普' },
  { id: 5, name: '生活艺术' },
  { id: 6, name: '童书' }
]

// 模拟图书详情数据
const book = ref({
  id: 201,
  title: '活着',
  author: '余华',
  publisher: '作家出版社',
  pubdate: '2012-08-01',
  cover: 'https://img1.doubanio.com/view/subject/s/public/s33859154.jpg',
  price: 39.80,
  originalPrice: 45.00,
  discount: '8.8折',
  rating: 4.5,
  ratingCount: 496825,
  stock: 1000,
  categoryId: 1,
  isbn: '9787506365437',
  pages: 191,
  binding: '平装',
  format: '32开',
  edition: '1',
  impression: '1',
  paper: '胶版纸',
  description: '《活着》是作家余华的代表作之一，讲述了在大时代背景下，普通人福贵的人生故事。福贵出身于地主家庭，因赌博而导致家道没落。\n短短几十年间，福贵亲历了战争、"解放"、"大跃进"、"文化大革命"等社会变革，目睹了无数死亡，最终只剩下一头老牛相伴。\n小说以朴实的语言描绘了福贵的坎坷命运，展现了中国底层普通人的尊严与坚韧，揭示了人如何在困境中寻找生活的意义。\n余华以冷静的笔触，通过福贵的故事展现了生命的顽强与生活的残酷，让读者在悲凉的叙述中感受生命的力量。',
  authorDescription: '余华，1960年4月3日生于浙江杭州，中国当代作家，2004年法兰西文学和艺术骑士勋章获得者。\n1983年开始发表小说，主要作品有《活着》《许三观卖血记》《在细雨中呼喊》《兄弟》等。\n其作品被翻译成20多种文字在美国、英国、法国等30多个国家和地区出版，赢得了广泛的国际赞誉。\n作品风格上以"先锋派"起家，与格非、苏童等人一起被称作"20世纪80年代中国先锋小说的代表作家"。',
  catalog: '第一章\n第二章\n第三章\n第四章\n第五章\n第六章',
  reviews: [
    {
      id: 1,
      user: '读者A',
      rating: 5,
      date: '2023-01-15',
      content: '这本书让我对生命有了新的思考，福贵的故事太震撼了，看完很久都无法平静。'
    },
    {
      id: 2,
      user: '读者B',
      rating: 4,
      date: '2023-02-22',
      content: '余华的笔触冷静克制，但却能让人感受到强烈的情感冲击。'
    },
    {
      id: 3,
      user: '读者C',
      rating: 5,
      date: '2023-03-05',
      content: '读完这本书，我变得更珍惜现在的生活了。福贵的一生很不幸，但他还是选择了活着，这种生命力很感人。'
    }
  ]
})

// 模拟相关推荐图书
const relatedBooks = [
  {
    id: 101,
    title: '许三观卖血记',
    author: '余华',
    cover: 'https://img9.doubanio.com/view/subject/s/public/s26012241.jpg',
    price: 35.00
  },
  {
    id: 102,
    title: '兄弟',
    author: '余华',
    cover: 'https://img1.doubanio.com/view/subject/s/public/s26577048.jpg',
    price: 59.00
  },
  {
    id: 103,
    title: '在细雨中呼喊',
    author: '余华',
    cover: 'https://img1.doubanio.com/view/subject/s/public/s1151479.jpg',
    price: 29.80
  },
  {
    id: 104,
    title: '第七天',
    author: '余华',
    cover: 'https://img9.doubanio.com/view/subject/s/public/s27279654.jpg',
    price: 32.00
  },
  {
    id: 105,
    title: '文学或者音乐',
    author: '余华',
    cover: 'https://img9.doubanio.com/view/subject/s/public/s29634528.jpg',
    price: 42.00
  },
  {
    id: 106,
    title: '我能想到最浪漫的事',
    author: '余华',
    cover: 'https://img1.doubanio.com/view/subject/s/public/s29906146.jpg',
    price: 45.00
  }
]

// 根据分类ID获取分类名称
const getCategoryName = (categoryId) => {
  const category = categories.find(item => item.id === categoryId)
  return category ? category.name : '未分类'
}

// 初次加载时模拟获取图书详情
onMounted(() => {
  // 在实际应用中，这里应该调用API获取图书详情
  // fetchBookDetail(bookId.value)
})

// 添加到购物车
const addToCart = () => {
  ElMessage.success(`已将《${book.value.title}》${quantity.value}件加入购物车`)
}

// 立即购买
const buyNow = () => {
  ElMessage.success(`正在跳转到结算页面...`)
  // 实际应用中跳转到结算页面
  // router.push('/checkout')
}

// 收藏
const addToFavorite = () => {
  ElMessage.success(`已收藏《${book.value.title}》`)
}

// 查看其他图书
const goToBookDetail = (id) => {
  router.push(`/book/${id}`)
}
</script>

<style scoped>
.book-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.book-detail-card {
  margin-top: 20px;
  margin-bottom: 30px;
}

.book-detail-content {
  display: flex;
  gap: 30px;
}

.book-image-section {
  flex: 0 0 300px;
}

.book-cover {
  width: 100%;
  height: 420px;
  border: 1px solid #e0e0e0;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.book-info-section {
  flex: 1;
}

.book-info-section h1 {
  font-size: 24px;
  margin-top: 0;
  margin-bottom: 15px;
}

.book-meta {
  color: #606266;
  margin-bottom: 15px;
}

.book-rating {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.rating-count {
  margin-left: 10px;
  color: #909399;
}

.price-section {
  margin: 20px 0;
}

.current-price {
  font-size: 28px;
  color: #ff6700;
  font-weight: bold;
}

.original-price {
  color: #909399;
  margin-top: 5px;
}

.discount {
  display: inline-block;
  background-color: #ff6700;
  color: #fff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  margin-top: 5px;
}

.quantity-section {
  display: flex;
  align-items: center;
  margin: 20px 0;
}

.label {
  margin-right: 10px;
}

.stock-info {
  margin-left: 15px;
  color: #909399;
}

.action-section {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.service-section {
  border-top: 1px dashed #e0e0e0;
  padding-top: 20px;
}

.service-section h4 {
  margin-top: 0;
  margin-bottom: 10px;
}

.service-list {
  list-style: none;
  padding: 0;
  display: flex;
  gap: 20px;
}

.service-list li {
  display: flex;
  align-items: center;
  color: #606266;
}

.service-list li .el-icon {
  color: #67c23a;
  margin-right: 5px;
}

.book-detail-tabs {
  margin-bottom: 30px;
}

.book-details h3 {
  margin-top: 30px;
  margin-bottom: 15px;
}

.book-description, .author-description {
  line-height: 1.8;
  color: #606266;
}

.book-catalog .catalog-content {
  line-height: 1.8;
  color: #606266;
}

.catalog-line {
  margin: 5px 0;
}

.book-reviews .review-item {
  border-bottom: 1px solid #ebeef5;
  padding: 20px 0;
}

.book-reviews .review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  margin-bottom: 15px;
}

.review-meta {
  margin-left: 15px;
}

.user-name {
  font-weight: bold;
  margin-bottom: 5px;
}

.review-rating {
  display: flex;
  align-items: center;
}

.review-date {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}

.review-content {
  line-height: 1.6;
  color: #303133;
}

.recommend-books {
  margin-bottom: 40px;
}

.recommend-books .section-header {
  margin-bottom: 20px;
}

.recommend-books .section-header h2 {
  font-size: 20px;
  margin: 0;
  position: relative;
  padding-left: 15px;
}

.recommend-books .section-header h2::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 20px;
  background-color: #409EFF;
}

.book-card {
  cursor: pointer;
  transition: all 0.3s;
}

.book-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.book-cover-small {
  height: 160px;
  width: 100%;
  display: block;
  margin-bottom: 10px;
}

.book-title {
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 5px;
}

.book-price {
  color: #ff6700;
  font-weight: bold;
}

@media (max-width: 768px) {
  .book-detail-content {
    flex-direction: column;
  }
  
  .book-image-section {
    flex: none;
    width: 100%;
    display: flex;
    justify-content: center;
  }
  
  .book-cover {
    width: 250px;
    height: 350px;
  }
  
  .action-section {
    flex-wrap: wrap;
  }
  
  .service-list {
    flex-wrap: wrap;
  }
}
</style> 