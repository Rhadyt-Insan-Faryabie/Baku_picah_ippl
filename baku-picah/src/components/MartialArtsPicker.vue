<script setup>
import { MARTIAL_ARTS } from '../constants/options';

const props = defineProps({
  modelValue: { type: Array, default: () => [] },
});
const emit = defineEmits(['update:modelValue']);

function isSelected(art) {
  return (props.modelValue || []).includes(art);
}

function toggle(art) {
  const current = props.modelValue || [];
  const next = current.includes(art)
    ? current.filter((a) => a !== art)
    : [...current, art];
  emit('update:modelValue', next);
}
</script>

<template>
  <div class="picker">
    <button
      v-for="art in MARTIAL_ARTS"
      :key="art"
      type="button"
      class="chip"
      :class="{ 'chip--on': isSelected(art) }"
      @click="toggle(art)"
    >
      {{ art }}
    </button>
  </div>
</template>

<style scoped>
.picker {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.chip {
  border: 1px solid var(--border);
  background: var(--surface-2);
  color: var(--text-muted);
  border-radius: var(--radius-pill);
  padding: 7px 14px;
  font-size: 13px;
  font-weight: 500;
}
.chip:hover {
  border-color: var(--blood-bright);
  color: var(--text);
}
.chip--on {
  background: var(--blood-soft);
  border-color: var(--blood-bright);
  color: var(--blood-bright);
  font-weight: 600;
}
</style>
