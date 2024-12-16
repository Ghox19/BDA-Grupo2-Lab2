<script setup>
import {useRouter} from "vue-router";
import {ref, onMounted} from "vue";
import Datepicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css';
import {getOrderById} from "../../../Services/OrdenService.js";
import editOrder from "./editOrder.vue";

const router = useRouter();
const idOrden = router.currentRoute.value.params.id;
const estado = ref('');
const selectedEstado = ref(null);
const fechaOrden = ref();

onMounted(async () => {
  const response = await getOrderById(idOrden);
  estado.value = response.data.estado;
  fechaOrden.value = response.data.fecha_orden;
});
const editarOrden = async () => {
  if (estado.value === '' || fechaOrden.value === '') {
    alert('Todos los campos son obligatorios');
    return;
  }

  console.log("Creando orden");
  const data = {
    estado: estado.value,
    fecha_orden: fechaOrden.value
  }

  console.log(data);
  const response = await editOrder(idOrden, data);
  console.log(response);
  if (response) {
    alert('Orden creada correctamente');
    //router.push({ name: 'OtraDireccion' });
  } else {
    alert('Error al crear la orden');
  }
}
</script>

<template>
  <div class="container">
    <div class="form-container">
      <h1 class="title">
        Editar Orden
      </h1>
      <form @submit.prevent="">
        <!---Datos de la orden-->
        <div class="form-row">
          <Datepicker v-model="fechaOrden" id="fechaOrden" placeholder="Seleccione una fecha y hora" style="margin-bottom: 20px"/>
        </div>
        <div class="botones">
          <button type="button">Volver</button>
          <button type="button">Guardar cambios</button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.title{
  text-align: center;
  color: #721B65;
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
  flex-grow: 5;
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

label {
  display: block;
  margin-bottom: 5px;
}

.container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #6a4c6b;
  padding: 0 10px;
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
</style>