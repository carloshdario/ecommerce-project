import authApi from '~/api/auth'; 

export const state = () => ({
  token: null,
  isAuthenticated: false,
  user: null, 
});

export const mutations = {
 
  SET_INITIAL_AUTH(state, { token, user }) {
      state.token = token;
      state.user = user;
      state.isAuthenticated = !!token;
  },

  
  CLEAR_AUTH(state) {
    state.token = null;
    state.isAuthenticated = false;
    state.user = null;
  }
};


export const actions = {
  
  async login({ commit, dispatch }, credentials) {
    try {
      const responseData = await authApi(this.$axios).login(credentials.username, credentials.password);
      
      const token = responseData.token;
      const user = responseData.user; 
      
      commit('SET_INITIAL_AUTH', { token, user });
      
      dispatch('persistAuthData', { token, user }); 
      
      this.$axios.setToken(token, 'Bearer');
      
      return token;
    } catch (error) {
      dispatch('logout'); 
      throw error; 
    }
  },

  async register({ }, userData) {
    try {
        const response = await authApi(this.$axios).register(userData);
        return response.data;
    } catch (error) {
        throw error;
    }
  },
  
  async logout({ commit }) {
      commit('CLEAR_AUTH');
      
      if (process.client) {
          localStorage.removeItem('authToken');
          localStorage.removeItem('authUser'); 
      }
      
      this.$axios.setToken(false);
  },


  async fetchUser({ commit, dispatch }) {
    try {
        const response = await this.$axios.get('/auth/me'); 
        
        const user = response.data; 
        
        if (process.client) {
             localStorage.setItem('authUser', JSON.stringify(user));
        }

        commit('SET_INITIAL_AUTH', { token: this.state.auth.token, user });
        return user;
    } catch (error) {
        console.error("Token inválido ou expirado. Deslogando.", error);
        dispatch('logout');
    }
  },


  persistAuthData({ commit }, { token, user }) {
    if (process.client && token && user) {
        localStorage.setItem('authToken', token);
        localStorage.setItem('authUser', JSON.stringify(user)); 
    }
  },
  

}