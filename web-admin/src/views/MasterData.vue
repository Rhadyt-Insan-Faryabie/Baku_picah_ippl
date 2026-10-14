<script setup>
import { ref } from 'vue';
import DataTable from '../components/DataTable.vue';
import Modal from '../components/Modal.vue';
import { weightClasses as wcSeed, venues as venueSeed } from '../services/mockData.js';

const tab = ref('classes');

const weightClasses = ref([...wcSeed]);
const venues = ref([...venueSeed]);

const classColumns = [
  { key: 'name', label: 'Kelas' },
  { key: 'maxKg', label: 'Maks (kg)' },
  { key: 'discipline', label: 'Disiplin' },
];
const venueColumns = [
  { key: 'name', label: 'Arena/Sasana' },
  { key: 'city', label: 'Kota' },
  { key: 'verified', label: 'Terverifikasi' },
];

const showAdd = ref(false);
const newClass = ref({ name: '', maxKg: '', discipline: 'Boxing' });
const newVenue = ref({ name: '', city: '', verified: false });

function addClass() {
  if (!newClass.value.name) return;
  weightClasses.value.push({ id: Date.now(), maxKg: Number(newClass.value.maxKg) || 0, ...newClass.value });
  newClass.value = { name: '', maxKg: '', discipline: 'Boxing' };
  showAdd.value = false;
}

function addVenue() {
  if (!newVenue.value.name) return;
  venues.value.push({ id: Date.now(), ...newVenue.value });
  newVenue.value = { name: '', city: '', verified: false };
  showAdd.value = false;
}
</script>

<template>
  <div>
    <div class="tabs">
      <button class="chip" :class="{ 'chip--active': tab === 'classes' }" @click="tab = 'classes'">
        Kelas Berat
      </button>
      <button class="chip" :class="{ 'chip--active': tab === 'venues' }" @click="tab = 'venues'">
        Arena / Sasana
      </button>
      <button class="btn btn--primary add-btn" @click="showAdd = true">
        + Tambah {{ tab === 'classes' ? 'Kelas' : 'Arena' }}
      </button>
    </div>

    <DataTable v-if="tab === 'classes'" :columns="classColumns" :rows="weightClasses" />
    <DataTable v-else :columns="venueColumns" :rows="venues">
      <template #verified="{ row }">
        <span :class="row.verified ? 'ok' : 'muted'">{{ row.verified ? 'Ya' : 'Belum' }}</span>
      </template>
    </DataTable>

    <Modal :open="showAdd" :title="tab === 'classes' ? 'Tambah Kelas Berat' : 'Tambah Arena'" @close="showAdd = false">
      <div v-if="tab === 'classes'" class="form">
        <div class="field"><label>Nama Kelas</label><input v-model="newClass.name" placeholder="mis. Featherweight" /></div>
        <div class="field"><label>Berat Maksimum (kg)</label><input v-model="newClass.maxKg" type="number" /></div>
        <div class="field">
          <label>Disiplin</label>
          <select v-model="newClass.discipline">
            <option>Boxing</option>
            <option>MMA</option>
            <option>Muay Thai</option>
            <option>BJJ</option>
          </select>
        </div>
      </div>
      <div v-else class="form">
        <div class="field"><label>Nama Arena/Sasana</label><input v-model="newVenue.name" placeholder="mis. Sasana Garuda" /></div>
        <div class="field"><label>Kota</label><input v-model="newVenue.city" /></div>
        <label class="check"><input v-model="newVenue.verified" type="checkbox" /> Tandai terverifikasi</label>
      </div>

      <template #footer>
        <button class="btn btn--ghost" @click="showAdd = false">Batal</button>
        <button class="btn btn--primary" @click="tab === 'classes' ? addClass() : addVenue()">Simpan</button>
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
.chip {
  background: var(--surface-2);
  border: 1px solid var(--border);
  border-radius: var(--radius-pill);
  color: var(--text-muted);
  padding: 7px 14px;
  font-size: 13px;
}
.chip--active {
  background: var(--blood-soft);
  border-color: var(--blood-bright);
  color: var(--blood-bright);
}
.add-btn {
  margin-left: auto;
}
.form {
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.check {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}
.ok {
  color: #66bb6a;
}
</style>
