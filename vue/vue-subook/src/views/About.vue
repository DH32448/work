<template>
  <div class="about-container">
    <el-card class="about-card">
      <template #header>
        <div class="card-header">
          <h2>关于我们</h2>
        </div>
      </template>
      <div class="about-content">
        <p>这是一个使用Vue 3、Element Plus、Pinia和Axios构建的示例项目。</p>
        <p>通过这个示例，您可以了解如何：</p>
        <el-divider></el-divider>
        <ul>
          <li>使用Element Plus组件构建用户界面</li>
          <li>使用Pinia进行状态管理</li>
          <li>使用Vue Router进行路由管理</li>
          <li>使用Axios进行API请求</li>
        </ul>
        <el-divider></el-divider>
        <el-button type="primary" @click="goBack">返回首页</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElButton, ElCard, ElDivider } from 'element-plus'

const router = useRouter()

const goBack = () => {
  router.push('/')
}
</script>

<style scoped>
.about-container {
  padding: 20px;
}

.about-card {
  max-width: 600px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.about-content {
  padding: 10px;
}

ul {
  text-align: left;
  margin: 15px 0;
}

li {
  margin: 10px 0;
}
</style> 