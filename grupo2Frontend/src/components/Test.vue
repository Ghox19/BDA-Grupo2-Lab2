<template>
    <div>
      <div style="height: 500px; width: 100%;">
        <l-map ref="map" v-model:zoom="zoom" :center="center">
          <l-tile-layer :url="url" :attribution="attribution"></l-tile-layer>
          <l-marker v-if="markerPosition" :lat-lng="markerPosition"></l-marker>
        </l-map>
      </div>
      
      <div class="search-container">
        <input 
          v-model="searchQuery" 
          @input="handleInput"
          @keyup.enter="handleEnter"
          type="text" 
          placeholder="Buscar dirección..."
        >
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
      
      <div v-if="markerPosition" class="coordinates">
        Coordenadas: 
        Latitud: {{ markerPosition.lat.toFixed(6) }}, 
        Longitud: {{ markerPosition.lng.toFixed(6) }}
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import { LMap, LTileLayer, LMarker } from "@vue-leaflet/vue-leaflet";
  import { OpenStreetMapProvider } from 'leaflet-geosearch';
  import "leaflet/dist/leaflet.css";
  
  const zoom = ref(13);
  const center = ref([-33.4372, -70.6483]);
  const url = ref('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png');
  const attribution = ref('&copy; OpenStreetMap contributors');
  const markerPosition = ref(null);
  const searchQuery = ref('');
  const suggestions = ref([]);
  const provider = new OpenStreetMapProvider({
  params: {
    'accept-language': 'es',
    countrycodes: 'cl',
    viewbox: '-71.0,-33.0,-70.0,-34.0', // Límites aproximados de la RM
    bounded: 1,
    addressdetails: 1
  }
    });
  
    const handleInput = async () => {
    if (searchQuery.value.length > 2) {
        const results = await provider.search({ 
        query: searchQuery.value,
        viewbox: '-71.0,-33.0,-70.0,-34.0',
        bounded: 1
        });
        
        suggestions.value = results
        .slice(0, 5)
        .map(result => ({
            label: result.label,
            x: result.x,
            y: result.y
        }));
    } else {
        suggestions.value = [];
    }
    };
  
  const selectSuggestion = (suggestion) => {
    searchQuery.value = suggestion.label;
    markerPosition.value = { 
      lat: suggestion.y, 
      lng: suggestion.x 
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
          lng: result.x 
        };
        center.value = [result.y, result.x];
      }
      suggestions.value = [];
    }
  };
  </script>
  
  <style scoped>
  .search-container {
    position: relative;
    margin: 10px;
    width: 300px;
  }
  
  .search-container input {
    width: 100%;
    padding: 8px;
    border: 1px solid #ccc;
  }
  
  .suggestions {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    background: white;
    border: 1px solid #ccc;
    color: black;
    border-top: none;
    max-height: 200px;
    overflow-y: auto;
    z-index: 1000;
  }
  
  .suggestion-item {
    padding: 8px;
    cursor: pointer;
  }
  
  .suggestion-item:hover {
    background-color: #f0f0f0;
  }
  
  .coordinates {
    margin-top: 10px;
    font-weight: bold;
  }
  </style>
  