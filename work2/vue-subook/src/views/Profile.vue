<script setup>
import { ref, onMounted } from 'vue'
import { 
  Edit,
  User,
  Wallet,
  Box,
  Van,
  ChatDotRound,
  Collection,
  Star,
  ShoppingBag,
  Medal
} from '@element-plus/icons-vue'
import { useAuth } from '../js/composables/useAuth'
import { useProfile } from '../js/composables/useProfile'

// 获取认证状态
const { userInfo: authUserInfo } = useAuth()

// 页面加载状态
const loading = ref(true)

// 表单引用
const formRef = ref(null)

// 使用个人中心组合式API
const {
  userInfo,
  orderStats,
  recentlyViewed,
  isEditing,
  isSaving,
  profileForm,
  rules,
  levelColor,
  startEditing,
  cancelEditing,
  saveProfile
} = useProfile(authUserInfo.value)

// 页面加载完成
onMounted(() => {
  // 模拟数据加载延迟
  setTimeout(() => {
    loading.value = false
  }, 500)
})
</script>

<template>
  <div class="profile-container">
    <el-skeleton :loading="loading" animated>
      <template #template>
        <div style="padding: 20px;">
          <el-skeleton-item variant="image" style="width: 100%; height: 200px; margin-bottom: 20px;" />
          <el-skeleton-item variant="p" style="width: 100%; height: 30px; margin-bottom: 10px;" />
          <el-skeleton-item variant="p" style="width: 100%; height: 500px;" />
        </div>
      </template>
      
      <template #default>
        <!-- 用户信息卡片 -->
        <div class="user-card">
          <div class="user-card-background"></div>
          
          <el-avatar
            :size="90"
            :src="userInfo.avatar"
            class="user-avatar"
          />
          
          <h2 class="user-name">{{ userInfo.username }}</h2>
          
          <div class="user-level" :style="{ color: levelColor, borderColor: levelColor }">
            <el-icon><Medal /></el-icon>
            {{ userInfo.levelName }}
          </div>
          
          <div class="user-meta">
            <div class="meta-item">
              <span class="meta-value">{{ userInfo.points }}</span>
              <span class="meta-label">积分</span>
            </div>
            <div class="meta-item">
              <span class="meta-value">{{ userInfo.favoritesCount }}</span>
              <span class="meta-label">收藏</span>
            </div>
            <div class="meta-item">
              <span class="meta-value">{{ userInfo.ordersCount }}</span>
              <span class="meta-label">订单</span>
            </div>
          </div>
        </div>
        
        <!-- 内容部分 -->
        <div class="profile-content">
          <!-- 左侧主要内容 -->
          <div class="profile-main">
            <!-- 订单统计 -->
            <el-card shadow="hover">
              <template #header>
                <div style="display: flex; align-items: center;">
                  <el-icon style="margin-right: 8px;"><ShoppingBag /></el-icon>
                  <span>我的订单</span>
                </div>
              </template>
              
              <div class="order-stats">
                <div 
                  v-for="(item, index) in orderStats" 
                  :key="index" 
                  class="order-stat-item"
                >
                  <div 
                    class="order-stat-icon" 
                    :style="{ 
                      backgroundColor: `var(--el-color-${item.type}-light-9)`, 
                      color: `var(--el-color-${item.type})` 
                    }"
                  >
                    <el-icon><component :is="item.icon" /></el-icon>
                  </div>
                  <div class="order-stat-value">{{ item.value }}</div>
                  <div class="order-stat-label">{{ item.label }}</div>
                </div>
              </div>
            </el-card>
            
            <!-- 个人资料 -->
            <el-card shadow="hover" class="info-card">
              <template #header>
                <div style="display: flex; justify-content: space-between; align-items: center;">
                  <div style="display: flex; align-items: center;">
                    <el-icon style="margin-right: 8px;"><User /></el-icon>
                    <span class="info-card-title">个人资料</span>
                  </div>
                  <div>
                    <template v-if="!isEditing">
                      <el-button type="primary" plain size="small" @click="startEditing">
                        <el-icon><Edit /></el-icon>
                        <span>编辑</span>
                      </el-button>
                    </template>
                    <template v-else>
                      <el-button size="small" @click="cancelEditing">
                        取消
                      </el-button>
                      <el-button 
                        type="primary" 
                        size="small" 
                        :loading="isSaving"
                        @click="saveProfile(formRef)"
                      >
                        保存
                      </el-button>
                    </template>
                  </div>
                </div>
              </template>
              
              <div class="info-card-content">
                <template v-if="!isEditing">
                  <el-descriptions :column="1" border>
                    <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
                    <el-descriptions-item label="邮箱">{{ userInfo.email }}</el-descriptions-item>
                    <el-descriptions-item label="手机号">{{ userInfo.phone }}</el-descriptions-item>
                    <el-descriptions-item label="地址">{{ userInfo.address }}</el-descriptions-item>
                    <el-descriptions-item label="生日">{{ userInfo.birthday }}</el-descriptions-item>
                    <el-descriptions-item label="注册时间">{{ userInfo.registerTime }}</el-descriptions-item>
                  </el-descriptions>
                </template>
                
                <template v-else>
                  <el-form 
                    ref="formRef"
                    :model="profileForm"
                    :rules="rules"
                    label-position="top"
                    class="profile-form"
                  >
                    <el-form-item label="昵称" prop="nickname">
                      <el-input v-model="profileForm.nickname" placeholder="请输入昵称" />
                    </el-form-item>
                    
                    <el-form-item label="邮箱" prop="email">
                      <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
                    </el-form-item>
                    
                    <el-form-item label="手机号" prop="phone">
                      <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
                    </el-form-item>
                    
                    <el-form-item label="地址" prop="address">
                      <el-input v-model="profileForm.address" placeholder="请输入地址" />
                    </el-form-item>
                    
                    <el-form-item label="生日">
                      <el-date-picker
                        v-model="profileForm.birthday"
                        type="date"
                        placeholder="选择日期"
                        format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD"
                        style="width: 100%"
                      />
                    </el-form-item>
                  </el-form>
                </template>
              </div>
            </el-card>
          </div>
          
          <!-- 右侧边栏 -->
          <div class="profile-sidebar">
            <!-- 最近浏览 -->
            <el-card shadow="hover" class="info-card">
              <template #header>
                <div style="display: flex; align-items: center;">
                  <el-icon style="margin-right: 8px;"><Collection /></el-icon>
                  <span class="info-card-title">最近浏览</span>
                </div>
              </template>
              
              <div class="recently-viewed-list">
                <div 
                  v-for="item in recentlyViewed" 
                  :key="item.id"
                  class="recently-viewed-item"
                >
                  <el-image
                    :src="item.cover"
                    fit="cover"
                    class="book-cover"
                  >
                    <template #error>
                      <div class="image-slot">
                        <el-icon><Collection /></el-icon>
                      </div>
                    </template>
                  </el-image>
                  
                  <div class="book-info">
                    <h4 class="book-title">{{ item.title }}</h4>
                    
                    <div class="book-meta">
                      <span class="book-price">¥{{ item.price.toFixed(2) }}</span>
                      <span class="view-time">{{ item.viewTime }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </el-card>
            
            <!-- 我的收藏 -->
            <el-card shadow="hover" class="info-card">
              <template #header>
                <div style="display: flex; align-items: center;">
                  <el-icon style="margin-right: 8px;"><Star /></el-icon>
                  <span class="info-card-title">我的收藏</span>
                </div>
              </template>
              
              <el-empty description="暂无收藏" :image-size="80"></el-empty>
            </el-card>
          </div>
        </div>
      </template>
    </el-skeleton>
  </div>
</template> 