<template>
    <nav class="navbar">
        <div class="navbar-links">
            <RouterLink to="/createProduct" class="navbar-link">
                Crear Producto
            </RouterLink>
            <RouterLink to="/getDeliveryByArea" class="navbar-link">
                Repartidores en un área
            </RouterLink>
        </div>
        <div class="navbar-actions">
            <button class="btn-logout" @click="logoutUser">
                Cerrar Sesión
            </button>
        </div>
    </nav>
</template>

<script setup>
import { logout } from '../../../Services/UserService';
import { useRouter } from 'vue-router';
import { useStore } from 'vuex';
import { RouterLink } from 'vue-router';

const store = useStore();
const route = useRouter();

const logoutUser = async () => {
    try {
        const response = logout();
        console.log('Response:', response);

        store.commit('logout');
        store.commit('clearUser');
        store.commit('clearOrder');
        alert('Sesión cerrada exitosamente');
        route.push('/login');
    } catch (error) {
        alert('Error al cerrar sesión');
    }
};
</script>

<style scoped>
.navbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem;
    background-color: #0b9157;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.navbar-links {
    display: flex;
    gap: 1.5rem;
}

.navbar-link {
    text-decoration: none;
    color: white;
    font-weight: bold;
    padding: 1rem;
    flex-grow: 1;
    text-align: center;
    border-right: 1px solid white;
    transition: background-color 0.3s ease, color 0.3s ease;
}

.navbar-link:last-child {
    border-right: none;
}

.navbar-link:hover {
    background-color: #0b9157; /* Color naranja oscuro para hover */
    color: white;
}

.navbar-actions {
    display: flex;
    align-items: center;
}

.btn-logout {
    padding: 0.8rem 1.5rem;
    background-color: white;
    color: #0b9157;
    border: 2px solid #0b9157;
    border-radius: 4px;
    font-weight: bold;
    cursor: pointer;
    transition: background-color 0.3s ease, color 0.3s ease;
}

.btn-logout:hover {
    background-color: #0b9157;
    color: white;
}
</style>