<template>
  <div class="pedido-detalle">
    <div class="pedido-info">
        <h2>Detalles del Pedido</h2>
        <p><strong>ID Pedido:</strong> {{ pedido.id_pedido }}</p>
        <p><strong>ID Orden:</strong> {{ props.ordenId }}</p>
        <p><strong>Comuna:</strong> {{ props.comuna }}</p>
        <p><strong>Dirección:</strong> {{ pedido.direccion }}</p>
        <p><strong>Estado actual:</strong> {{ pedido.estado }}</p>
        
        <div class="estado-cambio">
            <label for="nuevo-estado">Cambiar estado:</label>
            <select v-model="nuevoEstado" @change="cambiarEstado">
            <option value="en_preparacion">En preparación</option>
            <option value="en_camino">En camino</option>
            <option value="entregado">Entregado</option>
            </select>
            <button @click="confirmarCambio" class="confirm-button">Confirmar Cambio</button>
        </div>
        </div>

        <div class="mapa-container">
        <h3>Ubicación de entrega</h3>
        <div id="mapa" ref="mapaRef"></div>
    </div>
  </div>
</template>
  
<script setup>
  
  import { ref, onMounted, watch } from 'vue';
  import { useRoute } from 'vue-router';
  import L from 'leaflet';
  import 'leaflet/dist/leaflet.css';
  import { getPedidoById, updatePedido } from '../../Services/Pedido.js';
  
  const props = defineProps({
    id: {
        type: Number,
        required: true,
        validator: (value) => value !== undefined
    },
    ordenId: {
        type: Number,
        required: true
    },
    comuna: {
        type: String,
        required: true
    }
  });

  const pedido = ref({});
  const nuevoEstado = ref('');
  const mapaRef = ref(null);
  let mapa = null;
  
  const fetchPedido = async (id) => {
    pedido.value = await getPedidoById(id);
    console.log('Pedido:', pedido.value);
  };
  
  const cambiarEstado = () => {
    console.log(`Cambiando estado a: ${nuevoEstado.value}`);
    pedido.value.estado = nuevoEstado.value;
  };

  const confirmarCambio = async () => {
    try {
        const pedidoActualizado = {
        ...pedido.value,
        estado: nuevoEstado.value
        };
        
        await updatePedido(pedido.value.id_pedido, pedidoActualizado);
        alert('Estado actualizado correctamente');
        await fetchPedido(props.id); // Recargar el pedido
    } catch (error) {
        console.error('Error al actualizar el pedido:', error);
        alert('Error al actualizar el estado del pedido');
    }
  };
  
  const inicializarMapa = () => {
  if (mapa) {
    mapa.remove();
    }
  if (pedido.value && pedido.value.coordenada_direccion) {
    const coords = pedido.value.coordenada_direccion.coordinates;
    mapa = L.map(mapaRef.value).setView([coords[1], coords[0]], 13);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(mapa);
    L.marker([coords[1], coords[0]]).addTo(mapa)
      .bindPopup(`${pedido.value.direccion}`)
      .openPopup();
    }
  };

  
  onMounted(async () => {
    if (props.id) {
        await fetchPedido(props.id);
        inicializarMapa();
        console.log("comuna", props.comuna);
    }
  });
  
  watch(() => props.pedidoId, (newId) => {
    if (newId) {
        fetchPedido();
    }
    }, { immediate: true });
  </script>
  
  <style scoped>
  .pedido-detalle {
    display: flex;
    gap: 20px;
    padding: 20px;
  }
  
  .pedido-info, .mapa-container {
    flex: 1;
  }
  
  .estado-cambio {
    margin-top: 20px;
  }
  
  #mapa {
    height: 400px;
    width: 100%;
  }
  </style>
  