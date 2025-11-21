<template>
  <div class="max-w-7xl mx-auto py-12 px-4 sm:px-6 lg:px-8">
    
    <div class="flex justify-between items-center mb-10 border-b border-gray-200 pb-5">
      <h1 class="text-3xl sm:text-4xl font-extrabold text-gray-900 tracking-tight">
        <span class="text-blue-600">Gestão de Produtos</span> 🛒
      </h1>

      <div class="flex space-x-4">
        <NuxtLink 
          to="/"
          class="inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-xl text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-400 transition duration-150 shadow-sm"
        >
          Ver Loja
        </NuxtLink>
        
        <NuxtLink 
          to="/admin/reports/sales"
          class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-xl shadow-lg text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 transition duration-150"
        >
          Relatório de Vendas
        </NuxtLink>

        <NuxtLink 
          to="/admin/products/new"
          class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-xl shadow-lg text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition duration-150"
        >
          Novo Produto
        </NuxtLink>
        
        <button 
          @click="logout"
          class="inline-flex items-center px-4 py-2 border border-red-500 text-sm font-medium rounded-xl shadow-sm text-red-600 bg-white hover:bg-red-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 transition duration-150"
        >
          Sair
        </button>
      </div>
    </div>

    <div class="mb-6 flex space-x-4">
      <button
        @click="setStatusFilter('all')"
        :class="[filterStatus === 'all' ? 'bg-blue-600 text-white shadow-md' : 'bg-white text-gray-700 border border-gray-300 hover:bg-gray-50']"
        class="px-4 py-2 text-sm font-medium rounded-lg transition duration-150"
      >
        Todos ({{ totalElements }})
      </button>
      <button
        @click="setStatusFilter('active')"
        :class="[filterStatus === 'active' ? 'bg-blue-600 text-white shadow-md' : 'bg-white text-gray-700 border border-gray-300 hover:bg-gray-50']"
        class="px-4 py-2 text-sm font-medium rounded-lg transition duration-150"
      >
        Ativos
      </button>
      <button
        @click="setStatusFilter('inactive')"
        :class="[filterStatus === 'inactive' ? 'bg-blue-600 text-white shadow-md' : 'bg-white text-gray-700 border border-gray-300 hover:bg-gray-50']"
        class="px-4 py-2 text-sm font-medium rounded-lg transition duration-150"
      >
        Inativos
      </button>
    </div>

    <div v-if="isLoading" class="text-center py-12 bg-white rounded-xl shadow-lg">
      <svg class="animate-spin mx-auto h-8 w-8 text-blue-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
      </svg>
      <p class="mt-3 text-blue-600 text-lg font-semibold">A carregar produtos...</p>
    </div>

    <div v-else-if="error" class="bg-red-50 border border-red-300 text-red-800 px-5 py-4 rounded-xl shadow-md" role="alert">
      <strong class="font-bold">Erro de Carregamento!</strong>
      <span class="block sm:inline"> {{ error }}</span>
    </div>

    <div v-else>
      <ProductAdminTable 
        :products="filteredProducts"
        @productUpdated="fetchProducts" 
      />

      <div v-if="totalPages > 1" class="flex justify-center items-center space-x-4 mt-6">
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
import ProductAdminTable from '~/components/admin/ProductAdminTable.vue';

export default {
  middleware: 'admin',
  components: { ProductAdminTable },

  data() {
    return {
      allProducts: [],
      filterStatus: 'all',
      isLoading: false,
      error: null,
      currentPage: 0,
      pageSize: 10,
      totalPages: 1,
      totalElements: 0, 
    };
  },

  computed: {
    filteredProducts() {
      if (!Array.isArray(this.allProducts)) return [];
      return this.allProducts;
    },
    totalProductsCount() {
      return Array.isArray(this.allProducts) ? this.allProducts.length : 0;
    }
  },

  mounted() {
    this.fetchProducts();
  },

  methods: {
    setStatusFilter(status) {
      if (this.filterStatus !== status) {
        this.filterStatus = status;
        this.currentPage = 0;
        this.fetchProducts();
      }
    },

    async fetchProducts() {
      this.isLoading = true;
      this.error = null;

      try {
        const response = await this.$axios.get('/products', {
          params: { 
            includeInactive: this.filterStatus !== 'active',
            page: this.currentPage,
            size: this.pageSize
          }
        });

        const data = response.data;

        if (data && Array.isArray(data.data)) {
          this.allProducts = data.data; 
          
        
          this.totalPages = data.totalPages || data.pageCount || 1;
          this.currentPage = data.number || data.currentPage || 0; 
          this.totalElements = data.totalElements || data.content?.length || data.data.length;
          
          this.error = null;
        } else {
          this.allProducts = [];
          this.error = 'Estrutura de resposta do servidor inválida. (Faltando dados de paginação).';
        }
      } catch (e) {
        console.error('Erro ao buscar produtos:', e.response || e);
        
        if (e.response && e.response.status === 403) {
            this.error = 'Acesso Negado. Você precisa ser Administrador para ver esta lista.';
        } else if (e.response && e.response.status === 401) {
             this.error = 'Não Autorizado. Por favor, faça login novamente.';
        } else {
            this.error = 'Não foi possível buscar os produtos. Verifique os logs do servidor.';
        }
        this.allProducts = [];
      } finally {
        this.isLoading = false;
      }
    },

    goToPage(page) {
      if (page >= 0 && page < this.totalPages) {
        this.currentPage = page;
        this.fetchProducts();
        window.scrollTo({ top: 0, behavior: 'smooth' });
      }
    },

    logout() {
      this.$store.dispatch('auth/logout'); 
      
      this.$router.push('/login');
    }
  }
};
</script>