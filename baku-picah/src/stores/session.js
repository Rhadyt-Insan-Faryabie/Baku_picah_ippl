import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import {
  clearAuth,
  getStoredUser,
  getToken,
  setStoredUser,
  setToken,
} from '../services/authStorage';

export const useSessionStore = defineStore('session', () => {
  const token = ref(getToken() || '');
  const user = ref(getStoredUser());

  const isLoggedIn = computed(() => Boolean(token.value));
  const displayName = computed(() => user.value?.full_name || user.value?.username || 'Petarung');

  function setSession(accessToken, fighter) {
    token.value = accessToken;
    user.value = fighter;
    setToken(accessToken);
    setStoredUser(fighter);
  }

  function setUser(fighter) {
    user.value = fighter;
    setStoredUser(fighter);
  }

  function logout() {
    token.value = '';
    user.value = null;
    clearAuth();
  }

  return { token, user, isLoggedIn, displayName, setSession, setUser, logout };
});
