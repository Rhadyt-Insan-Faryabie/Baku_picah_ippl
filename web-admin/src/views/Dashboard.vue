<script setup>
import StatCard from '../components/StatCard.vue';
import DataTable from '../components/DataTable.vue';
import StatusBadge from '../components/StatusBadge.vue';
import { stats, bouts, disputes } from '../services/mockData.js';

const columns = [
  { key: 'fighterA', label: 'Petarung A' },
  { key: 'fighterB', label: 'Petarung B' },
  { key: 'discipline', label: 'Disiplin' },
  { key: 'date', label: 'Jadwal' },
  { key: 'status', label: 'Status' },
];

const recentBouts = bouts.slice(0, 4);
</script>

<template>
  <div class="dash">
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

    <section class="dash__grid">
      <div>
        <h2 class="dash__h2">Laga Terbaru</h2>
        <DataTable :columns="columns" :rows="recentBouts">
          <template #status="{ row }">
            <StatusBadge :status="row.status" />
          </template>
        </DataTable>
      </div>

      <div>
        <h2 class="dash__h2">Dispute Aktif</h2>
        <div class="dash__list">
          <div v-for="d in disputes" :key="d.id" class="card dash__dispute">
            <div class="dash__dispute-head">
              <strong>{{ d.id }}</strong>
              <span class="muted">{{ d.boutId }}</span>
            </div>
            <p class="muted">{{ d.fighterA }} vs {{ d.fighterB }}</p>
            <p class="dash__claim">Klaim: {{ d.claimed }}</p>
            <p class="dash__claim muted">Bantahan: {{ d.counter }}</p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.dash__stats {
  grid-template-columns: repeat(4, 1fr);
  margin-bottom: 24px;
}
.dash__grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
  align-items: start;
}
.dash__h2 {
  font-size: 16px;
  margin-bottom: 12px;
}
.dash__list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.dash__dispute-head {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
}
.dash__dispute p {
  margin: 4px 0;
  font-size: 13px;
}
.dash__claim {
  color: var(--text);
}

@media (max-width: 1100px) {
  .dash__stats {
    grid-template-columns: repeat(2, 1fr);
  }
  .dash__grid {
    grid-template-columns: 1fr;
  }
}
@media (max-width: 560px) {
  .dash__stats {
    grid-template-columns: 1fr;
  }
}
</style>
