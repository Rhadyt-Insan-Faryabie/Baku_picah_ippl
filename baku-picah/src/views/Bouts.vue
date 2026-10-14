<script setup>
import { useRouter } from 'vue-router';
import PageHeader from '../components/PageHeader.vue';
import EmptyState from '../components/EmptyState.vue';

const router = useRouter();
</script>

<template>
  <div>
    <PageHeader title="Bouts" subtitle="Pengajuan, verifikasi, dan pelacakan pertandingan." />
    <EmptyState
      icon="🥊"
      title="Belum ada pertandingan"
      message="Ajukan laga dari halaman Discover setelah menemukan lawan yang cocok. Jadwal yang diajukan akan tampil di sini."
    >
      <button class="btn btn--primary" style="margin-top: 18px" @click="router.push({ name: 'discover' })">
        Cari Lawan
      </button>
    </EmptyState>
  </div>
</template>
