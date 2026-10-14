<script setup>
defineProps({
  open: { type: Boolean, default: false },
});
defineEmits(['close']);

const items = [
  { name: 'dashboard', label: 'Dashboard', icon: '▦' },
  { name: 'verification', label: 'Verifikasi Laga', icon: '✓' },
  { name: 'arbitration', label: 'Arbitrase Hasil', icon: '⚖' },
  { name: 'master-data', label: 'Master Data', icon: '⚙' },
  { name: 'moderation', label: 'Moderasi', icon: '⚑' },
];
</script>

<template>
  <aside class="sidebar" :class="{ 'sidebar--open': open }">
    <div class="sidebar__brand">
      <span class="sidebar__logo">BAKU</span>
      <span class="sidebar__logo sidebar__logo--accent">PICAH</span>
    </div>

    <nav class="sidebar__nav">
      <router-link
        v-for="item in items"
        :key="item.name"
        :to="{ name: item.name }"
        class="sidebar__link"
        active-class="sidebar__link--active"
        @click="$emit('close')"
      >
        <span class="sidebar__icon">{{ item.icon }}</span>
        {{ item.label }}
      </router-link>
    </nav>

    <div class="sidebar__foot muted">Admin Panel · v0.1</div>
  </aside>
  <div v-if="open" class="sidebar__backdrop" @click="$emit('close')" />
</template>

<style scoped>
.sidebar {
  width: var(--sidebar-w);
  background: var(--surface);
  border-right: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  padding: 20px 14px;
  flex-shrink: 0;
}
.sidebar__brand {
  display: flex;
  gap: 6px;
  font-family: var(--font-display);
  font-size: 26px;
  padding: 8px 10px 24px;
}
.sidebar__logo--accent {
  color: var(--blood-bright);
}
.sidebar__nav {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
}
.sidebar__link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  border-radius: var(--radius-sm);
  color: var(--text-muted);
  font-weight: 500;
  font-size: 14px;
}
.sidebar__link:hover {
  background: var(--surface-2);
  color: var(--text);
}
.sidebar__link--active {
  background: var(--blood-soft);
  color: var(--blood-bright);
  font-weight: 600;
}
.sidebar__icon {
  width: 18px;
  text-align: center;
}
.sidebar__foot {
  font-size: 12px;
  padding: 10px;
}
.sidebar__backdrop {
  display: none;
}

@media (max-width: 900px) {
  .sidebar {
    position: fixed;
    inset: 0 auto 0 0;
    z-index: 30;
    transform: translateX(-100%);
    transition: transform 0.2s ease;
  }
  .sidebar--open {
    transform: translateX(0);
  }
  .sidebar__backdrop {
    display: block;
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.55);
    z-index: 20;
  }
}
</style>
