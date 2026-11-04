<script setup>
import { ref, computed } from 'vue';
import { escrows as seed } from '../services/mockData.js';

const escrowsList = ref([...seed]);
const filter = ref('Semua');
const selectedReceipt = ref(null);

const filters = ['Semua', 'Menunggu Verifikasi', 'Verified', 'Dicairkan'];

const filteredEscrows = computed(() => {
  if (filter.value === 'Semua') return escrowsList.value;
  return escrowsList.value.filter((e) => e.status === filter.value);
});

function openReceipt(item) {
  selectedReceipt.value = item;
}

function verifyDeposit(item) {
  item.status = 'Verified';
  selectedReceipt.value = null;
}

function releaseDeposit(item) {
  item.status = 'Dicairkan';
  selectedReceipt.value = null;
}
</script>

<template>
  <div class="escrow-view">
    <!-- Header (Figma 3:1905) -->
    <div class="escrow__header">
      <div>
        <h1 class="escrow__title">Deposit & Escrow Ledger</h1>
        <p class="escrow__subtitle muted">
          Pengawasan saldo rekening penampung bersama, verifikasi bukti transfer petarung, dan pencairan dana laga
        </p>
      </div>
      <button class="btn btn--secondary" @click="filter = 'Menunggu Verifikasi'">
        ⚡ Cek Menunggu Verifikasi
      </button>
    </div>

    <!-- Escrow Summary Cards (Figma 3:1905) -->
    <div class="escrow__stats grid">
      <div class="stat-box">
        <div class="stat-box__label">TOTAL ESCROW DITAHAN</div>
        <div class="stat-box__val text-danger">Rp3.600.000</div>
        <div class="stat-box__sub muted">12 laga aktif dalam sistem</div>
      </div>
      <div class="stat-box">
        <div class="stat-box__label">MENUNGGU VERIFIKASI</div>
        <div class="stat-box__val text-warning">2 Bukti</div>
        <div class="stat-box__sub muted">Segera cocokan dengan mutasi</div>
      </div>
      <div class="stat-box">
        <div class="stat-box__label">SIAP DICAIRKAN</div>
        <div class="stat-box__val text-success">Rp600.000</div>
        <div class="stat-box__sub muted">2 laga selesai tanpa sengketa</div>
      </div>
      <div class="stat-box">
        <div class="stat-box__label">DITAHAN KARENA SENGKETA</div>
        <div class="stat-box__val text-info">Rp600.000</div>
        <div class="stat-box__sub muted">Menunggu keputusan arbitrase</div>
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
        {{ f }}
      </button>
    </div>

    <!-- Escrow Ledger Table -->
    <div class="card table-card">
      <div class="table-responsive">
        <table class="table">
          <thead>
            <tr>
              <th>ID Transaksi</th>
              <th>ID Bout</th>
              <th>Fighter</th>
              <th>Nominal</th>
              <th>Metode Pembayaran</th>
              <th>Waktu Upload</th>
              <th>Bukti Bayar</th>
              <th>Status</th>
              <th>Aksi</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="e in filteredEscrows" :key="e.id">
              <td class="cell-id"><strong>{{ e.id }}</strong></td>
              <td class="muted">{{ e.boutId }}</td>
              <td><strong>{{ e.fighterName }}</strong></td>
              <td class="cell-amount">{{ e.amount }}</td>
              <td>
                <span class="method-pill">{{ e.method }}</span>
              </td>
              <td class="muted cell-time">{{ e.date }}</td>
              <td>
                <button class="btn-receipt-preview" @click="openReceipt(e)">
                  👁️ Lihat Struk
                </button>
              </td>
              <td>
                <span
                  class="badge"
                  :class="{
                    'badge--success': e.status === 'Verified' || e.status === 'Dicairkan',
                    'badge--warning': e.status === 'Menunggu Verifikasi',
                  }"
                >
                  {{ e.status }}
                </span>
              </td>
              <td>
                <button
                  v-if="e.status === 'Menunggu Verifikasi'"
                  class="btn btn--primary btn-sm"
                  @click="verifyDeposit(e)"
                >
                  Verifikasi
                </button>
                <button
                  v-else-if="e.status === 'Verified'"
                  class="btn btn--secondary btn-sm"
                  @click="releaseDeposit(e)"
                >
                  Cairkan
                </button>
                <span v-else class="muted text-sm">Selesai</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Receipt Preview Modal -->
    <div v-if="selectedReceipt" class="modal-backdrop" @click="selectedReceipt = null">
      <div class="modal-card" @click.stop>
        <div class="modal-head">
          <div>
            <h2 class="modal-title">Bukti Transfer Escrow</h2>
            <p class="modal-desc muted">{{ selectedReceipt.id }} · {{ selectedReceipt.boutId }}</p>
          </div>
          <span
            class="badge"
            :class="{
              'badge--success': selectedReceipt.status === 'Verified',
              'badge--warning': selectedReceipt.status === 'Menunggu Verifikasi',
            }"
          >
            {{ selectedReceipt.status }}
          </span>
        </div>

        <div class="receipt-mock">
          <div class="receipt-bank">BANK CENTRAL ASIA / QRIS NASIONAL</div>
          <div class="receipt-val">{{ selectedReceipt.amount }}</div>
          <div class="receipt-row">
            <span class="muted">Pengirim</span>
            <span>{{ selectedReceipt.fighterName }}</span>
          </div>
          <div class="receipt-row">
            <span class="muted">Rekening Tujuan</span>
            <span>Baku Picah Escrow Pool (BCA 7720-192-881)</span>
          </div>
          <div class="receipt-row">
            <span class="muted">Metode</span>
            <span>{{ selectedReceipt.method }}</span>
          </div>
          <div class="receipt-row">
            <span class="muted">Waktu Transaksi</span>
            <span>{{ selectedReceipt.date }}</span>
          </div>
          <div class="receipt-row">
            <span class="muted">No. Referensi</span>
            <span class="ref-code">TRX-20261015-8839201</span>
          </div>
        </div>

        <div class="modal-actions">
          <button class="btn btn--secondary" @click="selectedReceipt = null">Tutup</button>
          <button
            v-if="selectedReceipt.status === 'Menunggu Verifikasi'"
            class="btn btn--primary"
            @click="verifyDeposit(selectedReceipt)"
          >
            Verifikasi Penerimaan Dana
          </button>
          <button
            v-else-if="selectedReceipt.status === 'Verified'"
            class="btn btn--primary"
            @click="releaseDeposit(selectedReceipt)"
          >
            Cairkan Escrow ke Fighter
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.escrow-view {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.escrow__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.escrow__title {
  font-family: var(--font-display, 'Anton', sans-serif);
  font-size: 26px;
  letter-spacing: 0.04em;
  color: #ffffff;
  margin: 0;
}

.escrow__subtitle {
  font-size: 13px;
  color: #888888;
  margin: 4px 0 0;
}

/* Stats */
.escrow__stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-box {
  background: #181818;
  border: 1px solid #282828;
  border-radius: 10px;
  padding: 16px 18px;
}

.stat-box__label {
  font-size: 10.5px;
  font-weight: 700;
  color: #888888;
  letter-spacing: 0.05em;
  margin-bottom: 6px;
}

.stat-box__val {
  font-size: 22px;
  font-weight: 800;
  margin-bottom: 4px;
}

.stat-box__sub {
  font-size: 11.5px;
}

.text-danger { color: #ff5252; }
.text-warning { color: #fbbf24; }
.text-success { color: #4ade80; }
.text-info { color: #60a5fa; }

/* Toolbar */
.toolbar {
  display: flex;
  gap: 8px;
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
}

.chip--active {
  background: #282828;
  color: #ffffff;
  border-color: #555555;
}

/* Table */
.card {
  background: #181818;
  border: 1px solid #282828;
  border-radius: 12px;
}

.table-card {
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
}

.cell-id {
  font-family: monospace;
  color: #ff6666;
}

.cell-amount {
  font-weight: 700;
  color: #4ade80;
}

.method-pill {
  font-size: 11.5px;
  padding: 3px 8px;
  background: #222222;
  border: 1px solid #333333;
  border-radius: 4px;
  color: #d1d1d1;
}

.cell-time {
  font-size: 11.5px;
}

.btn-receipt-preview {
  background: #252525;
  border: 1px solid #353535;
  color: #f0f0f0;
  padding: 5px 10px;
  border-radius: 6px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.15s ease;
}

.btn-receipt-preview:hover {
  background: #333333;
}

.badge {
  font-size: 11px;
  font-weight: 700;
  padding: 3px 8px;
  border-radius: 4px;
  display: inline-block;
}

.badge--success {
  background: rgba(34, 197, 94, 0.12);
  color: #4ade80;
  border: 1px solid rgba(34, 197, 94, 0.3);
}

.badge--warning {
  background: rgba(245, 158, 11, 0.12);
  color: #fbbf24;
  border: 1px solid rgba(245, 158, 11, 0.3);
}

.btn-sm {
  padding: 4px 12px;
  font-size: 12px;
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
  max-width: 480px;
}

.modal-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.modal-title {
  font-size: 17px;
  font-weight: 700;
  color: #ffffff;
  margin: 0;
}

.modal-desc {
  font-size: 12px;
  color: #888888;
  margin: 2px 0 0;
}

.receipt-mock {
  background: #131313;
  border: 1px dashed #3a3a3a;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 20px;
}

.receipt-bank {
  font-size: 11px;
  font-weight: 800;
  color: #888888;
  letter-spacing: 0.05em;
  text-align: center;
  margin-bottom: 8px;
}

.receipt-val {
  font-size: 26px;
  font-weight: 800;
  color: #4ade80;
  text-align: center;
  margin-bottom: 16px;
  border-bottom: 1px solid #222222;
  padding-bottom: 12px;
}

.receipt-row {
  display: flex;
  justify-content: space-between;
  padding: 5px 0;
  font-size: 12.5px;
}

.ref-code {
  font-family: monospace;
  color: #ff6666;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 1000px) {
  .escrow__stats {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 600px) {
  .escrow__stats {
    grid-template-columns: 1fr;
  }
}
</style>
