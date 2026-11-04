<script setup>
import { ref, onMounted, onUnmounted } from 'vue';

defineProps({
  title: { type: String, default: '' },
});
defineEmits(['toggle-sidebar', 'logout']);

const searchQuery = ref('');
const currentTime = ref('');

function updateClock() {
  const now = new Date();
  const options = {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    timeZoneName: 'short',
  };
  try {
    currentTime.value = now.toLocaleDateString('id-ID', options);
  } catch (e) {
    currentTime.value = '16 Oktober 2026 · 14:32 WIB';
  }
}

let timer = null;
onMounted(() => {
  updateClock();
  timer = setInterval(updateClock, 30000);
});

onUnmounted(() => {
  if (timer) clearInterval(timer);
});
</script>

<template>
  <header class="topbar">
    <div class="topbar__left">
      <button class="topbar__hamburger btn-icon" @click="$emit('toggle-sidebar')">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <line x1="3" y1="12" x2="21" y2="12"></line>
          <line x1="3" y1="6" x2="21" y2="6"></line>
          <line x1="3" y1="18" x2="21" y2="18"></line>
        </svg>
      </button>

      <!-- Global Search Bar (Figma 3:1294) -->
      <div class="search-box">
        <svg class="search-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="11" cy="11" r="8"></circle>
          <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
        </svg>
        <input
          v-model="searchQuery"
          type="text"
          class="search-input"
          placeholder="Cari fighter, ID bout, atau transaksi…"
        />
        <span class="search-shortcut">Ctrl K</span>
      </div>
    </div>

    <div class="topbar__right">
      <!-- Live Date & Clock Pill -->
      <div class="date-pill">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
          <line x1="16" y1="2" x2="16" y2="6"></line>
          <line x1="8" y1="2" x2="8" y2="6"></line>
          <line x1="3" y1="10" x2="21" y2="10"></line>
        </svg>
        <span>{{ currentTime || '16 Oktober 2026 · 14:32 WIB' }}</span>
      </div>

      <!-- Admin Status Pill -->
      <div class="admin-badge">
        <span class="admin-badge__dot"></span>
        <span>ADMIN</span>
      </div>

      <!-- Notifications Bell -->
      <button class="btn-icon" title="Notifikasi Baru">
        <div class="bell-wrapper">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
            <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
          </svg>
          <span class="notification-badge"></span>
        </div>
      </button>

      <!-- Logout Button -->
      <button class="btn-logout-top" @click="$emit('logout')">
        Keluar
      </button>
    </div>
  </header>
</template>

<style scoped>
.topbar {
  height: 64px;
  background: #181818;
  border-bottom: 1px solid #2c2c2c;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 0 24px;
  position: sticky;
  top: 0;
  z-index: 40;
}

.topbar__left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  max-width: 520px;
}

.topbar__hamburger {
  display: none;
}

.btn-icon {
  background: transparent;
  border: none;
  color: #a0a0a0;
  padding: 8px;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.15s ease;
}

.btn-icon:hover {
  background: #262626;
  color: #ffffff;
}

.search-box {
  display: flex;
  align-items: center;
  background: #121212;
  border: 1px solid #2c2c2c;
  border-radius: 8px;
  padding: 0 12px;
  height: 38px;
  width: 100%;
  max-width: 380px;
  transition: border-color 0.15s ease;
}

.search-box:focus-within {
  border-color: #b30000;
  box-shadow: 0 0 0 2px rgba(179, 0, 0, 0.2);
}

.search-icon {
  color: #666666;
  margin-right: 8px;
  flex-shrink: 0;
}

.search-input {
  background: transparent;
  border: none;
  outline: none;
  color: #f5f5f5;
  font-size: 13px;
  width: 100%;
}

.search-input::placeholder {
  color: #707070;
}

.search-shortcut {
  font-size: 10px;
  font-weight: 600;
  color: #666666;
  background: #1e1e1e;
  border: 1px solid #333333;
  padding: 2px 6px;
  border-radius: 4px;
  white-space: nowrap;
}

.topbar__right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.date-pill {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #999999;
  background: #1e1e1e;
  padding: 6px 12px;
  border-radius: 6px;
  border: 1px solid #2b2b2b;
}

.admin-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  background: rgba(179, 0, 0, 0.15);
  border: 1px solid #8a0303;
  color: #ff6b6b;
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 0.1em;
  padding: 5px 10px;
  border-radius: 6px;
}

.admin-badge__dot {
  width: 6px;
  height: 6px;
  background: #ff3333;
  border-radius: 50%;
}

.bell-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.notification-badge {
  position: absolute;
  top: -2px;
  right: -2px;
  width: 7px;
  height: 7px;
  background: #b30000;
  border-radius: 50%;
  border: 1px solid #181818;
}

.btn-logout-top {
  background: #242424;
  border: 1px solid #333333;
  color: #cccccc;
  font-size: 12px;
  font-weight: 600;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.15s ease;
}

.btn-logout-top:hover {
  background: #8a0303;
  color: #ffffff;
  border-color: #b30000;
}

@media (max-width: 900px) {
  .topbar__hamburger {
    display: flex;
  }
  .search-box {
    display: none;
  }
  .date-pill {
    display: none;
  }
}
</style>
