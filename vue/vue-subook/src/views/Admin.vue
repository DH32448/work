<template>
  <div class="admin-container">
    <el-container>
      <el-aside width="200px" class="admin-aside">
        <div class="logo">
          <h2>Su Book 管理</h2>
        </div>
        <el-menu
          class="admin-menu"
          :default-active="activeMenu"
          @select="handleMenuSelect"
          router
        >
          <el-menu-item index="dashboard">
            <el-icon><DataLine /></el-icon>
            <span>数据概览</span>
          </el-menu-item>
          <el-sub-menu index="books">
            <template #title>
              <el-icon><Reading /></el-icon>
              <span>图书管理</span>
            </template>
            <el-menu-item index="books-list">
              <el-icon><List /></el-icon>
              <span>图书列表</span>
            </el-menu-item>
            <el-menu-item index="books-add">
              <el-icon><Plus /></el-icon>
              <span>添加图书</span>
            </el-menu-item>
            <el-menu-item index="books-category">
              <el-icon><Collection /></el-icon>
              <span>图书分类</span>
            </el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="orders">
            <template #title>
              <el-icon><ShoppingCart /></el-icon>
              <span>订单管理</span>
            </template>
            <el-menu-item index="orders-list">
              <el-icon><List /></el-icon>
              <span>订单列表</span>
            </el-menu-item>
            <el-menu-item index="orders-process">
              <el-icon><Finished /></el-icon>
              <span>订单处理</span>
            </el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="users">
            <template #title>
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="users-list">
              <el-icon><List /></el-icon>
              <span>用户列表</span>
            </el-menu-item>
            <el-menu-item index="users-roles">
              <el-icon><Lock /></el-icon>
              <span>角色权限</span>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item index="settings">
            <el-icon><Setting /></el-icon>
            <span>系统设置</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header height="60px" class="admin-header">
          <div class="header-left">
            <el-icon class="fold-icon" @click="toggleSidebar"><Fold /></el-icon>
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/admin' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item>{{ currentBreadcrumb }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-dropdown>
              <span class="user-dropdown">
                管理员 <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="goToProfile">个人信息</el-dropdown-item>
                  <el-dropdown-item @click="goToHome">返回前台</el-dropdown-item>
                  <el-dropdown-item @click="handleLogout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        <el-main class="admin-main">
          <!-- 仪表盘内容 -->
          <div v-if="activeMenu === 'dashboard'" class="dashboard">
            <el-row :gutter="20">
              <el-col :span="6">
                <el-card shadow="hover" class="stat-card">
                  <div class="stat-icon">
                    <el-icon><Reading /></el-icon>
                  </div>
                  <div class="stat-info">
                    <div class="stat-value">1,286</div>
                    <div class="stat-title">图书总数</div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="hover" class="stat-card">
                  <div class="stat-icon">
                    <el-icon><ShoppingCart /></el-icon>
                  </div>
                  <div class="stat-info">
                    <div class="stat-value">296</div>
                    <div class="stat-title">本月订单</div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="hover" class="stat-card">
                  <div class="stat-icon">
                    <el-icon><User /></el-icon>
                  </div>
                  <div class="stat-info">
                    <div class="stat-value">3,651</div>
                    <div class="stat-title">用户总数</div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="hover" class="stat-card">
                  <div class="stat-icon">
                    <el-icon><Money /></el-icon>
                  </div>
                  <div class="stat-info">
                    <div class="stat-value">¥42,689</div>
                    <div class="stat-title">本月销售额</div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            
            <el-row :gutter="20" class="charts-row">
              <el-col :span="16">
                <el-card shadow="hover" class="chart-card">
                  <template #header>
                    <div class="chart-header">
                      <span>销售统计</span>
                      <el-radio-group v-model="saleTimeRange" size="small">
                        <el-radio-button label="week">本周</el-radio-button>
                        <el-radio-button label="month">本月</el-radio-button>
                        <el-radio-button label="year">本年</el-radio-button>
                      </el-radio-group>
                    </div>
                  </template>
                  <div class="chart-placeholder">
                    [ 销售统计图表 ]
                  </div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card shadow="hover" class="chart-card">
                  <template #header>
                    <div class="chart-header">
                      <span>销售分类</span>
                    </div>
                  </template>
                  <div class="chart-placeholder">
                    [ 销售分类图表 ]
                  </div>
                </el-card>
              </el-col>
            </el-row>
            
            <el-row :gutter="20" class="latest-row">
              <el-col :span="12">
                <el-card shadow="hover" class="latest-card">
                  <template #header>
                    <div class="latest-header">
                      <span>最新订单</span>
                      <el-link type="primary">查看更多</el-link>
                    </div>
                  </template>
                  <el-table :data="latestOrders" style="width: 100%" size="small">
                    <el-table-column prop="id" label="订单号" width="100" />
                    <el-table-column prop="user" label="用户" width="120" />
                    <el-table-column prop="amount" label="金额" width="100">
                      <template #default="scope">
                        <span class="price">¥{{ scope.row.amount.toFixed(2) }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column prop="status" label="状态" width="120">
                      <template #default="scope">
                        <el-tag :type="scope.row.statusType">{{ scope.row.status }}</el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column prop="date" label="下单时间" />
                  </el-table>
                </el-card>
              </el-col>
              <el-col :span="12">
                <el-card shadow="hover" class="latest-card">
                  <template #header>
                    <div class="latest-header">
                      <span>最新用户</span>
                      <el-link type="primary">查看更多</el-link>
                    </div>
                  </template>
                  <el-table :data="latestUsers" style="width: 100%" size="small">
                    <el-table-column prop="id" label="ID" width="80" />
                    <el-table-column prop="name" label="用户名" width="120" />
                    <el-table-column prop="email" label="邮箱" />
                    <el-table-column prop="regDate" label="注册时间" width="180" />
                  </el-table>
                </el-card>
              </el-col>
            </el-row>
          </div>
          
          <!-- 其他管理内容可以通过v-if判断activeMenu来展示对应内容 -->
          <div v-if="activeMenu !== 'dashboard'" class="placeholder-content">
            <el-empty :description="`${currentBreadcrumb}功能开发中`">
              <el-button type="primary">即将上线</el-button>
            </el-empty>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { 
  DataLine, Reading, List, Plus, Collection, ShoppingCart, 
  Finished, User, Lock, Setting, Fold, ArrowDown, Money 
} from '@element-plus/icons-vue'

const router = useRouter()
const activeMenu = ref('dashboard')
const sidebarCollapsed = ref(false)
const saleTimeRange = ref('month')

// 面包屑导航文本
const menuLabels = {
  'dashboard': '数据概览',
  'books-list': '图书列表',
  'books-add': '添加图书',
  'books-category': '图书分类',
  'orders-list': '订单列表',
  'orders-process': '订单处理',
  'users-list': '用户列表',
  'users-roles': '角色权限',
  'settings': '系统设置'
}

const currentBreadcrumb = computed(() => {
  return menuLabels[activeMenu.value] || '数据概览'
})

// 模拟最新订单数据
const latestOrders = [
  { id: 'ORD-8901', user: '张三', amount: 129.99, status: '已完成', statusType: 'success', date: '2023-05-28 16:32:12' },
  { id: 'ORD-8900', user: '李四', amount: 258.50, status: '处理中', statusType: 'warning', date: '2023-05-28 15:10:45' },
  { id: 'ORD-8899', user: '王五', amount: 99.00, status: '已发货', statusType: 'primary', date: '2023-05-28 14:29:33' },
  { id: 'ORD-8898', user: '赵六', amount: 344.70, status: '已完成', statusType: 'success', date: '2023-05-28 12:05:21' }
]

// 模拟最新用户数据
const latestUsers = [
  { id: 1089, name: '张小明', email: 'zhangxm@example.com', regDate: '2023-05-28 18:25:10' },
  { id: 1088, name: '王丽', email: 'wangli@example.com', regDate: '2023-05-28 16:40:22' },
  { id: 1087, name: '李强', email: 'liqiang@example.com', regDate: '2023-05-28 14:15:30' },
  { id: 1086, name: '赵芳', email: 'zhaofang@example.com', regDate: '2023-05-28 09:51:48' }
]

const handleMenuSelect = (index) => {
  activeMenu.value = index
}

const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

const goToProfile = () => {
  // 跳转到个人信息页面
  ElMessage.info('跳转到个人信息页面')
}

const goToHome = () => {
  router.push('/')
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出管理系统吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('已安全退出系统')
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.admin-container {
  height: 100vh;
  overflow: hidden;
}

.admin-aside {
  background-color: #304156;
  color: #fff;
  overflow-y: auto;
  transition: width 0.3s;
}

.admin-menu {
  border-right: none;
  background-color: #304156;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  border-bottom: 1px solid #1f2d3d;
}

.admin-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 15px;
}

.header-left {
  display: flex;
  align-items: center;
}

.fold-icon {
  font-size: 20px;
  margin-right: 15px;
  cursor: pointer;
}

.user-dropdown {
  cursor: pointer;
  display: flex;
  align-items: center;
}

.admin-main {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  margin-right: 15px;
  font-size: 42px;
  color: #409EFF;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-title {
  font-size: 14px;
  color: #909399;
}

.charts-row {
  margin-top: 20px;
}

.chart-card {
  margin-bottom: 20px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #909399;
  background-color: #f5f7fa;
  font-size: 18px;
  border-radius: 4px;
}

.latest-row {
  margin-top: 20px;
}

.latest-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  color: #ff6700;
  font-weight: bold;
}

.placeholder-content {
  height: 600px;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style> 