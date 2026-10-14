import { createRouter, createWebHashHistory, createWebHistory } from 'vue-router';
import AppLayout from '../layouts/AppLayout.vue';
import { isAuthenticated } from '../services/authStorage';

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/Login.vue'),
    meta: { public: true },
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('../views/Register.vue'),
    meta: { public: true },
  },
  {
    path: '/setup',
    name: 'setup',
    component: () => import('../views/ProfileSetup.vue'),
  },
  {
    path: '/',
    component: AppLayout,
    children: [
      { path: '', redirect: { name: 'discover' } },
      { path: 'discover', name: 'discover', component: () => import('../views/Discover.vue') },
      { path: 'matches', name: 'matches', component: () => import('../views/Matches.vue') },
      { path: 'chat', name: 'chat', component: () => import('../views/Chat.vue') },
      { path: 'bouts', name: 'bouts', component: () => import('../views/Bouts.vue') },
      { path: 'profile', name: 'profile', component: () => import('../views/Profile.vue') },
    ],
  },
  { path: '/:pathMatch(.*)*', redirect: { name: 'discover' } },
];

// Electron loads the built app from file://; HTML5 history needs a server,
// so fall back to hash history when running as a desktop app.
const isFileProtocol =
  typeof window !== 'undefined' && window.location.protocol === 'file:';

const router = createRouter({
  history: isFileProtocol ? createWebHashHistory() : createWebHistory('/'),
  routes,
});

router.beforeEach((to) => {
  const authed = isAuthenticated();

  if (!to.meta.public && !authed) {
    return { name: 'login', query: { redirect: to.fullPath } };
  }
  if (to.meta.public && authed) {
    return { name: 'discover' };
  }
  return true;
});

export default router;
