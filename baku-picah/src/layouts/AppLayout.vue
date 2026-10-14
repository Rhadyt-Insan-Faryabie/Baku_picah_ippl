<script setup>
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useSessionStore } from '../stores/session';

const route = useRoute();
const router = useRouter();
const session = useSessionStore();

const navItems = [
  { name: 'discover', label: 'Discover', icon: '🔍' },
  { name: 'matches', label: 'Matches', icon: '🤝' },
  { name: 'chat', label: 'Chat', icon: '💬' },
  { name: 'bouts', label: 'Bouts', icon: '🥊' },
  { name: 'profile', label: 'Profil', icon: '👤' },
];

const initials = computed(() => session.displayName.slice(0, 1).toUpperCase());

function logout() {
  session.logout();
  router.push({ name: 'login' });
}
</script>

<template>
  <div class="shell">
    <header class="top">
      <router-link :to="{ name: 'discover' }" class="brand">
        <span>BAKU</span><span class="brand__accent">PICAH</span>
      </router-link>

      <nav class="nav">
        <router-link
          v-for="item in navItems"
          :key="item.name"
          :to="{ name: item.name }"
          class="nav__link"
          active-class="nav__link--active"
        >
          <span class="nav__icon">{{ item.icon }}</span>
          <span class="nav__label">{{ item.label }}</span>
        </router-link>
      </nav>

      <div class="user">
        <div class="avatar">{{ initials }}</div>
        <span class="user__name">{{ session.displayName }}</span>
        <button class="btn btn--ghost user__logout" @click="logout">Keluar</button>
      </div>
    </header>

    <main class="content">
      <div class="content__inner">
        <router-view />
      </div>
    </main>
  </div>
</template>

<style scoped>
.shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.top {
  position: sticky;
  top: 0;
  z-index: 20;
  display: flex;
  align-items: center;
  gap: 20px;
  height: var(--topbar-h);
  padding: 0 22px;
  background: var(--surface);
  border-bottom: 1px solid var(--border);
}
.brand {
  display: flex;
  gap: 6px;
  font-family: var(--font-display);
  font-size: 24px;
  flex-shrink: 0;
}
.brand__accent {
  color: var(--blood-bright);
}
.nav {
  display: flex;
  gap: 6px;
  flex: 1;
  overflow-x: auto;
}
.nav__link {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 9px 14px;
  border-radius: var(--radius-pill);
  color: var(--text-muted);
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
}
.nav__link:hover {
  background: var(--surface-2);
  color: var(--text);
}
.nav__link--active {
  background: var(--blood-soft);
  color: var(--blood-bright);
  font-weight: 600;
}
.user {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}
.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--blood);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-family: var(--font-display);
}
.user__name {
  font-size: 14px;
  font-weight: 600;
}
.user__logout {
  padding: 8px 16px;
}
.content {
  flex: 1;
  padding: 26px 22px 48px;
}
.content__inner {
  max-width: var(--maxw);
  margin: 0 auto;
}

@media (max-width: 720px) {
  .user__name {
    display: none;
  }
  .nav__label {
    display: none;
  }
  .brand {
    font-size: 20px;
  }
}
</style>
