<template>
  <div class="bg-white border border-gray-200 p-6 rounded-2xl shadow-xl transition duration-300 transform hover:scale-[1.03] hover:shadow-blue-300/50 flex flex-col justify-between h-full">
    
    <div class="bg-gray-100 h-32 mb-5 rounded-xl flex items-center justify-center text-gray-400 text-sm italic border border-gray-200">
      [Placeholder Imagem]
    </div>

    <div>
      <h3 class="text-xl font-extrabold text-gray-900 truncate mb-1" :title="product.name">{{ product.name }}</h3>
      
      <p class="text-3xl font-bold text-blue-700 mb-4">
        R$ {{ (product.price || 0).toFixed(2) }}
      </p>
      
      <p class="text-sm text-gray-500 mb-5">
          Estoque: 
          <span 
            :class="{ 
              'text-red-600 font-bold': product.quantity === 0, 
              'text-orange-600 font-bold': product.quantity > 0 && product.quantity <= 5,
              'text-green-600 font-bold': product.quantity > 5
            }"
          >
            {{ product.quantity }}
          </span>
      </p>
    </div>

    <button 
      @click="$emit('buy', product)" 
      :disabled="product.quantity === 0"
      class="w-full bg-blue-600 text-white font-semibold px-4 py-3 rounded-xl mt-auto shadow-md hover:bg-blue-700 transition duration-200 disabled:bg-gray-300 disabled:cursor-not-allowed text-base tracking-wider"
    >
      {{ product.quantity > 0 ? 'Comprar Agora' : 'Esgotado' }}
    </button>
  </div>
</template>

<script>
export default {
  props: {
    product: {
      type: Object,
      required: true,
      default: () => ({ name: 'Produto', price: 0, quantity: 0 })
    }
  }
}
</script>