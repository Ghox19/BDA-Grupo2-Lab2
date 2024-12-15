<script setup>
import { ref, onMounted } from 'vue';
import NavbarRepartidor from '../../components/repartidor/componente Navbar/NavRepartidor.vue';
import { getProducts } from '../../Services/ProductService';

const productos = ref([]);
const loading = ref(false);
const nullmessage = ref(false);
const errorMessage = ref('');

const getAllProducts = async () => {
  loading.value = true;
  try {
    const response = await getProducts(); // Llamar a la función del servicio
    productos.value = response; // Asignar datos a la referencia reactiva
  } catch (error) {
    errorMessage.value = 'Error al cargar los productos';
  } finally {
    loading.value = false;
    if(productos.value == null && errorMessage.value == '') {
      nullmessage.value = true;
    }
  }
};

// Llamar a la función cuando el componente se monte
onMounted(() => {
  getAllProducts();
});

</script>

<template>
  <div class="container-home">
    <NavbarRepartidor />
    <div class="content-home">
      <view-router />
    </div>
  </div>
</template>

<style scoped>
.container-home {
  height: 100%;
  width: 100%;
  background-color: white;
  display: flex;
  flex-direction: column;
}

.content-home {
  color: #333;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  width: 100%;
  overflow: hidden;
  box-sizing: border-box;
}
</style>