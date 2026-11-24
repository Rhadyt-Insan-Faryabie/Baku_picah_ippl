<script setup>
import { ref, computed, onMounted } from 'vue';
import StatusBadge from '../components/StatusBadge.vue';
import { fighters as seed } from '../services/mockData.js';
import api from '../services/api.js';

const fightersList = ref([...seed]);
const currentFilter = ref('Menunggu Verifikasi');
const searchQuery = ref('');
const selectedFighter = ref(fightersList.value[1] || fightersList.value[0]);
const rejectReason = ref('');
const showRejectModal = ref(false);

const filterTabs = [
  { label: 'Semua', count: 128 },
  { label: 'Menunggu Verifikasi', count: 12 },
  { label: 'Terverifikasi', count: 112 },
  { label: 'Ditolak', count: 4 },
];

async function loadRealFighters() {
  try {
    const res = await api.get('/fighters/list');
    if (res.data && res.data.length > 0) {
      const mapped = res.data.map((f) => ({
        id: f.id,
        name: f.full_name || f.username,
        nickname: f.username,
        gym: f.city ? `${f.city} Martial Arts` : 'Baku Picah Training Camp',
        weightClass: f.weight_class || 'Catchweight',
        weightKg: f.weight_kg || 70,
        record: `${f.wins || 0}-${f.losses || 0}-${f.draws || 0}`,
        status: 'Awaiting Verification',
        idCardUrl: '/mock/ktp_rambo.jpg',
        medicalCertUrl: '/mock/med_rambo.pdf',
        sparringVideoUrl: 'https://youtube.com',
        joinedDate: '2026-11-20',
        phone: '0812-3456-7890',
      }));
      fightersList.value = [...mapped, ...seed];
      selectedFighter.value = fightersList.value[0];
    }
  } catch (err) {
    console.warn('Menggunakan data mock petarung:', err.message);
  }
}

onMounted(() => {
  loadRealFighters();
});

const filteredFighters = computed(() => {
  return fightersList.value.filter((f) => {
    // Tab filter
    if (currentFilter.value === 'Menunggu Verifikasi' && f.status !== 'Awaiting Verification') return false;
    if (currentFilter.value === 'Terverifikasi' && f.status !== 'Verified') return false;
    if (currentFilter.value === 'Ditolak' && f.status !== 'Rejected') return false;

    // Search query
    if (!searchQuery.value) return true;
    const q = searchQuery.value.toLowerCase();
    return (
      f.name.toLowerCase().includes(q) ||
      f.nickname.toLowerCase().includes(q) ||
      f.gym.toLowerCase().includes(q)
    );
  });
});

function selectFighter(fighter) {
  selectedFighter.value = fighter;
}

function approveFighter() {
  if (!selectedFighter.value) return;
  selectedFighter.value.status = 'Verified';
  const idx = fightersList.value.findIndex((f) => f.id === selectedFighter.value.id);
  if (idx !== -1) {
    fightersList.value[idx].status = 'Verified';
  }
}

function confirmReject() {
  if (!selectedFighter.value || !rejectReason.value.trim()) return;
  selectedFighter.value.status = 'Rejected';
  const idx = fightersList.value.findIndex((f) => f.id === selectedFighter.value.id);
  if (idx !== -1) {
    fightersList.value[idx].status = 'Rejected';
  }
  showRejectModal.value = false;
  rejectReason.value = '';
}
</script>

<template>
  <div class="fv">
    <!-- Header & Filter Tabs (Figma 3:1472) -->
    <div class="fv__header">
      <div>
        <h1 class="fv__title">Verifikasi Fighter</h1>
        <p class="fv__subtitle muted">Tinjau kelengkapan berkas identitas, surat sehat medis, dan bukti keahlian petarung</p>
      </div>

      <div class="fv__search">
        <input
          v-model="searchQuery"
          type="text"
          placeholder="Cari nama, julukan, atau sasana..."
          class="fv__search-input"
        />
      </div>
    </div>

    <!-- Filter Tab Bar -->
    <div class="tabs-bar">
      <button
        v-for="tab in filterTabs"
        :key="tab.label"
        class="tab-btn"
        :class="{ 'tab-btn--active': currentFilter === tab.label }"
        @click="currentFilter = tab.label"
      >
        <span>{{ tab.label }}</span>
        <span class="tab-badge">{{ tab.count }}</span>
      </button>
    </div>

    <!-- Split View: Table on Left, Detail Panel on Right (Figma 3:1472) -->
    <div class="fv__split">
      <!-- Left: Table of Fighters -->
      <div class="card fv__table-card">
        <div class="table-responsive">
          <table class="table">
            <thead>
              <tr>
                <th>Petarung</th>
                <th>Sasana</th>
                <th>Kelas</th>
                <th>Rekor</th>
                <th>Status</th>
                <th>Aksi</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="f in filteredFighters"
                :key="f.id"
                :class="{ 'row--selected': selectedFighter?.id === f.id }"
                @click="selectFighter(f)"
              >
                <td>
                  <div class="cell-fighter">
                    <div class="fighter-avatar">{{ f.name.charAt(0) }}</div>
                    <div>
                      <div class="fighter-name">{{ f.name }}</div>
                      <div class="fighter-nickname muted">"{{ f.nickname }}"</div>
                    </div>
                  </div>
                </td>
                <td class="muted">{{ f.gym }}</td>
                <td>{{ f.weightClass }} ({{ f.weightKg }} kg)</td>
                <td><span class="record-pill">{{ f.record }}</span></td>
                <td>
                  <span
                    class="badge"
                    :class="{
                      'badge--success': f.status === 'Verified',
                      'badge--warning': f.status === 'Awaiting Verification',
                      'badge--danger': f.status === 'Rejected',
                    }"
                  >
                    {{ f.status === 'Verified' ? 'Terverifikasi' : f.status === 'Awaiting Verification' ? 'Menunggu Review' : 'Ditolak' }}
                  </span>
                </td>
                <td>
                  <button class="btn-inspect" @click.stop="selectFighter(f)">
                    Periksa
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Right: Detailed Verification Panel -->
      <aside v-if="selectedFighter" class="card fv__detail-card">
        <div class="detail__head">
          <div class="detail__profile">
            <div class="detail__avatar">{{ selectedFighter.name.charAt(0) }}</div>
            <div>
              <h2 class="detail__name">{{ selectedFighter.name }}</h2>
              <p class="detail__sub muted">"{{ selectedFighter.nickname }}" · {{ selectedFighter.gym }}</p>
            </div>
          </div>
          <span
            class="badge"
            :class="{
              'badge--success': selectedFighter.status === 'Verified',
              'badge--warning': selectedFighter.status === 'Awaiting Verification',
              'badge--danger': selectedFighter.status === 'Rejected',
            }"
          >
            {{ selectedFighter.status }}
          </span>
        </div>

        <div class="detail__stats-row">
          <div class="stat-mini">
            <span class="stat-mini__label">KELAS BERAT</span>
            <span class="stat-mini__value">{{ selectedFighter.weightClass }}</span>
          </div>
          <div class="stat-mini">
            <span class="stat-mini__label">BERAT AKTUAL</span>
            <span class="stat-mini__value">{{ selectedFighter.weightKg }} kg</span>
          </div>
          <div class="stat-mini">
            <span class="stat-mini__label">REKOR BERTANDING</span>
            <span class="stat-mini__value">{{ selectedFighter.record }}</span>
          </div>
          <div class="stat-mini">
            <span class="stat-mini__label">TELEPON</span>
            <span class="stat-mini__value">{{ selectedFighter.phone }}</span>
          </div>
        </div>

        <hr class="divider" />

        <!-- Document Inspection Checklist -->
        <h3 class="section-title">Dokumen & Berkas Wajib</h3>

        <div class="docs-list">
          <!-- KTP Review Card -->
          <div class="doc-card">
            <div class="doc-card__info">
              <div class="doc-icon">🪪</div>
              <div>
                <div class="doc-name">Kartu Tanda Penduduk (KTP)</div>
                <div class="doc-meta muted">Verifikasi usia >= 18 tahun & domisili Bandung</div>
              </div>
            </div>
            <a href="javascript:void(0)" class="doc-link">Pratinjau KTP ↗</a>
          </div>

          <!-- Medical Certificate Card -->
          <div class="doc-card">
            <div class="doc-card__info">
              <div class="doc-icon">🩺</div>
              <div>
                <div class="doc-name">Surat Keterangan Sehat Medis</div>
                <div class="doc-meta muted">Bebas penyakit jantung & cidera fatal (Maks 30 hari)</div>
              </div>
            </div>
            <a href="javascript:void(0)" class="doc-link">Lihat Surat ↗</a>
          </div>

          <!-- Sparring Video Card -->
          <div class="doc-card">
            <div class="doc-card__info">
              <div class="doc-icon">🎬</div>
              <div>
                <div class="doc-name">Video Rekaman Sparring</div>
                <div class="doc-meta muted">Bukti kesiapan bertanding & teknik dasar beladiri</div>
              </div>
            </div>
            <a :href="selectedFighter.sparringVideoUrl" target="_blank" class="doc-link">Buka Video ↗</a>
          </div>
        </div>

        <!-- Action CTAs -->
        <div class="detail__actions">
          <button
            class="btn btn--primary btn--full"
            :disabled="selectedFighter.status === 'Verified'"
            @click="approveFighter"
          >
            {{ selectedFighter.status === 'Verified' ? '✓ Telah Terverifikasi' : 'Setujui & Verifikasi Fighter' }}
          </button>
          <button
            class="btn btn--danger btn--full"
            :disabled="selectedFighter.status === 'Rejected'"
            @click="showRejectModal = true"
          >
            Tolak Dokumen
          </button>
        </div>
      </aside>
    </div>

    <!-- Reject Reason Modal -->
    <div v-if="showRejectModal" class="modal-backdrop" @click="showRejectModal = false">
      <div class="modal-card" @click.stop>
        <h2 class="modal-title">Tolak Verifikasi Dokumen</h2>
        <p class="modal-desc muted">Tuliskan alasan penolakan agar petarung dapat mengunggah ulang dokumen yang valid.</p>
        <textarea
          v-model="rejectReason"
          rows="4"
          class="modal-textarea"
          placeholder="Contoh: Surat keterangan sehat medis sudah kedaluwarsa lebih dari 30 hari..."
        ></textarea>
        <div class="modal-actions">
          <button class="btn btn--secondary" @click="showRejectModal = false">Batal</button>
          <button class="btn btn--danger" @click="confirmReject">Konfirmasi Penolakan</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.fv {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.fv__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.fv__title {
  font-family: var(--font-display, 'Anton', sans-serif);
  font-size: 26px;
  letter-spacing: 0.04em;
  color: #ffffff;
  margin: 0;
}

.fv__subtitle {
  font-size: 13px;
  color: #888888;
  margin: 4px 0 0;
}

.fv__search-input {
  background: #181818;
  border: 1px solid #2e2e2e;
  border-radius: 8px;
  color: #f5f5f5;
  padding: 9px 14px;
  font-size: 13px;
  width: 280px;
  outline: none;
}

.fv__search-input:focus {
  border-color: #b30000;
}

/* Tabs */
.tabs-bar {
  display: flex;
  gap: 8px;
  border-bottom: 1px solid #262626;
  padding-bottom: 8px;
}

.tab-btn {
  background: transparent;
  border: none;
  color: #888888;
  font-size: 13px;
  font-weight: 600;
  padding: 8px 14px;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.15s ease;
}

.tab-btn:hover {
  color: #ffffff;
  background: #202020;
}

.tab-btn--active {
  color: #ffffff;
  background: #262626;
}

.tab-badge {
  font-size: 11px;
  font-weight: 700;
  padding: 2px 6px;
  border-radius: 999px;
  background: #333333;
  color: #cccccc;
}

.tab-btn--active .tab-badge {
  background: #8a0303;
  color: #ffffff;
}

/* Split view */
.fv__split {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 20px;
  align-items: start;
}

.card {
  background: #181818;
  border: 1px solid #282828;
  border-radius: 12px;
  padding: 20px;
}

.fv__table-card {
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
  padding: 12px 14px;
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

.cell-fighter {
  display: flex;
  align-items: center;
  gap: 10px;
}

.fighter-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #2a2a2a;
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 13px;
}

.fighter-name {
  font-weight: 600;
  color: #ffffff;
}

.fighter-nickname {
  font-size: 11.5px;
  color: #888888;
}

.record-pill {
  font-size: 11.5px;
  font-weight: 700;
  padding: 2px 7px;
  border-radius: 4px;
  background: #242424;
  color: #e0e0e0;
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

.badge--danger {
  background: rgba(239, 68, 68, 0.12);
  color: #f87171;
  border: 1px solid rgba(239, 68, 68, 0.3);
}

.btn-inspect {
  background: #262626;
  border: 1px solid #363636;
  color: #f0f0f0;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
}

.btn-inspect:hover {
  background: #b30000;
  color: #ffffff;
}

/* Detail panel */
.fv__detail-card {
  position: sticky;
  top: 84px;
}

.detail__head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.detail__profile {
  display: flex;
  align-items: center;
  gap: 12px;
}

.detail__avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: #8a0303;
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 800;
}

.detail__name {
  font-size: 16px;
  font-weight: 700;
  color: #ffffff;
  margin: 0;
}

.detail__sub {
  font-size: 12px;
  color: #888888;
  margin: 2px 0 0;
}

.detail__stats-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  background: #141414;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #222222;
}

.stat-mini {
  display: flex;
  flex-direction: column;
}

.stat-mini__label {
  font-size: 10px;
  font-weight: 700;
  color: #666666;
  letter-spacing: 0.05em;
}

.stat-mini__value {
  font-size: 13px;
  font-weight: 600;
  color: #e0e0e0;
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
  margin: 0 0 12px;
}

.docs-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 20px;
}

.doc-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  background: #1e1e1e;
  border: 1px solid #2b2b2b;
  border-radius: 8px;
}

.doc-card__info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.doc-icon {
  font-size: 18px;
}

.doc-name {
  font-size: 12.5px;
  font-weight: 600;
  color: #ffffff;
}

.doc-meta {
  font-size: 10.5px;
  color: #888888;
}

.doc-link {
  font-size: 11.5px;
  font-weight: 600;
  color: #ff5252;
  text-decoration: none;
}

.doc-link:hover {
  text-decoration: underline;
}

.detail__actions {
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
  .fv__split {
    grid-template-columns: 1fr;
  }
}
</style>
