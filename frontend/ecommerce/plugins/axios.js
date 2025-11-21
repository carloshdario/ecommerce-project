export default function ({ $axios, store, redirect }) {
  // Intercepta todas respostas
  $axios.onError(error => {
    const code = parseInt(error.response && error.response.status);
    if (code === 401) {
      // Token inválido ou expirado -> desloga
      store.dispatch('auth/logout');
      redirect('/login');
    }
    return Promise.reject(error);
  });

  // Antes de cada requisição, garante que o token está setado
  $axios.onRequest(config => {
    if (store.state.auth.token) {
      config.headers.common['Authorization'] = `Bearer ${store.state.auth.token}`;
    }
    return config;
  });
}
