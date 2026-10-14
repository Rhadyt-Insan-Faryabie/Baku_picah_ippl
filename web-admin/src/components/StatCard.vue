<script setup>
defineProps({
  label: String,
  value: [String, Number],
  delta: String,
  tone: { type: String, default: 'muted' },
});
</script>

<template>
  <div class="stat card">
    <span class="stat__label muted">{{ label }}</span>
    <span class="stat__value display">{{ value }}</span>
    <span class="stat__delta" :class="`stat__delta--${tone}`">{{ delta }}</span>
  </div>
</template>

<style scoped>
.stat {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.stat__label {
  font-size: 13px;
}
.stat__value {
  font-size: 40px;
  line-height: 1;
}
.stat__delta {
  font-size: 12px;
  font-weight: 600;
}
.stat__delta--warning {
  color: var(--warning);
}
.stat__delta--info {
  color: var(--info);
}
.stat__delta--danger {
  color: var(--danger);
}
.stat__delta--success {
  color: var(--success);
}
</style>
