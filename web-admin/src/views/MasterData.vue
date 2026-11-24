<script setup>
import { ref, onMounted } from 'vue';
import DataTable from '../components/DataTable.vue';
import Modal from '../components/Modal.vue';
import { weightClasses as wcSeed } from '../services/mockData.js';
import api from '../services/api.js';

const activeTab = ref('weights'); // 'weights' | 'locations'

const weightClasses = ref([...wcSeed]);
const locations = ref([
  { id: 1, name: 'Baku Picah Neutral Octagon', city: 'Bandung', address: 'Jl. LLRE Martadinata No. 88', is_neutral: 'Ya (Official)' },
  { id: 2, name: 'Garuda Boxing & Combat Arena', city: 'Bandung', address: 'Jl. Asia Afrika No. 120', is_neutral: 'Ya (Official)' },
  { id: 3, name: 'Tiger MMA Center', city: 'Jakarta', address: 'Jl. Senopati No. 45', is_neutral: 'Ya (Official)' },
  { id: 4, name: 'Surabaya Fight Dome', city: 'Surabaya', address: 'Jl. Mayjen Sungkono No. 100', is_neutral: 'Ya (Official)' },
]);

const classColumns = [
  { key: 'name', label: 'Nama Kelas' },
  { key: 'maxKg', label: 'Maksimum (kg)' },
];

const locationColumns = [
  { key: 'name', label: 'Nama Arena Netral' },
  { key: 'city', label: 'Kota' },
  { key: 'address', label: 'Alamat' },
  { key: 'is_neutral', label: 'Status Netral' },
];

const showAddClass = ref(false);
const showAddLocation = ref(false);

const newClass = ref({ name: '', maxKg: '' });
const newLocation = ref({ name: '', city: 'Bandung', address: '' });

onMounted(async () => {
  try {
    const wcRes = await api.get('/master/weight-classes');
    if (wcRes.data && wcRes.data.length > 0) {
      weightClasses.value = wcRes.data;
    }
  } catch (e) {
    console.warn('Using seeded weight classes', e);
  }

  try {
    const locRes = await api.get('/master/locations');
    if (locRes.data && locRes.data.length > 0) {
      locations.value = locRes.data.map(l => ({
        ...l,
        is_neutral: l.is_neutral ? 'Ya (Official)' : 'Sasana Umum'
      }));
    }
  } catch (e) {
    console.warn('Using seeded locations', e);
  }
});

async function addClass() {
  if (!newClass.value.name) return;
  const payload = {
    id: Date.now(),
    name: newClass.value.name,
    maxKg: Number(newClass.value.maxKg) || 0,
  };
  try {
    await api.post('/master/weight-classes', payload);
  } catch (e) {
    console.warn('API error, saving locally', e);
  }
  weightClasses.value.push(payload);
  newClass.value = { name: '', maxKg: '' };
  showAddClass.value = false;
}

async function addLocation() {
  if (!newLocation.value.name) return;
  const payload = {
    id: Date.now(),
    name: newLocation.value.name,
    city: newLocation.value.city,
    address: newLocation.value.address || '-',
    is_neutral: true,
  };
  try {
    await api.post('/master/locations', payload);
  } catch (e) {
    console.warn('API error, saving locally', e);
  }
  locations.value.push({
    ...payload,
    is_neutral: 'Ya (Official)'
  });
  newLocation.value = { name: '', city: 'Bandung', address: '' };
  showAddLocation.value = false;
}
</script>

<template>
  <div>
    <!-- Navigation Tabs -->
    <div class="tabs">
      <button 
        class="tab-btn" 
        :class="{ active: activeTab === 'weights' }"
        @click="activeTab = 'weights'"
      >
        ⚖️ Kelas Pertandingan MMA (FR-022)
      </button>
      <button 
        class="tab-btn" 
        :class="{ active: activeTab === 'locations' }"
        @click="activeTab = 'locations'"
      >
        📍 Lokasi &amp; Arena Netral Titik Tengah (FR-023)
      </button>

      <div class="actions-right">
        <button 
          v-if="activeTab === 'weights'" 
          class="btn btn--primary" 
          @click="showAddClass = true"
        >
          + Tambah Kelas Berat
        </button>
        <button 
          v-else 
          class="btn btn--primary" 
          @click="showAddLocation = true"
        >
          + Tambah Arena Netral
        </button>
      </div>
    </div>

    <!-- Tab 1: Weight Classes -->
    <div v-if="activeTab === 'weights'">
      <p class="muted intro">Master data standar kelas berat MMA resmi Baku Picah untuk validasi matchmaking.</p>
      <DataTable :columns="classColumns" :rows="weightClasses" />
    </div>

    <!-- Tab 2: Neutral Locations -->
    <div v-else>
      <p class="muted intro">Master data arena netral terverifikasi (titik tengah sparring) untuk rekomendasi laga otomatis.</p>
      <DataTable :columns="locationColumns" :rows="locations" />
    </div>

    <!-- Modal Tambah Kelas Berat -->
    <Modal :open="showAddClass" title="Tambah Kelas Berat Baru" @close="showAddClass = false">
      <div class="form">
        <div class="field">
          <label>Nama Kelas</label>
          <input v-model="newClass.name" placeholder="mis. Super Lightweight" />
        </div>
        <div class="field">
          <label>Batas Maksimum Bobot (kg)</label>
          <input v-model="newClass.maxKg" type="number" step="0.1" placeholder="mis. 74.5" />
        </div>
      </div>

      <template #footer>
        <button class="btn btn--ghost" @click="showAddClass = false">Batal</button>
        <button class="btn btn--primary" @click="addClass()">Simpan Kelas</button>
      </template>
    </Modal>

    <!-- Modal Tambah Lokasi Netral -->
    <Modal :open="showAddLocation" title="Tambah Arena Netral Baku Picah" @close="showAddLocation = false">
      <div class="form">
        <div class="field">
          <label>Nama Arena / Sasana Netral</label>
          <input v-model="newLocation.name" placeholder="mis. Baku Picah Octagon East" />
        </div>
        <div class="field">
          <label>Kota</label>
          <select v-model="newLocation.city">
            <option value="Bandung">Bandung</option>
            <option value="Jakarta">Jakarta</option>
            <option value="Surabaya">Surabaya</option>
            <option value="Medan">Medan</option>
            <option value="Semarang">Semarang</option>
          </select>
        </div>
        <div class="field">
          <label>Alamat Lengkap</label>
          <input v-model="newLocation.address" placeholder="mis. Jl. Diponegoro No. 12" />
        </div>
      </div>

      <template #footer>
        <button class="btn btn--ghost" @click="showAddLocation = false">Batal</button>
        <button class="btn btn--primary" @click="addLocation()">Simpan Arena</button>
      </template>
    </Modal>
  </div>
</template>

<style scoped>
.tabs {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  border-bottom: 1px solid var(--border);
  padding-bottom: 12px;
}
.tab-btn {
  background: transparent;
  border: 1px solid var(--border);
  color: var(--text-muted);
  padding: 8px 16px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  transition: all 0.2s ease;
}
.tab-btn.active {
  background: var(--blood-soft);
  color: var(--blood-bright);
  border-color: var(--blood-bright);
}
.actions-right {
  margin-left: auto;
}
.intro {
  margin: 0 0 16px;
  font-size: 14px;
}
.form {
  display: flex;
  flex-direction: column;
  gap: 14px;
}
</style>
