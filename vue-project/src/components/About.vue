<script setup>
import { computed } from 'vue';
import { useStore } from 'vuex';

const store = useStore();
const count = computed(() => store.state.count);
</script>

<template>
  <div class="about-container">
    <h1>关于我们</h1>
    <p>这是一个示例项目，用于展示Vue 3生态系统的基本使用。</p>
    <p>当前计数: {{ count }}</p>
    <el-button type="success" @click="store.commit('increment')">增加计数</el-button>
    <el-button type="info" @click="store.dispatch('incrementAsync')">异步增加</el-button>
  </div>
</template>

<style scoped>
.about-container {
  padding: 20px;
  text-align: center;
}
</style> 