<script setup>
import NavbarRepartidor from '../../components/repartidor/componente Navbar/NavRepartidor.vue';
import { ref } from 'vue';

</script>

<template>
  <div class="container-home">
    <NavbarRepartidor />
    <div class="content-home">
      <router-view></router-view>
    </div>
  </div>
</template>

<style scoped>
.container-home {
  min-height: 100vh;
  width: 100%;
  background-color: white;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.content-home {
  flex: 1;
  padding: 20px;
  color: #333;
  display: flex;
  flex-direction: column;
  width: 100%;
  box-sizing: border-box;
}
</style>
