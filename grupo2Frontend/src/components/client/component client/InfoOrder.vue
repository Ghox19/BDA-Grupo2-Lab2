<script setup>
import { ref, onMounted } from 'vue';
import { LMap, LTileLayer, LMarker } from "@vue-leaflet/vue-leaflet";
import { OpenStreetMapProvider } from 'leaflet-geosearch';
import "leaflet/dist/leaflet.css";
import { getDetailsOrderbyOrder, deleteDetailsOrder } from '../../../Services/DetailsOrderService';
import { updateDetalleOrden } from '../../../Services/DetalleOrden';
import { getOrderById, calculateTotalOrden, PayOrder, CreateOrder } from '../../../Services/OrdenService';
import { getProductById } from '../../../Services/ProductService';
import { getPedidoById, updatePedido, verificacionCoordenada, esUbicacionRestringida, esUbicacionGratuita } from '../../../Services/Pedido';
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
const pedido = ref([]);

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
    headers: {
        "User-Agent": "UniversityProjectTBD/1.0 (gonzalo.moncada@usach.cl)"
    }
});

let lastRequestTime = 0;
const DELAY = 2000; 
const showDeliveryOptions = ref(false);
const isGratuita = ref(false);
const deliveryCost = ref(50);

const handleConfirmDelivery = async () => {
    const response = await PayOrder(idOrder);
    const responseOrden = await CreateOrder({
        fecha_orden: new Date(),
        id_cliente: User.id_user,
        estado: "en_proceso",
        total: isGratuita.value ? 0 : deliveryCost.value
    });
    store.commit('setOrder', responseOrden);
    alert('Orden pagada correctamente');
    router.push({ name: 'ListOrder' });
};

const handleRejectDelivery = () => {
    showDeliveryOptions.value = false;
};


const getOrderAndDetailsOrder = async () => {
    loading.value = true;
    const responseOrder = await getOrderById(idOrder);
    Order.value = {
        date: responseOrder.fecha_orden.split('T')[0],
        estado: responseOrder.estado,
    };
    pedido.value = await getPedidoById(responseOrder.id_pedido);

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
        console.log('ListDetailsOrder:', ListDetailsOrder.value);
    }
    total.value = await calculateTotalOrden(idOrder);
    loading.value = false;
};

const handleInput = async () => {
    if (searchQuery.value.length > 2) {
        const currentTime = Date.now();
        const timeSinceLastRequest = currentTime - lastRequestTime;
        
        if (timeSinceLastRequest < DELAY) {
            await new Promise(resolve => 
                setTimeout(resolve, DELAY - timeSinceLastRequest)
            );
        }
        
        try {
            lastRequestTime = Date.now();
            const encodedAddress = encodeURIComponent(`${searchQuery.value}, Chile`);
            
            const response = await fetch(
                `https://nominatim.openstreetmap.org/search?q=${encodedAddress}&format=json&addressdetails=1&countrycodes=cl&limit=5`,
                {
                    headers: {
                        "User-Agent": "UniversityProjectTBD/1.0 (gonzalo.moncada@usach.cl)"
                    }
                }
            );

            const results = await response.json();

            if (results.length > 0) {
                suggestions.value = results.map(result => ({
                label: result.display_name,
                x: parseFloat(result.lon),
                y: parseFloat(result.lat),
            }));
            } else {
                suggestions.value = [];
                console.log('No se encontraron resultados');
            }
        } catch (error) {
            console.error('Error en la búsqueda:', error);
            suggestions.value = [];
        }
    } else {
        suggestions.value = [];
    }
};

const handleEnter = async () => {
    if (searchQuery.value) {
        const formattedQuery = `${searchQuery.value}, Chile`; // Asegúrate de incluir Chile
        const results = await provider.search({ query: formattedQuery });

        if (results.length > 0) {
            const result = results[0];
            markerPosition.value = {
                lat: result.y,
                lng: result.x,
            };
            center.value = [result.y, result.x];
            console.log('Dirección encontrada:', result);
        } else {
            console.log('No se encontraron resultados para:', formattedQuery);
        }

        suggestions.value = [];
    }
};

const handleDeleteProductOrder = async (id_detailorden) => {
    const response = await deleteDetailsOrder(id_detailorden);
    if (response) {
        const index = ListDetailsOrder.value.findIndex(detail => detail.id_detailorden === id_detailorden);
        ListDetailsOrder.value.splice(index, 1);
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
    console.log('suggestion:', suggestion);
};

const handleConfirmPedido = async () => {
    const orderData = {
        id_pedido: pedido.value.id_pedido,
        id_zona: 1,
        id_cliente: User.id,
        coordenada_direccion: {
            coordinates: [
                markerPosition.value?.lng || center.value[1],
                markerPosition.value?.lat || center.value[0]
            ],
            type: "Point",
            srid: 4326
        },
        direccion: searchQuery.value,
        estado: "pendiente"
    };

    const response = await updatePedido(pedido.value.id_pedido, orderData);
    const verificacion = await verificacionCoordenada(pedido.value.id_pedido);

    if (verificacion) {
        const verificacion2 = await esUbicacionRestringida(pedido.value.id_pedido);
        if (!verificacion2) {
            const verificacion3 = await esUbicacionGratuita(pedido.value.id_pedido);
            isGratuita.value = verificacion3;
            showDeliveryOptions.value = true;
        } else {
            alert('La dirección ingresada está restringida');
        }
    } else {
        alert('La dirección ingresada es inválida');
    }
};

const updateQuantity = async (id_detailorden, cantidad, precio, id_producto) => {
    const DetalleOrden = {
        id_detalle: id_detailorden,
        id_orden: idOrder,
        id_producto: id_producto,
        cantidad: cantidad,
        precio_unitario: precio
    }

    const response = await updateDetalleOrden(DetalleOrden);
    console.log(response);
    if (response) {
       const index = ListDetailsOrder.value.findIndex(detail => detail.id_detailorden === id_detailorden);
       ListDetailsOrder.value[index].cantidad = cantidad;
       total.value = await calculateTotalOrden(idOrder);
    }
};



onMounted(() => {
    getOrderAndDetailsOrder();
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
                    <button
                        @click="updateQuantity(detailOrder.id_detailorden, detailOrder.cantidad - 1, detailOrder.precio, detailOrder.data.id_producto)"
                        class="btn-action"
                    >
                        -
                    </button>
                    <p>{{ detailOrder.cantidad }}</p>
                    <button
                        @click="updateQuantity(detailOrder.id_detailorden, detailOrder.cantidad + 1, detailOrder.precio, detailOrder.data.id_producto)"
                        class="btn-action"
                    >
                        +
                    </button>

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
            <div v-if="suggestions.length " class="suggestions">
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
        <div v-if="showDeliveryOptions" class="delivery-options">
            <div class="delivery-info">
                <div v-if="isGratuita"> 
                    <h3 >Entrega Gratuita</h3>
                </div>
                <div v-else>
                    <h3>Entrega: ${{ deliveryCost }}</h3>
                    <h3>Entrega: ${{ deliveryCost + total }}</h3>
                </div>
                
                
            </div>
            <div class="delivery-buttons">
                <button @click="handleConfirmDelivery" class="btn-confirm">Confirmar</button>
                <button @click="handleRejectDelivery" class="btn-reject">Rechazar</button>
            </div>
        </div>
        <button v-if="ListDetailsOrder.length && !showDeliveryOptions" @click="handleConfirmPedido" class="btn-pay">
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
.order-card{
    background-color: white;
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

.quantity-container .btn-action {
    background-color: #4944b8;
    color: white;
}

.card-content .btn-delete {
    background-color: red;
    color: white;
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

.delivery-options {
    width: 100%;
    margin: 1rem 0;
    padding: 1rem;
    border: 1px solid #ccc;
    border-radius: 0.5rem;
    text-align: center;
}

.delivery-info {
    margin-bottom: 1rem;
}

.delivery-buttons {
    display: flex;
    justify-content: center;
    gap: 1rem;
}

.btn-confirm, .btn-reject {
    padding: 0.5rem 1rem;
    border-radius: 0.5rem;
    cursor: pointer;
    font-weight: bold;
}

.btn-confirm {
    background-color: #4944b8;
    color: white;
    border: none;
}

.btn-reject {
    background-color: #ff4444;
    color: white;
    border: none;
}

</style>