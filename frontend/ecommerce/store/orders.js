
export const actions = {
  async fetchOrders({ state }) {
    try {
      const response = await this.$axios.get('/orders'); 
      return response.data;
    } catch (error) {
      if (error.response && error.response.status === 401) {
        
        this.$store.commit('CLEAR_AUTH');
        localStorage.removeItem('authToken');
        this.$axios.setToken(false); 
      }
      throw error;
    }
  },
};