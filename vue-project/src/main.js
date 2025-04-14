import { createApp } from 'vue';
import ElementPlus from 'element-plus';
import App from './App.vue';
import router from '../router/index.js';
import store from '../store/index.js';
import 'element-plus/dist/index.css';
import { $get, $postx, $postj, $postf } from '../ajax/index.js';

const app = createApp(App);

// 使用Element Plus
app.use(ElementPlus, { size: 'default', zIndex: 2000 });
// 使用Vue Router
app.use(router);
// 使用Vuex
app.use(store);

// 全局挂载HTTP请求方法
app.config.globalProperties.$get = $get;
app.config.globalProperties.$postx = $postx;
app.config.globalProperties.$postj = $postj;
app.config.globalProperties.$postf = $postf;

app.mount('#app');