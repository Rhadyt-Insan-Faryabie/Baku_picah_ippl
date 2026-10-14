<script setup>
import { computed } from 'vue';
import { statusTone } from '../services/status.js';

const props = defineProps({
  status: { type: String, required: true },
});

const tone = computed(() => statusTone(props.status));
</script>

<template>
  <span class="badge" :class="`badge--${tone}`">{{ status }}</span>
</template>

<style scoped>
.badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: var(--radius-pill);
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}
.badge--muted {
  background: var(--surface-3);
  color: var(--text-muted);
}
.badge--info {
  background: rgba(21, 101, 192, 0.18);
  color: #64b5f6;
}
.badge--warning {
  background: rgba(249, 168, 37, 0.18);
  color: var(--warning);
}
.badge--success {
  background: rgba(46, 125, 50, 0.2);
  color: #66bb6a;
}
.badge--danger {
  background: var(--blood-soft);
  color: #ef5350;
}
</style>
