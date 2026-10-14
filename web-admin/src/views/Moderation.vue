<script setup>
import { ref } from 'vue';
import DataTable from '../components/DataTable.vue';
import StatusBadge from '../components/StatusBadge.vue';
import Modal from '../components/Modal.vue';
import { reports as seed } from '../services/mockData.js';

const reports = ref([...seed]);
const selected = ref(null);
const action = ref('warning');
const note = ref('');

const columns = [
  { key: 'id', label: 'ID' },
  { key: 'target', label: 'Terlapor' },
  { key: 'reporter', label: 'Pelapor' },
  { key: 'reason', label: 'Alasan' },
  { key: 'severity', label: 'Tingkat' },
  { key: 'status', label: 'Status' },
  { key: 'date', label: 'Tanggal' },
];

function open(row) {
  selected.value = { ...row };
  action.value = 'warning';
  note.value = '';
}

function apply() {
  reports.value = reports.value.map((r) =>
    r.id === selected.value.id ? { ...r, status: 'Resolved' } : r
  );
  selected.value = null;
}
</script>

<template>
  <div>
    <p class="muted intro">Tindak lanjuti laporan pelanggaran etika tanding.</p>

    <DataTable :columns="columns" :rows="reports">
      <template #severity="{ row }"><StatusBadge :status="row.severity" /></template>
      <template #status="{ row }"><StatusBadge :status="row.status" /></template>
      <template #actions="{ row }">
        <button class="btn btn--ghost" @click="open(row)">Tindak</button>
      </template>
    </DataTable>

    <Modal :open="!!selected" title="Tindakan Moderasi" @close="selected = null">
      <div v-if="selected" class="mod">
        <div class="mod__row"><span class="muted">Terlapor</span><span>{{ selected.target }}</span></div>
        <div class="mod__row"><span class="muted">Pelapor</span><span>{{ selected.reporter }}</span></div>
        <div class="mod__row"><span class="muted">Alasan</span><span>{{ selected.reason }}</span></div>

        <div class="field" style="margin-top: 12px">
          <label>Sanksi</label>
          <select v-model="action">
            <option value="warning">Peringatan</option>
            <option value="suspend">Suspend Sementara</option>
            <option value="ban">Ban Permanen</option>
          </select>
        </div>
        <div class="field">
          <label>Catatan</label>
          <textarea v-model="note" rows="3" placeholder="Catatan tindakan..."></textarea>
        </div>
      </div>

      <template #footer>
        <button class="btn btn--ghost" @click="selected = null">Batal</button>
        <button class="btn btn--primary" @click="apply">Terapkan Sanksi</button>
      </template>
    </Modal>
  </div>
</template>

<style scoped>
.intro {
  margin: 0 0 16px;
  font-size: 14px;
}
.mod {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.mod__row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  font-size: 14px;
}
</style>
