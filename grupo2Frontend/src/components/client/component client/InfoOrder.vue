<script setup>
import { ref, onMounted } from 'vue';
import { LMap, LTileLayer, LMarker } from "@vue-leaflet/vue-leaflet";
import { OpenStreetMapProvider } from 'leaflet-geosearch';
import "leaflet/dist/leaflet.css";
import { getDetailsOrderbyOrder, deleteDetailsOrder } from '../../../Services/DetailsOrderService';
import { getOrderById, calculateTotalOrden, PayOrder, CreateOrder } from '../../../Services/OrdenService';
import { getProductById } from '../../../Services/ProductService';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';

const router = useRouter();
const idOrder = router.currentRoute.value.params.id;
const store = useStore();
const User = store.getters.getUser;
const Order = ref({});
const ListDetailsOrder = ref([]);
const total = ref(0);
const loading = ref(true);

// Mapa y búsqueda
const zoom = ref(13);
const center = ref([-33.4372, -70.6483]); // Santiago, Chile
const url = ref('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png');
const attribution = ref('&copy; OpenStreetMap contributors');
const markerPosition = ref(null);
const searchQuery = ref('');
const suggestions = ref([]);
const provider = new OpenStreetMapProvider({
    params: {
        'accept-language': 'es',
        countrycodes: 'cl',
        viewbox: '-71.0,-33.0,-70.0,-34.0',
        bounded: 1,
        addressdetails: 1,
    },
});

const getOrderAndDetailsOrder = async () => {
    loading.value = true;
    const responseOrder = await getOrderById(idOrder);
    total.value = await calculateTotalOrden(idOrder);
    Order.value = {
        date: responseOrder.fecha_orden.split('T')[0],
        estado: responseOrder.estado,
    };

    const response = await getDetailsOrderbyOrder(idOrder);
    for (const detail of response) {
        const responseProduct = await getProductById(detail.id_producto);
        ListDetailsOrder.value.push({
            ...responseProduct,
            id_detailorden: detail.id_detalle,
            cantidad: detail.cantidad,
            precio: detail.precio_unitario,
            nombre: responseProduct.data.nombre,
        });
    }
    loading.value = false;
};

const handleInput = async () => {
    if (searchQuery.value.length > 2) {
        const results = await provider.search({
            query: searchQuery.value,
            viewbox: '-71.0,-33.0,-70.0,-34.0',
            bounded: 1,
        });

        suggestions.value = results.slice(0, 5).map(result => ({
            label: result.label,
            x: result.x,
            y: result.y,
        }));
    } else {
        suggestions.value = [];
    }
};

const selectSuggestion = (suggestion) => {
    searchQuery.value = suggestion.label;
    markerPosition.value = {
        lat: suggestion.y,
        lng: suggestion.x,
    };
    center.value = [suggestion.y, suggestion.x];
    suggestions.value = [];
};

const handleEnter = async () => {
    if (searchQuery.value) {
        const formattedQuery = `${searchQuery.value}, Chile`;
        const results = await provider.search({ query: formattedQuery });
        if (results.length > 0) {
            const result = results[0];
            markerPosition.value = {
                lat: result.y,
                lng: result.x,
            };
            center.value = [result.y, result.x];
        }
        suggestions.value = [];
    }
};

onMounted(() => {
    getOrderAndDetailsOrder();
    loading.value = false;
});
</script>

<template>
<div v-if="loading" class="loading">
    <lottie-player
        src="https://lottie.host/fa45db1f-9757-4d93-9601-bb3bbfa51423/3UlJpn36MI.json"
        background="##FFFFFF"
        speed="1"
        style="width: 400px; height: 400px"
        loop
        autoplay
    ></lottie-player>
</div>
<div v-else class="container-order">
    <div class="container-listOrder">
        <div
            v-for="detailOrder in ListDetailsOrder"
            :key="detailOrder.id_detailorden"
            class="order-card"
        >
            <div class="card-content">
                <h3>{{ detailOrder.nombre }}</h3>
                <p>Precio unitario: {{ detailOrder.precio }}</p>
                <div class="quantity-container">
                    <button @click="updateProductQuantity(detailOrder, -1)">-</button>
                    <span>{{ detailOrder.cantidad }}</span>
                    <button @click="updateProductQuantity(detailOrder, 1)">+</button>
                </div>
                <button
                    @click="handleDeleteProductOrder(detailOrder.id_detailorden)"
                    class="btn-delete"
                >
                    Eliminar
                </button>
            </div>
        </div>
    </div>
    <div class="container-content">
        <h1>Total: $ {{ total }}</h1>
        <div class="map-section">
            <l-map ref="map" :zoom="zoom" :center="center">
                <l-tile-layer :url="url" :attribution="attribution" />
                <l-marker v-if="markerPosition" :lat-lng="markerPosition" />
            </l-map>
        </div>
        <div class="search-container">
            <input
                v-model="searchQuery"
                @input="handleInput"
                @keyup.enter="handleEnter"
                type="text"
                placeholder="Buscar dirección..."
            />
            <div v-if="suggestions.length" class="suggestions">
                <div
                    v-for="suggestion in suggestions"
                    :key="suggestion.label"
                    @click="selectSuggestion(suggestion)"
                    class="suggestion-item"
                >
                    {{ suggestion.label }}
                </div>
            </div>
        </div>
        <button
            v-if="ListDetailsOrder.length"
            @click="handleConfirmOrder"
            class="btn-pay"
        >
            Confirmar pedido
        </button>
    </div>
</div>
</template>


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
    font-size: 4rem;
    margin: 2rem;
}

.content-order {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    height: 40%;
    margin: 2rem;
}

.text-order {
    font-size: 1.2rem;
    font-weight: bold;
}

.container-content {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: center;
    width: 50%;
    height: 100%;
    top: 0%;
    margin: 2rem;
    padding: 2rem;
}

.input-order {
    border: 1px solid #ccc;
    padding: 0.5rem;
    border-radius: 5px;
    width: 100%;
    margin-bottom: 1rem;
    font-size: 1rem;
}

.map-section {
    width: 100%;
    height: 400px;
}

.search-container {
    position: relative;
    width: 100%;
    justify-content: center;
    align-items: center;
    margin: 5rem;
}

.search-container input {
    width: 100%;
    padding: 1rem;
    background-color: white;
    color: black;
    border: 1px solid #ccc;
    border-radius: 0.5rem;
}

.suggestions {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    background: white;
    border: 1px solid #ccc;
    max-height: 200px;
    overflow-y: auto;
    z-index: 1000;
    border-radius: 4px;
}

.suggestion-item {
    padding: 8px;
    cursor: pointer;
}

.suggestion-item:hover {
    background-color: #f0f0f0;
}

.quantity-container {
    display: flex;
    align-items: center;
    gap: 0.5rem;
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
    margin: 1rem;
}

button {
  color: black;
}

.container-listOrder {
  height: 100%;
  width: 50%;
  overflow-y: auto;
  overflow-x: hidden;
  display: flex;
  flex-direction: column;
  background-color: #5EA9DC52;
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
</style>