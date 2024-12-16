<script setup>
import { ref, onMounted } from 'vue';
import { getPedidosSinAsignar, updatePedido } from '../../../Services/Pedido';
import { useStore } from 'vuex';

const pedidos = ref([]);
const store = useStore();
const idRepartidor = store.state.user.id_user;

onMounted(async () => {
  try {
    const pedidosSinAsignar = await getPedidosSinAsignar();
    pedidos.value = pedidosSinAsignar;
    console.log('Pedidos sin asignar:', pedidos.value);
  } catch (error) {
    console.error('Error al cargar los pedidos:', error);
  }
});


const asignarPedido = async (pedido) => {

  const pedidoActualizado = {
    id_pedido: pedido.id_pedido,
    id_zona: pedido.id_zona,
    id_cliente: idRepartidor, 
    coordenada_direccion: pedido.coordenada_direccion,
    estado: pedido.estado, 
  };

  try {
    const actualizado = await updatePedido(pedido.id_pedido, pedidoActualizado);
    if (actualizado) {
      pedidos.value = pedidos.value.filter(p => p.id_pedido !== pedido.id_pedido);
      alert('Pedido asignado con éxito.');
    } else {
      alert('No se pudo asignar el pedido.');
    }
  } catch (error) {
    console.error('Error al asignar el pedido:', error);
    alert('Error al asignar el pedido.');
  }
};

</script>

<template>
  <div class="container-ListPedidos">
    <h1 class="titulo">Lista de pedidos sin asignar</h1>
    <div class="list-pedidos">
      <ul>
        <li 
          v-for="pedido in pedidos" 
          :key="pedido.id_pedido" 
          class="pedido-item"
        >
          <div class="pedido-info">
            <p><strong>Dirección:</strong> {{ pedido.direccion }} </p>
          </div>
          <button 
            class="asignar-btn" 
            @click="asignarPedido(pedido)"
          >
            Asignar como mío
          </button>
        </li>
      </ul>
    </div>
  </div>
</template>

<style scoped>
.container-ListPedidos {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  height: 100%;
  width: 100%;
  background-color: #f9f9f9;
  padding: 20px;
  font-family: Arial, sans-serif;
}

.titulo {
  color: #333;
  font-size: 24px;
  margin-bottom: 20px;
  margin-top: 60px;
}

.list-pedidos {
  width: 100%;
  height: 100%;
    display: flex;
    justify-content: center;
}

.list-pedidos ul {
  list-style: none;
  padding: 0;
  margin: 0;
  width: 100%;
  max-width: 600px;
}

.pedido-item {
  display: flex;
  justify-content: space-between;
  width: 100%;
  align-items: center;
  padding: 15px;
  margin-bottom: 10px;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.pedido-info p {
  margin: 0 0 5px;
  width: 100%;
  color: #555;
}

.asignar-btn {
  padding: 8px 12px;
  font-size: 14px;
  color: white;
  background-color: #4CAF50;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.asignar-btn:hover {
  background-color: #45a049;
}
</style>