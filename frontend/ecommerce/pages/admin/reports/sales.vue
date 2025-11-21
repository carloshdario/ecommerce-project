<template>
  <div class="min-h-screen bg-gray-50 p-6 sm:p-10">
    <div class="max-w-7xl mx-auto">
      
      <div class="flex justify-between items-center mb-10 flex-wrap gap-4 border-b border-gray-200 pb-4">
        <h1 class="text-4xl sm:text-5xl font-extrabold text-gray-900 tracking-tight">
          <span class="text-green-600">Relatório de Vendas</span> 📊
        </h1>
        <div class="flex space-x-3">
          <NuxtLink 
            to="/admin/products"
            class="bg-white border border-gray-300 text-gray-700 hover:bg-gray-100 font-semibold px-5 py-2.5 rounded-xl shadow-md transition duration-200 flex items-center focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-400"
          >
            Voltar para Produtos
          </NuxtLink>
        </div>
      </div>

      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8 mb-12">
        
        <div class="bg-white p-8 rounded-xl shadow-xl border-l-4 border-green-600 transition duration-300 hover:shadow-2xl">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium text-gray-500 uppercase tracking-wider mb-1">Faturamento Total</p>
              <p class="text-4xl font-extrabold text-green-600">{{ formatCurrency(totalRevenue) }}</p>
            </div>
            </div>
        </div>

        <div class="bg-white p-8 rounded-xl shadow-xl border-l-4 border-indigo-600 transition duration-300 hover:shadow-2xl">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium text-gray-500 uppercase tracking-wider mb-1">Total de Itens Vendidos</p>
              <p class="text-4xl font-extrabold text-indigo-600">{{ totalQuantity }}</p>
            </div>
            </div>
        </div>
        
        <div class="bg-white p-8 rounded-xl shadow-xl border-l-4 border-red-600 transition duration-300 hover:shadow-2xl">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium text-gray-500 uppercase tracking-wider mb-1">Total de Registros</p>
              <p class="text-4xl font-extrabold text-red-600">{{ sales.length }}</p>
            </div>
            </div>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-2xl overflow-hidden">
        <div class="p-6 border-b border-gray-200 flex justify-between items-center">
            <h3 class="text-3xl font-semibold text-gray-800">Detalhes de Cada Venda</h3>
            <button 
              @click="fetchSales" 
              :disabled="loading"
              class="bg-gray-100 text-gray-700 hover:bg-gray-200 text-sm font-semibold px-4 py-2 rounded-lg transition duration-150 flex items-center disabled:opacity-50">
                <span>{{ loading ? 'A Carregar...' : 'Atualizar Dados' }}</span>
            </button>
        </div>
        
        <div v-if="loading" class="text-center p-8 text-gray-500">
          Carregando dados de vendas, por favor aguarde...
        </div>

        <div v-else-if="error" class="p-6">
          <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative" role="alert">
            <strong class="font-bold">Erro de Carregamento:</strong>
            <span class="block sm:inline"> {{ error.message }}. Verifique o endpoint.</span>
          </div>
        </div>
        
        <div v-else class="overflow-x-auto">
          <table class="min-w-full text-left border-collapse">
            <thead>
              <tr class="text-gray-600 uppercase text-xs font-bold bg-gray-50 border-b border-gray-200">
                <th class="px-6 py-4">Pedido ID</th>
                <th class="px-6 py-4">Produto</th>
                <th class="px-6 py-4 text-center">Qtde</th>
                <th class="px-6 py-4 text-right">Preço Unitário</th>
                <th class="px-6 py-4 text-right">Total</th>
                <th class="px-6 py-4">Data da Compra</th>
                <th class="px-6 py-4">Cliente</th>
              </tr>
            </thead>
            <tbody>
              <tr 
                v-for="sale in sales" 
                :key="sale.order_id" 
                class="border-b border-gray-100 text-sm hover:bg-green-50/50 transition duration-100"
              >
                <td class="px-6 py-3 font-medium text-gray-800">{{ sale.order_id }}</td>
                <td class="px-6 py-3 font-medium text-gray-800">{{ sale.product_name }}</td>
                <td class="px-6 py-3 text-center text-gray-600">{{ sale.quantity }}</td>
                
                <td class="px-6 py-3 text-right text-gray-700">
                   {{ formatCurrency(sale.price) }}
                </td>
                
                <td class="px-6 py-3 text-right font-bold text-green-700">
                   {{ formatCurrency(sale.price * sale.quantity) }}
                </td>

                <td class="px-6 py-3 text-gray-500 text-xs">{{ formatDate(sale.purchase_date) }}</td>
                <td class="px-6 py-3 text-gray-500 text-xs">{{ sale.customer_username }}</td>
              </tr>
              
              <tr v-if="sales.length === 0">
                <td colspan="7" class="px-6 py-10 text-center text-xl font-medium text-red-500">
                  Nenhuma venda encontrada no sistema. 🛑
                </td>
              </tr>
            </tbody>
            <tfoot v-if="sales.length > 0">
                <tr class="bg-green-50 font-extrabold text-green-800 text-lg border-t-2 border-green-600">
                    <td colspan="4" class="px-6 py-3 text-right uppercase">TOTAL GERAL:</td>
                    <td class="px-6 py-3 text-right">{{ formatCurrency(totalRevenue) }}</td>
                    <td colspan="2" class="px-6 py-3"></td>
                </tr>
            </tfoot>
          </table>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
export default {
  middleware: 'admin',  
  data() {
    return {
      sales: [],
      loading: false,
      error: null
    }
  },
  
  computed: {
    totalRevenue() {
      return this.sales.reduce((sum, sale) => sum + (sale.price * sale.quantity), 0);
    },
    totalQuantity() {
      return this.sales.reduce((sum, sale) => sum + sale.quantity, 0);
    },
  },

  created() {
    this.fetchSales();
  },

  methods: {
    async fetchSales() {
      this.loading = true;
      this.error = null;
      try {
        const response = await this.$axios.get('/orders/admin/all-history');
        
        this.sales = response.data.map(sale => ({
          order_id: sale.order_id, 
          product_name: sale.product_name,
          quantity: parseInt(sale.quantity) || 0, 
          price: parseFloat(sale.price) || 0, 
          purchase_date: sale.purchase_date,
          customer_username: sale.customer_username 
        }));
      } catch (e) {
        this.error = e.response ? { message: `Status ${e.response.status}` } : { message: 'Erro de rede' };
        console.error('Erro ao buscar vendas:', e);
        this.sales = [];
      } finally {
        this.loading = false;
      }
    },
    
    formatCurrency(value) {
      if (value === null || value === undefined) return 'R$ 0,00';
      return Number(value).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
    },
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleDateString('pt-BR') + ' ' + date.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
    }
  }
}
</script>