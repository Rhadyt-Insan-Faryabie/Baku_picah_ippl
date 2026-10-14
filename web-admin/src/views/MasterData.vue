<script setup>
import { ref } from 'vue';
import DataTable from '../components/DataTable.vue';
import Modal from '../components/Modal.vue';
import { weightClasses as wcSeed } from '../services/mockData.js';

const weightClasses = ref([...wcSeed]);

const classColumns = [
  { key: 'name', label: 'Kelas' },
  { key: 'maxKg', label: 'Maks (kg)' },
];

const showAdd = ref(false);
const newClass = ref({ name: '', maxKg: '' });

function addClass() {
  if (!newClass.value.name) return;
  weightClasses.value.push({ id: Date.now(), maxKg: Number(newClass.value.maxKg) || 0, ...newClass.value });
  newClass.value = { name: '', maxKg: '' };
  showAdd.value = false;
}
</script>

<template>
  <div>
    <div class="tabs">
      <span class="rules">Aturan tanding: MMA</span>
      <button class="btn btn--primary add-btn" @click="showAdd = true">+ Tambah Kelas</button>
    </div>

    <DataTable :columns="classColumns" :rows="weightClasses" />

    <Modal :open="showAdd" title="Tambah Kelas Berat" @close="showAdd = false">
      <div class="form">
        <div class="field"><label>Nama Kelas</label><input v-model="newClass.name" placeholder="mis. Featherweight" /></div>
        <div class="field"><label>Berat Maksimum (kg)</label><input v-model="newClass.maxKg" type="number" /></div>
      </div>

      <template #footer>
        <button class="btn btn--ghost" @click="showAdd = false">Batal</button>
        <button class="btn btn--primary" @click="addClass()">Simpan</button>
      </template>
    </Modal>
  </div>
</template>

<style scoped>
.tabs {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}
.rules {
  font-family: var(--font-display);
  font-size: 16px;
  color: var(--blood-bright);
  border: 1px solid var(--blood-bright);
  background: var(--blood-soft);
  border-radius: var(--radius-pill);
  padding: 4px 16px;
}
.add-btn {
  margin-left: auto;
}
.form {
  display: flex;
  flex-direction: column;
  gap: 14px;
}
</style>
