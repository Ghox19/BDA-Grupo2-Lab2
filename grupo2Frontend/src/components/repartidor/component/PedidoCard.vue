<template>
    <div class="order-card">
      <div class="order-info">
        <div class="info-item">
          <strong>ID Pedido:</strong>
          <span>{{ pedidoId }}</span>
        </div>
        <div class="info-item">
          <strong>ID Orden:</strong>
          <span>{{ ordenId }}</span>
        </div>
        <div class="info-item">
          <strong>Comuna:</strong>
          <span>{{ comuna }}</span>
        </div>
        <div class="info-item">
          <strong>Estado:</strong>
          <span>{{ estado }}</span>
        </div>
      </div>
      <button @click="verDetalles" class="details-button">Ver Detalles</button>
    </div>
  </template>
  
  <script setup>
  import { defineProps, defineEmits, onMounted, ref } from 'vue';
  import { getOrdenByPedidoId, getZonaById } from '../../../Services/Repartidor.js';
  
  const ordenId = ref(null);
  const comuna = ref('');
  
  const props = defineProps({
    pedidoId: {
      type: Number,
      required: true
    },
    comunaId: {
      type: String,
      required: true
    },
    estado: {
      type: String,
      required: true
    }
  });
  
  const getOrdenById = async (id) => {
    try {
      const response = await getOrdenByPedidoId(id);
      ordenId.value = response.id_orden;
    } catch (error) {
      console.error('Error fetching orden:', error);
    }
  };

  const getComunaById = async (id) => {
    try {
      const response = await getZonaById(id);
      comuna.value = response.comuna;
    } catch (error) {
      console.error('Error fetching comuna:', error);
    }
  };
  
  const emit = defineEmits(['verDetalles']);

    const verDetalles = () => {
    emit('verDetalles', {
        pedidoId: props.pedidoId,
        ordenId: ordenId.value,
        comuna: comuna.value
    });
    };
  
  onMounted(() => {
    getOrdenById(props.pedidoId);
    getComunaById(props.comunaId);
  });
  </script>
  
  <style scoped>
  .order-card {
    display: flex;
    align-items: center;
    background-color: #f9f9f9;
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 15px;
    margin-bottom: 10px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  }
  
  .order-info {
    display: flex;
    flex: 1;
    gap: 30px;
    align-items: center;
  }
  
  .info-item {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  
  .info-item strong {
    color: #666;
    white-space: nowrap;
  }
  
  .info-item span {
    color: #333;
    font-weight: 500;
  }
  
  .details-button {
    background-color: #4CAF50;
    color: white;
    border: none;
    padding: 8px 16px;
    border-radius: 4px;
    cursor: pointer;
    font-weight: 500;
    transition: background-color 0.2s;
    white-space: nowrap;
  }
  
  .details-button:hover {
    background-color: #45a049;
  }
  </style>
  