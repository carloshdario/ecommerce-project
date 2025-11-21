<template>
  <div class="bg-white p-8 rounded-xl shadow-2xl border border-gray-100 max-w-2xl mx-auto">
    
    <h2 class="text-3xl font-bold mb-8 text-gray-800 border-b pb-4">
      {{ isEditing ? 'Editar Produto Existente' : 'Criar Novo Produto' }}
    </h2>
    
    <form @submit.prevent="submitForm" class="space-y-6">
      
      <div>
        <label for="name" class="block text-sm font-semibold text-gray-700 mb-1">Nome do Produto</label>
        <input v-model="form.name" type="text" id="name" required placeholder="Nome do produto (máximo 255 caracteres)" 
               class="mt-1 block w-full border border-gray-300 rounded-lg shadow-sm p-3 focus:ring-indigo-500 focus:border-indigo-500">
      </div>
      
      <div>
        <label for="description" class="block text-sm font-semibold text-gray-700 mb-1">Descrição</label>
        <textarea v-model="form.description" id="description" rows="3" required placeholder="Detalhes completos do produto"
                  class="mt-1 block w-full border border-gray-300 rounded-lg shadow-sm p-3 focus:ring-indigo-500 focus:border-indigo-500"></textarea>
      </div>

      <div class="grid grid-cols-1 sm:grid-cols-2 gap-6">
        <div>
          <label for="price" class="block text-sm font-semibold text-gray-700 mb-1">Preço (R$)</label>
          <input v-model.number="form.price" type="number" id="price" min="0.01" step="0.01" required 
                 class="mt-1 block w-full border border-gray-300 rounded-lg shadow-sm p-3 text-lg focus:ring-indigo-500 focus:border-indigo-500">
        </div>
        <div>
          <label for="quantity" class="block text-sm font-semibold text-gray-700 mb-1">Estoque Inicial / Atual</label>
          <input v-model.number="form.quantity" type="number" id="quantity" min="0" required 
                 class="mt-1 block w-full border border-gray-300 rounded-lg shadow-sm p-3 text-lg focus:ring-indigo-500 focus:border-indigo-500">
        </div>
      </div>

      <div class="flex items-center pt-2 border-t border-gray-100">
        <input v-model="form.isActive" type="checkbox" id="isActive" class="h-5 w-5 text-red-600 border-gray-300 rounded focus:ring-red-500">
        <label for="isActive" class="ml-3 block text-base font-semibold text-gray-700">
          Produto Ativo (Visível no Catálogo)
        </label>
      </div>
      
      <p v-if="error" class="text-sm font-medium text-red-700 bg-red-50 border border-red-300 p-3 rounded-xl text-center shadow-inner">
          ❌ {{ error }}
      </p>

      <div class="grid grid-cols-2 gap-4 pt-4"> 
        
        <NuxtLink 
          to="/admin/products" 
          class="w-full flex justify-center py-3 px-4 border border-gray-300 rounded-xl shadow-lg text-lg font-bold text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-400 transition duration-150"
        >
          Voltar
        </NuxtLink>
        
        <button
          type="submit"
          :disabled="isLoading || !isFormValid"
          class="w-full flex justify-center py-3 px-4 border border-transparent rounded-xl shadow-lg text-lg font-bold text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition duration-150 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <svg v-if="isLoading" class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <span>{{ actionLabel }}</span>
        </button>
      </div>
    </form>
    
    </div>
</template>

<script>
// O script permanece idêntico
export default {
  props: {
    productId: {
      type: [String, Number],
      default: null,
    },
    isEditing: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      form: {
        id: null,
        name: '',
        price: 0.01,
        description: '',
        quantity: 0,
        isActive: true, 
      },
      isLoading: false,
      error: null,
    };
  },
  computed: {
    actionLabel() {
      return this.isLoading 
        ? (this.isEditing ? 'A Salvar Alterações...' : 'A Criar Produto...')
        : (this.isEditing ? 'Salvar Alterações' : 'Criar Produto');
    },
    isFormValid() {
      return this.form.name.trim() !== '' && 
             this.form.description.trim() !== '' && 
             this.form.price > 0 && 
             this.form.quantity >= 0;
    }
  },
  watch: {
    productId: {
      immediate: true,
      handler(newId) {
        if (this.isEditing && newId) {
          this.fetchProductData(newId);
        }
      }
    }
  },
  methods: {
    async fetchProductData(id) {
      this.isLoading = true;
      try {
        const response = await this.$axios.get(`/products/${id}`);
        const data = response.data;
        
        this.form = {
          id: data.id,
          name: data.name || '',
          price: parseFloat(data.price) || 0.01,
          description: data.description || '',
          quantity: data.quantity || 0,
          isActive: data.isActive !== undefined ? data.isActive : true,
        };
      } catch (e) {
        this.error = 'Erro ao carregar dados do produto para edição. Verifique o ID.';
        console.error('Erro de fetch:', e.response || e);
      } finally {
        this.isLoading = false;
      }
    },
    
    async submitForm() {
      if (!this.isFormValid) return;

      this.isLoading = true;
      this.error = null;
      
      const payload = { ...this.form };
      
      let url = '/products';
      let method = 'post';

      if (this.isEditing) {
        url = `/products/${payload.id}`;
        method = 'put'; 
      }

      try {
        await this.$axios({
          method: method,
          url: url,
          data: payload,
        });

        alert(`✅ Produto ${this.isEditing ? 'atualizado' : 'criado'} com sucesso!`);
        this.$router.push('/admin/products');

      } catch (e) {
        console.error('Erro ao salvar produto:', e.response || e);
        if (e.response && e.response.status === 403) {
            this.error = 'Acesso Negado. Você não tem permissão de Administrador.';
        } else {
            this.error = e.response?.data?.error || 'Falha ao salvar. Verifique o servidor ou a conexão.';
        }
      } finally {
        this.isLoading = false;
      }
    },
  },
};
</script>