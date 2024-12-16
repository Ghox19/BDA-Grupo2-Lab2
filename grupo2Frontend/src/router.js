import { createRouter, createWebHistory } from 'vue-router';
import Home from './components/client/Home.vue';
import HomeRepartidor from './components/repartidor/HomeRepartidor.vue';
import HomeAdmin from './components/admin/HomeAdmin.vue';
import Register from './components/client/register.vue';
import Login from './components/client/login.vue';
import allProducts from './components/client/component client/allProducts.vue';
import Product from './components/client/component client/product.vue';
import ListOrder from "./components/client/component client/ListOrder.vue";
import Pay from './components/client/component client/ViewNavbar/PayDetails.vue';
import createProduct from './components/admin/adminComponents/createProduct.vue';
import editProduct from './components/admin/adminComponents/editProduct.vue';
import Log from './components/auditoria/Log.vue';
import Ranked from './components/ranked/Ranked.vue';
import Test from './components/Test.vue';
import ListPedidos from './components/repartidor/ViewsRepartidor/ListPedidos.vue';
import getDeliveryByArea from './components/admin/adminComponents/getDeliveryByArea.vue'
import InfOrder from './components/client/component client/InfoOrder.vue';
import RepartidorPedidos from './components/repartidor/RepartidorPedidos.vue';
import PedidoDetail from './components/repartidor/PedidoDetail.vue';
import { auth } from './Services/authentication';
import MostrarProductos from './components/admin/mostrarProductos.vue';

const routes = [
  {
    path: '/', redirect: { name: 'allproducts', params: { id: 1 } }
  },
  {
    path: '/home',
    name: 'home',
    component: Home,
    children: [
      {
        path: 'allproducts/:id',
        name: 'allproducts',
        component: allProducts
      },
      {
        path: 'product/:id',
        name: 'product',
        component: Product,
        beforeEnter: auth
      },
      {
        path: 'ListOrder',
        name: 'ListOrder',
        component: ListOrder,
        meta: { roles: ['cliente'] },
        beforeEnter: auth
      },
      {
        path: 'InfOrder/:id',
        name: 'InfOrder',
        component: InfOrder,
        meta: { roles: ['cliente'] },
        beforeEnter: auth
      },
      {
        path: 'pay/:id',
        name: 'pay',
        component: Pay,
        meta: { roles: ['cliente'] },
        beforeEnter: auth
      }
    ]
    },
  {
    path: '/Repartidor',
    name: 'Repartidor',
    component: HomeRepartidor,
    children: [
      {
        path: 'ListPedidos',
        name: 'ListPedidos',
        component: ListPedidos,
      },
      {
        path: 'RepartidorPedidos',
        name: 'RepartidorPedidos',
        component: RepartidorPedidos
      },
      {
        path: '/pedido/:id/:ordenId/:comuna',
        name: 'PedidoDetail',
        component: PedidoDetail,
        props: true
      }
    ]
    },
  {
    path: '/Admin',
    name: 'Admin',
    component: HomeAdmin,
    children: [
      {
        path: 'mostrarProductos/:id',
        name: 'mostrarProductos',
        component: MostrarProductos
      }
    ]
  },
  {
    path: '/createProduct',
    name: 'createProduct',
    component: createProduct
  },
  {
    path: '/editProduct/:id',
    name: 'editProduct',
    component: editProduct
  },
  {
    path: '/login',
    name: 'login',
    component: Login
  },
  {
    path: '/register',
    name: 'register',
    component: Register
  },
  {
    path: '/log',
    name: 'log',
    component: Log
  },
  {
    path: '/ranked',
    name: 'ranked',
    component: Ranked
  },
  {
    path: '/test',
    name: 'test',
    component: Test
  },
  {
    path: '/getDeliveryByArea',
    name: 'getDeliveryByArea',
    component: getDeliveryByArea
  }
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

export default router;