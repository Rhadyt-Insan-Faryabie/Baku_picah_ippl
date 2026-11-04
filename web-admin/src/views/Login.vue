<script setup>
import { ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import api from '../services/api';

const router = useRouter();
const route = useRoute();

const email = ref('admin@bakupicah.id');
const password = ref('Admin123456!');
const showPassword = ref(false);
const loading = ref(false);
const error = ref('');

async function login() {
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

    if (data.fighter?.role !== 'admin') {
      error.value = 'Akun ini bukan administrator.';
      return;
    }

    localStorage.setItem('bp_admin_token', data.access_token);
    localStorage.setItem('bp_admin_user', JSON.stringify(data.fighter));
    router.push(route.query.redirect || { name: 'dashboard' });
  } catch (e) {
    const detail = e.response?.data?.detail;
    error.value = Array.isArray(detail)
      ? detail[0]?.msg
      : detail || 'Gagal terhubung ke backend. Anda dapat menggunakan tombol Demo Login di bawah.';
  } finally {
    loading.value = false;
  }
}

function demoLogin() {
  const mockAdminUser = {
    id: 99,
    name: 'Nadia Aulia',
    email: 'admin@bakupicah.id',
    role: 'admin',
    nickname: 'Control Room Admin',
  };
  localStorage.setItem('bp_admin_token', 'mock_jwt_admin_token_nadia_aulia');
  localStorage.setItem('bp_admin_user', JSON.stringify(mockAdminUser));
  router.push(route.query.redirect || { name: 'dashboard' });
}
</script>

<template>
  <div class="auth">
    <div class="auth__card card">
      <div class="auth__brand">
        <div class="auth__title">
          <span class="auth__logo">BAKU</span>
          <span class="auth__logo auth__logo--accent">PICAH</span>
        </div>
        <div class="auth__badge">CONTROL ROOM</div>
      </div>
      <p class="auth__tag muted">Sistem Operasional & Manajemen Pertandingan</p>

      <form class="auth__form" @submit.prevent="login">
        <div class="field">
          <label>Email Administrator</label>
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
          {{ loading ? 'Memproses...' : 'Masuk Control Room' }}
        </button>
      </form>

      <div class="auth__divider">
        <span>ATAU AKSES LANGSUNG</span>
      </div>

      <button class="btn btn--demo" type="button" @click="demoLogin">
        ⚡ Demo Login: Nadia Aulia (Administrator)
      </button>

      <p class="auth__hint muted">Akses terbatas untuk Administrator baku picah Bandung.</p>
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
  background: radial-gradient(circle at 50% 0%, rgba(138, 3, 3, 0.25), transparent 65%), #121212;
}
.auth__card {
  width: 100%;
  max-width: 420px;
  background: #181818;
  border: 1px solid #2e2e2e;
  border-radius: 14px;
  padding: 32px 28px;
  box-shadow: 0 16px 36px rgba(0, 0, 0, 0.6);
}
.auth__brand {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 8px;
}
.auth__title {
  display: flex;
  gap: 6px;
  font-family: var(--font-display, 'Anton', sans-serif);
  font-size: 32px;
  letter-spacing: 0.05em;
}
.auth__logo {
  color: #f7f5f1;
}
.auth__logo--accent {
  color: #b30000;
}
.auth__badge {
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 0.2em;
  color: #b30000;
  margin-top: 2px;
}
.auth__tag {
  text-align: center;
  font-size: 12.5px;
  color: #888888;
  margin: 0 0 24px;
}
.auth__form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.field label {
  font-size: 12px;
  font-weight: 600;
  color: #bbbbbb;
}
.field input {
  background: #121212;
  border: 1px solid #333333;
  color: #f5f5f5;
  padding: 10px 14px;
  border-radius: 8px;
  font-size: 13.5px;
  outline: none;
  transition: border-color 0.15s ease;
}
.field input:focus {
  border-color: #b30000;
}
.auth__pw {
  position: relative;
  display: flex;
  align-items: center;
}
.auth__pw input {
  width: 100%;
  padding-right: 90px;
}
.auth__toggle {
  position: absolute;
  right: 10px;
  background: transparent;
  border: none;
  color: #888888;
  font-size: 11px;
  font-weight: 600;
  cursor: pointer;
}
.auth__toggle:hover {
  color: #ffffff;
}
.auth__error {
  color: #ff5252;
  font-size: 12px;
  background: rgba(255, 82, 82, 0.1);
  border: 1px solid rgba(255, 82, 82, 0.3);
  padding: 8px 12px;
  border-radius: 6px;
  margin: 0;
}
.auth__submit {
  background: #8a0303;
  color: #ffffff;
  border: none;
  padding: 12px;
  border-radius: 8px;
  font-weight: 700;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.15s ease;
  margin-top: 4px;
}
.auth__submit:hover:not(:disabled) {
  background: #b30000;
}
.auth__divider {
  display: flex;
  align-items: center;
  margin: 20px 0 16px;
  text-align: center;
}
.auth__divider::before,
.auth__divider::after {
  content: '';
  flex: 1;
  border-bottom: 1px solid #2c2c2c;
}
.auth__divider span {
  font-size: 10px;
  font-weight: 700;
  color: #666666;
  padding: 0 10px;
  letter-spacing: 0.1em;
}
.btn--demo {
  width: 100%;
  background: #242424;
  border: 1px solid #3c3c3c;
  color: #e0e0e0;
  padding: 10px 14px;
  border-radius: 8px;
  font-weight: 600;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.15s ease;
}
.btn--demo:hover {
  background: #2d2d2d;
  border-color: #666666;
  color: #ffffff;
}
.auth__hint {
  font-size: 11px;
  color: #666666;
  text-align: center;
  margin-top: 18px;
}
</style>
