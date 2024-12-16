<template>
    <div v-if="loading" class="loading">
        <lottie-player src="https://lottie.host/fa45db1f-9757-4d93-9601-bb3bbfa51423/3UlJpn36MI.json" 
            background="##FFFFFF" 
            speed="1" 
            style="width: 400px; height: 400px"
            loop 
            autoplay
        ></lottie-player>
    </div>
    <div v-else class="container-order">
        <div class="container-content">
            <h1>Detalles de la orden</h1>
            <div class="content-order">
                <p>Estado:</p>
                <p class="text-order">{{ Order.estado }}</p>
                <p>Fecha:</p>
                <p class="text-order">{{ Order.date }}</p>
                <p>Total:</p>
                <p class="text-order">${{ total }}</p>
                <p>Direccion:</p>
                <p class="text-order">{{ direccion }}</p>
                <p>Estado:</p>
                <p class="text-order">{{ estado }}</p>
            </div>
            <div class="map-section">
                <l-map ref="map" :zoom="zoom" :center="center">
                    <l-tile-layer :url="url" :attribution="attribution" />
                    <l-marker v-if="markerPosition" :lat-lng="markerPosition" />
                </l-map>
            </div>
        </div>
        <div class="container-listOrder">
            <div v-for="detailOrder in ListDetailsOrder" :key="detailOrder.id_detailorden" class="order-card">
                <div class="card-content">
                    <h3>{{ detailOrder.nombre }}</h3>
                    <p>Precio unitario: {{ detailOrder.precio }}</p>
                    <p>Cantidad: {{ detailOrder.cantidad }}</p>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getDetailsOrderbyOrder} from '../../../../Services/DetailsOrderService';
import { getOrderById, calculateTotalOrden } from '../../../../Services/OrdenService';   
import { getProductById} from '../../../../Services/ProductService';
import { PayOrder, CreateOrder } from '../../../../Services/OrdenService';
import { getPedidoById } from '../../../../Services/Pedido';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';
import { LMap, LTileLayer, LMarker, LPopup } from "@vue-leaflet/vue-leaflet";
import "leaflet/dist/leaflet.css";

const router = useRouter();
const idOrder = router.currentRoute.value.params.id;
const store = useStore();
const User = store.getters.getUser;
const direccion = ref('');
const estado = ref('');
const Order = ref({});
const ListDetailsOrder = ref([]);
const total = ref(0);
const loading = ref(true);
const pedido = ref([]);
const zoom = ref(13);
const center = ref([-33.4372, -70.6483]); // Santiago, Chile
const url = ref('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png');
const attribution = ref('&copy; OpenStreetMap contributors');
const markerPosition = ref(null);

const getOrderAndDetailsOrder = async () => {
    loading.value = true;
    const responseOrder = await getOrderById(idOrder);
    total.value = await calculateTotalOrden(idOrder);
    Order.value = {
        date: responseOrder.fecha_orden.split("T")[0],
        estado: responseOrder.estado
    }
    pedido.value = await getPedidoById(responseOrder.id_pedido);

    console.log('Pedido:', pedido.value);

    if (pedido.value.coordenada_direccion) {
        const coords = pedido.value.coordenada_direccion.coordinates;
        markerPosition.value = {
            lat: coords[1],
            lng: coords[0]
        };
        center.value = [coords[1], coords[0]];

        direccion.value = pedido.value.direccion;
        estado.value = pedido.value.estado;
    }

    const response = await getDetailsOrderbyOrder(idOrder);
    const List = response;
    
    for(const detail of List){
        const responseProduct = await getProductById(detail.id_producto);
        ListDetailsOrder.value.push({
            ...responseProduct,
            cantidad: detail.cantidad,
            precio: detail.precio_unitario,
            nombre: responseProduct.data.nombre
        })
    }

    console.log('ListDetailsOrder:', ListDetailsOrder.value);
    loading.value = false;
};

onMounted(() => {
    getOrderAndDetailsOrder();
});
</script>

<style scoped>
.loading {
    height: 100%;
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
}

.container-order {
    height: 100%;
    width: 100%;
    background-color: white;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.container-order h1 {
    font-size: clamp(2rem, 4vw, 3rem);
    margin: 1rem 0;
}

.container-order .content-order {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: center;
    width: 50%;
    height: 50%;
    margin: 2rem;
}

.content-order .text-order {
    font-size: 1.2rem;
    font-weight: bold;
    margin-bottom: 0.5rem;
}

.content-order p {
    margin: 0;
    padding: 0;
}

.btn-pay{
    width: 50%;
    height: 4rem;
    padding: 1rem;
    background-color: #4944b8;
    color: white;
    border-radius: 0.5rem;
    border: solid 2px #4944b8;
    cursor: pointer;
    font-size: 1rem;
    font-weight: bold;
    justify-content: center;
    align-items: center;
    display: flex;
    transition: background-color 0.3s ease, color 0.3s ease;
}

.btn-pay:hover {
    background-color: white;
    color: #4944b8;
    border: solid 2px #4944b8;
}

.container-listOrder {
  height: 100%;
  width: 50%;
  overflow-y: auto;
  overflow-x: hidden;
  display: flex;
  flex-direction: column;
  background-color: white;
  box-sizing: border-box;
}

::-webkit-scrollbar {
  width: 0.7rem;
}


::-webkit-scrollbar-track {
  background: #f0f0f0; 
  border-radius: 1rem; 
}


::-webkit-scrollbar-thumb {
  background-color: #4a44b89b; 
  border-radius: 1rem; 
  border: 2px solid #f0f0f0; 
}


::-webkit-scrollbar-thumb:hover {
  background-color: #4944b8;
}


.order-card {
    height: 6rem;
    width: calc(100% - 2rem);
    display: flex;
    align-items: center;
    border: 1px solid #5EA9DC52;
    border-radius: 0.5rem;
    margin: 1rem;
    box-shadow: 0px 2px 4px rgba(0, 0, 0, 0.1);
}

.order-card .card-content {
    height: 100%;
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 2rem;
}

.map-section {
    width: 100%;
    height: 400px;
    margin: 1rem 0;
    border-radius: 0.5rem;
    overflow: hidden;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.container-content {
    height: 100%;
    width: 50%;
    background-color: #5EA9DC52;
    border-right: 1px solid #5EA9DC52;
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    align-items: center;
    padding: 1rem;
    gap: 1rem;
    overflow-y: auto;
}


</style>