<script setup>
import { ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import AuthLayout from '../layouts/AuthLayout.vue';
import api, { apiErrorMessage } from '../services/api';
import { useSessionStore } from '../stores/session';

const router = useRouter();
const route = useRoute();
const session = useSessionStore();

const email = ref('');
const password = ref('');
const loading = ref(false);
const error = ref('');

async function submit() {
  error.value = '';
  if (!email.value || !password.value) {
    error.value = 'Email dan password wajib diisi.';
    return;
  }

  loading.value = true;
  try {
    const { data } = await api.post('/auth/login', {
      email: email.value,
      password: password.value,
    });
    session.setSession(data.access_token, data.fighter);

    const fighter = data.fighter || {};
    const profileIncomplete =
      !fighter.weight_kg || !fighter.city || !(fighter.martial_arts?.length);

    if (profileIncomplete) {
      router.push({ name: 'setup' });
    } else {
      router.push(route.query.redirect || { name: 'discover' });
    }
  } catch (e) {
    error.value = apiErrorMessage(e, 'Gagal masuk. Periksa email dan password.');
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <AuthLayout>
    <form class="stack" @submit.prevent="submit">
      <div class="field">
        <label>Email</label>
        <input v-model="email" type="email" placeholder="kamu@email.com" autocomplete="username" />
      </div>
      <div class="field">
        <label>Password</label>
        <input
          v-model="password"
          type="password"
          placeholder="••••••••"
          autocomplete="current-password"
        />
      </div>

      <p v-if="error" class="error-text">{{ error }}</p>

      <button class="btn btn--primary btn--block" type="submit" :disabled="loading">
        {{ loading ? 'Memproses...' : 'Masuk' }}
      </button>

      <p class="muted foot">
        Belum punya akun?
        <router-link :to="{ name: 'register' }" class="link">Daftar</router-link>
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
