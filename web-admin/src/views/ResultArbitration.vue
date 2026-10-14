<script setup>
import { ref } from 'vue';
import DataTable from '../components/DataTable.vue';
import Modal from '../components/Modal.vue';
import { disputes as seed } from '../services/mockData.js';

const disputes = ref([...seed]);
const selected = ref(null);
const resolution = ref('fighterA');

const columns = [
  { key: 'id', label: 'ID' },
  { key: 'boutId', label: 'Laga' },
  { key: 'fighters', label: 'Petarung' },
  { key: 'claimed', label: 'Klaim' },
  { key: 'counter', label: 'Bantahan' },
  { key: 'reported', label: 'Dilaporkan' },
];

function open(row) {
  selected.value = { ...row };
  resolution.value = 'fighterA';
}

function resolve() {
  disputes.value = disputes.value.filter((d) => d.id !== selected.value.id);
  selected.value = null;
}
</script>

<template>
  <div>
    <p class="muted intro">
      Selesaikan hasil laga yang diperselisihkan kedua petarung sebelum rekor diperbarui.
    </p>

    <DataTable :columns="columns" :rows="disputes" empty-text="Tidak ada dispute aktif. 🎉">
      <template #fighters="{ row }">{{ row.fighterA }} vs {{ row.fighterB }}</template>
      <template #actions="{ row }">
        <button class="btn btn--ghost" @click="open(row)">Arbitrase</button>
      </template>
    </DataTable>

    <Modal :open="!!selected" title="Arbitrase Hasil Laga" @close="selected = null">
      <div v-if="selected" class="arb">
        <p class="muted">{{ selected.boutId }} · dilaporkan {{ selected.reported }}</p>
        <label class="arb__opt" :class="{ 'arb__opt--active': resolution === 'fighterA' }">
          <input v-model="resolution" type="radio" value="fighterA" />
          <span><strong>{{ selected.fighterA }}</strong> — {{ selected.claimed }}</span>
        </label>
        <label class="arb__opt" :class="{ 'arb__opt--active': resolution === 'fighterB' }">
          <input v-model="resolution" type="radio" value="fighterB" />
          <span><strong>{{ selected.fighterB }}</strong> — {{ selected.counter }}</span>
        </label>
        <label class="arb__opt" :class="{ 'arb__opt--active': resolution === 'draw' }">
          <input v-model="resolution" type="radio" value="draw" />
          <span>Draw / No Contest</span>
        </label>
        <p class="muted arb__ev">Bukti terlampir: {{ selected.evidence }}</p>
      </div>

      <template #footer>
        <button class="btn btn--ghost" @click="selected = null">Batal</button>
        <button class="btn btn--primary" @click="resolve">Tetapkan Hasil</button>
      </template>
    </Modal>
  </div>
</template>

<style scoped>
.intro {
  margin: 0 0 16px;
  font-size: 14px;
}
.arb {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.arb__opt {
  display: flex;
  align-items: center;
  gap: 10px;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  padding: 12px 14px;
  font-size: 14px;
  cursor: pointer;
}
.arb__opt--active {
  border-color: var(--blood-bright);
  background: var(--blood-soft);
}
.arb__ev {
  font-size: 12px;
  margin: 4px 0 0;
}
</style>
