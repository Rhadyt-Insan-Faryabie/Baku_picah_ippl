<script setup>
defineProps({
  title: { type: String, required: true },
  subtitle: { type: String, default: '' },
});
</script>

<template>
  <header class="ph">
    <h1 class="ph__title">{{ title }}</h1>
    <p v-if="subtitle" class="ph__sub muted">{{ subtitle }}</p>
    <div class="ph__actions">
      <slot name="actions" />
    </div>
  </header>
</template>

<style scoped>
.ph {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}
.ph__title {
  font-size: 26px;
}
.ph__sub {
  margin: 6px 0 0;
  font-size: 14px;
  width: 100%;
}
.ph__actions {
  display: flex;
  gap: 10px;
}
</style>
