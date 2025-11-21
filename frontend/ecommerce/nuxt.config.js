export default {

  server: {
    host: 'localhost', // Necessário para garantir que seja acessível
    port: 3000         // Porta Fixa
  },

  // Configuração de Módulos
  modules: [
    '@nuxtjs/axios', 
    '@nuxtjs/tailwindcss'
  ],

  // Configuração de CSS
  css: [
    '@/assets/css/tailwind.css'
  ],

  // 🚀 Plugins essenciais
  plugins: [
    // Intercepta erros de autenticação (401)
    { src: '~/plugins/axios.js', mode: 'client' }, 
    
    // Carrega o token do localStorage e injeta Authorization header
    { src: '~/plugins/initAuth.js', mode: 'client' }
  ],

  // Configuração do Axios
  axios: {
    baseURL: process.env.NUXT_ENV_API_URL || 'http://localhost:8080/api',  
  },
}
