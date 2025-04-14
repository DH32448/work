import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  timeout: 5000
});

export const getCartItems = async () => {
  try {
    const response = await api.get('/cart');
    return response.data;
  } catch (error) {
    console.error('获取购物车失败:', error);
    throw error;
  }
};

export const addToCart = async (bookId, quantity = 1) => {
  try {
    const response = await api.post('/cart/add', { bookId, quantity });
    return response.data;
  } catch (error) {
    console.error('添加到购物车失败:', error);
    throw error;
  }
};

export const removeFromCart = async (bookId) => {
  try {
    const response = await api.delete('/cart/' + bookId);
    return response.data;
  } catch (error) {
    console.error('从购物车移除失败:', error);
    throw error;
  }
};

export const updateCartItemQuantity = async (bookId, quantity) => {
  try {
    const response = await api.put('/cart/' + bookId, { quantity });
    return response.data;
  } catch (error) {
    console.error('更新购物车数量失败:', error);
    throw error;
  }
}; 