<template>
  <div class="min-h-screen bg-gray-50">
    <TheNavbar /> 
    
    <main>
      <nuxt />
    </main>
  </div>
</template>

<script>
export default {
  
  async fetch() {
    if (process.client && !this.$store.state.auth.isAuthenticated) {
      try {
        console.log('Layout: Forçando inicialização de autenticação (fetch hook)...');

        await this.$store.dispatch('auth/nuxtClientInit'); 
        
      } catch (e) {
       
        console.error('Falha na inicialização do layout/auth:', e);
      }
    }
  },
  fetchOnServer: false
}
</script>