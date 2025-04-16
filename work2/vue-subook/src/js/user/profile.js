import { ref } from 'vue'
import { ElMessage } from 'element-plus'

/**
 * 获取用户基本信息
 * @param {Object} userData 用户基础数据
 * @returns {Object} 带有详细信息的用户数据
 */
export function getUserProfile(userData) {
  // 这里模拟从API获取详细用户信息
  // 实际应用中应该从后端获取
  return {
    ...userData,
    email: 'user@example.com',
    phone: '138****1234',
    address: '上海市浦东新区XX路XX号',
    birthday: '1990-01-01',
    registerTime: '2023-01-15',
    points: 520,
    level: 3,
    levelName: '铂金会员',
    favoritesCount: 28,
    ordersCount: 16
  }
}

/**
 * 用户订单统计数据
 * @returns {Array} 订单统计数据
 */
export function getOrderStats() {
  return [
    { label: '待付款', value: 2, icon: 'Wallet', type: 'warning' },
    { label: '待发货', value: 3, icon: 'Box', type: 'primary' },
    { label: '待收货', value: 1, icon: 'Van', type: 'success' },
    { label: '待评价', value: 5, icon: 'ChatDotRound', type: 'info' }
  ]
}

/**
 * 用户最近阅读记录
 * @returns {Array} 阅读记录
 */
export function getRecentlyViewed() {
  return [
    {
      id: 1,
      title: '深入理解计算机系统',
      cover: 'https://img.duoziwang.com/2018/26/08301737145845.jpg', 
      price: 128.00,
      viewTime: '2023-04-15'
    },
    {
      id: 2,
      title: '算法导论',
      cover: 'https://img.duoziwang.com/2018/21/1011930115583.jpg',
      price: 89.00,
      viewTime: '2023-04-12'
    },
    {
      id: 3,
      title: 'JavaScript高级程序设计',
      cover: 'https://img.duoziwang.com/2019/07/12080840825696.jpg',
      price: 99.00,
      viewTime: '2023-04-10'
    }
  ]
}

/**
 * 更新用户资料
 * @param {Object} data 用户资料
 * @returns {Promise}
 */
export function updateUserProfile(data) {
  return new Promise((resolve) => {
    // 模拟API调用延迟
    setTimeout(() => {
      // 模拟成功
      ElMessage.success('个人资料更新成功')
      resolve({ success: true, data })
    }, 1000)
  })
} 