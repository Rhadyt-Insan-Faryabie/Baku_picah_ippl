<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import StatCard from '../components/StatCard.vue';
import StatusBadge from '../components/StatusBadge.vue';
import { stats, bouts, escrows } from '../services/mockData.js';

const router = useRouter();
const urgentBouts = ref(bouts.slice(0, 4));

const recentLogs = [
  { time: '14:22 WIB', text: 'Deposit Rp150.000 diverifikasi untuk Ardi Saputra (BT-2026-088)', type: 'escrow' },
  { time: '13:50 WIB', text: 'Deni Southpaw mengajukan dokumen KTP & video sparring', type: 'fighter' },
  { time: '11:15 WIB', text: 'Jadwal laga BT-2026-085 dikonfirmasi di GOR Pajajaran', type: 'bout' },
  { time: '09:30 WIB', text: 'Dispute DS-2026-012 dilaporkan oleh Galih Permana', type: 'dispute' },
];
</script>

<template>
  <div class="dash">
    <!-- Header Hero Banner (Figma 3:1256) -->
    <div class="dash__hero">
      <div class="dash__hero-content">
        <div class="dash__tag">CONTROL ROOM · WORKSPACE</div>
        <h1 class="dash__title">CONTROL THE ARENA</h1>
        <p class="dash__desc">
          Pantau verifikasi petarung, jadwal laga aktif, dan status pencairan deposit secara real-time.
        </p>
      </div>
      <div class="dash__hero-actions">
        <button class="btn btn--primary" @click="router.push({ name: 'bouts' })">
          Tinjau Bout (7)
        </button>
        <button class="btn btn--secondary" @click="router.push({ name: 'escrow' })">
          Cek Escrow
        </button>
      </div>
    </div>

    <!-- 4 Metric Cards (Figma 3:1256) -->
    <section class="dash__stats grid">
      <StatCard
        v-for="s in stats"
        :key="s.label"
        :label="s.label"
        :value="s.value"
        :delta="s.delta"
        :tone="s.tone"
      />
    </section>

    <!-- Main Two-Column Layout -->
    <div class="dash__columns">
      <!-- Left: Antrean Verifikasi Mendesak -->
      <section class="dash__main-card card">
        <div class="card__header">
          <div>
            <h2 class="card__title">Antrean Verifikasi Mendesak</h2>
            <p class="card__subtitle muted">Laga yang membutuhkan konfirmasi jadwal dan deposit admin</p>
          </div>
          <router-link :to="{ name: 'bouts' }" class="card__link">
            Lihat Semua →
          </router-link>
        </div>

        <div class="table-responsive">
          <table class="table">
            <thead>
              <tr>
                <th>ID Bout</th>
                <th>Petarung (A vs B)</th>
                <th>Kelas & Venue</th>
                <th>Deposit Escrow</th>
                <th>Status</th>
                <th>Aksi</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="b in urgentBouts" :key="b.id">
                <td class="cell-id"><strong>{{ b.id }}</strong></td>
                <td>
                  <div class="cell-fighters">
                    <span class="fighter-name">{{ b.fighterA }}</span>
                    <span class="vs-pill">VS</span>
                    <span class="fighter-name">{{ b.fighterB }}</span>
                  </div>
                </td>
                <td>
                  <div class="cell-sub">{{ b.weight }}</div>
                  <div class="cell-venue muted">{{ b.location }}</div>
                </td>
                <td>
                  <div class="escrow-pill" :class="{ 'escrow-pill--complete': b.checkEscrowA && b.checkEscrowB }">
                    {{ b.depositTotal }}
                  </div>
                </td>
                <td>
                  <StatusBadge :status="b.status" />
                </td>
                <td>
                  <button class="btn-table-action" @click="router.push({ name: 'bouts' })">
                    Tinjau
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <!-- Right: Log Aktivitas Sistem & Quick Actions -->
      <aside class="dash__side-column">
        <!-- Live System Feed -->
        <div class="card feed-card">
          <div class="card__header">
            <h2 class="card__title">Log Aktivitas Operasional</h2>
            <span class="live-dot" title="Live Event Feed"></span>
          </div>

          <div class="feed-list">
            <div v-for="(log, idx) in recentLogs" :key="idx" class="feed-item">
              <div class="feed-time">{{ log.time }}</div>
              <div class="feed-text">{{ log.text }}</div>
            </div>
          </div>
        </div>

        <!-- Quick Nav Shortcuts -->
        <div class="card quick-card">
          <h2 class="card__title">Aksi Cepat Admin</h2>
          <div class="quick-grid">
            <button class="quick-btn" @click="router.push({ name: 'fighters' })">
              <span class="quick-icon">🥋</span>
              <span>Verifikasi Fighter</span>
            </button>
            <button class="quick-btn" @click="router.push({ name: 'bouts' })">
              <span class="quick-icon">🥊</span>
              <span>Setujui Bout</span>
            </button>
            <button class="quick-btn" @click="router.push({ name: 'escrow' })">
              <span class="quick-icon">💰</span>
              <span>Pencairan Escrow</span>
            </button>
            <button class="quick-btn" @click="router.push({ name: 'arbitration' })">
              <span class="quick-icon">⚖️</span>
              <span>Arbitrase Sengketa</span>
            </button>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<style scoped>
.dash {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.dash__hero {
  background: linear-gradient(135deg, #1c1010 0%, #151515 100%);
  border: 1px solid #331919;
  border-radius: 12px;
  padding: 24px 28px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.dash__tag {
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 0.2em;
  color: #ff4444;
  margin-bottom: 6px;
}

.dash__title {
  font-family: var(--font-display, 'Anton', sans-serif);
  font-size: 32px;
  letter-spacing: 0.04em;
  color: #ffffff;
  margin: 0 0 6px;
}

.dash__desc {
  color: #a8a8a8;
  font-size: 13.5px;
  margin: 0;
  max-width: 600px;
}

.dash__hero-actions {
  display: flex;
  gap: 12px;
  flex-shrink: 0;
}

.btn--secondary {
  background: #242424;
  border: 1px solid #3c3c3c;
  color: #f0f0f0;
}

.btn--secondary:hover {
  background: #303030;
}

.dash__stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.dash__columns {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  align-items: start;
}

.card {
  background: #181818;
  border: 1px solid #282828;
  border-radius: 12px;
  padding: 20px;
}

.card__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.card__title {
  font-size: 16px;
  font-weight: 700;
  color: #f5f5f5;
  margin: 0;
}

.card__subtitle {
  font-size: 12px;
  color: #888888;
  margin: 4px 0 0;
}

.card__link {
  font-size: 12.5px;
  color: #ff4444;
  font-weight: 600;
  text-decoration: none;
}

.card__link:hover {
  text-decoration: underline;
}

/* Table styles */
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
  color: #888888;
  font-size: 11.5px;
  font-weight: 600;
  padding: 10px 12px;
  border-bottom: 1px solid #282828;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.table td {
  padding: 14px 12px;
  border-bottom: 1px solid #222222;
  color: #e0e0e0;
  vertical-align: middle;
}

.cell-id {
  font-family: monospace;
  color: #ff6666;
  font-size: 12.5px;
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
  font-weight: 500;
}

.cell-venue {
  font-size: 11px;
  color: #888888;
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

.btn-table-action {
  background: #262626;
  border: 1px solid #363636;
  color: #ffffff;
  padding: 5px 12px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.15s ease;
}

.btn-table-action:hover {
  background: #8a0303;
  border-color: #b30000;
}

/* Side Column */
.dash__side-column {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.live-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #ff3333;
  box-shadow: 0 0 8px #ff3333;
}

.feed-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.feed-item {
  padding-bottom: 10px;
  border-bottom: 1px solid #222222;
}

.feed-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.feed-time {
  font-size: 11px;
  font-weight: 700;
  color: #888888;
  margin-bottom: 2px;
}

.feed-text {
  font-size: 12.5px;
  color: #d8d8d8;
  line-height: 1.4;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  margin-top: 14px;
}

.quick-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px 10px;
  background: #1f1f1f;
  border: 1px solid #2e2e2e;
  border-radius: 8px;
  color: #e0e0e0;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.15s ease;
}

.quick-btn:hover {
  background: #2a2a2a;
  border-color: #555555;
  color: #ffffff;
}

.quick-icon {
  font-size: 20px;
}

@media (max-width: 1200px) {
  .dash__stats {
    grid-template-columns: repeat(2, 1fr);
  }
  .dash__columns {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 600px) {
  .dash__stats {
    grid-template-columns: 1fr;
  }
  .dash__hero {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
