<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search, ShoppingCart, User, Menu as IconMenu } from '@element-plus/icons-vue'

const router = useRouter()
const searchKeyword = ref('')
const isLoggedIn = computed(() => localStorage.getItem('isLoggedIn') === 'true')
const isAdmin = computed(() => localStorage.getItem('isAdmin') === 'true')
const mobileMenuVisible = ref(false)

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({
      path: '/search',
      query: { keyword: searchKeyword.value.trim() }
    })
    searchKeyword.value = ''
  }
}

const logout = () => {
  localStorage.removeItem('isLoggedIn')
  localStorage.removeItem('isAdmin')
  router.push('/login')
}

const toggleMobileMenu = () => {
  mobileMenuVisible.value = !mobileMenuVisible.value
}
</script>

<template>
  <div class="app-container">
    <el-container>
      <el-header height="70px">
        <div class="header-content">
          <div class="left-section">
            <div class="logo" @click="router.push('/')">
              <img src="./assets/vue.svg" alt="Vue logo" />
              <h1>Su Book</h1>
            </div>
            
            <div class="search-box desktop-only">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索图书"
                class="search-input"
                @keyup.enter="handleSearch"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
            </div>
          </div>
          
          <div class="nav-menu desktop-only">
            <el-menu mode="horizontal" router :default-active="$route.path">
              <el-menu-item index="/">首页</el-menu-item>
              <el-menu-item index="/new-books">新书上架</el-menu-item>
              <el-menu-item index="/hot-books">热门图书</el-menu-item>
              <el-menu-item index="/promotion/1">促销活动</el-menu-item>
              <el-menu-item index="/about">关于我们</el-menu-item>
            </el-menu>
          </div>
          
          <div class="right-section">
            <div class="user-actions">
              <el-badge :value="3" class="cart-badge desktop-only">
                <el-button
                  type="primary"
                  :icon="ShoppingCart"
                  circle
                  @click="router.push('/cart')"
                  class="cart-btn"
                />
              </el-badge>
              
              <el-dropdown v-if="isLoggedIn" class="user-dropdown">
                <div class="user-avatar">
                  <el-avatar :size="40">{{ isAdmin ? 'A' : 'U' }}</el-avatar>
                </div>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="router.push('/user/profile')">个人中心</el-dropdown-item>
                    <el-dropdown-item @click="router.push('/user/orders')">我的订单</el-dropdown-item>
                    <el-dropdown-item v-if="isAdmin" @click="router.push('/admin')">管理后台</el-dropdown-item>
                    <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
              
              <div v-else class="login-actions desktop-only">
                <el-button type="primary" plain @click="router.push('/login')" class="login-btn">登录</el-button>
                <el-button type="primary" @click="router.push('/register')" class="register-btn">注册</el-button>
              </div>
            </div>
            
            <div class="mobile-menu-toggle mobile-only" @click="toggleMobileMenu">
              <el-icon :size="24"><IconMenu /></el-icon>
            </div>
          </div>
        </div>
      </el-header>
      
      <!-- 移动端菜单 -->
      <div class="mobile-menu mobile-only" :class="{ active: mobileMenuVisible }">
        <div class="search-box-mobile">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索图书"
            class="search-input"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </div>
        
        <div class="mobile-nav-links">
          <div class="mobile-nav-item" @click="router.push('/'); toggleMobileMenu()">
            <span>首页</span>
          </div>
          <div class="mobile-nav-item" @click="router.push('/new-books'); toggleMobileMenu()">
            <span>新书上架</span>
          </div>
          <div class="mobile-nav-item" @click="router.push('/hot-books'); toggleMobileMenu()">
            <span>热门图书</span>
          </div>
          <div class="mobile-nav-item" @click="router.push('/promotion/1'); toggleMobileMenu()">
            <span>促销活动</span>
          </div>
          <div class="mobile-nav-item" @click="router.push('/about'); toggleMobileMenu()">
            <span>关于我们</span>
          </div>
          <div class="mobile-nav-item" @click="router.push('/cart'); toggleMobileMenu()">
            <span>购物车</span>
            <el-badge :value="3" class="cart-badge-mobile" />
          </div>
          
          <div v-if="!isLoggedIn" class="mobile-login-actions">
            <el-button type="primary" @click="router.push('/login'); toggleMobileMenu()">登录</el-button>
            <el-button type="primary" plain @click="router.push('/register'); toggleMobileMenu()">注册</el-button>
          </div>
          <div v-else class="mobile-login-actions">
            <el-button type="primary" @click="router.push('/user/profile'); toggleMobileMenu()">个人中心</el-button>
            <el-button type="warning" @click="logout(); toggleMobileMenu()">退出登录</el-button>
          </div>
        </div>
      </div>
      
      <el-main>
        <!-- 路由视图 -->
        <router-view />
      </el-main>
      
      <el-footer height="auto">
        <div class="footer-content">
          <div class="footer-top">
            <div class="footer-section about-section">
              <h3>Su Book 书城</h3>
              <p class="about-desc">Su Book是一家专注于提供优质阅读体验的在线图书商城，致力于为读者提供丰富多样的图书资源，打造一站式阅读服务平台。</p>
              <div class="footer-contact">
                <p><i class="el-icon-location"></i> 地址：北京市海淀区中关村大街1号</p>
                <p><i class="el-icon-phone"></i> 电话：400-123-4567</p>
                <p><i class="el-icon-message"></i> 邮箱：contact@subook.com</p>
              </div>
            </div>
            
            <div class="footer-section">
              <h3>快速链接</h3>
              <ul>
                <li><a @click="router.push('/')">首页</a></li>
                <li><a @click="router.push('/new-books')">新书上架</a></li>
                <li><a @click="router.push('/hot-books')">热门图书</a></li>
                <li><a @click="router.push('/promotion/1')">促销活动</a></li>
                <li><a @click="router.push('/about')">关于我们</a></li>
              </ul>
            </div>
            
            <div class="footer-section">
              <h3>客户服务</h3>
              <ul>
                <li><a href="#">订单查询</a></li>
                <li><a href="#">配送说明</a></li>
                <li><a href="#">退换政策</a></li>
                <li><a href="#">帮助中心</a></li>
                <li><a href="#">常见问题</a></li>
              </ul>
            </div>
            
            <div class="footer-section">
              <h3>关注我们</h3>
              <div class="qr-code">
                <img src="https://img.alicdn.com/imgextra/i3/O1CN01sCVtA729JzCN2CWx3_!!6000000008040-2-tps-200-200.png" alt="QR Code" />
                <p>扫码关注公众号</p>
              </div>
              <div class="social-icons">
                <a href="#" class="social-icon"><i class="el-icon-s-platform"></i></a>
                <a href="#" class="social-icon"><i class="el-icon-s-promotion"></i></a>
                <a href="#" class="social-icon"><i class="el-icon-s-cooperation"></i></a>
                <a href="#" class="social-icon"><i class="el-icon-s-marketing"></i></a>
              </div>
            </div>
          </div>
        </div>
        
        <div class="copyright">
          <div class="copyright-content">
            <p>© 2023 Su Book - 最佳阅读体验的在线图书商城</p>
            <p>基于 Vue 3 + Element Plus + Vite 构建</p>
          </div>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<style scoped>
.app-container {
  min-height: 100vh;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}

/* 头部样式 */
.el-header {
  background-color: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  position: fixed;
  width: 100%;
  top: 0;
  z-index: 100;
  padding: 0;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 20px;
}

.left-section {
  display: flex;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  margin-right: 20px;
}

.logo img {
  height: 32px;
}

.logo h1 {
  font-size: 1.6rem;
  margin: 0;
  font-weight: 600;
  background: linear-gradient(120deg, #1a6fc9, #2a8cf2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.search-box {
  display: flex;
  align-items: center;
  max-width: 400px;
  width: 100%;
}

.search-input {
  margin-right: 10px;
}

.right-section {
  display: flex;
  align-items: center;
}

.nav-menu {
  margin-right: 30px;
}

.nav-menu .el-menu {
  border-bottom: none;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.cart-btn {
  font-size: 18px;
  background: linear-gradient(120deg, #1a6fc9, #2a8cf2);
  border: none;
}

.cart-badge :deep(.el-badge__content) {
  border: none;
}

.login-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.login-btn, .register-btn {
  min-width: 80px;
  height: 40px;
}

.register-btn {
  background: linear-gradient(120deg, #1a6fc9, #2a8cf2);
  border: none;
}

.user-dropdown {
  cursor: pointer;
}

.user-avatar {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 移动端菜单 */
.mobile-menu-toggle {
  display: none;
  cursor: pointer;
}

.mobile-menu {
  display: none;
  position: fixed;
  top: 70px;
  left: 0;
  width: 100%;
  height: 0;
  background-color: #fff;
  z-index: 99;
  overflow: hidden;
  transition: height 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.mobile-menu.active {
  height: calc(100vh - 70px);
  overflow-y: auto;
  padding: 20px;
}

.search-box-mobile {
  display: flex;
  margin-bottom: 20px;
}

.mobile-nav-links {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.mobile-nav-item {
  padding: 10px 0;
  font-size: 16px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mobile-login-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

/* 主要内容区 */
.el-main {
  background-color: #f5f7fa;
  padding: 90px 20px 40px;
  min-height: calc(100vh - 350px);
}

/* 底部样式 */
.el-footer {
  background-color: #2c3e50;
  color: #e5e5e5;
  padding: 0;
}

.footer-content {
  max-width: 1400px;
  margin: 0 auto;
}

.footer-top {
  display: flex;
  flex-wrap: wrap;
  padding: 50px 20px;
  gap: 30px;
}

.footer-section {
  flex: 1;
  min-width: 200px;
}

.about-section {
  flex: 2;
  min-width: 300px;
}

.about-desc {
  line-height: 1.6;
  font-size: 14px;
  opacity: 0.8;
  margin-bottom: 15px;
}

.footer-section h3 {
  font-size: 18px;
  margin-top: 0;
  margin-bottom: 20px;
  position: relative;
  padding-bottom: 10px;
}

.footer-section h3::after {
  content: "";
  position: absolute;
  left: 0;
  bottom: 0;
  width: 40px;
  height: 2px;
  background: linear-gradient(120deg, #1a6fc9, #2a8cf2);
}

.footer-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-section ul li {
  margin-bottom: 10px;
}

.footer-section ul li a {
  color: #e5e5e5;
  text-decoration: none;
  transition: color 0.3s;
  cursor: pointer;
  font-size: 14px;
  opacity: 0.8;
}

.footer-section ul li a:hover {
  color: #409EFF;
  opacity: 1;
}

.footer-contact p {
  margin: 8px 0;
  font-size: 14px;
  opacity: 0.8;
}

.qr-code {
  text-align: center;
  margin-bottom: 15px;
}

.qr-code img {
  width: 100px;
  height: 100px;
  border-radius: 4px;
}

.qr-code p {
  margin-top: 8px;
  font-size: 12px;
  opacity: 0.8;
}

.social-icons {
  display: flex;
  gap: 12px;
  margin-top: 15px;
}

.social-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.1);
  display: flex;
  justify-content: center;
  align-items: center;
  transition: all 0.3s;
  color: #fff;
  text-decoration: none;
}

.social-icon:hover {
  background-color: #409EFF;
  transform: translateY(-3px);
}

.copyright {
  background-color: rgba(0, 0, 0, 0.2);
  padding: 20px 0;
  text-align: center;
}

.copyright-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

.copyright p {
  margin: 5px 0;
  font-size: 12px;
  opacity: 0.6;
}

/* 响应式设计 */
@media (max-width: 992px) {
  .desktop-only {
    display: none !important;
  }
  
  .mobile-only {
    display: block !important;
  }
  
  .mobile-menu-toggle {
    display: block;
  }
  
  .footer-top {
    flex-direction: column;
    gap: 30px;
  }
  
  .footer-section {
    flex: none;
    width: 100%;
  }
}

@media (min-width: 993px) {
  .desktop-only {
    display: block !important;
  }
  
  .mobile-only {
    display: none !important;
  }
}
</style>
