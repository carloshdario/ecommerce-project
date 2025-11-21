<template>
  <div class="overflow-x-auto bg-white rounded-xl shadow-2xl border border-gray-100">
    <table class="min-w-full table-auto border-collapse">
      <thead>
        <tr class="bg-gray-100 text-gray-700 uppercase text-sm leading-normal border-b border-gray-200">
          <th class="py-3 px-4 text-left font-bold w-1/4">Produto (ID)</th>
          <th class="py-3 px-4 text-center font-bold">Estoque</th>
          <th class="py-3 px-4 text-right font-bold">Preço (R$)</th>
          <th class="py-3 px-4 text-center font-bold">Status</th>
          <th class="py-3 px-4 text-center font-bold">Ações</th>
        </tr>
      </thead>
      <tbody class="text-gray-700 text-sm font-light">
        <tr 
          v-for="product in products" 
          :key="product.id"
          class="border-b border-gray-100 hover:bg-indigo-50/50 transition duration-100"
        >
          <td class="py-3 px-4 text-left font-medium">
            <span class="font-bold text-gray-900">{{ product.name }}</span>
            <span class="text-xs text-gray-500 block">ID: {{ product.id }}</span>
          </td> 
          
          <td class="py-3 px-4 text-center font-semibold">{{ product.quantity }}</td> 
          
          <td class="py-3 px-4 text-right font-semibold text-green-600">
            {{ (product.price || 0).toFixed(2) }}
          </td> 
          
          <td class="py-3 px-4 text-center">
             <span 
                :class="product.isActive ? 'bg-green-200 text-green-800' : 'bg-red-200 text-red-800'"
                class="py-1 px-3 rounded-full text-xs font-semibold"
             >
                {{ product.isActive ? 'Ativo' : 'Inativo' }}
             </span>
          </td>
          
          <td class="py-3 px-4 text-center space-x-2">
            
            <NuxtLink 
              :to="`/admin/products/${product.id}`"
              class="bg-blue-500 hover:bg-blue-600 text-white py-1 px-3 rounded-md text-xs font-semibold transition duration-150"
            >
              Editar
            </NuxtLink>
            
            <button
              @click="toggleActiveStatus(product)"
              :disabled="loadingStatus[product.id]"
              :class="product.isActive ? 'bg-red-500 hover:bg-red-600' : 'bg-green-500 hover:bg-green-600'"
              class="text-white py-1 px-3 rounded-md text-xs font-semibold transition duration-150 disabled:opacity-50"
            >
              {{ product.isActive ? 'Inativar' : 'Ativar' }}
            </button>
          </td>
        </tr>
        <tr v-if="products.length === 0">
           <td colspan="5" class="py-6 text-center text-gray-500 italic">
               Nenhum produto encontrado.
           </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
export default {
  props: {
    products: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      // Estado para gerenciar o loading individual de cada linha (botão)
      loadingStatus: {}
    }
  },
  methods: {
    /**
     * Alterna o status ativo/inativo do produto (Soft Delete).
     */
    async toggleActiveStatus(product) {
      // Usando product.isActive para a verificação do status atual
      if (!confirm(`Tem certeza que deseja ${product.isActive ? 'INATIVAR' : 'ATIVAR'} o produto: ${product.name}?`)) {
        return;
      }

      this.$set(this.loadingStatus, product.id, true); // Inicia loading

      // Baseado em product.isActive
      const newStatus = !product.isActive;
      
      try {
        // Envia o payload com 'isActive' (para coincidir com a DTO do backend)
        await this.$axios.put(`/products/${product.id}`, {
          ...product,
          isActive: newStatus // 🚨 CRÍTICO: Usando 'isActive' no payload
        });
        
        alert(`Status do produto '${product.name}' alterado para: ${newStatus ? 'ATIVO' : 'INATIVO'}.`);
        
        // 🔑 CORREÇÃO DO DESAPARECIMENTO:
        // Atualiza o status localmente no objeto 'product' ANTES de emitir o evento.
        this.$set(product, 'isActive', newStatus);

        // Emite evento para que a página pai recarregue a lista
        this.$emit('productUpdated'); 
        
      } catch (e) {
        console.error('Erro ao alternar status do produto:', e.response || e);
        if (e.response && e.response.status === 403) {
            alert('Acesso Negado. Você não tem permissão de Administrador.');
        } else {
            alert('Falha ao atualizar o status do produto.');
        }
      } finally {
        this.$set(this.loadingStatus, product.id, false); // Finaliza loading
      }
    }
  }
}
</script>