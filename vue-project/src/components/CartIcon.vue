<script setup>
import { ref, onMounted, watch } from 'vue';
import { getCartItems } from '../api/cart';

const cartCount = ref(0);

const updateCartCount = async () => {
  try {
    const items = await getCartItems();
    cartCount.value = items.reduce((sum, item) => sum + item.quantity, 0);
  } catch (error) {
    console.error('获取购物车数量失败:', error);
  }
};

onMounted(updateCartCount);

// 监听购物车更新事件
const handleCartUpdate = () => {
  updateCartCount();
};

// 暴露更新方法给父组件
defineExpose({
  updateCount: updateCartCount
});
</script>

<template>
  <div class="cart-icon" @click="$router.push('/cart')">
    <div class="icon">🛒</div>
    <span v-if="cartCount > 0" class="count">{{ cartCount }}</span>
  </div>
</template>

<style scoped>
.cart-icon {
  position: fixed;
  right: 20px;
  bottom: 20px;
  width: 60px;
  height: 60px;
  background-color: #4CAF50;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0,0,0,0.2);
  transition: all 0.3s ease;
  z-index: 1000;
}

.cart-icon:hover {
  transform: scale(1.1);
  background-color: #45a049;
}

.icon {
  font-size: 24px;
  color: white;
}

.count {
  position: absolute;
  top: -5px;
  right: -5px;
  background-color: #ff4757;
  color: white;
  font-size: 12px;
  min-width: 20px;
  height: 20px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 6px;
}

@media (max-width: 768px) {
  .cart-icon {
    width: 50px;
    height: 50px;
    right: 15px;
    bottom: 15px;
  }

  .icon {
    font-size: 20px;
  }
}
</style> 