import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  timeout: 5000
});

// 模拟数据
const mockBooks = Array(100).fill(null).map((_, index) => ({
  id: index + 1,
  title: '图书' + (index + 1),
  author: '作者' + (index + 1),
  price: Math.floor(Math.random() * 100) + 29.9,
  cover: '/images/book' + ((index % 8) + 1) + '.jpg',
  rating: (Math.random() * 2 + 3).toFixed(1),
  sales: Math.floor(Math.random() * 1000)
}));

export const getBooks = async (params = {}) => {
  try {
    // 模拟分页
    const { page = 1, pageSize = 8 } = params;
    const start = (page - 1) * pageSize;
    const end = start + pageSize;
    
    // 模拟API延迟
    await new Promise(resolve => setTimeout(resolve, 500));
    
    return {
      items: mockBooks.slice(start, end),
      total: mockBooks.length,
      page,
      pageSize
    };
  } catch (error) {
    console.error('获取图书列表失败:', error);
    throw error;
  }
};

export const searchBooks = async (query) => {
  try {
    // 模拟搜索
    await new Promise(resolve => setTimeout(resolve, 300));
    return mockBooks.filter(book => 
      book.title.includes(query) || 
      book.author.includes(query)
    );
  } catch (error) {
    console.error('搜索图书失败:', error);
    throw error;
  }
};

export const getBooksByCategory = async (categoryId) => {
  try {
    // 模拟分类筛选
    await new Promise(resolve => setTimeout(resolve, 300));
    return mockBooks.filter(book => book.id % 5 === categoryId % 5).slice(0, 8);
  } catch (error) {
    console.error('获取分类图书失败:', error);
    throw error;
  }
};

export const getRecommendedBooks = async () => {
  try {
    // 模拟推荐
    await new Promise(resolve => setTimeout(resolve, 300));
    return mockBooks.slice(0, 6);
  } catch (error) {
    console.error('获取推荐图书失败:', error);
    throw error;
  }
};

export const getNewsItems = async () => {
  try {
    // 模拟新闻数据
    await new Promise(resolve => setTimeout(resolve, 300));
    return Array(3).fill(null).map((_, index) => ({
      id: index + 1,
      title: '热门资讯' + (index + 1),
      date: '2024-01-' + String(index + 1).padStart(2, '0'),
      isNew: index === 0
    }));
  } catch (error) {
    console.error('获取新闻列表失败:', error);
    throw error;
  }
}; 