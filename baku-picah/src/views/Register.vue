<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import AuthLayout from '../layouts/AuthLayout.vue';
import api, { apiErrorMessage } from '../services/api';
import { useSessionStore } from '../stores/session';

const router = useRouter();
const session = useSessionStore();

const fullName = ref('');
const username = ref('');
const email = ref('');
const password = ref('');
const loading = ref(false);
const error = ref('');

async function submit() {
  error.value = '';
  if (!fullName.value || !username.value || !email.value) {
    error.value = 'Lengkapi nama, username, dan email.';
    return;
  }
  if (password.value.length < 6) {
    error.value = 'Password minimal 6 karakter.';
    return;
  }

  loading.value = true;
  try {
    const { data } = await api.post('/auth/register', {
      username: username.value,
      email: email.value,
      password: password.value,
      full_name: fullName.value,
    });
    session.setSession(data.access_token, data.fighter);
    router.push({ name: 'setup' });
  } catch (e) {
    error.value = apiErrorMessage(e, 'Gagal mendaftar. Coba lagi.');
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <AuthLayout>
    <form class="stack" @submit.prevent="submit">
      <div class="field">
        <label>Nama / Nickname</label>
        <input v-model="fullName" type="text" placeholder="mis. Rambo Sitorus" />
      </div>
      <div class="field">
        <label>Username</label>
        <input v-model="username" type="text" placeholder="mis. rambo" autocomplete="username" />
      </div>
      <div class="field">
        <label>Email</label>
        <input v-model="email" type="email" placeholder="kamu@email.com" autocomplete="email" />
      </div>
      <div class="field">
        <label>Password</label>
        <input
          v-model="password"
          type="password"
          placeholder="min. 6 karakter"
          autocomplete="new-password"
        />
      </div>

      <p v-if="error" class="error-text">{{ error }}</p>

      <button class="btn btn--primary btn--block" type="submit" :disabled="loading">
        {{ loading ? 'Memproses...' : 'Daftar' }}
      </button>

      <p class="muted foot">
        Sudah punya akun?
        <router-link :to="{ name: 'login' }" class="link">Masuk</router-link>
      </p>
    </form>
  </AuthLayout>
</template>

<style scoped>
.foot {
  text-align: center;
  font-size: 14px;
  margin: 4px 0 0;
}
.link {
  color: var(--blood-bright);
  font-weight: 700;
}
</style>
