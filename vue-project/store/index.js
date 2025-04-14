import { createStore } from 'vuex';

const store = createStore({
  state: {
    count: 0,
    user: null
  },
  getters: {
    getCount: (state) => state.count,
    getUser: (state) => state.user
  },
  mutations: {
    increment(state) {
      state.count++;
    },
    setUser(state, userData) {
      state.user = userData;
    }
  },
  actions: {
    incrementAsync({ commit }) {
      setTimeout(() => {
        commit('increment');
      }, 1000);
    }
  }
});

export default store; 