<script setup>
import { computed, ref, onMounted } from 'vue';
import StatusBadge from '../components/StatusBadge.vue';
import { bouts as seed } from '../services/mockData.js';
import api from '../services/api.js';

const boutsList = ref([...seed]);
const filter = ref('Awaiting Admin Verification');
const selectedBout = ref(boutsList.value[0]);
const showCancelModal = ref(false);
const cancelReason = ref('');
const loading = ref(false);

const filters = [
  'Semua',
  'Awaiting Admin Verification',
  'Opponent Accepted',
  'Confirmed',
  'Completed',
];

function normalizeStatus(st) {
  if (st === 'pending') return 'Opponent Accepted';
  if (st === 'accepted') return 'Opponent Accepted';
  if (st === 'awaiting_admin') return 'Awaiting Admin Verification';
  if (st === 'approved' || st === 'confirmed') return 'Confirmed';
  if (st === 'completed') return 'Completed';
  if (st === 'rejected' || st === 'cancelled') return 'Cancelled';
  return st;
}

function formatApiBout(b) {
  return {
    id: `BT-${String(b.id).padStart(4, '0')}`,
    rawId: b.id,
    fighterA: b.challenger?.full_name || b.challenger?.username || `Fighter #${b.challenger_id}`,
    fighterB: b.opponent?.full_name || b.opponent?.username || `Fighter #${b.opponent_id}`,
    fighterAId: b.challenger_id,
    fighterBId: b.opponent_id,
    weight: b.weight_class ? `${b.weight_class} (${b.challenger?.weight_kg || 70} kg)` : 'Catchweight (70 kg)',
    rules: `${b.rules || 'MMA'} / 3 Ronde x 3 Menit`,
    location: b.venue_name || b.location || 'GOR Cikutra Arena, Bandung',
    date: b.scheduled_at ? new Date(b.scheduled_at).toLocaleString('id-ID') : 'Menunggu Jadwal',
    depositA: b.challenger_deposit_paid ? `Rp${(b.deposit_amount || 150000).toLocaleString('id-ID')} (Lunas)` : 'Belum Bayar',
    depositB: b.opponent_deposit_paid ? `Rp${(b.deposit_amount || 150000).toLocaleString('id-ID')} (Lunas)` : 'Belum Bayar',
    depositTotal: `Rp${((b.deposit_amount || 150000) * 2).toLocaleString('id-ID')}`,
    status: normalizeStatus(b.status),
    checkWeightAgreed: true,
    checkVenueReady: Boolean(b.venue_name || b.location),
    checkEscrowA: b.challenger_deposit_paid,
    checkEscrowB: b.opponent_deposit_paid,
    notes: b.notes || 'Pertandingan terdaftar dalam sistem.',
  };
}

async function loadBouts() {
  try {
    loading.value = true;
    const res = await api.get('/bout/');
    if (res.data && res.data.length > 0) {
      const apiBouts = res.data.map(formatApiBout);
      boutsList.value = [...apiBouts, ...seed];
      selectedBout.value = boutsList.value[0];
    }
  } catch (err) {
    console.warn('Menggunakan data mock:', err.message);
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  loadBouts();
});

const filteredBouts = computed(() => {
  if (filter.value === 'Semua') return boutsList.value;
  return boutsList.value.filter((b) => b.status === filter.value);
});

function selectBout(bout) {
  selectedBout.value = bout;
}

async function approveBout() {
  if (!selectedBout.value) return;
  if (selectedBout.value.rawId) {
    try {
      await api.post(`/bout/${selectedBout.value.rawId}/approve`);
    } catch (err) {
      console.error('Gagal approve di backend:', err);
    }
  }
  selectedBout.value.status = 'Confirmed';
  selectedBout.value.checkWeightAgreed = true;
  selectedBout.value.checkVenueReady = true;
  selectedBout.value.checkEscrowA = true;
  selectedBout.value.checkEscrowB = true;

  const idx = boutsList.value.findIndex((b) => b.id === selectedBout.value.id);
  if (idx !== -1) {
    boutsList.value[idx].status = 'Confirmed';
  }
}

async function cancelBout() {
  if (!selectedBout.value || !cancelReason.value.trim()) return;
  if (selectedBout.value.rawId) {
    try {
      await api.post(`/bout/${selectedBout.value.rawId}/reject`, {
        rejection_reason: cancelReason.value.trim(),
      });
    } catch (err) {
      console.error('Gagal reject di backend:', err);
    }
  }
  selectedBout.value.status = 'Cancelled';
  const idx = boutsList.value.findIndex((b) => b.id === selectedBout.value.id);
  if (idx !== -1) {
    boutsList.value[idx].status = 'Cancelled';
  }
  showCancelModal.value = false;
  cancelReason.value = '';
}
</script>

<template>
  <div class="bv">
    <!-- Header (Figma 3:1697) -->
    <div class="bv__header">
      <div>
        <h1 class="bv__title">Manajemen Bout</h1>
        <p class="bv__subtitle muted">
          Verifikasi kelayakan laga, kesepakatan berat, izin venue, dan escrow deposit sebelum jadwal dikunci
        </p>
      </div>
    </div>

    <!-- Filter toolbar -->
    <div class="toolbar">
      <button
        v-for="f in filters"
        :key="f"
        class="chip"
        :class="{ 'chip--active': filter === f }"
        @click="filter = f"
      >
        {{ f === 'Awaiting Admin Verification' ? 'Menunggu Verifikasi Admin' : f === 'Opponent Accepted' ? 'Menunggu Deposit' : f === 'Confirmed' ? 'Jadwal Terkunci' : f === 'Completed' ? 'Selesai' : 'Semua' }}
      </button>
    </div>

    <!-- Split Layout (Figma 3:1697) -->
    <div class="bv__split">
      <!-- Left: Bouts Table -->
      <div class="card bv__table-card">
        <div class="table-responsive">
          <table class="table">
            <thead>
              <tr>
                <th>ID Bout</th>
                <th>Petarung (A vs B)</th>
                <th>Kelas & Aturan</th>
                <th>Deposit Escrow</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="b in filteredBouts"
                :key="b.id"
                :class="{ 'row--selected': selectedBout?.id === b.id }"
                @click="selectBout(b)"
              >
                <td class="cell-id"><strong>{{ b.id }}</strong></td>
                <td>
                  <div class="cell-fighters">
                    <span class="fighter-name">{{ b.fighterA }}</span>
                    <span class="vs-pill">VS</span>
                    <span class="fighter-name">{{ b.fighterB }}</span>
                  </div>
                </td>
                <td>
                  <div>{{ b.weight }}</div>
                  <div class="cell-sub muted">{{ b.rules }}</div>
                </td>
                <td>
                  <div class="escrow-pill" :class="{ 'escrow-pill--complete': b.checkEscrowA && b.checkEscrowB }">
                    {{ b.depositTotal }}
                  </div>
                </td>
                <td>
                  <StatusBadge :status="b.status" />
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Right: 4-Phase Verification Checklist Panel (Figma 3:1697) -->
      <aside v-if="selectedBout" class="card bv__review-card">
        <div class="review__head">
          <div>
            <div class="review__id">{{ selectedBout.id }}</div>
            <h2 class="review__title">{{ selectedBout.fighterA }} vs {{ selectedBout.fighterB }}</h2>
            <p class="review__meta muted">{{ selectedBout.location }} · {{ selectedBout.date }}</p>
          </div>
          <StatusBadge :status="selectedBout.status" />
        </div>

        <hr class="divider" />

        <h3 class="section-title">Checklist Verifikasi Kelayakan Laga</h3>

        <!-- 4-Phase Checkpoints -->
        <div class="checklist">
          <!-- Item 1: Weight & Rules -->
          <label class="check-item">
            <input type="checkbox" v-model="selectedBout.checkWeightAgreed" class="check-input" />
            <div class="check-content">
              <div class="check-label">1. Kesepakatan Berat & Rule Set</div>
              <div class="check-sub muted">
                {{ selectedBout.weight }} · Aturan: {{ selectedBout.rules }}
              </div>
            </div>
          </label>

          <!-- Item 2: Venue & Medical -->
          <label class="check-item">
            <input type="checkbox" v-model="selectedBout.checkVenueReady" class="check-input" />
            <div class="check-content">
              <div class="check-label">2. Ketersediaan Venue & Medis</div>
              <div class="check-sub muted">
                {{ selectedBout.location }} (Wasit & Tim Medis Terkonfirmasi)
              </div>
            </div>
          </label>

          <!-- Item 3: Escrow Fighter A -->
          <label class="check-item">
            <input type="checkbox" v-model="selectedBout.checkEscrowA" class="check-input" />
            <div class="check-content">
              <div class="check-label">3. Deposit Escrow {{ selectedBout.fighterA }}</div>
              <div class="check-sub muted">
                {{ selectedBout.depositA }} — Rekening Escrow Baku Picah
              </div>
            </div>
          </label>

          <!-- Item 4: Escrow Fighter B -->
          <label class="check-item">
            <input type="checkbox" v-model="selectedBout.checkEscrowB" class="check-input" />
            <div class="check-content">
              <div class="check-label">4. Deposit Escrow {{ selectedBout.fighterB }}</div>
              <div class="check-sub muted">
                {{ selectedBout.depositB }} — Rekening Escrow Baku Picah
              </div>
            </div>
          </label>
        </div>

        <!-- Notes / Catatan -->
        <div class="notes-box">
          <div class="notes-title">Catatan Verifikator:</div>
          <p class="notes-text">{{ selectedBout.notes }}</p>
        </div>

        <!-- Actions -->
        <div class="review__actions">
          <button
            class="btn btn--primary btn--full"
            :disabled="selectedBout.status === 'Confirmed' || selectedBout.status === 'Completed'"
            @click="approveBout"
          >
            {{ selectedBout.status === 'Confirmed' ? '✓ Jadwal Terkunci' : 'Setujui Laga & Kunci Jadwal' }}
          </button>
          <button
            class="btn btn--danger btn--full"
            :disabled="selectedBout.status === 'Cancelled' || selectedBout.status === 'Completed'"
            @click="showCancelModal = true"
          >
            Batalkan / Tolak Laga
          </button>
        </div>
      </aside>
    </div>

    <!-- Cancel Modal -->
    <div v-if="showCancelModal" class="modal-backdrop" @click="showCancelModal = false">
      <div class="modal-card" @click.stop>
        <h2 class="modal-title">Batalkan Pengajuan Laga</h2>
        <p class="modal-desc muted">Tuliskan alasan pembatalan agar escrow dapat dikembalikan kepada kedua belah pihak.</p>
        <textarea
          v-model="cancelReason"
          rows="4"
          class="modal-textarea"
          placeholder="Contoh: Salah satu petarung mengalami cedera saat latihan atau venue tidak tersedia..."
        ></textarea>
        <div class="modal-actions">
          <button class="btn btn--secondary" @click="showCancelModal = false">Kembali</button>
          <button class="btn btn--danger" @click="cancelBout">Konfirmasi Pembatalan</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.bv {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.bv__title {
  font-family: var(--font-display, 'Anton', sans-serif);
  font-size: 26px;
  letter-spacing: 0.04em;
  color: #ffffff;
  margin: 0;
}

.bv__subtitle {
  font-size: 13px;
  color: #888888;
  margin: 4px 0 0;
}

/* Toolbar */
.toolbar {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.chip {
  background: #181818;
  border: 1px solid #2e2e2e;
  color: #888888;
  padding: 8px 14px;
  border-radius: 6px;
  font-size: 12.5px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.15s ease;
}

.chip:hover {
  background: #242424;
  color: #ffffff;
}

.chip--active {
  background: #282828;
  color: #ffffff;
  border-color: #555555;
}

/* Split View */
.bv__split {
  display: grid;
  grid-template-columns: 1fr 420px;
  gap: 20px;
  align-items: start;
}

.card {
  background: #181818;
  border: 1px solid #282828;
  border-radius: 12px;
  padding: 20px;
}

.bv__table-card {
  padding: 0;
  overflow: hidden;
}

.table-responsive {
  overflow-x: auto;
}

.table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
  text-align: left;
}

.table th {
  background: #1e1e1e;
  color: #888888;
  font-size: 11.5px;
  font-weight: 600;
  padding: 12px 14px;
  border-bottom: 1px solid #282828;
  text-transform: uppercase;
}

.table td {
  padding: 14px 14px;
  border-bottom: 1px solid #222222;
  color: #e0e0e0;
  cursor: pointer;
}

.table tbody tr:hover {
  background: #202020;
}

.table tbody tr.row--selected {
  background: rgba(179, 0, 0, 0.12);
  border-left: 3px solid #b30000;
}

.cell-id {
  font-family: monospace;
  color: #ff6666;
}

.cell-fighters {
  display: flex;
  align-items: center;
  gap: 6px;
}

.fighter-name {
  font-weight: 600;
  color: #ffffff;
}

.vs-pill {
  font-size: 10px;
  font-weight: 800;
  color: #888888;
  background: #242424;
  padding: 2px 6px;
  border-radius: 4px;
}

.cell-sub {
  font-size: 11.5px;
}

.escrow-pill {
  display: inline-block;
  font-size: 11.5px;
  font-weight: 700;
  padding: 3px 8px;
  border-radius: 4px;
  background: #262626;
  color: #f5f5f5;
  border: 1px solid #363636;
}

.escrow-pill--complete {
  background: rgba(34, 197, 94, 0.12);
  color: #4ade80;
  border-color: rgba(34, 197, 94, 0.3);
}

/* Review Panel */
.bv__review-card {
  position: sticky;
  top: 84px;
}

.review__head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 14px;
}

.review__id {
  font-size: 12px;
  font-family: monospace;
  color: #ff6666;
  font-weight: 700;
  margin-bottom: 2px;
}

.review__title {
  font-size: 17px;
  font-weight: 700;
  color: #ffffff;
  margin: 0;
}

.review__meta {
  font-size: 12px;
  color: #888888;
  margin: 4px 0 0;
}

.divider {
  border: none;
  border-top: 1px solid #262626;
  margin: 16px 0;
}

.section-title {
  font-size: 13.5px;
  font-weight: 700;
  color: #f5f5f5;
  margin: 0 0 14px;
}

.checklist {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 16px;
}

.check-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 10px 12px;
  background: #141414;
  border: 1px solid #262626;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.15s ease;
}

.check-item:hover {
  border-color: #444444;
}

.check-input {
  margin-top: 3px;
  accent-color: #b30000;
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.check-label {
  font-size: 13px;
  font-weight: 600;
  color: #ffffff;
}

.check-sub {
  font-size: 11px;
  color: #888888;
  margin-top: 2px;
}

.notes-box {
  background: #1e1515;
  border: 1px solid #3d1c1c;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 18px;
}

.notes-title {
  font-size: 11px;
  font-weight: 700;
  color: #ff6666;
  margin-bottom: 4px;
}

.notes-text {
  font-size: 12px;
  color: #d1d1d1;
  margin: 0;
}

.review__actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.btn--full {
  width: 100%;
  justify-content: center;
}

.btn--danger {
  background: #331515;
  border: 1px solid #551c1c;
  color: #ff6666;
}

.btn--danger:hover:not(:disabled) {
  background: #4a1c1c;
}

/* Modal */
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.75);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  padding: 20px;
}

.modal-card {
  background: #1c1c1c;
  border: 1px solid #333333;
  border-radius: 12px;
  padding: 24px;
  width: 100%;
  max-width: 440px;
}

.modal-title {
  font-size: 17px;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 6px;
}

.modal-desc {
  font-size: 12.5px;
  color: #888888;
  margin: 0 0 16px;
}

.modal-textarea {
  width: 100%;
  background: #141414;
  border: 1px solid #333333;
  border-radius: 8px;
  color: #f5f5f5;
  padding: 10px 12px;
  font-size: 13px;
  outline: none;
  resize: vertical;
  margin-bottom: 16px;
}

.modal-textarea:focus {
  border-color: #b30000;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 1100px) {
  .bv__split {
    grid-template-columns: 1fr;
  }
}
</style>
