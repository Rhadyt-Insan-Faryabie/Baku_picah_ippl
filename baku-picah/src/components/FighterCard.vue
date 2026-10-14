<script setup>
import { computed } from 'vue';
import { FIGHT_RULES } from '../constants/options';

const props = defineProps({
  fighter: { type: Object, required: true },
  score: { type: Number, default: null },
  reasons: { type: Array, default: () => [] },
});

const initials = computed(() =>
  (props.fighter.full_name || props.fighter.username || 'P').slice(0, 1).toUpperCase(),
);

const scorePct = computed(() =>
  props.score === null || props.score === undefined
    ? null
    : Math.round(props.score * 100),
);

const record = computed(
  () =>
    `${props.fighter.wins ?? 0}-${props.fighter.losses ?? 0}-${props.fighter.draws ?? 0}`,
);
</script>

<template>
  <article class="fighter card">
    <header class="fighter__head">
      <div class="fighter__avatar">{{ initials }}</div>
      <div class="fighter__id">
        <h2 class="fighter__name">{{ fighter.full_name || fighter.username }}</h2>
        <p class="muted fighter__handle">@{{ fighter.username }}</p>
      </div>
      <div v-if="scorePct !== null" class="fighter__score">
        <span class="fighter__score-num">{{ scorePct }}%</span>
        <span class="muted">cocok</span>
      </div>
    </header>

    <div class="fighter__meta">
      <span class="rules">{{ FIGHT_RULES }}</span>
      <span v-if="fighter.weight_class" class="pill">{{ fighter.weight_class }}</span>
      <span v-if="fighter.city" class="pill">📍 {{ fighter.city }}</span>
    </div>

    <div class="fighter__record">
      <span><strong>{{ record }}</strong> <span class="muted">rekor (M-K-S)</span></span>
      <span v-if="fighter.height_cm" class="muted">{{ fighter.height_cm }} cm</span>
      <span v-if="fighter.weight_kg" class="muted">{{ fighter.weight_kg }} kg</span>
    </div>

    <div v-if="fighter.martial_arts && fighter.martial_arts.length" class="chips">
      <span v-for="art in fighter.martial_arts" :key="art" class="chip">{{ art }}</span>
    </div>

    <ul v-if="reasons.length" class="reasons">
      <li v-for="reason in reasons" :key="reason">{{ reason }}</li>
    </ul>

    <div v-if="$slots.actions" class="fighter__actions">
      <slot name="actions" />
    </div>
  </article>
</template>

<style scoped>
.fighter__head {
  display: flex;
  align-items: center;
  gap: 14px;
}
.fighter__avatar {
  width: 60px;
  height: 60px;
  flex: 0 0 60px;
  border-radius: 50%;
  background: var(--blood);
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: var(--font-display);
  font-size: 26px;
}
.fighter__id {
  flex: 1;
  min-width: 0;
}
.fighter__name {
  font-size: 20px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.fighter__handle {
  margin: 4px 0 0;
  font-size: 13px;
}
.fighter__score {
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 11px;
}
.fighter__score-num {
  font-family: var(--font-display);
  font-size: 24px;
  color: var(--blood-bright);
}
.fighter__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 16px;
}
.rules {
  font-family: var(--font-display);
  font-size: 14px;
  color: var(--blood-bright);
  border: 1px solid var(--blood-bright);
  background: var(--blood-soft);
  border-radius: var(--radius-pill);
  padding: 3px 14px;
}
.pill {
  border: 1px solid var(--border);
  border-radius: var(--radius-pill);
  padding: 4px 12px;
  font-size: 12px;
  color: var(--text-muted);
}
.fighter__record {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-top: 14px;
  font-size: 13px;
}
.fighter__record strong {
  font-family: var(--font-display);
  font-size: 16px;
}
.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 14px;
}
.chip {
  border: 1px solid var(--blood-bright);
  background: var(--blood-soft);
  color: var(--blood-bright);
  border-radius: var(--radius-pill);
  padding: 5px 13px;
  font-size: 12px;
  font-weight: 600;
}
.reasons {
  list-style: none;
  margin: 16px 0 0;
  padding: 14px 0 0;
  border-top: 1px solid var(--border);
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  font-size: 13px;
  color: var(--text-muted);
}
.reasons li::before {
  content: '✓ ';
  color: var(--success);
  font-weight: 700;
}
.fighter__actions {
  display: flex;
  gap: 10px;
  margin-top: 18px;
}
.fighter__actions :deep(.btn) {
  flex: 1;
}
</style>
