<script setup>
import {ref, onMounted} from 'vue';
import { useRouter } from 'vue-router';
import {getCategorias} from "../../../Services/Categoria.js";
import {createProduct} from "../../../Services/ProductService.js";

const router = useRouter();
const nombre = ref('')
const precio = ref('')
const stock = ref('')
const descripcion = ref('')
const estado = ref('')
const categoria = ref('')
const selectedCategoria = ref(null);

onMounted(async () => {
  const response = await getCategorias();
  categoria.value = response;
});
const crearProducto = async () => {
  if (!nombre.value || !precio.value || !stock.value || !descripcion.value || !categoria.value || !selectedCategoria.value) {
    alert('Todos los campos son obligatorios');
    return;
  }

  console.log('Creando producto');
  const data = {
    nombre: nombre.value,
    descripcion: descripcion.value,
    precio: precio.value,
    stock: stock.value,
    estado: "disponible",
    id_categoria: selectedCategoria.value
  }

  console.log(data);
  const response = await createProduct(data);
  console.log(response);
  if (response) {
    alert('Producto creado correctamente');
    router.push({ name: 'mostrarProductos', params: { id: 1 }});
  } else {
    alert('Error al crear el producto');
  }
};

const volver = () => {
  router.push({ name: 'mostrarProductos', params: { id: 1 }});
};

</script>

<template>
  <div class="container">
    <div class="form-container">
      <h1 class="title">Agregar Producto</h1>
      <form @submit.prevent="crearProducto">
        <div class="form-row">
          <div class="form-group">
            <label for="nombre" class="text">Nombre:</label>
            <input type="text" v-model="nombre" id="nombre" placeholder="Nombre del producto" />
          </div>
          <div class="form-group">
            <label for="precio" class="text">Precio:</label>
            <input type="number" v-model="precio" id="precio" placeholder="Precio del producto" inputmode="decimal" min="0"/>
          </div>
        </div>
        <div class="form-row">
          <div class="form-group stock">
            <label for="stock" class="text">Stock:</label>
            <input type="number" v-model="stock" id="stock" placeholder="Cantidad en stock" step="1" />
          </div>
          <div class="form-group">
            <label for="categoria" class="text">Categoría:</label>
            <select class="desplegable" v-model="selectedCategoria" id="categoria" style="background-color: white; width: 100%; height: 55%;">
              <option disabled value="">Seleccionar categoría</option>
              <option v-for="cat in categoria" :key="cat.id_categoria" :value="cat.id_categoria">{{ cat.nombre }}</option>
            </select>
          </div>
        </div>
        <div class="form-group">
          <label for="descripcion" class="text">Descripción:</label>
          <textarea class="textarea" v-model="descripcion" id="descripcion" placeholder="Descripción del producto" style="height: 80px;"></textarea>
        </div>
        <div class=" botones">
          <button type="button" @click="volver">Volver</button>
          <button type="submit">Agregar Producto</button>
        </div>
      </form>
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
  background-color: #13c691;
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
</style>