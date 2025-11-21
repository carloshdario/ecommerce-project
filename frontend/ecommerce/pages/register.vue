<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 p-4">
    <div class="w-full max-w-md p-10 space-y-8 bg-white rounded-2xl shadow-2xl border border-gray-100 transition duration-300 hover:shadow-3xl">
      
      <h2 class="text-4xl font-extrabold text-center text-gray-900 tracking-tight mb-6">
         Crie a sua Conta
      </h2>
      
      <form class="space-y-6" @submit.prevent="handleRegister">
        
        <div>
          <label for="name" class="block text-sm font-semibold text-gray-700 mb-2">Nome Completo</label>
          <input
            id="name"
            v-model="name"
            type="text"
            required
            placeholder="Seu nome e sobrenome"
            autocomplete="name"
            autofocus
            class="appearance-none block w-full px-4 py-3 border border-gray-300 rounded-xl shadow-sm placeholder-gray-400 focus:outline-none focus:ring-green-500 focus:border-green-500 text-base transition duration-150"
            :disabled="isLoading"
          />
        </div>

        <div>
          <label for="username" class="block text-sm font-semibold text-gray-700 mb-2">Usuario</label>
          <input
            id="username"
            v-model="username"
            type="text"
            required
            placeholder="Escolha um nome de utilizador"
            autocomplete="username"
            class="appearance-none block w-full px-4 py-3 border border-gray-300 rounded-xl shadow-sm placeholder-gray-400 focus:outline-none focus:ring-green-500 focus:border-green-500 text-base transition duration-150"
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
            autocomplete="new-password"
            class="appearance-none block w-full px-4 py-3 border border-gray-300 rounded-xl shadow-sm placeholder-gray-400 focus:outline-none focus:ring-green-500 focus:border-green-500 text-base transition duration-150"
            :disabled="isLoading"
          />
        </div>

        <div>
          <label for="confirmPassword" class="block text-sm font-semibold text-gray-700 mb-2">Confirmar senha</label>
          <input
            id="confirmPassword"
            v-model="confirmPassword"
            type="password"
            required
            placeholder="Repita a palavra-passe"
            autocomplete="new-password"
            class="appearance-none block w-full px-4 py-3 border border-gray-300 rounded-xl shadow-sm placeholder-gray-400 focus:outline-none focus:ring-green-500 focus:border-green-500 text-base transition duration-150"
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
            class="w-full flex justify-center py-3 px-4 border border-transparent rounded-xl shadow-lg text-lg font-bold text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 transition duration-150 ease-in-out disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <svg v-if="isLoading" class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            
            <span>{{ isLoading ? 'Criando Conta...' : 'Registar' }}</span>
          </button>
        </div>
      </form>

      <div class="text-center text-sm text-gray-600 border-t border-gray-200 pt-6">
        Já tem uma conta? 
        <NuxtLink to="/login" class="font-bold text-blue-600 hover:text-blue-700 transition duration-150">
          Acesse aqui
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
      name: '', 
      username: '',
      password: '',
      confirmPassword: '',
      error: null,
      isLoading: false,
    };
  },

  computed: {
    isFormValid() {
      const passwordValid = this.password.trim().length >= 6;
      const passwordsMatch = this.password === this.confirmPassword;
      
      return this.name.trim() !== '' && this.username.trim() !== '' && passwordValid && passwordsMatch;
    }
  },
  
  methods: {
    async handleRegister() {
      if (this.name.trim() === '' || this.username.trim() === '') {
          this.error = 'O Nome e o Usuario são obrigatórios.';
          return;
      }
      if (this.password.trim().length < 6) {
          this.error = 'A senha deve ter pelo menos 6 caracteres.';
          return;
      }
      if (this.password !== this.confirmPassword) {
          this.error = 'As senhas não coincidem.';
          return;
      }

      this.error = null;
      this.isLoading = true;

      try {
        await this.$store.dispatch('auth/register', { 
            name: this.name,
            username: this.username,
            password: this.password,
        });
        
        alert('Registro concluído! Agora pode acessar à sua conta.');

        this.$router.push('/login'); 
        
      } catch (e) {
        console.error('Erro de Registo:', e.response || e);
        
        let message = 'Ocorreu um erro ao tentar criar a conta.';
        if (e.response && e.response.status === 400) {
            message = e.response.data.message || 'Nome de utilizador já em uso ou dados inválidos.';
        }
        
        this.error = message;
        
      } finally {
        this.isLoading = false;
      }
    },
  },
};
</script>