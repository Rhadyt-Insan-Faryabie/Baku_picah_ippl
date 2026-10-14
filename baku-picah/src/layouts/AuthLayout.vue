<template>
  <div class="auth">
    <div class="auth__card card">
      <div class="auth__brand">
        <span>BAKU</span><span class="auth__accent">PICAH</span>
      </div>
      <p class="auth__tag muted">Fighter Platform — Matchmaking &amp; Bout</p>
      <slot />
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
  max-width: 420px;
}
.auth__brand {
  display: flex;
  gap: 8px;
  font-family: var(--font-display);
  font-size: 34px;
}
.auth__accent {
  color: var(--blood-bright);
}
.auth__tag {
  font-size: 13px;
  margin: 6px 0 24px;
}
</style>
