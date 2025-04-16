<template>
  <div class="cart-container">
    <el-card class="cart-card">
      <template #header>
        <div class="cart-header">
          <h2>我的购物车</h2>
          <el-button type="primary" @click="clearCart" :disabled="cartItems.length === 0">清空购物车</el-button>
        </div>
      </template>
      <div v-if="cartItems.length === 0" class="empty-cart">
        <el-empty description="购物车为空">
          <el-button type="primary" @click="goToHome">去选购图书</el-button>
        </el-empty>
      </div>
      <div v-else>
        <el-table :data="cartItems" style="width: 100%" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" />
          <el-table-column label="图书" min-width="300">
            <template #default="scope">
              <div class="book-info">
                <el-image :src="scope.row.cover" class="book-cover" fit="cover" lazy></el-image>
                <div class="book-detail">
                  <div class="book-title">{{ scope.row.title }}</div>
                  <div class="book-author">{{ scope.row.author }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="price" label="单价" width="150">
            <template #default="scope">
              <span class="price">¥{{ scope.row.price.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="数量" width="200">
            <template #default="scope">
              <el-input-number 
                v-model="scope.row.quantity" 
                :min="1" 
                :max="10"
                @change="updateQuantity(scope.row)"
              ></el-input-number>
            </template>
          </el-table-column>
          <el-table-column label="小计" width="150">
            <template #default="scope">
              <span class="subtotal">¥{{ (scope.row.price * scope.row.quantity).toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button 
                type="danger" 
                @click="removeItem(scope.row.id)"
                link
              >删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="cart-footer">
          <div class="selected-info">
            已选择 <span class="selected-count">{{ selectedItems.length }}</span> 件商品
          </div>
          <div class="total-info">
            合计: <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span>
          </div>
          <el-button type="primary" size="large" @click="checkout" :disabled="selectedItems.length === 0">
            去结算
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'

const router = useRouter()
const selectedItems = ref([])

// 模拟购物车数据
const cartItems = ref([
  {
    id: 1,
    title: '活着',
    author: '余华',
    cover: 'https://img1.doubanio.com/view/subject/s/public/s33859154.jpg',
    price: 39.80,
    quantity: 1
  },
  {
    id: 2,
    title: '红楼梦',
    author: '曹雪芹',
    cover: 'https://img2.doubanio.com/view/subject/s/public/s1070959.jpg',
    price: 59.70,
    quantity: 2
  },
  {
    id: 3,
    title: '三体',
    author: '刘慈欣',
    cover: 'https://img9.doubanio.com/view/subject/s/public/s2768378.jpg',
    price: 23.00,
    quantity: 1
  }
])

const totalPrice = computed(() => {
  return selectedItems.value.reduce((total, item) => {
    return total + item.price * item.quantity
  }, 0)
})

const handleSelectionChange = (items) => {
  selectedItems.value = items
}

const updateQuantity = (item) => {
  // 在实际应用中，这里应当调用API更新数量
  ElMessage.success(`已更新《${item.title}》的数量为 ${item.quantity}`)
}

const removeItem = (id) => {
  ElMessageBox.confirm('确定要将该商品从购物车中移除吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    cartItems.value = cartItems.value.filter(item => item.id !== id)
    ElMessage.success('已从购物车中移除')
  }).catch(() => {})
}

const clearCart = () => {
  ElMessageBox.confirm('确定要清空购物车吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    cartItems.value = []
    ElMessage.success('购物车已清空')
  }).catch(() => {})
}

const checkout = () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请至少选择一件商品')
    return
  }
  
  // 模拟结算过程
  ElMessage.success('正在跳转到结算页面...')
  // 实际应用中跳转到结算页面
  // router.push('/checkout')
}

const goToHome = () => {
  router.push('/')
}
</script>

<style scoped>
.cart-container {
  padding: 20px;
}

.cart-card {
  width: 100%;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.empty-cart {
  padding: 40px 0;
}

.book-info {
  display: flex;
  align-items: center;
}

.book-cover {
  width: 60px;
  height: 80px;
  border-radius: 4px;
  margin-right: 15px;
}

.book-detail {
  display: flex;
  flex-direction: column;
}

.book-title {
  font-weight: bold;
  margin-bottom: 5px;
}

.book-author {
  color: #909399;
  font-size: 12px;
}

.price {
  color: #ff6700;
  font-weight: bold;
}

.subtotal {
  color: #ff6700;
  font-weight: bold;
}

.cart-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.selected-info {
  margin-right: 20px;
}

.selected-count {
  color: #ff6700;
  font-weight: bold;
}

.total-info {
  margin-right: 20px;
  font-size: 16px;
}

.total-price {
  color: #ff6700;
  font-weight: bold;
  font-size: 20px;
}
</style> 