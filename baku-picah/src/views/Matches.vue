<script setup>
import PageHeader from '../components/PageHeader.vue';
import EmptyState from '../components/EmptyState.vue';
</script>

<template>
  <div>
    <PageHeader title="Matches" subtitle="Petarung yang saling cocok dan menerima tantangan." />
    <EmptyState
      icon="🤝"
      title="Belum ada kecocokan"
      message="Daftar kecocokan akan muncul di sini setelah kamu mulai melakukan like/pass di halaman Discover."
    />
  </div>
</template>
