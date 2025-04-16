<template>
  <div class="common-layout">
    <slot></slot>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
.common-layout {
  width: 100%;
}
</style> 