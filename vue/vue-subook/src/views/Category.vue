<template>
  <div class="category-container">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>{{ categoryName }}</el-breadcrumb-item>
    </el-breadcrumb>
    
    <div class="category-header">
      <h1>{{ categoryName }}</h1>
      <div class="filter-options">
        <el-radio-group v-model="sortBy" size="small">
          <el-radio-button label="default">默认排序</el-radio-button>
          <el-radio-button label="sales">销量优先</el-radio-button>
          <el-radio-button label="price-asc">价格从低到高</el-radio-button>
          <el-radio-button label="price-desc">价格从高到低</el-radio-button>
          <el-radio-button label="rating">评分优先</el-radio-button>
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
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const categoryId = computed(() => Number(route.params.id))
const loading = ref(true)
const sortBy = ref('default')
const currentPage = ref(1)
const pageSize = ref(12)
const totalBooks = ref(0)

// 分类数据
const categories = [
  { id: 1, name: '文学小说' },
  { id: 2, name: '经管励志' },
  { id: 3, name: '人文社科' },
  { id: 4, name: '科技科普' },
  { id: 5, name: '生活艺术' },
  { id: 6, name: '童书' }
]

// 当前分类名称
const categoryName = computed(() => {
  const category = categories.find(item => item.id === categoryId.value)
  return category ? category.name : '未知分类'
})

// 模拟图书数据
const allBooks = [
  // 文学小说
  {
    id: 101,
    title: '克拉拉与太阳',
    author: '[英] 石黑一雄',
    cover: 'https://img2.doubanio.com/view/subject/s/public/s33821754.jpg',
    price: 59.00,
    originalPrice: 69.00,
    categoryId: 1,
    sales: 1200,
    rating: 4.5
  },
  {
    id: 102,
    title: '活着',
    author: '余华',
    cover: 'https://img1.doubanio.com/view/subject/s/public/s33859154.jpg',
    price: 39.80,
    originalPrice: 45.00,
    discount: '8.8折',
    categoryId: 1,
    sales: 8500,
    rating: 4.8
  },
  {
    id: 103,
    title: '百年孤独',
    author: '[哥伦比亚] 加西亚·马尔克斯',
    cover: 'https://img9.doubanio.com/view/subject/s/public/s27237850.jpg',
    price: 39.50,
    originalPrice: 55.00,
    discount: '7.2折',
    categoryId: 1,
    sales: 5600,
    rating: 4.7
  },
  {
    id: 104,
    title: '红楼梦',
    author: '曹雪芹',
    cover: 'https://img2.doubanio.com/view/subject/s/public/s1070959.jpg',
    price: 59.70,
    originalPrice: 99.50,
    discount: '6折',
    categoryId: 1,
    sales: 3400,
    rating: 4.9
  },
  
  // 经管励志
  {
    id: 201,
    title: '被讨厌的勇气',
    author: '[日] 岸见一郎 / 古贺史健',
    cover: 'https://img9.doubanio.com/view/subject/s/public/s29237648.jpg',
    price: 35.00,
    originalPrice: 39.80,
    categoryId: 2,
    sales: 7800,
    rating: 4.5
  },
  {
    id: 202,
    title: '认知觉醒',
    author: '周岭',
    cover: 'https://img2.doubanio.com/view/subject/s/public/s33956854.jpg',
    price: 59.00,
    originalPrice: 69.00,
    discount: '8.5折',
    categoryId: 2,
    sales: 3200,
    rating: 4.4
  },
  
  // 人文社科
  {
    id: 301,
    title: '人类简史',
    author: '[以色列] 尤瓦尔·赫拉利',
    cover: 'https://img1.doubanio.com/view/subject/s/public/s27814883.jpg',
    price: 58.00,
    originalPrice: 68.00,
    discount: '8.5折',
    categoryId: 3,
    sales: 9800,
    rating: 4.7
  },
  {
    id: 302,
    title: '枢纽',
    author: '施展',
    cover: 'https://img1.doubanio.com/view/subject/s/public/s29799794.jpg',
    price: 62.30,
    originalPrice: 89.00,
    discount: '7折',
    categoryId: 3,
    sales: 2400,
    rating: 4.6
  },
  
  // 科技科普
  {
    id: 401,
    title: '三体',
    author: '刘慈欣',
    cover: 'https://img9.doubanio.com/view/subject/s/public/s2768378.jpg',
    price: 23.00,
    originalPrice: 29.00,
    discount: '7.9折',
    categoryId: 4,
    sales: 12000,
    rating: 4.9
  },
  {
    id: 402,
    title: '时间简史',
    author: '[英] 史蒂芬·霍金',
    cover: 'https://img3.doubanio.com/view/subject/s/public/s29958602.jpg',
    price: 35.90,
    originalPrice: 45.00,
    discount: '8折',
    categoryId: 4,
    sales: 4500,
    rating: 4.6
  },
  
  // 生活艺术
  {
    id: 501,
    title: '生活蒙太奇',
    author: '范晓萱',
    cover: 'https://img1.doubanio.com/view/subject/s/public/s33633191.jpg',
    price: 42.00,
    originalPrice: 52.80,
    categoryId: 5,
    sales: 1800,
    rating: 4.3
  },
  {
    id: 502,
    title: '远方的红树林',
    author: '白先勇',
    cover: 'https://img2.doubanio.com/view/subject/s/public/s33916157.jpg',
    price: 45.00,
    originalPrice: null,
    categoryId: 5,
    sales: 1200,
    rating: 4.4
  },
  
  // 童书
  {
    id: 601,
    title: '窗边的小豆豆',
    author: '[日] 黑柳彻子',
    cover: 'https://img9.doubanio.com/view/subject/s/public/s1067911.jpg',
    price: 20.00,
    originalPrice: 39.80,
    discount: '5折',
    categoryId: 6,
    sales: 6800,
    rating: 4.8
  },
  {
    id: 602,
    title: '绿野仙踪',
    author: '[美] 莱曼·弗兰克·鲍姆',
    cover: 'https://img9.doubanio.com/view/subject/s/public/s3668327.jpg',
    price: 22.50,
    originalPrice: 29.80,
    discount: '7.5折',
    categoryId: 6,
    sales: 3400,
    rating: 4.5
  }
]

// 获取对应分类的书籍
const books = ref([])

// 根据分类ID和排序选项获取图书数据
const fetchBooks = () => {
  loading.value = true
  
  // 模拟API请求延迟
  setTimeout(() => {
    // 过滤当前分类的图书
    let result = allBooks.filter(book => book.categoryId === categoryId.value)
    
    // 根据排序选项排序
    switch (sortBy.value) {
      case 'sales':
        result.sort((a, b) => b.sales - a.sales)
        break
      case 'price-asc':
        result.sort((a, b) => a.price - b.price)
        break
      case 'price-desc':
        result.sort((a, b) => b.price - a.price)
        break
      case 'rating':
        result.sort((a, b) => b.rating - a.rating)
        break
      default:
        // 默认排序，保持原顺序
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

// 监听分类ID变化
watch(categoryId, () => {
  currentPage.value = 1 // 重置页码
  sortBy.value = 'default' // 重置排序选项
  fetchBooks()
})

// 初始加载
onMounted(() => {
  fetchBooks()
})
</script>

<style scoped>
.category-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.category-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 20px 0;
}

.category-header h1 {
  font-size: 24px;
  margin: 0;
  position: relative;
  padding-left: 15px;
}

.category-header h1::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 24px;
  background-color: #409EFF;
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

.pagination {
  margin-top: 30px;
  text-align: center;
}
</style> 