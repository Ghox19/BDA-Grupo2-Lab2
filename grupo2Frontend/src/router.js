import { createRouter, createWebHistory } from 'vue-router';
import Home from './components/client/Home.vue';
import Register from './components/client/register.vue';
import Login from './components/client/login.vue';
import allProducts from './components/client/component client/allProducts.vue';
import Product from './components/client/component client/product.vue';
import ListOrder from "./components/client/component client/ListOrder.vue";
import Order from './components/client/component client/ViewNavbar/orderDetails.vue';
import createProduct from './components/admin/adminComponents/createProduct.vue';
import editProduct from './components/admin/adminComponents/editProduct.vue';
import Log from './components/auditoria/Log.vue';
import Ranked from './components/ranked/Ranked.vue';
import Test from './components/Test.vue';
import { auth } from './Services/authentication';


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
        beforeEnter: auth
      },
      {
        path: 'order/:id',
        name: 'order',
        component: Order,
        beforeEnter: auth
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
  }
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

export default router;