/**
 * 认证相关的本地存储管理
 */

// 用户存储键名
const USER_STORAGE_KEY = 'user'

/**
 * 保存用户信息到本地存储
 * @param {Object} userData 用户数据
 */
export function saveUserData(userData) {
  localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(userData))
}

/**
 * 从本地存储获取用户信息
 * @returns {Object|null} 用户数据，如果不存在则返回null
 */
export function getUserData() {
  try {
    const savedUser = localStorage.getItem(USER_STORAGE_KEY)
    return savedUser ? JSON.parse(savedUser) : null
  } catch (error) {
    console.error('无效的用户数据', error)
    removeUserData()
    return null
  }
}

/**
 * 从本地存储中移除用户信息
 */
export function removeUserData() {
  localStorage.removeItem(USER_STORAGE_KEY)
} 