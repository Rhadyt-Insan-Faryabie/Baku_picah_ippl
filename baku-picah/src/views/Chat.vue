<script setup>
import PageHeader from '../components/PageHeader.vue';
import EmptyState from '../components/EmptyState.vue';
</script>

<template>
  <div>
    <PageHeader title="Chat" subtitle="Diskusikan kesepakatan laga secara privat." />
    <EmptyState
      icon="💬"
      title="Belum ada percakapan"
      message="Ruang chat 1-on-1 akan tersedia setelah kamu terhubung dengan lawan di halaman Matches."
    />
  </div>
</template>
