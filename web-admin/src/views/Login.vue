<script setup>
import { ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();

const email = ref('');
const password = ref('');
const showPassword = ref(false);
const loading = ref(false);
const error = ref('');

function login() {
  error.value = '';
  if (!email.value || !password.value) {
    error.value = 'Email dan password wajib diisi.';
    return;
  }
  loading.value = true;
  // Placeholder: replace with real API call.
  setTimeout(() => {
    loading.value = false;
    if (password.value.length < 4) {
      error.value = 'Password minimal 4 karakter.';
      return;
    }
    localStorage.setItem('bp_admin_token', 'mock-admin-token');
    router.push(route.query.redirect || { name: 'dashboard' });
  }, 400);
}
</script>

<template>
  <div class="auth">
    <div class="auth__card card">
      <div class="auth__brand">
        <span class="auth__logo">BAKU</span>
        <span class="auth__logo auth__logo--accent">PICAH</span>
      </div>
      <p class="auth__tag muted">Admin Dashboard — Fighter Matchmaking</p>

      <form class="auth__form" @submit.prevent="login">
        <div class="field">
          <label>Email</label>
          <input v-model="email" type="email" placeholder="admin@bakupicah.id" autocomplete="username" />
        </div>
        <div class="field">
          <label>Password</label>
          <div class="auth__pw">
            <input
              v-model="password"
              :type="showPassword ? 'text' : 'password'"
              placeholder="••••••••"
              autocomplete="current-password"
            />
            <button type="button" class="auth__toggle" @click="showPassword = !showPassword">
              {{ showPassword ? 'Sembunyikan' : 'Lihat' }}
            </button>
          </div>
        </div>

        <p v-if="error" class="auth__error">{{ error }}</p>

        <button class="btn btn--primary auth__submit" type="submit" :disabled="loading">
          {{ loading ? 'Memproses...' : 'Masuk' }}
        </button>
      </form>

      <p class="auth__hint muted">Demo: isi email & password apa pun (min. 4 karakter).</p>
    </div>
  </div>
</template>

<style scoped>
.auth {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: radial-gradient(circle at 50% 0%, rgba(138, 3, 3, 0.22), transparent 60%), var(--bg);
}
.auth__card {
  width: 100%;
  max-width: 400px;
}
.auth__brand {
  display: flex;
  gap: 8px;
  font-family: var(--font-display);
  font-size: 36px;
}
.auth__logo--accent {
  color: var(--blood-bright);
}
.auth__tag {
  font-size: 13px;
  margin: 6px 0 24px;
}
.auth__form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.auth__pw {
  display: flex;
  align-items: center;
  background: var(--surface-2);
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  padding-right: 10px;
}
.auth__pw input {
  border: none;
  background: transparent;
  flex: 1;
}
.auth__toggle {
  background: none;
  border: none;
  color: var(--text-muted);
  font-size: 12px;
}
.auth__submit {
  justify-content: center;
  width: 100%;
  margin-top: 4px;
}
.auth__error {
  color: #ef5350;
  font-size: 13px;
  margin: 0;
}
.auth__hint {
  font-size: 12px;
  margin: 18px 0 0;
  text-align: center;
}
</style>
