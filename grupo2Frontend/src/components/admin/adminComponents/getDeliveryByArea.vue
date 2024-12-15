<script setup>
import { ref } from "vue";
import { getRepartidoresPorZona } from "../../../Services/Pedido.js";

const comuna = ref(""); 
const repartidores = ref([]); 
const error = ref(null); 
const loading = ref(false); 

const buscarRepartidores = async () => {
  if (!comuna.value) {
    error.value = "Por favor, ingresa una comuna.";
    return;
  }

  try {
    loading.value = true;
    error.value = null;
    repartidores.value = [];

    const data = await getRepartidoresPorZona(comuna.value);

    if (data && data.length > 0) {
      repartidores.value = data;
    } else {
      error.value = "No se encontraron repartidores en esta comuna.";
    }
  } catch (err) {
    error.value = "Hubo un error al buscar los repartidores.";
    console.error(err);
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="container">
    <div class="form-container">
      <h1 class="title">Buscar Repartidores por Zona</h1>
      <form @submit.prevent="buscarRepartidores">
        <div class="form-row">
          <div class="form-group">
            <label for="comuna" class="text">Ingresa la comuna:</label>
            <input
              type="text"
              id="comuna"
              v-model="comuna"
              placeholder="Ejemplo: Santiago"
              required
            />
          </div>
          <div class="form-group">
            <button type="submit">Buscar</button>
          </div>
        </div>
      </form>

      <!-- Mostrar error si lo hay -->
      <p v-if="error" class="error">{{ error }}</p>

      <!-- Mostrar lista de repartidores -->
      <div v-if="repartidores.length > 0">
        <h2 class="text">Repartidores disponibles:</h2>
          <div class="repartidores-list">
            <div class="repartidor-card" v-for="(repartidor, index) in repartidores" :key="index">
              <p><strong>ID Cliente:</strong> {{ repartidor.id_cliente }}</p>
              <p><strong>Nombre:</strong> {{ repartidor.nombre }}</p>
            </div>
          </div>
      </div>


      <!-- Mostrar mensaje si no hay resultados -->
      <p v-else-if="comuna && !loading && !error">
        No se encontraron repartidores en esta comuna.
      </p>

      <!-- Mostrar indicador de carga -->
      <p v-if="loading">Buscando repartidores...</p>
    </div>
  </div>
</template>

<style scoped>
.title {
  text-align: center;
  color: #721B65;
}

.container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #4944b8;
  padding: 0 10px;
}

.form-container {
  background-color: white;
  padding: 20px 40px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 700px;
  box-sizing: border-box;
}

.text {
  color: #721B65;
  font-weight: bold;
}

.form-row {
  display: flex;
  justify-content: space-between;
  gap: 20px;
}

.form-group {
  flex-grow: 1;
}

.stock {
  flex-basis: 170px; /* Ajustamos el ancho del campo 'Stock' */
}

label {
  display: block;
  margin-bottom: 5px;
}

input,
textarea,
select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  background: white;
  color: black;
  box-sizing: border-box;
}

textarea {
  resize: none;
}

.desplegable {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  background: white;
  color: black;
  box-sizing: border-box;
  cursor: pointer;
}

button {
  background-color: #721B65;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  width: 100%;
}

.botones {
  display: flex;
  justify-content: space-between;
  gap: 20px;
}

button:hover {
  background-color: #45a049;
}

input[type="number"]::-webkit-inner-spin-button,
input[type="number"]::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.repartidores-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.repartidor-card {
  background-color: #ddefff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px 20px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s, box-shadow 0.2s;
}

.repartidor-card:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

.repartidor-card p {
  margin: 5px 0;
  color: #333;
}

.repartidor-card strong {
  color: #721B65;
}

</style>
