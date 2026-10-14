<script setup>
import { ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import Sidebar from '../components/Sidebar.vue';
import Topbar from '../components/Topbar.vue';

const route = useRoute();
const router = useRouter();
const sidebarOpen = ref(false);

function logout() {
  localStorage.removeItem('bp_admin_token');
  router.push({ name: 'login' });
}

const titles = {
  dashboard: 'Dashboard',
  verification: 'Verifikasi Pertandingan',
  arbitration: 'Arbitrase Hasil',
  'master-data': 'Master Data',
  moderation: 'Moderasi & Keamanan',
};
</script>

<template>
  <div class="layout">
    <Sidebar :open="sidebarOpen" @close="sidebarOpen = false" />
    <div class="layout__main">
      <Topbar :title="titles[route.name] || 'BAKU PICAH'" @toggle-sidebar="sidebarOpen = !sidebarOpen" @logout="logout" />
      <main class="layout__content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<style scoped>
.layout {
  display: flex;
  min-height: 100vh;
}
.layout__main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.layout__content {
  padding: 24px;
  flex: 1;
}
</style>
