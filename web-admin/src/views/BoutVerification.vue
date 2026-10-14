<script setup>
import { computed, ref } from 'vue';
import DataTable from '../components/DataTable.vue';
import StatusBadge from '../components/StatusBadge.vue';
import Modal from '../components/Modal.vue';
import { bouts as seed } from '../services/mockData.js';

const bouts = ref([...seed]);
const filter = ref('Semua');
const selected = ref(null);
const rejectReason = ref('');
const decision = ref('');

const filters = ['Semua', 'Awaiting Verification', 'Opponent Accepted', 'Pending Opponent', 'Confirmed', 'Completed'];

const columns = [
  { key: 'id', label: 'ID' },
  { key: 'match', label: 'Laga' },
  { key: 'discipline', label: 'Disiplin' },
  { key: 'venue', label: 'Venue' },
  { key: 'date', label: 'Jadwal' },
  { key: 'deposit', label: 'Deposit' },
  { key: 'status', label: 'Status' },
];

const filtered = computed(() =>
  filter.value === 'Semua' ? bouts.value : bouts.value.filter((b) => b.status === filter.value)
);

function open(row) {
  selected.value = { ...row };
  rejectReason.value = '';
  decision.value = '';
}

function approve() {
  setStatus('Confirmed');
  close();
}

function reject() {
  if (!rejectReason.value.trim()) return;
  bouts.value = bouts.value.filter((b) => b.id !== selected.value.id);
  close();
}

function setStatus(status) {
  bouts.value = bouts.value.map((b) => (b.id === selected.value.id ? { ...b, status } : b));
}

function close() {
  selected.value = null;
}
</script>

<template>
  <div>
    <div class="toolbar">
      <button
        v-for="f in filters"
        :key="f"
        class="chip"
        :class="{ 'chip--active': filter === f }"
        @click="filter = f"
      >
        {{ f }}
      </button>
    </div>

    <DataTable :columns="columns" :rows="filtered">
      <template #match="{ row }">
        <strong>{{ row.fighterA }}</strong> <span class="muted">vs</span> <strong>{{ row.fighterB }}</strong>
      </template>
      <template #status="{ row }">
        <StatusBadge :status="row.status" />
      </template>
      <template #actions="{ row }">
        <button class="btn btn--ghost" @click="open(row)">Tinjau</button>
      </template>
    </DataTable>

    <Modal :open="!!selected" title="Tinjau Pengajuan Laga" @close="close">
      <div v-if="selected" class="detail">
        <div class="detail__row"><span class="muted">ID</span><span>{{ selected.id }}</span></div>
        <div class="detail__row"><span class="muted">Petarung A</span><span>{{ selected.fighterA }}</span></div>
        <div class="detail__row"><span class="muted">Petarung B</span><span>{{ selected.fighterB }}</span></div>
        <div class="detail__row"><span class="muted">Disiplin</span><span>{{ selected.discipline }}</span></div>
        <div class="detail__row"><span class="muted">Kelas Berat</span><span>{{ selected.weight }}</span></div>
        <div class="detail__row"><span class="muted">Venue</span><span>{{ selected.venue }}</span></div>
        <div class="detail__row"><span class="muted">Jadwal</span><span>{{ selected.date }}</span></div>
        <div class="detail__row"><span class="muted">Deposit</span><span>{{ selected.deposit }}</span></div>
        <div class="detail__row">
          <span class="muted">Status</span><StatusBadge :status="selected.status" />
        </div>

        <div class="field" style="margin-top: 14px">
          <label>Alasan penolakan (jika ditolak)</label>
          <textarea v-model="rejectReason" rows="3" placeholder="Tulis alasan..."></textarea>
        </div>
      </div>

      <template #footer>
        <button class="btn btn--danger" @click="reject">Tolak</button>
        <button class="btn btn--success" @click="approve">Setujui</button>
      </template>
    </Modal>
  </div>
</template>

<style scoped>
.toolbar {
  display: flex;
  flex-wrap: wrap;
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
.detail {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.detail__row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  font-size: 14px;
  border-bottom: 1px solid var(--border);
  padding-bottom: 8px;
}
</style>
