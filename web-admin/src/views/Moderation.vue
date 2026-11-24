<script setup>
import { ref, onMounted } from 'vue';
import DataTable from '../components/DataTable.vue';
import StatusBadge from '../components/StatusBadge.vue';
import Modal from '../components/Modal.vue';
import { reports as seed } from '../services/mockData.js';
import api from '../services/api.js';

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

function formatApiReport(r) {
  return {
    id: `REP-${String(r.id).padStart(4, '0')}`,
    rawId: r.id,
    target: r.target_name || `Fighter #${r.target_id}`,
    reporter: r.reporter_name || `Fighter #${r.reporter_id}`,
    reason: r.reason,
    severity: r.severity || 'Medium',
    status: r.status || 'Pending',
    date: r.created_at ? new Date(r.created_at).toLocaleDateString('id-ID') : 'Hari ini',
    details: r.details || '-',
  };
}

onMounted(async () => {
  try {
    const res = await api.get('/reports/');
    if (res.data && res.data.length > 0) {
      reports.value = res.data.map(formatApiReport);
    }
  } catch (e) {
    console.warn('Using seeded reports data', e);
  }
});

function open(row) {
  selected.value = { ...row };
  action.value = 'warning';
  note.value = '';
}

async function apply() {
  if (!selected.value) return;
  const targetId = selected.value.rawId || selected.value.id;
  try {
    await api.post(`/reports/${targetId}/sanction`, {
      action: action.value,
      note: note.value,
    });
  } catch (e) {
    console.warn('API error, applying locally', e);
  }

  reports.value = reports.value.map((r) =>
    r.id === selected.value.id ? { ...r, status: 'Resolved' } : r
  );
  selected.value = null;
}
</script>

<template>
  <div>
    <p class="muted intro">Tindak lanjuti laporan pelanggaran etika tanding &amp; tetapkan sanksi resmi Baku Picah.</p>

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
          <label>Sanksi Resmi</label>
          <select v-model="action">
            <option value="warning">Peringatan Tertulis</option>
            <option value="suspend">Suspend Sementara (7 Hari)</option>
            <option value="ban">Ban Permanen (Blacklist)</option>
          </select>
        </div>
        <div class="field">
          <label>Catatan Admin</label>
          <textarea v-model="note" rows="3" placeholder="Alasan sanksi untuk arsip komisi etik..."></textarea>
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
