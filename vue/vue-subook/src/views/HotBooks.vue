<template>
  <div class="hot-books-container">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>热门图书</el-breadcrumb-item>
    </el-breadcrumb>
    
    <div class="page-header">
      <h1>热门图书</h1>
      <div class="filter-options">
        <el-radio-group v-model="sortBy" size="small">
          <el-radio-button label="default">热度排序</el-radio-button>
          <el-radio-button label="price-asc">价格从低到高</el-radio-button>
          <el-radio-button label="price-desc">价格从高到低</el-radio-button>
          <el-radio-button label="sales">销量优先</el-radio-button>
        </el-radio-group>
      </div>
    </div>
    
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="5" animated />
    </div>
    
    <div v-else-if="books.length === 0" class="empty-result">
      <el-empty description="暂无图书" />
    </div>
    
    <div v-else class="books-grid">
      <el-row :gutter="20">
        <el-col :xs="12" :sm="8" :md="6" :lg="4" v-for="book in books" :key="book.id">
          <el-card shadow="hover" class="book-card" @click="goToBookDetail(book.id)">
            <div v-if="book.discount" class="discount-tag">{{ book.discount }}折</div>
            <div v-if="book.rank <= 3" class="hot-tag">Top {{ book.rank }}</div>
            <el-image :src="book.cover" fit="cover" class="book-cover" loading="lazy" />
            <div class="book-title">{{ book.title }}</div>
            <div class="book-author">{{ book.author }}</div>
            <div class="book-rating">
              <el-rate
                v-model="book.rating"
                disabled
                text-color="#ff9900"
                score-template="{value}"
                :show-score="true"
              />
              <span class="sales-count">{{ book.salesCount }}人已购买</span>
            </div>
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
    
    <el-pagination
      v-if="books.length > 0"
      layout="prev, pager, next, jumper"
      :total="totalBooks"
      :page-size="pageSize"
      :current-page="currentPage"
      @current-change="handlePageChange"
      class="pagination"
    />
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(true)
const sortBy = ref('default')
const currentPage = ref(1)
const pageSize = ref(12)
const totalBooks = ref(0)

// 模拟热门图书数据
const allHotBooks = [
  {
    id: 201,
    title: '活着',
    author: '余华',
    cover: 'https://img9.doubanio.com/view/subject/s/public/s29053580.jpg',
    price: 39.00,
    originalPrice: 45.00,
    rating: 4.8,
    salesCount: 15680,
    discount: 8.5,
    rank: 1
  },
  {
    id: 202,
    title: '百年孤独',
    author: '[哥伦比亚] 加西亚·马尔克斯',
    cover: 'https://img2.doubanio.com/view/subject/s/public/s27237850.jpg',
    price: 55.00,
    originalPrice: 68.00,
    rating: 4.9,
    salesCount: 14320,
    discount: 8.1,
    rank: 2
  },
  {
    id: 203,
    title: '解忧杂货店',
    author: '[日] 东野圭吾',
    cover: 'https://img2.doubanio.com/view/subject/s/public/s27264181.jpg',
    price: 39.50,
    originalPrice: 49.80,
    rating: 4.5,
    salesCount: 13890,
    discount: 7.9,
    rank: 3
  },
  {
    id: 204,
    title: '围城',
    author: '钱钟书',
    cover: 'https://img1.doubanio.com/view/subject/s/public/s1070222.jpg',
    price: 39.00,
    originalPrice: 47.00,
    rating: 4.6,
    salesCount: 12560,
    discount: 8.3,
    rank: 4
  },
  {
    id: 205,
    title: '月亮与六便士',
    author: '[英] 毛姆',
    cover: 'https://img9.doubanio.com/view/subject/s/public/s28322944.jpg',
    price: 39.50,
    originalPrice: 45.00,
    rating: 4.7,
    salesCount: 11980,
    discount: 8.8,
    rank: 5
  },
  {
    id: 206,
    title: '三体',
    author: '刘慈欣',
    cover: 'https://img9.doubanio.com/view/subject/s/public/s2768378.jpg',
    price: 46.50,
    originalPrice: 58.00,
    rating: 4.9,
    salesCount: 11870,
    discount: 8.0,
    rank: 6
  },
  {
    id: 207,
    title: '人类简史',
    author: '[以色列] 尤瓦尔·赫拉利',
    cover: 'https://img1.doubanio.com/view/subject/s/public/s27814883.jpg',
    price: 68.00,
    originalPrice: 83.00,
    rating: 4.8,
    salesCount: 10980,
    discount: 8.2,
    rank: 7
  },
  {
    id: 208,
    title: '平凡的世界',
    author: '路遥',
    cover: 'https://img9.doubanio.com/view/subject/s/public/s29634528.jpg',
    price: 128.00,
    originalPrice: 168.00,
    rating: 4.7,
    salesCount: 10540,
    discount: 7.6,
    rank: 8
  },
  {
    id: 209,
    title: '红楼梦',
    author: '曹雪芹',
    cover: 'https://img1.doubanio.com/view/subject/s/public/s1070959.jpg',
    price: 59.70,
    originalPrice: 66.00,
    rating: 4.8,
    salesCount: 9870,
    discount: 9.0,
    rank: 9
  },
  {
    id: 210,
    title: '挪威的森林',
    author: '[日] 村上春树',
    cover: 'https://img2.doubanio.com/view/subject/s/public/s1228930.jpg',
    price: 35.00,
    originalPrice: 45.00,
    rating: 4.6,
    salesCount: 9650,
    discount: 7.8,
    rank: 10
  },
  {
    id: 211,
    title: '追风筝的人',
    author: '[美] 卡勒德·胡赛尼',
    cover: 'https://img2.doubanio.com/view/subject/s/public/s1727290.jpg',
    price: 29.00,
    originalPrice: 39.00,
    rating: 4.7,
    salesCount: 9320,
    discount: 7.4,
    rank: 11
  },
  {
    id: 212,
    title: '明朝那些事儿',
    author: '当年明月',
    cover: 'https://img1.doubanio.com/view/subject/s/public/s29531981.jpg',
    price: 399.00,
    originalPrice: 498.80,
    rating: 4.5,
    salesCount: 8970,
    discount: 8.0,
    rank: 12
  }
]

// 获取图书数据
const books = ref([])

// 根据排序选项获取图书数据
const fetchBooks = () => {
  loading.value = true
  
  // 模拟API请求延迟
  setTimeout(() => {
    // 复制一份数据，避免修改原始数据
    let result = [...allHotBooks]
    
    // 根据排序选项排序
    switch (sortBy.value) {
      case 'price-asc':
        result.sort((a, b) => a.price - b.price)
        break
      case 'price-desc':
        result.sort((a, b) => b.price - a.price)
        break
      case 'sales':
        result.sort((a, b) => b.salesCount - a.salesCount)
        break
      default:
        // 默认排序，按热度排序（即rank）
        result.sort((a, b) => a.rank - b.rank)
        break
    }
    
    totalBooks.value = result.length
    
    // 分页
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    books.value = result.slice(start, end)
    
    loading.value = false
  }, 500)
}

// 页码变化
const handlePageChange = (page) => {
  currentPage.value = page
  fetchBooks()
  
  // 滚动到顶部
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

// 查看图书详情
const goToBookDetail = (id) => {
  router.push(`/book/${id}`)
}

// 加入购物车
const addToCart = (book) => {
  ElMessage.success(`已将《${book.title}》加入购物车`)
}

// 监听排序选项变化
watch(sortBy, () => {
  currentPage.value = 1 // 重置页码
  fetchBooks()
})

// 初始加载
onMounted(() => {
  fetchBooks()
})
</script>

<style scoped>
.hot-books-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 20px 0;
}

.page-header h1 {
  font-size: 24px;
  margin: 0;
  position: relative;
  padding-left: 15px;
}

.page-header h1::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 24px;
  background-color: #F56C6C;
}

.loading-container {
  padding: 40px 0;
}

.empty-result {
  padding: 60px 0;
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

.hot-tag {
  position: absolute;
  top: 10px;
  right: 10px;
  background-color: #F56C6C;
  color: #fff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  z-index: 1;
}

.discount-tag {
  position: absolute;
  top: 10px;
  left: 10px;
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
  margin-bottom: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.book-rating {
  display: flex;
  flex-direction: column;
  margin-bottom: 5px;
}

.sales-count {
  font-size: 12px;
  color: #909399;
  margin-top: 3px;
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

.pagination {
  margin-top: 30px;
  text-align: center;
}
</style> 