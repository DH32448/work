<template>
  <div class="page-container">
    <el-card class="about-card">
      <template #header>
        <div class="card-header">
          <h2>关于我们</h2>
          <el-icon color="#409EFF" :size="24"><InfoFilled /></el-icon>
        </div>
      </template>
      <div class="about-content">
        <el-row>
          <el-col :span="24">
            <div class="text-center">
              <img src="../assets/vue.svg" alt="Vue logo" class="about-logo" />
              <h1 class="text-primary">Su Book Store</h1>
              <el-divider></el-divider>
            </div>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="24">
            <p class="about-desc">
              这是一个基于Vue 3、Element Plus、Pinia和Axios构建的在线书店示例项目。
              该项目展示了现代前端框架的使用方式和最佳实践。
            </p>
            
            <el-descriptions title="技术栈" :column="2" border>
              <el-descriptions-item label="前端框架">Vue 3</el-descriptions-item>
              <el-descriptions-item label="UI组件库">Element Plus</el-descriptions-item>
              <el-descriptions-item label="状态管理">Pinia</el-descriptions-item>
              <el-descriptions-item label="HTTP请求">Axios</el-descriptions-item>
              <el-descriptions-item label="构建工具">Vite</el-descriptions-item>
              <el-descriptions-item label="路由">Vue Router</el-descriptions-item>
            </el-descriptions>
            
            <el-alert
              title="免责声明"
              type="info"
              description="本项目仅用于学习和演示目的，不提供实际的图书销售服务。"
              show-icon
              :closable="false"
              style="margin: 30px 0;"
            />
            
            <p class="text-center">
              <el-button type="primary" @click="$router.push('/')">
                <el-icon class="el-icon--left"><HomeFilled /></el-icon>
                返回首页
              </el-button>
            </p>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { InfoFilled, HomeFilled } from '@element-plus/icons-vue'
</script> 