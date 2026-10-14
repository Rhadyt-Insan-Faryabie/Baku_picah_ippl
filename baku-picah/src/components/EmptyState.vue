<script setup>
defineProps({
  title: { type: String, default: 'Segera hadir' },
  message: { type: String, default: '' },
  icon: { type: String, default: '🚧' },
});
</script>

<template>
  <div class="empty card">
    <div class="empty__icon">{{ icon }}</div>
    <h3 class="empty__title">{{ title }}</h3>
    <p class="empty__msg muted">{{ message }}</p>
    <slot />
  </div>
</template>

<style scoped>
.empty {
  text-align: center;
  padding: 44px 24px;
}
.empty__icon {
  font-size: 40px;
}
.empty__title {
  margin: 12px 0 6px;
  font-size: 18px;
}
.empty__msg {
  margin: 0 auto;
  max-width: 420px;
  font-size: 14px;
  line-height: 1.6;
}
</style>
