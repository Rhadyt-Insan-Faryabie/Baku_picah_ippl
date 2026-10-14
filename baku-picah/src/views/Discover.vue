<script setup>
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import PageHeader from '../components/PageHeader.vue';
import EmptyState from '../components/EmptyState.vue';
import FighterCard from '../components/FighterCard.vue';
import api, { apiErrorMessage } from '../services/api';
import { useSessionStore } from '../stores/session';

const session = useSessionStore();
const router = useRouter();

const queue = ref([]);
const loading = ref(true);
const acting = ref(false);
const error = ref('');
const notice = ref('');
const sameClassOnly = ref(false);

const current = computed(() => queue.value[0] || null);
const profileReady = computed(
  () => Boolean(session.user?.weight_kg) && Boolean(session.user?.city),
);

async function load() {
  loading.value = true;
  error.value = '';
  notice.value = '';
  try {
    const { data } = await api.get('/match/recommendations', {
      params: { same_weight_class_only: sameClassOnly.value, limit: 20 },
    });
    queue.value = data;
  } catch (e) {
    error.value = apiErrorMessage(e, 'Gagal memuat rekomendasi lawan.');
  } finally {
    loading.value = false;
  }
}

async function act(action) {
  const target = current.value;
  if (!target || acting.value) return;

  acting.value = true;
  error.value = '';
  notice.value = '';
  try {
    const { data } = await api.post(`/match/${target.fighter.id}/${action}`);
    const name = target.fighter.full_name || target.fighter.username;

    if (data.matched) {
      notice.value = `Kecocokan! Kamu dan ${name} saling tertarik. Cek halaman Matches.`;
    } else if (action === 'like') {
      notice.value = `Kamu menantang ${name}. Menunggu respons mereka.`;
    } else {
      notice.value = `${name} dilewati.`;
    }
    queue.value = queue.value.slice(1);
  } catch (e) {
    error.value = apiErrorMessage(e, 'Aksi gagal. Coba lagi.');
  } finally {
    acting.value = false;
  }
}

function goToMatches() {
  router.push({ name: 'matches' });
}

onMounted(load);
</script>

<template>
  <div>
    <PageHeader
      title="Cari Lawan"
      subtitle="Rekomendasi lawan tanding (kelas berat, kota & latar belakang bela diri)."
    >
      <template #actions>
        <label class="toggle">
          <input v-model="sameClassOnly" type="checkbox" @change="load" />
          <span>Kelas berat sama</span>
        </label>
        <button class="btn btn--ghost" :disabled="loading" @click="load">Muat ulang</button>
      </template>
    </PageHeader>

    <div v-if="!profileReady" class="hint card">
      <p class="hint__text">
        Lengkapi <strong>berat badan</strong> dan <strong>kota</strong> di halaman Profil agar
        rekomendasi lebih akurat.
      </p>
      <button class="btn btn--primary btn--sm" @click="router.push({ name: 'profile' })">
        Lengkapi Profil
      </button>
    </div>

    <p v-if="notice" class="notice">
      {{ notice }}
      <button class="link" type="button" @click="goToMatches">Lihat Matches</button>
    </p>
    <p v-if="error" class="error-text">{{ error }}</p>

    <div v-if="loading" class="muted">Mencari lawan...</div>

    <template v-else-if="current">
      <div class="counter muted">Sisa {{ queue.length }} kandidat</div>
      <FighterCard
        :key="current.fighter.id"
        :fighter="current.fighter"
        :score="current.score"
        :reasons="current.reasons"
      >
        <template #actions>
          <button class="btn btn--ghost" :disabled="acting" @click="act('pass')">
            Lewati
          </button>
          <button class="btn btn--primary" :disabled="acting" @click="act('like')">
            {{ acting ? 'Memproses...' : 'Tantang' }}
          </button>
        </template>
      </FighterCard>
    </template>

    <EmptyState
      v-else
      icon="🔍"
      title="Belum ada kandidat"
      message="Semua kandidat sudah kamu nilai, atau belum ada petarung lain yang cocok. Coba matikan filter kelas berat atau muat ulang."
    >
      <button class="btn btn--primary" @click="load">Muat ulang</button>
    </EmptyState>
  </div>
</template>

<style scoped>
.hint {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
  border-color: var(--warning);
}
.hint__text {
  margin: 0;
  font-size: 14px;
}
.btn--sm {
  padding: 8px 16px;
  font-size: 13px;
}
.notice {
  color: #66bb6a;
  font-size: 14px;
  margin: 0 0 14px;
}
.link {
  background: none;
  border: none;
  color: var(--blood-bright);
  font-size: 14px;
  font-weight: 600;
  padding: 0 0 0 6px;
  text-decoration: underline;
}
.toggle {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--text-muted);
  cursor: pointer;
}
.counter {
  font-size: 13px;
  margin-bottom: 10px;
}
@media (max-width: 620px) {
  .hint {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
