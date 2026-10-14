<script setup>
import { CITIES } from '../constants/options';

defineProps({
  modelValue: { type: String, default: '' },
});
const emit = defineEmits(['update:modelValue']);
</script>

<template>
  <select
    :value="modelValue || ''"
    @change="emit('update:modelValue', $event.target.value || null)"
  >
    <option value="">Pilih kota…</option>
    <option v-for="city in CITIES" :key="city" :value="city">{{ city }}</option>
  </select>
</template>
