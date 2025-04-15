<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { getBooks, searchBooks, getBooksByCategory, getRecommendedBooks, getNewsItems } from '../api/books';
import { addToCart } from '../api/cart';
import { useRouter } from 'vue-router';
import { checkAuth, logout } from '../api/auth';
import CartIcon from './CartIcon.vue';
import AuthManager from './AuthManager.vue';

const router = useRouter();

// 搜索相关
const searchQuery = ref('');
const currentPage = ref(1);
const pageSize = ref(12);
const total = ref(0);
const loading = ref(false);
const products = ref([]);
const recommendedBooks = ref([]);

// 分类相关
const categories = ref([
  { id: 1, name: '文学', icon: '📚' },
  { id: 2, name: '历史', icon: '📜' },
  { id: 3, name: '科技', icon: '💻' },
  { id: 4, name: '艺术', icon: '🎨' },
  { id: 5, name: '教育', icon: '📖' }
]);

const currentCategory = ref(null);

// 轮播图相关
const defaultBookCover = new URL('../assets/images/default-book-cover.jpg', import.meta.url).href;
const defaultCarouselImage = new URL('../assets/images/default-carousel.jpg', import.meta.url).href;
const defaultProductImage = new URL('../assets/images/default-product.jpg', import.meta.url).href;

const carouselItems = ref([
  { 
    id: 1, 
    title: '精选好书', 
    image: '/images/carousel-1.jpg',
    description: '发现好书' 
  },
  { 
    id: 2, 
    title: '特惠活动', 
    image: '/images/carousel-2.jpg',
    description: '限时特惠' 
  },
  { 
    id: 3, 
    title: '新书上架', 
    image: '/images/carousel-3.jpg',
    description: '新书速递' 
  }
]);

const currentCarouselIndex = ref(0);
const carouselInterval = ref(null);

const startCarousel = () => {
  carouselInterval.value = setInterval(() => {
    nextSlide();
  }, 3000);
};

const stopCarousel = () => {
  if (carouselInterval.value) {
    clearInterval(carouselInterval.value);
  }
};

const nextSlide = () => {
  currentCarouselIndex.value = (currentCarouselIndex.value + 1) % carouselItems.value.length;
};

const prevSlide = () => {
  currentCarouselIndex.value = currentCarouselIndex.value === 0 
    ? carouselItems.value.length - 1 
    : currentCarouselIndex.value - 1;
};

const setSlide = (index) => {
  currentCarouselIndex.value = index;
};

// 推荐商品相关
const newsItems = ref([]);

// 购物车相关
const cartIconRef = ref(null);
const cartMessage = ref('');
const showCartMessage = ref(false);

// 登录相关
const showAuthModal = ref(false);
const isAuthenticated = ref(false);
const userInfo = ref(null);

// 广播信息相关
const broadcasts = ref([
  '欢迎来到书城！',
  '新书上架：《Vue.js 3.0实战指南》',
  '限时特惠：全场图书满200减50',
  '618大促即将开始，敬请期待'
]);
const currentBroadcastIndex = ref(0);

// 计算分页数字
const pageNumbers = computed(() => {
  const total = Math.ceil(total.value / pageSize.value);
  const current = currentPage.value;
  const delta = 2;
  const range = [];
  
  for (let i = Math.max(2, current - delta); i <= Math.min(total - 1, current + delta); i++) {
    range.push(i);
  }
  
  if (current - delta > 2) {
    range.unshift('...');
  }
  if (current + delta < total - 1) {
    range.push('...');
  }
  
  range.unshift(1);
  if (total > 1) {
    range.push(total);
  }
  
  return range;
});

// 获取初始数据
const fetchInitialData = async () => {
  loading.value = true;
  try {
    console.log('开始获取数据...');
    const [booksData, recommendedData, newsData] = await Promise.all([
      getBooks({ page: currentPage.value, pageSize: pageSize.value }),
      getRecommendedBooks(),
      getNewsItems()
    ]);
    
    console.log('获取到的图书数据:', booksData);
    products.value = booksData.items.map(item => ({
      ...item,
      cover: item.cover || defaultProductImage
    }));
    total.value = booksData.total;
    recommendedBooks.value = recommendedData.map(item => ({
      ...item,
      cover: item.cover || defaultBookCover
    }));
    newsItems.value = newsData;
  } catch (error) {
    console.error('获取数据失败:', error);
    products.value = [];
    recommendedBooks.value = [];
    newsItems.value = [];
  } finally {
    loading.value = false;
  }
};

const handleAddToCart = async (book) => {
  try {
    await addToCart(book.id);
    cartMessage.value = '已添加到购物车';
    showCartMessage.value = true;
    // 更新购物车图标数量
    cartIconRef.value?.updateCount();
    setTimeout(() => {
      showCartMessage.value = false;
    }, 2000);
  } catch (error) {
    cartMessage.value = '添加失败，请重试';
    showCartMessage.value = true;
    setTimeout(() => {
      showCartMessage.value = false;
    }, 2000);
  }
};

// 处理分类点击
const handleCategoryClick = async (categoryId) => {
  currentCategory.value = categoryId;
  loading.value = true;
  try {
    const data = await getBooksByCategory(categoryId);
    products.value = data;
  } catch (error) {
    console.error('获取分类数据失败:', error);
  } finally {
    loading.value = false;
  }
};

// 处理搜索
const handleSearch = async () => {
  if (!searchQuery.value.trim()) return;
  loading.value = true;
  try {
    const data = await searchBooks(searchQuery.value);
    products.value = data;
  } finally {
    loading.value = false;
  }
};

// 处理页码变化
const handlePageChange = (page) => {
  currentPage.value = page;
  fetchInitialData();
};

// 检查登录状态
const checkLoginStatus = async () => {
  const { isAuthenticated: authStatus, user } = await checkAuth();
  isAuthenticated.value = authStatus;
  userInfo.value = user;
};

// 处理登录成功
const handleLoginSuccess = (user) => {
  isAuthenticated.value = true;
  userInfo.value = user;
  showAuthModal.value = false;
};

// 处理退出登录
const handleLogout = async () => {
  try {
    await logout();
    isAuthenticated.value = false;
    userInfo.value = null;
    // 跳转到登录页面
    router.push('/login');
  } catch (error) {
    console.error('退出登录失败:', error);
  }
};

// 启动广播轮播
const startBroadcast = () => {
  setInterval(() => {
    currentBroadcastIndex.value = (currentBroadcastIndex.value + 1) % broadcasts.value.length;
  }, 4000);
};

// 图片加载错误处理
const handleImageError = (event, type) => {
  const defaultImages = {
    'book': defaultBookCover,
    'carousel': defaultCarouselImage,
    'product': defaultProductImage
  };
  
  // 防止循环触发
  if (!event.target.dataset.retried) {
    event.target.dataset.retried = 'true';
    event.target.src = defaultImages[type];
  }
};

onMounted(() => {
  checkLoginStatus();
  fetchInitialData();
  startCarousel();
  startBroadcast(); // 启动广播
});

onUnmounted(() => {
  stopCarousel();
});
</script>

<template>
  <div class="home">
    <!-- 顶部导航栏 -->
    <div class="top-nav">
      <div class="nav-container">
        <div class="nav-left">
          <h1 class="site-title">书城</h1>
          
          <!-- 搜索栏移到导航栏 -->
          <div class="search-bar">
            <input 
              v-model="searchQuery"
              type="text" 
              placeholder="搜索图书" 
              class="search-input"
              @keyup.enter="handleSearch"
            >
            <button class="search-btn" @click="handleSearch">
              <i class="search-icon">🔍</i>
            </button>
          </div>
        </div>
        <div class="nav-right">
          <div class="auth-buttons" v-if="!isAuthenticated">
            <el-button type="primary" @click="showAuthModal = true">登录/注册</el-button>
          </div>
          <div class="user-info" v-else>
            <span>欢迎，{{ userInfo?.username }}</span>
            <el-button type="text" @click="handleLogout">退出</el-button>
          </div>
          <CartIcon ref="cartIconRef" />
        </div>
      </div>
    </div>

    <!-- 登录/注册模态框 -->
    <AuthManager 
      v-if="showAuthModal"
      @login-success="handleLoginSuccess"
      @close="showAuthModal = false"
    />

    <div class="main-container">
      <!-- 广播信息 -->
      <div class="broadcast-container">
        <div class="broadcast-icon">📢</div>
        <div class="broadcast-content">
          <transition name="slide-fade">
            <div :key="currentBroadcastIndex" class="broadcast-message">
              {{ broadcasts[currentBroadcastIndex] }}
            </div>
          </transition>
        </div>
      </div>
      
      <!-- 分类导航 -->
      <div class="category-navigation">
        <div 
          v-for="category in categories" 
          :key="category.id"
          :class="['category-tab', { active: currentCategory === category.id }]"
          @click="handleCategoryClick(category.id)"
        >
          <span class="category-icon">{{ category.icon }}</span>
          {{ category.name }}
        </div>
      </div>

      <!-- 轮播图 -->
      <div class="carousel" @mouseenter="stopCarousel" @mouseleave="startCarousel">
        <div class="carousel-container" :style="{ transform: 'translateX(' + (-currentCarouselIndex * 100) + '%)' }">
          <div v-for="item in carouselItems" :key="item.id" class="carousel-item">
            <img 
              :src="item.image" 
              :alt="item.title" 
              class="carousel-image"
              @error="(e) => handleImageError(e, 'carousel')"
              loading="lazy"
            >
            <div class="carousel-content">
              <h3>{{ item.title }}</h3>
              <p>{{ item.description }}</p>
            </div>
          </div>
        </div>
        <button class="carousel-btn prev" @click="prevSlide">❮</button>
        <button class="carousel-btn next" @click="nextSlide">❯</button>
        <div class="carousel-dots">
          <span 
            v-for="(_, index) in carouselItems" 
            :key="index"
            :class="['dot', { active: index === currentCarouselIndex }]"
            @click="setSlide(index)"
          ></span>
        </div>
      </div>

      <!-- 主要内容区 -->
      <div class="content-wrapper">
        <!-- 左侧推荐区 -->
        <div class="side-content">
          <div class="section-title">热门推荐</div>
          <div v-if="recommendedBooks.length === 0" class="empty-state">
            暂无推荐图书
          </div>
          <div class="recommended-list">
            <div 
              v-for="book in recommendedBooks" 
              :key="book.id" 
              class="recommended-item"
            >
              <img 
                :src="book.cover"
                :alt="book.title" 
                class="book-cover" 
                loading="lazy"
                @error="(e) => handleImageError(e, 'book')"
              >
              <div class="book-info">
                <div class="book-title">{{ book.title }}</div>
                <div class="book-rating">
                  <span class="stars">★★★★★</span>
                  <span class="score">{{ book.rating }}</span>
                </div>
                <div class="book-price">¥{{ book.price }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧主要商品展示区 -->
        <div class="main-products">
          <div class="section-title">精选图书</div>
          <div v-if="loading" class="loading-spinner">
            <div class="spinner"></div>
          </div>
          <div v-else-if="products.length === 0" class="empty-state">
            暂无图书数据
          </div>
          <div v-else>
            <div class="product-grid">
              <div 
                v-for="product in products" 
                :key="product.id" 
                class="product-card"
                @click="handleProductClick(product)"
              >
                <div class="product-image">
                  <img 
                    :src="product.cover"
                    :alt="product.title" 
                    loading="lazy"
                    @error="(e) => handleImageError(e, 'product')"
                  >
                </div>
                <div class="product-details">
                  <h4 class="product-title">{{ product.title }}</h4>
                  <p class="product-author">{{ product.author }}</p>
                  <div class="product-rating">
                    <span class="stars">★★★★★</span>
                    <span class="score">{{ product.rating }}</span>
                    <span class="sales">{{ product.sales }}人已购</span>
                  </div>
                  <div class="product-bottom">
                    <span class="price">¥{{ product.price }}</span>
                    <button class="add-cart-btn" @click.stop="handleAddToCart(product)">
                      加入购物车
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- 分页器 -->
            <div class="pagination" v-if="total > pageSize">
              <button 
                class="page-btn"
                :disabled="currentPage === 1"
                @click="handlePageChange(currentPage - 1)"
              >
                上一页
              </button>
              <div class="page-numbers">
                <span 
                  v-for="page in pageNumbers" 
                  :key="page"
                  :class="['page-number', { active: currentPage === page }]"
                  @click="handlePageChange(page)"
                >
                  {{ page }}
                </span>
              </div>
              <button 
                class="page-btn"
                :disabled="currentPage >= Math.ceil(total / pageSize)"
                @click="handlePageChange(currentPage + 1)"
              >
                下一页
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 购物车消息提示 -->
    <div v-if="showCartMessage" class="cart-message" :class="{ 'show': showCartMessage }">
      {{ cartMessage }}
    </div>
  </div>
</template>

<style scoped>
.home {
  margin: 0 auto;
  background-color: #f5f7fa;
}

.main-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 80px 20px 20px;
}

.top-nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: #ffffff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  z-index: 100;
}

.nav-container {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 20px;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 30px;
}

.site-title {
  font-size: 24px;
  font-weight: bold;
  color: #4CAF50;
  margin: 0;
}

.search-bar {
  display: flex;
  align-items: center;
}

.search-input {
  width: 300px;
  padding: 8px 15px;
  border: 1px solid #e0e0e0;
  border-radius: 4px 0 0 4px;
  font-size: 14px;
  outline: none;
  transition: all 0.3s;
}

.search-input:focus {
  border-color: #4CAF50;
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2);
}

.search-btn {
  padding: 0 15px;
  height: 36px;
  background: #4CAF50;
  border: none;
  border-radius: 0 4px 4px 0;
  color: white;
  cursor: pointer;
  transition: background-color 0.3s;
}

.search-btn:hover {
  background: #45a049;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.auth-buttons {
  margin-left: auto;
  display: flex;
  align-items: center;
}

.user-info {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 10px;
}

.category-navigation {
  display: flex;
  gap: 10px;
  padding: 15px 0;
  overflow-x: auto;
  scrollbar-width: thin;
}

.category-tab {
  padding: 10px 20px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 5px;
  background: white;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  white-space: nowrap;
}

.category-tab:hover {
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
  transform: translateY(-2px);
}

.category-tab.active {
  background: #4CAF50;
  color: white;
}

.category-icon {
  font-size: 18px;
}

.content-wrapper {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 25px;
  margin-top: 25px;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid #4CAF50;
  color: #2c3e50;
}

.side-content {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  height: fit-content;
}

.recommended-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.recommended-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-radius: 6px;
  transition: all 0.3s;
  cursor: pointer;
  border: 1px solid transparent;
}

.recommended-item:hover {
  background: #f9f9f9;
  border-color: #e0e0e0;
  transform: translateY(-2px);
}

.book-cover {
  width: 60px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  background-color: #f5f5f5;
  border: 1px solid #eee;
  transition: transform 0.3s;
}

.recommended-item:hover .book-cover {
  transform: scale(1.05);
}

.book-info {
  flex: 1;
}

.book-title {
  font-size: 14px;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  font-weight: 500;
}

.book-rating {
  color: #ffd700;
  font-size: 12px;
  margin-bottom: 5px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.book-price {
  color: #ff4757;
  font-weight: bold;
}

.main-products {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 25px;
  margin-bottom: 25px;
}

.product-card {
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
  cursor: pointer;
  background: white;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  border: 1px solid #f0f0f0;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 15px rgba(0,0,0,0.1);
}

.product-image {
  height: 220px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  background-color: #f5f5f5;
  transition: transform 0.3s;
  border: 1px solid #eee;
  min-height: 200px;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.product-details {
  padding: 15px;
}

.product-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 500;
}

.product-author {
  color: #666;
  font-size: 14px;
  margin-bottom: 8px;
}

.product-rating {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 12px;
}

.stars {
  color: #ffd700;
}

.sales {
  color: #666;
  font-size: 12px;
}

.product-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  color: #ff4757;
  font-weight: bold;
  font-size: 18px;
}

.add-cart-btn {
  padding: 8px 14px;
  background: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.add-cart-btn:hover {
  background: #45a049;
  transform: translateY(-2px);
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-top: 30px;
}

.page-numbers {
  display: flex;
  gap: 5px;
}

.page-number {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  background: #f5f5f5;
}

.page-number:hover {
  background: #e0e0e0;
}

.page-number.active {
  background: #4CAF50;
  color: white;
}

.page-btn {
  padding: 8px 16px;
  background: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.page-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.loading-spinner {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #4CAF50;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.cart-message {
  position: fixed;
  top: 20px;
  right: 20px;
  background-color: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 12px 24px;
  border-radius: 8px;
  z-index: 1000;
  transform: translateX(120%);
  transition: transform 0.3s ease;
}

.cart-message.show {
  transform: translateX(0);
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #666;
  font-size: 14px;
  background: #f9f9f9;
  border-radius: 8px;
  margin: 20px 0;
}

/* 广播样式 */
.broadcast-container {
  background: linear-gradient(to right, #fff8e1, #fffde7);
  padding: 12px 20px;
  margin-bottom: 20px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.broadcast-icon {
  font-size: 20px;
  color: #ff9800;
}

.broadcast-content {
  flex: 1;
  overflow: hidden;
  position: relative;
  height: 24px;
}

.broadcast-message {
  position: absolute;
  width: 100%;
  color: #666;
}

/* 轮播图样式 */
.carousel {
  position: relative;
  width: 100%;
  height: 400px;
  margin-bottom: 25px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.carousel-container {
  display: flex;
  transition: transform 0.5s ease;
  height: 100%;
}

.carousel-item {
  min-width: 100%;
  position: relative;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  background-color: #f5f5f5;
  border: 1px solid #eee;
  min-height: 300px;
}

.carousel-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 25px;
  background: linear-gradient(transparent, rgba(0,0,0,0.7));
  color: white;
}

.carousel-content h3 {
  margin: 0 0 10px 0;
  font-size: 28px;
  text-shadow: 0 1px 3px rgba(0,0,0,0.3);
}

.carousel-content p {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
}

.carousel-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(255,255,255,0.3);
  border: none;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  color: white;
  font-size: 24px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(2px);
}

.carousel-btn:hover {
  background: rgba(255,255,255,0.5);
  transform: translateY(-50%) scale(1.1);
}

.carousel-btn.prev {
  left: 20px;
}

.carousel-btn.next {
  right: 20px;
}

.carousel-dots {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
}

.dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: rgba(255,255,255,0.5);
  cursor: pointer;
  transition: all 0.3s;
}

.dot.active {
  background: white;
  transform: scale(1.2);
}

/* 过渡动画 */
.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.5s ease;
}

.slide-fade-enter-from {
  transform: translateY(20px);
  opacity: 0;
}

.slide-fade-leave-to {
  transform: translateY(-20px);
  opacity: 0;
}

@media (max-width: 1200px) {
  .content-wrapper {
    grid-template-columns: 250px 1fr;
    gap: 20px;
  }
}

@media (max-width: 992px) {
  .nav-left {
    gap: 15px;
  }
  
  .search-input {
    width: 220px;
  }
}

@media (max-width: 768px) {
  .main-container {
    padding: 70px 15px 15px;
  }
  
  .content-wrapper {
    grid-template-columns: 1fr;
  }
  
  .nav-container {
    flex-direction: column;
    padding: 10px 15px;
    height: auto;
  }
  
  .top-nav {
    height: auto;
  }
  
  .nav-left, .nav-right {
    width: 100%;
    justify-content: space-between;
    margin-bottom: 10px;
  }
  
  .search-input {
    width: 100%;
  }
  
  .category-tab {
    padding: 8px 15px;
    font-size: 14px;
  }
  
  .product-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 15px;
  }
  
  .carousel {
    height: 250px;
  }
  
  .carousel-content h3 {
    font-size: 22px;
  }
  
  .carousel-content p {
    font-size: 14px;
  }
  
  .carousel-btn {
    width: 36px;
    height: 36px;
    font-size: 20px;
  }
}

@media (max-width: 576px) {
  .product-grid {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
    gap: 10px;
  }
  
  .product-image {
    height: 180px;
  }
  
  .carousel {
    height: 200px;
  }
}
</style> 