<script setup>
defineProps({
  open: { type: Boolean, default: false },
});
const emit = defineEmits(['close', 'logout']);

const items = [
  {
    name: 'dashboard',
    label: 'Ringkasan',
    badge: null,
    icon: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect></svg>`,
  },
  {
    name: 'fighters',
    label: 'Fighter',
    badge: '12',
    icon: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg>`,
  },
  {
    name: 'bouts',
    label: 'Manajemen bout',
    badge: '7',
    icon: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg>`,
  },
  {
    name: 'escrow',
    label: 'Deposit & escrow',
    badge: 'Rp3,6jt',
    icon: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="4" width="20" height="16" rx="2"></rect><line x1="2" y1="10" x2="22" y2="10"></line></svg>`,
  },
  {
    name: 'arbitration',
    label: 'Hasil & sengketa',
    badge: '2',
    icon: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m16 16 3-8 3 8c-.87.65-1.92 1-3 1s-2.13-.35-3-1Z"></path><path d="m2 16 3-8 3 8c-.87.65-1.92 1-3 1s-2.13-.35-3-1Z"></path><path d="M7 21h10"></path><path d="M12 3v18"></path><path d="M3 7h2c2 0 5-1 7-2 2 1 5 2 7 2h2"></path></svg>`,
  },
];

function handleLogout() {
  emit('logout');
}
</script>

<template>
  <aside class="sidebar" :class="{ 'sidebar--open': open }">
    <!-- Brand Header -->
    <div class="sidebar__brand">
      <div class="sidebar__title">
        <span class="sidebar__logo">BAKU</span>
        <span class="sidebar__logo sidebar__logo--accent">PICAH</span>
      </div>
      <div class="sidebar__tag">CONTROL ROOM</div>
    </div>

    <!-- Navigation List -->
    <nav class="sidebar__nav">
      <router-link
        v-for="item in items"
        :key="item.name"
        :to="{ name: item.name }"
        class="sidebar__link"
        active-class="sidebar__link--active"
        @click="$emit('close')"
      >
        <span class="sidebar__icon" v-html="item.icon"></span>
        <span class="sidebar__label">{{ item.label }}</span>
        <span v-if="item.badge" class="sidebar__badge">{{ item.badge }}</span>
      </router-link>
    </nav>

    <!-- Operational Status Card -->
    <div class="sidebar__status">
      <div class="status-indicator">
        <span class="status-dot"></span>
        <div class="status-text">
          <div class="status-city">OPERASIONAL BANDUNG</div>
          <div class="status-sub">Sistem aktif · Port 8000</div>
        </div>
      </div>
    </div>

    <!-- Admin Account Card -->
    <div class="sidebar__user">
      <div class="user-avatar">NA</div>
      <div class="user-info">
        <div class="user-name">Nadia Aulia</div>
        <div class="user-role">Administrator</div>
      </div>
      <button class="btn-logout" title="Keluar dari sesi" @click="handleLogout">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
          <polyline points="16 17 21 12 16 7"></polyline>
          <line x1="21" y1="12" x2="9" y2="12"></line>
        </svg>
      </button>
    </div>
  </aside>

  <div v-if="open" class="sidebar__backdrop" @click="$emit('close')" />
</template>

<style scoped>
.sidebar {
  width: 236px;
  background: #181818;
  border-right: 1px solid #2c2c2c;
  display: flex;
  flex-direction: column;
  padding: 16px 12px;
  flex-shrink: 0;
  height: 100vh;
  position: sticky;
  top: 0;
  user-select: none;
}

.sidebar__brand {
  padding: 8px 10px 20px;
  border-bottom: 1px solid #262626;
  margin-bottom: 12px;
}

.sidebar__title {
  display: flex;
  gap: 6px;
  font-family: var(--font-display, 'Anton', sans-serif);
  font-size: 24px;
  line-height: 1.1;
  letter-spacing: 0.04em;
}

.sidebar__logo {
  color: #f7f5f1;
}

.sidebar__logo--accent {
  color: #b30000;
}

.sidebar__tag {
  margin-top: 4px;
  font-size: 10px;
  font-weight: 800;
  letter-spacing: 0.22em;
  color: #b30000;
  text-transform: uppercase;
}

.sidebar__nav {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
  overflow-y: auto;
}

.sidebar__link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 8px;
  color: #a0a0a0;
  font-weight: 500;
  font-size: 13.5px;
  transition: all 0.15s ease;
  text-decoration: none;
}

.sidebar__link:hover {
  background: #242424;
  color: #f5f5f5;
}

.sidebar__link--active {
  background: rgba(179, 0, 0, 0.16);
  color: #f0f0f0;
  font-weight: 600;
  border-left: 3px solid #b30000;
}

.sidebar__icon {
  display: flex;
  align-items: center;
  color: inherit;
}

.sidebar__label {
  flex: 1;
}

.sidebar__badge {
  font-size: 11px;
  font-weight: 700;
  padding: 2px 7px;
  border-radius: 999px;
  background: #2b2b2b;
  color: #d1d1d1;
}

.sidebar__link--active .sidebar__badge {
  background: #8a0303;
  color: #ffffff;
}

.sidebar__status {
  padding: 10px 12px;
  background: #1e1e1e;
  border: 1px solid #2a2a2a;
  border-radius: 8px;
  margin-bottom: 10px;
}

.status-indicator {
  display: flex;
  align-items: center;
  gap: 10px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #22c55e;
  box-shadow: 0 0 8px rgba(34, 197, 94, 0.7);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(0.85); }
}

.status-city {
  font-size: 11px;
  font-weight: 700;
  color: #f5f5f5;
  letter-spacing: 0.05em;
}

.status-sub {
  font-size: 10px;
  color: #888888;
}

.sidebar__user {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 10px;
  background: #141414;
  border-radius: 8px;
  border: 1px solid #242424;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #8a0303;
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 12px;
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-size: 12.5px;
  font-weight: 600;
  color: #f5f5f5;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-role {
  font-size: 10.5px;
  color: #8a8a8a;
}

.btn-logout {
  background: transparent;
  border: none;
  color: #777777;
  padding: 6px;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  transition: all 0.15s ease;
}

.btn-logout:hover {
  background: #2a2a2a;
  color: #ff6b6b;
}

.sidebar__backdrop {
  display: none;
}

@media (max-width: 900px) {
  .sidebar {
    position: fixed;
    top: 0;
    left: 0;
    bottom: 0;
    z-index: 100;
    transform: translateX(-100%);
    transition: transform 0.25s ease;
  }
  .sidebar--open {
    transform: translateX(0);
  }
  .sidebar__backdrop {
    display: block;
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.7);
    z-index: 90;
  }
}
</style>
