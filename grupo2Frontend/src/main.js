import { createApp } from 'vue';
import './style.css';
import App from './App.vue';
import router from './router';
import VueCookies from 'vue3-cookies';
import store from './store';
import 'leaflet/dist/leaflet.css'
import L from 'leaflet'


createApp(App).use(router).use(VueCookies).use(store).mount('#app');
