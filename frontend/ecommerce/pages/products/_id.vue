<template>
  <div class="min-h-screen bg-gray-50 p-6 sm:p-10">
    <div class="max-w-4xl mx-auto bg-white shadow-2xl p-8 lg:p-12 rounded-xl border border-gray-100">

      <button
        @click="$router.push('/')"
        class="mb-8 text-base text-gray-600 hover:text-blue-600 font-medium transition duration-150 flex items-center group"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2 group-hover:-translate-x-1 transition duration-150" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
        </svg>
        Voltar para o Catálogo
      </button>

      <div v-if="isLoading" class="animate-pulse flex flex-col md:flex-row gap-8">
        <div class="w-full md:w-1/3 h-80 bg-gray-200 rounded-xl"></div>
        <div class="w-full md:w-2/3">
          <div class="h-12 bg-gray-300 rounded w-3/4 mb-6"></div>
          <div class="h-6 bg-gray-200 rounded w-full mb-3"></div>
          <div class="h-6 bg-gray-200 rounded w-11/12 mb-8"></div>
          <div class="h-10 bg-gray-400 rounded w-1/3"></div>
        </div>
      </div>

      <div v-else-if="error" class="text-center p-12 bg-red-50 rounded-xl border border-red-200">
        <p class="text-2xl text-red-700 font-bold mb-4">
          ❌ Erro ao carregar o produto.
        </p>
        <p class="text-gray-700">{{ error }}</p>
      </div>

      <div v-else class="flex flex-col md:flex-row gap-10">

        <div class="w-full md:w-1/3 flex-shrink-0">
          <div class="bg-gray-100 h-80 rounded-xl flex items-center justify-center text-gray-500 text-base italic border border-gray-300 shadow-md">
            [Image Placeholder: {{ product.name }}]
          </div>
        </div>

        <div class="w-full md:w-2/3">
          <h2 class="text-4xl lg:text-5xl font-extrabold text-gray-900 mb-4">{{ product.name }}</h2>

          <p class="text-lg text-gray-600 mb-6 leading-relaxed border-b border-gray-200 pb-6">
            {{ product.description }}
          </p>

          <div class="flex items-end justify-between mb-8">
            <p class="text-6xl font-black text-blue-700">
              R$ {{ product.price?.toFixed(2) }}
            </p>

            <p class="text-lg font-medium text-gray-600">
              Em Estoque: 
              <span 
                :class="{ 
                    'text-red-600': product.quantity <= 5, 
                    'text-green-600': product.quantity > 5 
                }" 
                class="font-extrabold">
                {{ product.quantity }}
              </span>
            </p>
          </div>
          
          <p v-if="product.quantity === 0" class="text-red-700 font-bold text-xl p-4 bg-red-100 rounded-lg shadow-inner text-center">
            ⚠️ Esgotado! Produto sem estoque no momento.
          </p>

          <div v-else class="mt-8 bg-gray-50 p-6 rounded-xl border border-gray-200">
            <div class="flex items-center justify-between mb-6">
              <label for="quantity" class="text-xl font-bold text-gray-800">Selecione a Quantidade:</label>
              
              <input
                id="quantity"
                type="number"
                v-model.number="selectedQuantity"
                min="1"
                :max="product.quantity"
                class="w-28 p-3 border-2 border-blue-500 rounded-xl text-center font-bold text-2xl shadow-inner focus:ring-2 focus:ring-blue-500 transition"
                @change="validateQuantity"
              />
            </div>

            <p v-if="selectedQuantity > product.quantity" class="text-red-600 text-sm mb-4">
                Não é possível comprar {{ selectedQuantity }} unidades. Máximo em estoque: {{ product.quantity }}.
            </p>
            <p v-else-if="selectedQuantity < 1" class="text-red-600 text-sm mb-4">
                A quantidade mínima é 1.
            </p>
            
            <button
              @click="purchase"
              :disabled="isPurchasing || !isQuantityValid || !isAuthenticated"
              class="w-full bg-green-600 hover:bg-green-700 text-white font-extrabold py-4 px-6 rounded-xl shadow-2xl transition duration-300 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center space-x-2 text-xl mt-4"
            >
              <svg v-if="isPurchasing" class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <span>{{ isPurchasing ? 'A processar...' : 'Finalizar Compra Agora' }} ({{ selectedQuantity }} Item<span v-if="selectedQuantity > 1">s</span>)</span>
            </button>
            
            <p v-if="!isAuthenticated" class="mt-4 text-center text-sm font-medium text-red-500">
              * Você precisa estar logado para finalizar a compra.
            </p>

          </div>
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
      product: {},
      isLoading: true, 
      isPurchasing: false, 
      error: null,
      selectedQuantity: 1 
    }
  },
  
  computed: {
    isAuthenticated() {
      return this.$store.state.auth.isAuthenticated
    },
    isQuantityValid() {
        const qty = this.selectedQuantity;
        const max = this.product.quantity;
        return qty && qty >= 1 && qty <= max;
    }
  },

  async created() {
    const id = this.$route.params.id
    this.isLoading = true
    this.error = null

    try {
      const res = await this.$axios.get(`/products/${id}`)
      this.product = res.data
    } catch (err) {
      console.error('Erro ao buscar detalhes do produto:', err.response || err)
      this.error = 'Não foi possível carregar os detalhes do produto. Ele pode não existir.'
    } finally {
      this.isLoading = false
    }
  },
  
  methods: {
    validateQuantity() {
        let qty = Math.max(1, parseInt(this.selectedQuantity) || 1);
        qty = Math.min(qty, this.product.quantity);
        this.selectedQuantity = qty;
    },
    
    async purchase() {
      if (!this.isQuantityValid) {
          alert('Por favor, selecione uma quantidade válida.');
          return;
      }
      
      this.isPurchasing = true

      try {
        
        await this.$axios.post('/orders', 
          { 
            productId: this.product.id,
            quantity: this.selectedQuantity 
          }
        )

        alert(`🎉 Compra de ${this.selectedQuantity} unidades de ${this.product.name} realizada com sucesso! Você será redirecionado para o Painel.`)
        this.$router.push('/dashboard') 

      } catch (err) {
        console.error('Erro de compra:', err.response || err)
        
        let errorMessage = 'Erro ao finalizar a compra. Tente novamente.'
        
        if (err.response) {
          if (err.response.status === 401 || err.response.status === 403) {
            errorMessage = 'Sua sessão expirou ou é inválida. Por favor, faça login novamente.'
            this.$store.dispatch('auth/logout'); 
            this.$router.push('/login')
          } else if (err.response.data && err.response.data.message) {
             errorMessage = err.response.data.message
          } else if (err.response.status === 400) {
             errorMessage = 'Requisição inválida. Verifique se o estoque é suficiente.'
          }
        }
        
        alert(errorMessage)
      } finally {
        this.isPurchasing = false
      }
    }
  }
}
</script>