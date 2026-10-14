<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import PageHeader from '../components/PageHeader.vue';
import CitySelect from '../components/CitySelect.vue';
import MartialArtsPicker from '../components/MartialArtsPicker.vue';
import api, { apiErrorMessage } from '../services/api';
import { useSessionStore } from '../stores/session';
import { FIGHT_RULES } from '../constants/options';

const session = useSessionStore();

const loading = ref(true);
const editing = ref(false);
const saving = ref(false);
const error = ref('');
const notice = ref('');

const form = reactive({
  full_name: '',
  city: null,
  weight_kg: '',
  height_cm: '',
  martial_arts: [],
});

const profile = computed(() => session.user || {});
const initials = computed(() =>
  (profile.value.full_name || profile.value.username || 'P').slice(0, 1).toUpperCase(),
);

function fillForm() {
  const p = session.user || {};
  form.full_name = p.full_name || '';
  form.city = p.city || null;
  form.weight_kg = p.weight_kg ?? '';
  form.height_cm = p.height_cm ?? '';
  form.martial_arts = p.martial_arts || [];
}

async function load() {
  loading.value = true;
  try {
    const { data } = await api.get('/fighters/me');
    session.setUser(data);
    fillForm();
  } catch (e) {
    error.value = apiErrorMessage(e, 'Gagal memuat profil.');
  } finally {
    loading.value = false;
  }
}

function startEdit() {
  fillForm();
  notice.value = '';
  editing.value = true;
}

async function save() {
  saving.value = true;
  error.value = '';
  try {
    const { data } = await api.put('/fighters/me', {
      full_name: form.full_name || null,
      city: form.city || null,
      weight_kg: form.weight_kg === '' ? null : Number(form.weight_kg),
      height_cm: form.height_cm === '' ? null : Number(form.height_cm),
      martial_arts: form.martial_arts.length ? form.martial_arts : null,
    });
    session.setUser(data);
    editing.value = false;
    notice.value = 'Profil berhasil diperbarui.';
  } catch (e) {
    error.value = apiErrorMessage(e, 'Gagal menyimpan profil.');
  } finally {
    saving.value = false;
  }
}

onMounted(load);
</script>

<template>
  <div>
    <PageHeader title="Profil Petarung" subtitle="Kelola identitas fisik dan gaya bertarungmu.">
      <template #actions>
        <button v-if="!editing" class="btn btn--primary" @click="startEdit">Edit Profil</button>
      </template>
    </PageHeader>

    <p v-if="notice" class="notice">{{ notice }}</p>
    <p v-if="error" class="error-text">{{ error }}</p>

    <div v-if="loading" class="muted">Memuat...</div>

    <div v-else class="grid profile-grid">
      <section class="card identity">
        <div class="identity__avatar">{{ initials }}</div>
        <h2 class="identity__name">{{ profile.full_name || profile.username }}</h2>
        <p class="muted">@{{ profile.username }}</p>

        <div class="tagline">
          <span class="tagline__rules">{{ FIGHT_RULES }}</span>
          <span v-if="profile.city" class="tagline__city">📍 {{ profile.city }}</span>
        </div>

        <div class="record">
          <div class="record__item">
            <span class="record__num">{{ profile.wins ?? 0 }}</span>
            <span class="muted">Menang</span>
          </div>
          <div class="record__item">
            <span class="record__num">{{ profile.losses ?? 0 }}</span>
            <span class="muted">Kalah</span>
          </div>
          <div class="record__item">
            <span class="record__num">{{ profile.draws ?? 0 }}</span>
            <span class="muted">Seri</span>
          </div>
        </div>
      </section>

      <section class="card">
        <template v-if="!editing">
          <h3 class="section-title">Data Fisik</h3>
          <dl class="detail">
            <div><dt>Email</dt><dd>{{ profile.email || '-' }}</dd></div>
            <div><dt>Kota</dt><dd>{{ profile.city || '-' }}</dd></div>
            <div><dt>Berat Badan</dt><dd>{{ profile.weight_kg ? profile.weight_kg + ' kg' : '-' }}</dd></div>
            <div><dt>Kelas Berat</dt><dd>{{ profile.weight_class || '-' }}</dd></div>
            <div><dt>Tinggi Badan</dt><dd>{{ profile.height_cm ? profile.height_cm + ' cm' : '-' }}</dd></div>
          </dl>

          <h3 class="section-title mt">Background Bela Diri</h3>
          <div v-if="profile.martial_arts && profile.martial_arts.length" class="chips">
            <span v-for="art in profile.martial_arts" :key="art" class="chip">{{ art }}</span>
          </div>
          <p v-else class="muted">Belum diisi.</p>
        </template>

        <form v-else class="stack" @submit.prevent="save">
          <h3 class="section-title">Edit Data</h3>
          <div class="field">
            <label>Nama / Nickname</label>
            <input v-model="form.full_name" type="text" />
          </div>
          <div class="row">
            <div class="field">
              <label>Berat Badan (kg)</label>
              <input v-model="form.weight_kg" type="number" step="0.1" />
            </div>
            <div class="field">
              <label>Tinggi Badan (cm)</label>
              <input v-model="form.height_cm" type="number" step="0.1" />
            </div>
          </div>
          <div class="field">
            <label>Kota</label>
            <CitySelect v-model="form.city" />
          </div>
          <div class="field">
            <label>Background Bela Diri (boleh lebih dari satu)</label>
            <MartialArtsPicker v-model="form.martial_arts" />
          </div>

          <div class="actions">
            <button class="btn btn--ghost" type="button" @click="editing = false">Batal</button>
            <button class="btn btn--primary" type="submit" :disabled="saving">
              {{ saving ? 'Menyimpan...' : 'Simpan' }}
            </button>
          </div>
        </form>
      </section>
    </div>
  </div>
</template>

<style scoped>
.notice {
  color: #66bb6a;
  font-size: 14px;
  margin: 0 0 14px;
}
.profile-grid {
  grid-template-columns: 300px 1fr;
  align-items: start;
}
.identity {
  text-align: center;
}
.identity__avatar {
  width: 96px;
  height: 96px;
  margin: 6px auto 14px;
  border-radius: 50%;
  background: var(--blood);
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: var(--font-display);
  font-size: 40px;
}
.identity__name {
  font-size: 20px;
}
.tagline {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  margin-top: 10px;
}
.tagline__rules {
  font-family: var(--font-display);
  font-size: 16px;
  color: var(--blood-bright);
  border: 1px solid var(--blood-bright);
  background: var(--blood-soft);
  border-radius: var(--radius-pill);
  padding: 3px 16px;
}
.tagline__city {
  font-size: 13px;
  color: var(--text-muted);
}
.record {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin-top: 20px;
  border-top: 1px solid var(--border);
  padding-top: 16px;
}
.record__item {
  display: flex;
  flex-direction: column;
  gap: 2px;
  font-size: 12px;
}
.record__num {
  font-family: var(--font-display);
  font-size: 24px;
}
.section-title {
  font-size: 16px;
  margin-bottom: 14px;
}
.section-title.mt {
  margin-top: 22px;
}
.detail {
  margin: 0;
  display: flex;
  flex-direction: column;
}
.detail > div {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 12px 0;
  border-bottom: 1px solid var(--border);
  font-size: 14px;
}
.detail dt {
  color: var(--text-muted);
}
.detail dd {
  margin: 0;
  font-weight: 500;
  text-align: right;
}
.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.chip {
  border: 1px solid var(--blood-bright);
  background: var(--blood-soft);
  color: var(--blood-bright);
  border-radius: var(--radius-pill);
  padding: 6px 14px;
  font-size: 13px;
  font-weight: 600;
}
.field :deep(select) {
  width: 100%;
}
.row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}
.actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 6px;
}
@media (max-width: 780px) {
  .profile-grid {
    grid-template-columns: 1fr;
  }
}
</style>
