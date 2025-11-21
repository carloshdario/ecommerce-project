<template>
  <div class="py-10">
    <ProductForm :is-editing="false" />
  </div>
</template>

<script>
import ProductForm from '~/components/admin/ProductForm.vue';

export default {
  middleware: 'admin',
  
  components: {
    ProductForm
  },
  
  head: {
    title: 'Novo Produto - Admin'
  }
};
</script>