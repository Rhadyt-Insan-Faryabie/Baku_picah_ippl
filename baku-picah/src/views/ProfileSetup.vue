<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import AuthLayout from '../layouts/AuthLayout.vue';
import CitySelect from '../components/CitySelect.vue';
import MartialArtsPicker from '../components/MartialArtsPicker.vue';
import api, { apiErrorMessage } from '../services/api';
import { useSessionStore } from '../stores/session';
import { FIGHT_RULES } from '../constants/options';

const router = useRouter();
const session = useSessionStore();

const fullName = ref(session.user?.full_name || '');
const weight = ref('');
const height = ref('');
const city = ref(session.user?.city || null);
const martialArts = ref(session.user?.martial_arts || []);
const loading = ref(false);
const error = ref('');

async function submit() {
  error.value = '';
  loading.value = true;
  try {
    const { data } = await api.put('/fighters/me', {
      full_name: fullName.value || null,
      city: city.value || null,
      weight_kg: weight.value ? Number(weight.value) : null,
      height_cm: height.value ? Number(height.value) : null,
      martial_arts: martialArts.value.length ? martialArts.value : null,
    });
    session.setUser(data);
    router.push({ name: 'discover' });
  } catch (e) {
    error.value = apiErrorMessage(e, 'Gagal menyimpan profil.');
  } finally {
    loading.value = false;
  }
}

function skip() {
  router.push({ name: 'discover' });
}
</script>

<template>
  <AuthLayout>
    <h1 class="title">Lengkapi Profil</h1>
    <p class="muted sub">Data ini membantu AI mencocokkan lawan yang seimbang.</p>

    <div class="rules">
      <span class="rules__label">Aturan Tanding</span>
      <span class="rules__value">{{ FIGHT_RULES }}</span>
    </div>

    <form class="stack" @submit.prevent="submit">
      <div class="field">
        <label>Nama / Nickname</label>
        <input v-model="fullName" type="text" placeholder="Nama tampilanmu" />
      </div>

      <div class="row">
        <div class="field">
          <label>Berat Badan (kg)</label>
          <input v-model="weight" type="number" step="0.1" placeholder="mis. 70" />
        </div>
        <div class="field">
          <label>Tinggi Badan (cm)</label>
          <input v-model="height" type="number" step="0.1" placeholder="mis. 175" />
        </div>
      </div>

      <div class="field">
        <label>Kota</label>
        <CitySelect v-model="city" />
      </div>

      <div class="field">
        <label>Background Bela Diri (boleh lebih dari satu)</label>
        <MartialArtsPicker v-model="martialArts" />
      </div>

      <p v-if="error" class="error-text">{{ error }}</p>

      <button class="btn btn--primary btn--block" type="submit" :disabled="loading">
        {{ loading ? 'Menyimpan...' : 'Simpan & Lanjut' }}
      </button>
      <button class="btn btn--ghost btn--block" type="button" @click="skip">Lewati dulu</button>
    </form>
  </AuthLayout>
</template>

<style scoped>
.title {
  font-size: 26px;
}
.sub {
  margin: 6px 0 20px;
  font-size: 14px;
}
.rules {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border: 1px solid var(--blood-bright);
  background: var(--blood-soft);
  border-radius: var(--radius-sm);
  padding: 10px 14px;
  margin-bottom: 18px;
}
.rules__label {
  font-size: 13px;
  color: var(--text-muted);
}
.rules__value {
  font-family: var(--font-display);
  font-size: 18px;
  color: var(--blood-bright);
}
.row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}
.field :deep(select) {
  width: 100%;
}
@media (max-width: 420px) {
  .row {
    grid-template-columns: 1fr;
  }
}
</style>
