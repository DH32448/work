<template>
  <div class="new-books-container">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>新书上架</el-breadcrumb-item>
    </el-breadcrumb>
    
    <div class="page-header">
      <h1>新书上架</h1>
      <div class="filter-options">
        <el-radio-group v-model="sortBy" size="small">
          <el-radio-button label="default">默认排序</el-radio-button>
          <el-radio-button label="price-asc">价格从低到高</el-radio-button>
          <el-radio-button label="price-desc">价格从高到低</el-radio-button>
          <el-radio-button label="date">出版日期</el-radio-button>
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
            <div class="new-tag">新书</div>
            <el-image :src="book.cover" fit="cover" class="book-cover" loading="lazy" />
            <div class="book-title">{{ book.title }}</div>
            <div class="book-author">{{ book.author }}</div>
            <div class="book-info">
              <div class="publish-date">{{ book.publishDate }}</div>
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

// 模拟新书数据
const allNewBooks = [
  {
    id: 101,
    title: '克拉拉与太阳',
    author: '[英] 石黑一雄',
    cover: 'https://img2.doubanio.com/view/subject/s/public/s33821754.jpg',
    price: 59.00,
    originalPrice: 69.00,
    publishDate: '2023-05-15',
    publisherName: '上海译文出版社',
    isbn: '9787532785230'
  },
  {
    id: 102,
    title: '我们生活在巨大的差距里',
    author: '梁鸿',
    cover: 'https://img9.doubanio.com/view/subject/s/public/s33956867.jpg',
    price: 49.80,
    originalPrice: null,
    publishDate: '2023-05-10',
    publisherName: '人民文学出版社',
    isbn: '9787020170760'
  },
  {
    id: 103,
    title: '生活蒙太奇',
    author: '范晓萱',
    cover: 'https://img1.doubanio.com/view/subject/s/public/s33633191.jpg',
    price: 42.00,
    originalPrice: 52.80,
    publishDate: '2023-04-28',
    publisherName: '北京联合出版公司',
    isbn: '9787559664068'
  },
  {
    id: 104,
    title: '光明共和国',
    author: '[法] 安德烈·纪德',
    cover: 'https://img2.doubanio.com/view/subject/s/public/s33932585.jpg',
    price: 58.00,
    originalPrice: 68.00,
    publishDate: '2023-04-25',
    publisherName: '译林出版社',
    isbn: '9787544787260'
  },
  {
    id: 105,
    title: '远方的红树林',
    author: '白先勇',
    cover: 'https://img2.doubanio.com/view/subject/s/public/s33916157.jpg',
    price: 45.00,
    originalPrice: null,
    publishDate: '2023-04-20',
    publisherName: '广西师范大学出版社',
    isbn: '9787559840332'
  },
  {
    id: 106,
    title: '庸人自扰的艺术',
    author: '[英] 达伦·麦加维',
    cover: 'https://img2.doubanio.com/view/subject/s/public/s33933871.jpg',
    price: 49.80,
    originalPrice: 59.90,
    publishDate: '2023-04-18',
    publisherName: '天津人民出版社',
    isbn: '9787201183671'
  },
  {
    id: 107,
    title: '文化脉络',
    author: '[美] 玛格丽特·米德',
    cover: 'https://img1.doubanio.com/view/subject/s/public/s33957251.jpg',
    price: 58.00,
    originalPrice: 68.00,
    publishDate: '2023-04-15',
    publisherName: '广西师范大学出版社',
    isbn: '9787559842480'
  },
  {
    id: 108,
    title: '成为伍尔夫',
    author: '[英] 伊莲·肖瓦尔特',
    cover: 'https://img2.doubanio.com/view/subject/s/public/s33921177.jpg',
    price: 88.00,
    originalPrice: 98.00,
    publishDate: '2023-04-10',
    publisherName: '上海三联书店',
    isbn: '9787542680662'
  },
  {
    id: 109,
    title: '认知觉醒',
    author: '周岭',
    cover: 'https://img2.doubanio.com/view/subject/s/public/s33956854.jpg',
    price: 59.00,
    originalPrice: 69.00,
    publishDate: '2023-04-05',
    publisherName: '人民邮电出版社',
    isbn: '9787115583710'
  },
  {
    id: 110,
    title: '虚构集',
    author: '陈春成',
    cover: 'https://img1.doubanio.com/view/subject/s/public/s33954995.jpg',
    price: 52.00,
    originalPrice: 59.00,
    publishDate: '2023-04-02',
    publisherName: '人民文学出版社',
    isbn: '9787020171668'
  },
  {
    id: 111,
    title: '长安的荔枝',
    author: '马伯庸',
    cover: 'https://img1.doubanio.com/view/subject/s/public/s34020273.jpg',
    price: 45.00,
    originalPrice: 59.00,
    publishDate: '2023-03-25',
    publisherName: '湖南文艺出版社',
    isbn: '9787540516116'
  },
  {
    id: 112,
    title: '暗涌',
    author: '麦家',
    cover: 'https://img1.doubanio.com/view/subject/s/public/s34031415.jpg',
    price: 59.80,
    originalPrice: 69.80,
    publishDate: '2023-03-20',
    publisherName: '北京十月文艺出版社',
    isbn: '9787530221327'
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
    let result = [...allNewBooks]
    
    // 根据排序选项排序
    switch (sortBy.value) {
      case 'price-asc':
        result.sort((a, b) => a.price - b.price)
        break
      case 'price-desc':
        result.sort((a, b) => b.price - a.price)
        break
      case 'date':
        result.sort((a, b) => new Date(b.publishDate) - new Date(a.publishDate))
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

// 初始加载
onMounted(() => {
  fetchBooks()
})
</script>

<style scoped>
.new-books-container {
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

.new-tag {
  position: absolute;
  top: 10px;
  right: 10px;
  background-color: #409EFF;
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

.book-info {
  font-size: 12px;
  color: #909399;
  margin-bottom: 5px;
}

.publish-date {
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