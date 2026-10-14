<script setup>
defineProps({
  open: { type: Boolean, default: false },
  title: { type: String, default: '' },
});
defineEmits(['close']);
</script>

<template>
  <teleport to="body">
    <div v-if="open" class="modal" @click.self="$emit('close')">
      <div class="modal__panel card">
        <div class="modal__head">
          <h2 class="modal__title">{{ title }}</h2>
          <button class="modal__x" @click="$emit('close')">✕</button>
        </div>
        <div class="modal__body">
          <slot />
        </div>
        <div v-if="$slots.footer" class="modal__foot">
          <slot name="footer" />
        </div>
      </div>
    </div>
  </teleport>
</template>

<style scoped>
.modal {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.65);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 50;
  padding: 20px;
}
.modal__panel {
  width: 100%;
  max-width: 560px;
  max-height: 88vh;
  overflow: auto;
}
.modal__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.modal__title {
  font-size: 20px;
}
.modal__x {
  background: none;
  border: none;
  color: var(--text-muted);
  font-size: 18px;
}
.modal__foot {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid var(--border);
}
</style>
