<script setup>
import { ref, onMounted } from 'vue';
import { getPedidosSinAsignar } from '../../../Services/OrdenService.js';
</script>

<template>
    <div class="container-ListPedidos">
        <h1>Lista de pedidos sin asignar</h1>
        <div v-for="" class="pedidos-list">

        </div>
    </div>
</template>

<style scoped>
.container-ListPedidos {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 100%;
    width: 100%;
    background-color: red;
}
</style>