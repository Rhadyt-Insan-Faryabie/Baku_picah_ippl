<script setup>
import { ref } from 'vue';
import { disputes as seed } from '../services/mockData.js';

const disputesList = ref([...seed]);
const selectedDispute = ref(disputesList.value[0]);
const decisionNote = ref('');
const showSuccessNotice = ref(false);

function selectDispute(item) {
  selectedDispute.value = item;
  showSuccessNotice.value = false;
  decisionNote.value = '';
}

function resolveWithWinner(winnerName) {
  if (!selectedDispute.value) return;
  selectedDispute.value.status = 'Resolved';
  selectedDispute.value.decision = `Pemenang disahkan: ${winnerName}. Escrow dicairkan.`;
  showSuccessNotice.value = true;
}

function resolveNoContest() {
  if (!selectedDispute.value) return;
  selectedDispute.value.status = 'Resolved';
  selectedDispute.value.decision = 'Hasil: No Contest. Escrow dikembalikan 50:50 ke masing-masing fighter.';
  showSuccessNotice.value = true;
}
</script>

<template>
  <div class="arb-view">
    <!-- Header (Figma 3:2114) -->
    <div class="arb__header">
      <div>
        <h1 class="arb__title">Hasil & Sengketa (Arbitrase)</h1>
        <p class="arb__subtitle muted">
          Tinjau rekaman pertandingan, laporan wasit, dan putuskan perselisihan hasil laga sebelum rekor resmi diperbarui
        </p>
      </div>
    </div>

    <!-- Split View (Figma 3:2114) -->
    <div class="arb__split">
      <!-- Left: Active Dispute Tickets -->
      <div class="card arb__list-card">
        <h2 class="card-title">Daftar Sengketa Aktif</h2>
        <div class="tickets-list">
          <div
            v-for="d in disputesList"
            :key="d.id"
            class="ticket-item"
            :class="{ 'ticket-item--active': selectedDispute?.id === d.id }"
            @click="selectDispute(d)"
          >
            <div class="ticket-head">
              <span class="ticket-id">{{ d.id }}</span>
              <span class="ticket-bout muted">{{ d.boutId }}</span>
            </div>
            <div class="ticket-fighters">
              <strong>{{ d.fighterA }}</strong> <span class="muted">vs</span> <strong>{{ d.fighterB }}</strong>
            </div>
            <div class="ticket-meta muted">{{ d.venue }} · {{ d.date }}</div>
            <div class="ticket-badge-row">
              <span class="badge badge--warning">{{ d.status }}</span>
              <span class="escrow-tag">Escrow: {{ d.escrowAmount }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Right: Arbitration Panel (Figma 3:2114) -->
      <aside v-if="selectedDispute" class="card arb__panel-card">
        <div class="panel__head">
          <div>
            <div class="panel__id">{{ selectedDispute.id }} · {{ selectedDispute.boutId }}</div>
            <h2 class="panel__title">{{ selectedDispute.fighterA }} vs {{ selectedDispute.fighterB }}</h2>
            <p class="panel__meta muted">{{ selectedDispute.rules }} · {{ selectedDispute.venue }} ({{ selectedDispute.date }})</p>
          </div>
          <div class="panel__escrow-badge">
            <span class="escrow-label">TOTAL DANA DITAHAN</span>
            <span class="escrow-val">{{ selectedDispute.escrowAmount }}</span>
          </div>
        </div>

        <div v-if="showSuccessNotice" class="alert-success">
          ✓ {{ selectedDispute.decision }}
        </div>

        <!-- Dual Side Comparison: Fighter A vs Fighter B -->
        <div class="comparison-grid">
          <!-- Fighter A Card -->
          <div class="side-card side-card--a">
            <div class="side-head">
              <div class="side-avatar">{{ selectedDispute.fighterA.charAt(0) }}</div>
              <div>
                <div class="side-role">SUDUT MERAH</div>
                <div class="side-name">{{ selectedDispute.fighterA }}</div>
              </div>
            </div>
            <div class="claim-box">
              <div class="claim-label">KLAIM HASIL:</div>
              <div class="claim-text">{{ selectedDispute.claimed }}</div>
            </div>
            <button
              class="btn btn--side btn--side-a"
              :disabled="selectedDispute.status === 'Resolved'"
              @click="resolveWithWinner(selectedDispute.fighterA)"
            >
              Sahkan Pemenang: {{ selectedDispute.fighterA }}
            </button>
          </div>

          <!-- Fighter B Card -->
          <div class="side-card side-card--b">
            <div class="side-head">
              <div class="side-avatar">{{ selectedDispute.fighterB.charAt(0) }}</div>
              <div>
                <div class="side-role">SUDUT BIRU</div>
                <div class="side-name">{{ selectedDispute.fighterB }}</div>
              </div>
            </div>
            <div class="claim-box">
              <div class="claim-label">BANTAHAN / SENGKETA:</div>
              <div class="claim-text">{{ selectedDispute.counter }}</div>
            </div>
            <button
              class="btn btn--side btn--side-b"
              :disabled="selectedDispute.status === 'Resolved'"
              @click="resolveWithWinner(selectedDispute.fighterB)"
            >
              Sahkan Pemenang: {{ selectedDispute.fighterB }}
            </button>
          </div>
        </div>

        <!-- Evidence & Replay Section -->
        <div class="evidence-box">
          <div class="evidence-head">
            <span class="evidence-icon">📹</span>
            <div>
              <div class="evidence-title">Bukti Rekaman Video & Catatan Wasit Ring</div>
              <div class="evidence-sub muted">Diputar pada detik 02:14 ronde kedua saat insiden benturan kepala terjadi</div>
            </div>
          </div>
          <a :href="selectedDispute.evidenceUrl" target="_blank" class="btn btn--secondary btn-sm">
            Putar Video Bukti ↗
          </a>
        </div>

        <!-- Neutral / Draw Decisions -->
        <div class="neutral-actions">
          <button
            class="btn btn--secondary btn--full"
            :disabled="selectedDispute.status === 'Resolved'"
            @click="resolveNoContest"
          >
            Putuskan No Contest (Bagi Escrow 50:50)
          </button>
        </div>
      </aside>
    </div>
  </div>
</template>

<style scoped>
.arb-view {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.arb__title {
  font-family: var(--font-display, 'Anton', sans-serif);
  font-size: 26px;
  letter-spacing: 0.04em;
  color: #ffffff;
  margin: 0;
}

.arb__subtitle {
  font-size: 13px;
  color: #888888;
  margin: 4px 0 0;
}

.arb__split {
  display: grid;
  grid-template-columns: 340px 1fr;
  gap: 20px;
  align-items: start;
}

.card {
  background: #181818;
  border: 1px solid #282828;
  border-radius: 12px;
  padding: 20px;
}

.card-title {
  font-size: 15px;
  font-weight: 700;
  color: #f5f5f5;
  margin: 0 0 14px;
}

.tickets-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.ticket-item {
  background: #141414;
  border: 1px solid #262626;
  border-radius: 8px;
  padding: 12px;
  cursor: pointer;
  transition: all 0.15s ease;
}

.ticket-item:hover {
  background: #1c1c1c;
  border-color: #444444;
}

.ticket-item--active {
  background: rgba(179, 0, 0, 0.14);
  border-color: #b30000;
}

.ticket-head {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.ticket-id {
  font-family: monospace;
  font-weight: 700;
  font-size: 12px;
  color: #ff6666;
}

.ticket-bout {
  font-size: 11px;
}

.ticket-fighters {
  font-size: 13.5px;
  color: #ffffff;
  margin-bottom: 4px;
}

.ticket-meta {
  font-size: 11px;
  margin-bottom: 8px;
}

.ticket-badge-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.escrow-tag {
  font-size: 11px;
  font-weight: 700;
  color: #4ade80;
}

.badge {
  font-size: 10.5px;
  font-weight: 700;
  padding: 2px 7px;
  border-radius: 4px;
}

.badge--warning {
  background: rgba(245, 158, 11, 0.15);
  color: #fbbf24;
  border: 1px solid rgba(245, 158, 11, 0.3);
}

/* Right Panel */
.arb__panel-card {
  position: sticky;
  top: 84px;
}

.panel__head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 18px;
}

.panel__id {
  font-size: 12px;
  font-family: monospace;
  color: #ff6666;
  font-weight: 700;
  margin-bottom: 2px;
}

.panel__title {
  font-size: 20px;
  font-weight: 700;
  color: #ffffff;
  margin: 0;
}

.panel__meta {
  font-size: 12.5px;
  margin: 4px 0 0;
}

.panel__escrow-badge {
  background: #141414;
  border: 1px solid #2e2e2e;
  border-radius: 8px;
  padding: 8px 12px;
  text-align: right;
}

.escrow-label {
  font-size: 10px;
  font-weight: 700;
  color: #888888;
  display: block;
}

.escrow-val {
  font-size: 16px;
  font-weight: 800;
  color: #4ade80;
}

.alert-success {
  background: rgba(34, 197, 94, 0.15);
  border: 1px solid rgba(34, 197, 94, 0.4);
  color: #4ade80;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 13.5px;
  font-weight: 600;
  margin-bottom: 18px;
}

/* Comparison Grid */
.comparison-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 18px;
}

.side-card {
  background: #131313;
  border: 1px solid #262626;
  border-radius: 10px;
  padding: 16px;
  display: flex;
  flex-direction: column;
}

.side-card--a {
  border-top: 3px solid #ff4444;
}

.side-card--b {
  border-top: 3px solid #3b82f6;
}

.side-head {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.side-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #242424;
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  font-size: 14px;
}

.side-role {
  font-size: 10px;
  font-weight: 800;
  letter-spacing: 0.1em;
  color: #888888;
}

.side-name {
  font-size: 15px;
  font-weight: 700;
  color: #ffffff;
}

.claim-box {
  background: #1c1c1c;
  border-radius: 6px;
  padding: 10px 12px;
  margin-bottom: 14px;
  flex: 1;
}

.claim-label {
  font-size: 10.5px;
  font-weight: 700;
  color: #ff6666;
  margin-bottom: 4px;
}

.claim-text {
  font-size: 13px;
  color: #f0f0f0;
  font-weight: 500;
}

.btn--side {
  width: 100%;
  padding: 10px;
  font-size: 12.5px;
  font-weight: 700;
  border-radius: 6px;
  cursor: pointer;
  border: none;
}

.btn--side-a {
  background: #8a0303;
  color: #ffffff;
}

.btn--side-a:hover:not(:disabled) {
  background: #b30000;
}

.btn--side-b {
  background: #1d4ed8;
  color: #ffffff;
}

.btn--side-b:hover:not(:disabled) {
  background: #2563eb;
}

/* Evidence Box */
.evidence-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  background: #141414;
  border: 1px solid #2b2b2b;
  border-radius: 8px;
  padding: 14px 16px;
  margin-bottom: 18px;
}

.evidence-head {
  display: flex;
  align-items: center;
  gap: 12px;
}

.evidence-icon {
  font-size: 22px;
}

.evidence-title {
  font-size: 13px;
  font-weight: 700;
  color: #f5f5f5;
}

.evidence-sub {
  font-size: 11.5px;
  color: #888888;
}

.btn--full {
  width: 100%;
  justify-content: center;
}

.neutral-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

@media (max-width: 1000px) {
  .arb__split {
    grid-template-columns: 1fr;
  }
  .comparison-grid {
    grid-template-columns: 1fr;
  }
}
</style>
