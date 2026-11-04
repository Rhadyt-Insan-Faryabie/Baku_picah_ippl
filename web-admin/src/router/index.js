import { createRouter, createWebHistory } from 'vue-router';
import AdminLayout from '../layouts/AdminLayout.vue';

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/Login.vue'),
    meta: { public: true },
  },
  {
    path: '/',
    component: AdminLayout,
    children: [
      { path: '', redirect: { name: 'dashboard' } },
      {
        path: 'dashboard',
        name: 'dashboard',
        component: () => import('../views/Dashboard.vue'),
      },
      {
        path: 'fighters',
        name: 'fighters',
        component: () => import('../views/FighterVerification.vue'),
      },
      {
        path: 'bouts',
        name: 'bouts',
        component: () => import('../views/BoutVerification.vue'),
      },
      {
        path: 'escrow',
        name: 'escrow',
        component: () => import('../views/DepositEscrow.vue'),
      },
      {
        path: 'arbitration',
        name: 'arbitration',
        component: () => import('../views/ResultArbitration.vue'),
      },
      // Aliases & legacy routes
      {
        path: 'verification',
        name: 'verification',
        redirect: { name: 'bouts' },
      },
      {
        path: 'master-data',
        name: 'master-data',
        component: () => import('../views/MasterData.vue'),
      },
      {
        path: 'moderation',
        name: 'moderation',
        component: () => import('../views/Moderation.vue'),
      },
    ],
  },
  { path: '/:pathMatch(.*)*', redirect: { name: 'dashboard' } },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to) => {
  const isAuthed = Boolean(localStorage.getItem('bp_admin_token'));
  if (!to.meta.public && !isAuthed) {
    return { name: 'login', query: { redirect: to.fullPath } };
  }
  if (to.name === 'login' && isAuthed) {
    return { name: 'dashboard' };
  }
  return true;
});

export default router;
