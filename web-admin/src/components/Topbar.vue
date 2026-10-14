<script setup>
defineProps({
  title: { type: String, default: '' },
});
defineEmits(['toggle-sidebar', 'logout']);
</script>

<template>
  <header class="topbar">
    <button class="topbar__hamburger btn btn--ghost" @click="$emit('toggle-sidebar')">☰</button>
    <h1 class="topbar__title">{{ title }}</h1>
    <div class="topbar__right">
      <span class="topbar__user">Admin</span>
      <button class="btn btn--ghost" @click="$emit('logout')">Keluar</button>
    </div>
  </header>
</template>

<style scoped>
.topbar {
  height: var(--topbar-h);
  background: var(--surface);
  border-bottom: 1px solid var(--border);
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 0 24px;
}
.topbar__title {
  font-size: 20px;
  flex: 1;
}
.topbar__right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.topbar__user {
  color: var(--text-muted);
  font-size: 14px;
}
.topbar__hamburger {
  display: none;
}

@media (max-width: 900px) {
  .topbar__hamburger {
    display: inline-flex;
  }
}
</style>
