<script setup>
import { ref, onMounted } from 'vue';
import { getCartItems, removeFromCart, updateCartItemQuantity } from '../api/cart';

const cartItems = ref([]);
const loading = ref(false);
const error = ref(null);

const fetchCartItems = async () => {
  loading.value = true;
  error.value = null;
  try {
    const data = await getCartItems();
    cartItems.value = data;
  } catch (err) {
    error.value = '获取购物车失败';
  } finally {
    loading.value = false;
  }
};

const handleRemoveItem = async (bookId) => {
  try {
    await removeFromCart(bookId);
    cartItems.value = cartItems.value.filter(item => item.id !== bookId);
  } catch (err) {
    error.value = '移除商品失败';
  }
};

const handleQuantityChange = async (bookId, quantity) => {
  if (quantity < 1) return;
  try {
    await updateCartItemQuantity(bookId, quantity);
    const item = cartItems.value.find(item => item.id === bookId);
    if (item) {
      item.quantity = quantity;
    }
  } catch (err) {
    error.value = '更新数量失败';
  }
};

const calculateTotal = () => {
  return cartItems.value.reduce((total, item) => {
    return total + (item.price * item.quantity);
  }, 0).toFixed(2);
};

onMounted(fetchCartItems);
</script>

<template>
  <div class="cart-container">
    <h2>购物车</h2>
    
    <div v-if="error" class="error-message">
      {{ error }}
    </div>

    <div v-if="loading" class="loading-spinner">
      <div class="spinner"></div>
    </div>

    <div v-else-if="cartItems.length === 0" class="empty-cart">
      <div class="empty-cart-icon">🛒</div>
      <p>购物车是空的</p>
      <button class="continue-shopping" @click="$router.push('/')">
        继续购物
      </button>
    </div>

    <div v-else class="cart-content">
      <div class="cart-items">
        <div v-for="item in cartItems" :key="item.id" class="cart-item">
          <div class="item-image">
            <img :src="item.cover" :alt="item.title" loading="lazy">
          </div>
          <div class="item-info">
            <h3>{{ item.title }}</h3>
            <p class="author">{{ item.author }}</p>
            <p class="price">¥{{ item.price }}</p>
          </div>
          <div class="item-quantity">
            <button 
              class="quantity-btn"
              @click="handleQuantityChange(item.id, item.quantity - 1)"
              :disabled="item.quantity <= 1"
            >
              -
            </button>
            <span class="quantity">{{ item.quantity }}</span>
            <button 
              class="quantity-btn"
              @click="handleQuantityChange(item.id, item.quantity + 1)"
            >
              +
            </button>
          </div>
          <div class="item-total">
            <p>小计: ¥{{ (item.price * item.quantity).toFixed(2) }}</p>
          </div>
          <button class="remove-btn" @click="handleRemoveItem(item.id)">
            删除
          </button>
        </div>
      </div>

      <div class="cart-summary">
        <div class="summary-row">
          <span>商品总数:</span>
          <span>{{ cartItems.reduce((sum, item) => sum + item.quantity, 0) }}</span>
        </div>
        <div class="summary-row total">
          <span>总计:</span>
          <span class="total-price">¥{{ calculateTotal() }}</span>
        </div>
        <button class="checkout-btn">
          结算
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.cart-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.error-message {
  background-color: #ffebee;
  color: #c62828;
  padding: 10px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.empty-cart {
  text-align: center;
  padding: 40px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.empty-cart-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.continue-shopping {
  background-color: #4CAF50;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s;
}

.continue-shopping:hover {
  background-color: #45a049;
}

.cart-content {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 20px;
}

.cart-items {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  padding: 20px;
}

.cart-item {
  display: grid;
  grid-template-columns: 100px 1fr auto auto auto;
  gap: 20px;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.cart-item:last-child {
  border-bottom: none;
}

.item-image {
  width: 100px;
  height: 140px;
  overflow: hidden;
  border-radius: 8px;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info h3 {
  margin: 0 0 5px 0;
  font-size: 16px;
}

.author {
  color: #666;
  font-size: 14px;
}

.price {
  color: #ff4757;
  font-weight: bold;
  margin-top: 5px;
}

.item-quantity {
  display: flex;
  align-items: center;
  gap: 10px;
}

.quantity-btn {
  width: 30px;
  height: 30px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.quantity-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quantity-btn:hover:not(:disabled) {
  background: #f5f5f5;
}

.quantity {
  min-width: 30px;
  text-align: center;
}

.remove-btn {
  padding: 8px 16px;
  background: #ff4757;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.remove-btn:hover {
  background: #ff3748;
}

.cart-summary {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  padding: 20px;
  height: fit-content;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.summary-row.total {
  border-bottom: none;
  font-weight: bold;
  font-size: 18px;
}

.total-price {
  color: #ff4757;
}

.checkout-btn {
  width: 100%;
  padding: 12px;
  background: #4CAF50;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.checkout-btn:hover {
  background: #45a049;
}

@media (max-width: 768px) {
  .cart-content {
    grid-template-columns: 1fr;
  }

  .cart-item {
    grid-template-columns: 80px 1fr;
    grid-template-areas:
      "image info"
      "image quantity"
      "total total"
      "remove remove";
    gap: 10px;
  }

  .item-image {
    grid-area: image;
    width: 80px;
    height: 120px;
  }

  .item-info {
    grid-area: info;
  }

  .item-quantity {
    grid-area: quantity;
  }

  .item-total {
    grid-area: total;
    text-align: right;
  }

  .remove-btn {
    grid-area: remove;
    width: 100%;
  }
}
</style> 