<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 p-4">
    <div class="w-full max-w-md p-10 space-y-8 bg-white rounded-2xl shadow-2xl border border-gray-100 transition duration-300 hover:shadow-3xl">
      
      <h2 class="text-4xl font-extrabold text-center text-gray-900 tracking-tight mb-6">
         Acesse sua Conta
      </h2>
      
      <form class="space-y-6" @submit.prevent="handleLogin">
        
        <div>
          <label for="username" class="block text-sm font-semibold text-gray-700 mb-2">Usuário</label>
          <input
            id="username"
            v-model="username"
            type="text"
            required
            placeholder="Nome de utilizador ou email"
            autocomplete="username"
            autofocus
            class="appearance-none block w-full px-4 py-3 border border-gray-300 rounded-xl shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 text-base transition duration-150"
            :disabled="isLoading"
          />
        </div>

        <div>
          <label for="password" class="block text-sm font-semibold text-gray-700 mb-2">Senha</label>
          <input
            id="password"
            v-model="password"
            type="password"
            required
            placeholder="Mínimo 6 caracteres"
            autocomplete="current-password"
            class="appearance-none block w-full px-4 py-3 border border-gray-300 rounded-xl shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 text-base transition duration-150"
            :disabled="isLoading"
          />
        </div>

        <p v-if="error" class="text-sm font-medium text-red-700 bg-red-50 border border-red-300 p-3 rounded-xl text-center shadow-inner">
          {{ error }}
        </p>

        <div>
          <button
            type="submit"
            :disabled="isLoading || !isFormValid"
            class="w-full flex justify-center py-3 px-4 border border-transparent rounded-xl shadow-lg text-lg font-bold text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition duration-150 ease-in-out disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <svg v-if="isLoading" class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            
            <span>{{ isLoading ? 'A Autenticar...' : 'Entrar' }}</span>
          </button>
        </div>
      </form>

      <div class="text-center text-sm text-gray-600 border-t border-gray-200 pt-6">
        Não tem uma conta? 
        <NuxtLink to="/register" class="font-bold text-blue-600 hover:text-blue-700 transition duration-150">
          Registre-se
        </NuxtLink>
      </div>

    </div>
  </div>
</template>

<script>
export default {
  middleware: 'guest', 
  
  data() {
    return {
      username: '',
      password: '',
      error: null,
      isLoading: false, 
    };
  },

  computed: {
    isFormValid() {
      return this.username.trim() !== '' && this.password.trim().length >= 6;
    }
  },
  
  methods: {
    async handleLogin() {
      if (!this.isFormValid) {
          this.error = 'Por favor, insira um nome de utilizador e uma palavra-passe válida (mínimo 6 caracteres).';
          return;
      }

      this.error = null;
      this.isLoading = true;

      try {
        await this.$store.dispatch('auth/login', { 
          username: this.username,
          password: this.password,
        });
        
        const user = this.$store.state.auth.user;

        if (user && user.role === 'ROLE_ADMIN') {
            this.$router.push('/admin/products'); 
        } else {
            this.$router.push('/dashboard'); 
        }
        
      } catch (e) {
        const message = e.response?.data?.message || e.message || 'Credenciais inválidas ou erro de rede.';
        this.error = message; 
        
      } finally {
        this.isLoading = false;
      }
    },
  },
};
</script>