<template>
    <div class="pedidos-container">
      <h2>Repartidor</h2>
      <div class="pedidos-list">
        <PedidoCard
          v-for="pedido in pedidos"
          :key="pedido.id_pedido"
          :pedidoId="pedido.id_pedido"
          :comunaId="pedido.id_zona"
          :estado="pedido.estado"
          @verDetalles="handleVerDetalles"
        />
      </div>
    </div>
</template>
  
<script setup>
  import PedidoCard from './component/PedidoCard.vue';
  import { ref, onMounted } from 'vue';
  import { getPedidosByRepartidorId } from '../../Services/Repartidor.js';
  import { useStore } from 'vuex';
  import router from '../../router.js';
  
  const store = useStore();
  const pedidos = ref([]);
  const User = store.getters.getUser;
  
  const fetchPedidos = async () => {
    try {
      const response = await getPedidosByRepartidorId(User.id_user);
      pedidos.value = response;
      console.log('Pedidos:', pedidos.value);
    } catch (error) {
      console.error('Error fetching pedidos:', error);
    }
  };
  
  const handleVerDetalles = (datos) => {
  console.log(`Ver detalles del pedido ${datos.pedidoId}, orden ${datos.ordenId}`);
  console.log(`Comuna: ${datos.comuna}`);
  router.push({ 
    name: 'PedidoDetail', 
    params: { 
      id: datos.pedidoId,
      ordenId: datos.ordenId, // Make sure this is being passed
      comuna: datos.comuna // Make sure this is being passed
    }
  });
  };
  
  onMounted(() => {
    fetchPedidos();
  });
  </script>
  
  <style scoped>
  .pedidos-container {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
  }
  
  .pedidos-list {
    margin-top: 20px;
    display: flex;
    flex-direction: column;
    gap: 10px;
  }
  
  h2 {
    margin-bottom: 20px;
  }
  </style>
  