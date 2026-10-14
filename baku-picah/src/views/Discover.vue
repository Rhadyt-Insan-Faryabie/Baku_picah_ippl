<script setup>
import PageHeader from '../components/PageHeader.vue';
import EmptyState from '../components/EmptyState.vue';
</script>

<template>
  <div>
    <PageHeader title="Cari Lawan" subtitle="Rekomendasi lawan tanding berbasis AI (kelas berat & pengalaman)." />
    <EmptyState
      icon="🔍"
      title="Matchmaking segera hadir"
      message="Fitur pencocokan lawan akan aktif setelah modul matchmaking backend selesai. Sementara itu, lengkapi profilmu agar rekomendasi lebih akurat."
    />
  </div>
</template>
