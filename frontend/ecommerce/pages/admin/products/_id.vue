<template>
  <div class="py-10">
    <ProductForm 
      :is-editing="true" 
      :product-id="$route.params.id" 
    />
  </div>
</template>

<script>
import ProductForm from '~/components/admin/ProductForm.vue';

export default {
  middleware: 'admin',
  
  components: {
    ProductForm
  },
  
  head() {
    return {
      title: `Editar Produto ${this.$route.params.id} - Admin`
    }
  }
};
</script>