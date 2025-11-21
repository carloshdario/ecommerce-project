// ~/plugins/initAuth.js

export default ({ store, app: { $axios } }) => {
  // Executamos isso SÓ no lado do cliente, após a primeira renderização.
  if (process.client) {
    const token = localStorage.getItem('authToken');
    const userJson = localStorage.getItem('authUser');

    if (token) {
      // 1. Configura o Axios imediatamente
      $axios.setToken(token, 'Bearer');

      if (userJson) {
        try {
          const user = JSON.parse(userJson);
          
          // 2. Comita a mutação para definir o estado Vuex ANTES do middleware de rota
          store.commit('auth/SET_INITIAL_AUTH', { token, user }); 
          
        } catch (e) {
          // Se o JSON do usuário estiver corrompido, tentamos o fetchUser.
          // Nota: Você pode precisar ajustar o store.dispatch para ser síncrono ou lidar com isso de forma mais robusta.
          // Para simplicidade, vamos apenas chamar a mutação de limpeza e deixar a próxima requisição falhar no fetchUser.
          store.commit('auth/CLEAR_AUTH');
          console.error("Erro ao parsear dados do Local Storage. O estado foi limpo.", e);
        }
      } else {
        // Se o token existe, mas o usuário não, o estado será setado com o token, 
        // e o primeiro fetchUser que ocorrer no sistema tentará recuperar os dados.
        // Isto é menos crítico para o redirecionamento.
      }
    }
  }
}