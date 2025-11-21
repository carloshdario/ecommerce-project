export default (axios) => ({

  async login(username, password) {
    const url = '/auth/login'; 

    try {
      const response = await axios.post(url, {
        username: username, 
        password: password,
      });
      
      return response.data; 
      
    } catch (error) {
      const message = error.response && error.response.data && error.response.data.error 
                      ? error.response.data.error 
                      : 'Credenciais inválidas ou erro de rede.';
      throw new Error(message); 
    }
  },


  async register(userData) {
    const url = '/auth/register'; 

    try {
      const response = await axios.post(url, userData);
      return response.data; 
    } catch (error) {
      const message = error.response && error.response.data && error.response.data.error 
                      ? error.response.data.error 
                      : 'Falha no registo. O utilizador pode já existir.';
      throw new Error(message);
    }
  },
});