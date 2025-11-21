<template>
  <div class="overflow-hidden bg-white rounded-xl shadow-2xl border border-gray-100">
    <div class="px-6 py-4 border-b border-gray-200 bg-gray-50">
        <h3 class="text-lg font-bold text-gray-900">Histórico Recente de Compras</h3>
    </div>
    <table class="min-w-full table-auto border-collapse">
      <thead>
        <tr class="bg-gray-50 text-gray-600 uppercase text-xs font-semibold leading-normal border-b border-gray-200">
          <th class="py-3 px-6 text-left">Produto</th>
          <th class="py-3 px-6 text-center">Quantidade</th>
          <th class="py-3 px-6 text-right">Preço Unitário</th>
          <th class="py-3 px-6 text-left">Data da Compra</th>
        </tr>
      </thead>
      <tbody class="text-gray-700 text-sm font-light divide-y divide-gray-100">
        <tr 
          v-for="order in orders" 
          :key="order.ORDER_ID"
          class="hover:bg-blue-50/50 transition duration-100"
        >
          <td class="py-3 px-6 text-left font-medium">{{ order.product_name || 'Produto Desconhecido' }}</td> 
          
          <td class="py-3 px-6 text-center font-medium">{{ order.QUANTITY }}</td> 
          
          <td class="py-3 px-6 text-right font-bold text-blue-600">
            R$ {{ parseFloat(order.PRICE).toFixed(2) }}
          </td> 
          
          <td class="py-3 px-6 text-left text-xs text-gray-500">
            {{ formatPurchaseDate(order.PURCHASE_DATE) }}
          </td>
        </tr>
        <tr v-if="orders.length === 0">
           <td colspan="4" class="py-6 text-center text-gray-500 italic">
               Nenhum histórico de compras encontrado.
           </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
export default {
  props: {
    orders: {
        type: Array,
        default: () => []
    }
  },
  methods: {
    formatPurchaseDate(dateString) {
        if (!dateString) return 'N/A';
        try {
            const date = new Date(dateString);
            return date.toLocaleDateString('pt-BR') + ' ' + date.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
        } catch (e) {
            return dateString; 
        }
    }
  }
}
</script>