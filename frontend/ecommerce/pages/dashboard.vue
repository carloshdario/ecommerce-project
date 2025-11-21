<template>
  <div class="min-h-screen bg-gray-50 p-6 sm:p-10">
    <div class="max-w-7xl mx-auto">

      <div class="flex justify-between items-center mb-10 flex-wrap gap-4 border-b border-gray-200 pb-4">
        <h1 class="text-4xl sm:text-5xl font-extrabold text-gray-900 tracking-tight">
          Meu Dashboard
        </h1>
        <div class="flex space-x-3">
          <button @click="$router.push('/')" 
                  class="bg-blue-600 hover:bg-blue-700 text-white font-semibold px-5 py-2.5 rounded-xl shadow-md transition duration-200 flex items-center focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
            Voltar para a Loja
          </button>
          <button @click="logout" 
                  class="bg-white border border-red-500 text-red-500 hover:bg-red-50 hover:text-red-700 font-semibold px-5 py-2.5 rounded-xl shadow-md transition duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500">
            Sair
          </button>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-2xl p-8 mb-12 text-center border-t-4 border-blue-600">
        <h2 class="text-3xl font-bold mb-2 text-gray-800">
          Olá, {{ username }}! 
        </h2>
        <p class="text-lg text-gray-600">Este é o resumo da sua atividade de compras e histórico de pedidos.</p>
      </div>

      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8 mb-12">
        
        <div class="bg-white p-8 rounded-xl shadow-xl border-l-4 border-blue-600 transition duration-300 hover:shadow-2xl">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium text-gray-500 uppercase tracking-wider mb-1">Total Gasto</p>
              <p class="text-4xl font-extrabold text-blue-600">R$ {{ totalSpent.toFixed(2) }}</p>
            </div>
            </div>
        </div>

        <div class="bg-white p-8 rounded-xl shadow-xl border-l-4 border-green-600 transition duration-300 hover:shadow-2xl">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium text-gray-500 uppercase tracking-wider mb-1">Itens Adquiridos</p>
              <p class="text-4xl font-extrabold text-green-600">{{ totalProducts }}</p>
            </div>
            </div>
        </div>
        
        <div class="bg-white p-8 rounded-xl shadow-xl border-l-4 border-purple-600 transition duration-300 hover:shadow-2xl">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium text-gray-500 uppercase tracking-wider mb-1">Total de Pedidos</p>
              <p class="text-4xl font-extrabold text-purple-600">{{ orders.length }}</p>
            </div>
            </div>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-2xl overflow-hidden">
        <div class="p-6 border-b border-gray-200">
            <h3 class="text-3xl font-semibold text-gray-800">Detalhes das Suas Compras</h3>
        </div>
        
        <div v-if="loading" class="text-center p-8 text-gray-500">
          Carregando histórico, por favor aguarde...
        </div>
        
        <div v-else class="overflow-x-auto">
          <table class="min-w-full text-left border-collapse">
            <thead>
              <tr class="text-gray-600 uppercase text-xs font-bold bg-gray-50 border-b border-gray-200">
                <th class="px-6 py-4">Produto</th>
                <th class="px-6 py-4 text-center">Qtde.</th>
                <th class="px-6 py-4 text-right">Preço Unitário</th>
                <th class="px-6 py-4 text-right">Total</th>
                <th class="px-6 py-4">Data da Compra</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="order in orders" :key="order.order_id" 
                  class="border-b border-gray-100 text-sm hover:bg-blue-50/50 transition duration-100">
                
                <td class="px-6 py-3 font-medium text-gray-800">{{ order.productName }}</td>
                <td class="px-6 py-3 text-center text-gray-600">{{ order.quantity }}</td>
                
                <td class="px-6 py-3 text-right text-gray-700">
                   R$ {{ (order.price || 0).toFixed(2) }}
                </td>
                
                <td class="px-6 py-3 text-right font-bold text-gray-800">
                   R$ {{ ((order.price || 0) * (order.quantity || 0)).toFixed(2) }}
                </td>

                <td class="px-6 py-3 text-gray-500 text-xs">{{ formatDate(order.purchase_date) }}</td>
              </tr>
              
              <tr v-if="orders.length === 0">
                <td colspan="5" class="px-6 py-10 text-center text-xl font-medium text-blue-500">
                  Nenhum pedido encontrado. Vá às compras! 🛍️
                </td>
              </tr>
            </tbody>
            <tfoot v-if="orders.length > 0">
                <tr class="bg-blue-50 font-extrabold text-blue-800 text-lg border-t-2 border-blue-600">
                    <td colspan="3" class="px-6 py-3 text-right uppercase">TOTAL GERAL:</td>
                    <td class="px-6 py-3 text-right">R$ {{ totalSpent.toFixed(2) }}</td>
                    <td class="px-6 py-3"></td>
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
  middleware: 'auth', 
  data() {
    return {
      username: '',
      orders: [],
      loading: true
    }
  },
  computed: {
    totalProducts() {
      return this.orders.reduce((sum, o) => sum + (o.quantity || 0), 0)
    },
    totalSpent() {
      return this.orders.reduce((sum, o) => {
        const price = parseFloat(o.price) || 0;
        const quantity = o.quantity || 0;
        return sum + (price * quantity);
      }, 0);
    }
  },
  methods: {
    async fetchOrders() {
      if (!this.$store.state.auth.isAuthenticated) {
         this.loading = false;
         return;
      }
      
      try {
        const res = await this.$axios.get('/orders/history') 
        this.orders = res.data.map(o => ({
          order_id: o.order_id, 
          productName: o.product_name,
          quantity: parseInt(o.quantity) || 0, 
          price: parseFloat(o.price) || 0, 
          purchase_date: o.purchase_date
        }))
        this.username = this.$store.state.auth.user?.username || 'Usuário'
      } catch (err) {
        console.error("Erro ao buscar histórico de pedidos:", err.response || err)
        this.orders = []
      } finally {
        this.loading = false
      }
    },
    
    formatDate(date) {
      if (!date) {
        return 'N/A' 
      }
      
      const parsedDate = new Date(date)
      
      if (isNaN(parsedDate)) {
        return date.split('T')[0] || 'Inválido' 
      }

      return parsedDate.toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' })
    },
    
    logout() {
      this.$store.dispatch('auth/logout') 
      this.$router.push('/login')
    }
  },
  
  mounted() {
    this.username = this.$store.state.auth.user?.username || 'Usuário'
    this.fetchOrders()
  }
}
</script>