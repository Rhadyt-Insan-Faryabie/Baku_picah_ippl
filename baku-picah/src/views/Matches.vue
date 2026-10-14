<script setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import PageHeader from '../components/PageHeader.vue';
import EmptyState from '../components/EmptyState.vue';
import FighterCard from '../components/FighterCard.vue';
import api, { apiErrorMessage } from '../services/api';

const router = useRouter();

const matches = ref([]);
const loading = ref(true);
const error = ref('');

async function load() {
  loading.value = true;
  error.value = '';
  try {
    const { data } = await api.get('/match/matches');
    matches.value = data;
  } catch (e) {
    error.value = apiErrorMessage(e, 'Gagal memuat daftar matches.');
  } finally {
    loading.value = false;
  }
}

function openChat(fighter) {
  router.push({ name: 'chat', query: { with: fighter.id } });
}

onMounted(load);
</script>

<template>
  <div>
    <PageHeader title="Matches" subtitle="Petarung yang saling cocok dan menerima tantangan.">
      <template #actions>
        <button class="btn btn--ghost" :disabled="loading" @click="load">Muat ulang</button>
      </template>
    </PageHeader>

    <p v-if="error" class="error-text">{{ error }}</p>
    <div v-if="loading" class="muted">Memuat matches...</div>

    <div v-else-if="matches.length" class="stack">
      <FighterCard
        v-for="item in matches"
        :key="item.fighter.id"
        :fighter="item.fighter"
        :score="item.score"
        :reasons="item.reasons"
      >
        <template #actions>
          <button class="btn btn--ghost" @click="router.push({ name: 'profile' })">
            Lihat Profil
          </button>
          <button class="btn btn--primary" @click="openChat(item.fighter)">
            Mulai Chat
          </button>
        </template>
      </FighterCard>
    </div>

    <EmptyState
      v-else
      icon="🤝"
      title="Belum ada kecocokan"
      message="Daftar kecocokan akan muncul di sini setelah kamu mulai melakukan like di halaman Cari Lawan dan lawan membalas like-mu."
    >
      <button class="btn btn--primary" @click="router.push({ name: 'discover' })">
        Cari Lawan
      </button>
    </EmptyState>
  </div>
</template>
