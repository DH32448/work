<template>
  <div v-if="visible" class="model">
    <div class="model-content">
      <slot></slot>
      <button @click="close">关闭</button>
    </div>
  </div>
<!--
const visible=ref(false)
<Model :visible="visible" @update:visible="visible = $event">
  </Model>
-->
</template>

<script>
export default {
  name: "Model",
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  methods: {
    close() {
      this.$emit('update:visible', false);
    }
  }
}
</script>

<style scoped>
.model {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9; /* 确保登录框位于最前面 */
}

.model-content {
  background: white;
  padding: 20px;
  border-radius: 8px;
}
</style>
