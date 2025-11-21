<template>
  <div class="container mx-auto px-4 py-12">
    <header class="mb-12 flex justify-between items-center border-b pb-4">
      <div>
        <h1 class="text-4xl sm:text-5xl font-extrabold text-gray-900 tracking-tight">Catálogo de Produtos</h1>
        <p class="text-xl text-gray-600">Explore as nossas ofertas mais recentes.</p>
      </div>

      <div class="flex space-x-4">
        <NuxtLink 
          v-if="!isAuthenticated" 
          to="/login"
          class="inline-flex items-center px-6 py-3 border border-transparent text-base font-semibold rounded-xl shadow-lg text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition duration-150"
        >
          Entrar / Cadastrar
        </NuxtLink>

        <NuxtLink 
          v-else
          :to="userRole === 'ROLE_ADMIN' ? '/admin/products' : '/dashboard'"
          class="inline-flex items-center px-6 py-3 border border-transparent text-base font-semibold rounded-xl shadow-lg text-white"
          :class="userRole === 'ROLE_ADMIN' ? 'bg-red-600 hover:bg-red-700 focus:ring-red-500' : 'bg-blue-600 hover:bg-blue-700 focus:ring-blue-500'"
        >
          {{ userRole === 'ROLE_ADMIN' ? 'Painel Admin' : 'Meu Dashboard' }}
        </NuxtLink>
      </div>
    </header>

    <div v-if="$fetchState.pending" class="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-8 animate-pulse">
      <div v-for="n in pageSize" :key="n" class="rounded-2xl shadow-xl p-6 bg-white border border-gray-100 h-96">
        <div class="h-40 bg-gray-200 rounded-xl mb-4"></div>
        <div class="h-8 bg-gray-300 rounded w-4/5 mb-3"></div>
        <div class="h-4 bg-gray-200 rounded w-full mb-2"></div>
        <div class="h-4 bg-gray-200 rounded w-1/2 mb-4"></div>
        <div class="h-10 bg-gray-400 rounded-lg w-full"></div>
      </div>
    </div>

    <div v-else-if="error" class="text-center p-20 bg-red-50 rounded-2xl border border-red-200 shadow-lg">
      <p class="text-3xl font-medium text-red-800 mb-4">❌ Falha ao Carregar Produtos</p>
      <p class="text-gray-700">{{ error }}</p>
    </div>

    <div v-else>
      <div v-if="products.length === 0" class="text-center p-20 bg-white rounded-2xl border-2 border-dashed border-gray-300 shadow-inner">
        <p class="text-3xl font-bold mb-4 text-blue-600">🤷‍♂️ Lista de Produtos Vazia</p>
        <p class="text-gray-600">Nenhum produto encontrado na página {{ currentPage + 1 }}.</p>
      </div>

      <div v-else class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8">
        <div v-for="product in products" :key="product.id" class="bg-white rounded-2xl overflow-hidden shadow-2xl transition duration-300 transform hover:scale-[1.03] hover:shadow-blue-300/50 flex flex-col">
          <div class="bg-gray-100 h-48 flex items-center justify-center text-gray-400 text-sm italic border-b border-gray-200">
            [Imagem de {{ product.name }}]
          </div>

          <div class="p-6 flex flex-col flex-grow">
            <h2 class="text-xl font-bold text-gray-900 mb-1 line-clamp-2 min-h-[56px]">{{ product.name }}</h2>
            <p class="text-sm text-gray-500 mb-4 line-clamp-3 flex-grow">{{ product.description }}</p>

            <div class="mt-auto">
              <p class="text-3xl font-extrabold mb-3 text-blue-700">
                R$ {{ product.price ? product.price.toFixed(2) : '0.00' }}
              </p>

              <div class="flex items-center justify-between text-sm mb-4">
                <p v-if="product.quantity === 0" class="font-semibold text-red-600 bg-red-100 px-3 py-1 rounded-full">⚠️ Esgotado</p>
                <p v-else-if="product.quantity <= 5" class="font-semibold text-orange-600 bg-orange-100 px-3 py-1 rounded-full">Poucas unidades! ({{ product.quantity }})</p>
                <p v-else class="font-semibold text-green-600 bg-green-100 px-3 py-1 rounded-full">Em estoque</p>
              </div>

              <button
                @click="handleBuy(product)"
                :disabled="product.quantity === 0"
                class="w-full bg-blue-600 text-white font-bold py-3 rounded-xl shadow-md hover:bg-blue-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition duration-150 tracking-wider text-lg"
              >
                Ver Detalhes
              </button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="totalPages > 1" class="flex justify-center items-center space-x-4 mt-12 py-4 border-t border-gray-200">
        <button
          @click="goToPage(currentPage - 1)"
          :disabled="currentPage === 0"
          class="px-4 py-2 text-sm font-medium rounded-lg border bg-white hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          &larr; Anterior
        </button>

        <span class="text-sm font-semibold text-gray-700">
          Página {{ currentPage + 1 }} de {{ totalPages }}
        </span>
        
        <button
          @click="goToPage(currentPage + 1)"
          :disabled="currentPage === totalPages - 1"
          class="px-4 py-2 text-sm font-medium rounded-lg border bg-white hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          Próximo &rarr;
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      products: [],
      error: null,
      currentPage: 0,
      pageSize: 12,
      totalElements: 0,
      totalPages: 1,
    };
  },

  async fetch() {
    const page = parseInt(this.$route.query.page) || 0;
    const size = parseInt(this.$route.query.size) || this.pageSize;

    try {
      const res = await this.$axios.get('/products', { params: { page, size } });
      const data = res.data;

      if (data && data.data) {
        this.products = data.data;
        this.currentPage = data.currentPage ?? page;
        this.pageSize = data.pageSize ?? size;
        this.totalElements = data.totalElements ?? data.data.length;
        this.totalPages = data.totalPages ?? 1;
      } else {
        this.products = [];
        this.error = 'Estrutura de resposta do servidor inválida.';
      }
    } catch (err) {
      console.error('Erro ao carregar produtos:', err.response || err);
      this.error = 'Falha na conexão com o servidor. Verifique o backend.';
    }
  },

  computed: {
    isAuthenticated() {
      return this.$store.state.auth.isAuthenticated;
    },
    userRole() {
      return this.$store.state.auth.user ? this.$store.state.auth.user.role : null;
    },
  },

  watch: {
    '$route.query'(newQuery) {
      this.$fetch(); // Recarrega quando a query mudar
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  },

  methods: {
    goToPage(page) {
      if (page >= 0 && page < this.totalPages) {
        this.$router.push({ path: this.$route.path, query: { ...this.$route.query, page } });
      }
    },

    handleBuy(product) {
      if (!this.isAuthenticated) {
        alert('É necessário estar logado para prosseguir com a compra ou ver detalhes protegidos.');
        this.$router.push('/login'); 
      } else {
        this.$router.push(`/products/${product.id}`);
      }
    }
  }
};
</script>

<style scoped>
.container { max-width: 1400px; }
.line-clamp-2 { display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.line-clamp-3 { display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden; }
.min-h-\[56px\] { min-height: 56px; }
</style>