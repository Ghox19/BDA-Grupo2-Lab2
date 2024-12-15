<script setup>
import NavbarRepartidor from '../../components/repartidor/componente Navbar/NavRepartidor.vue';
import ListPedidos from './ViewsRepartidor/ListPedidos.vue';
</script>

<template>
  <div class="container-home">
    <NavbarRepartidor />
    <div class="content-home">
        <ListPedidos />
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