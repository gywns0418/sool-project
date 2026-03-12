<template>
  <router-link class="drink-card" :to="`/drinks/${item.id}`">
    <div class="drink-thumb">
      {{ item.emoji }}
      <div class="drink-rank" :class="{ gold: item.gold }">{{ item.rank }}</div>
    </div>
    <div class="drink-info">
      <div class="drink-cat">{{ item.category }}</div>
      <div class="drink-name">{{ item.name }}</div>
      <div class="drink-meta">
        <span class="drink-stars">{{ item.rating }}</span>
        <span class="drink-like">♡ {{ item.likes }}</span>
      </div>
    </div>
  </router-link>
</template>

<script setup>
defineProps({
  item: {
    type: Object,
    required: true
  }
})
</script>

<style scoped>
.drink-card {
  background: var(--white);
  border: 1px solid var(--border);
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.18s, transform 0.18s;
  display: block;
}

.drink-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  transform: translateY(-3px);
}

.drink-thumb {
  height: 130px;
  background: linear-gradient(145deg, var(--surface), #ede8e0);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 50px;
  border-bottom: 1px solid var(--border);
  position: relative;
}

.drink-rank {
  position: absolute;
  top: 10px;
  left: 10px;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: var(--ink);
  color: white;
  font-size: 10px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}

.drink-rank.gold {
  background: var(--yellow);
  color: var(--ink);
}

.drink-info {
  padding: 14px 16px;
}

.drink-cat {
  font-size: 10px;
  letter-spacing: 1px;
  text-transform: uppercase;
  color: var(--point);
  font-weight: 600;
  margin-bottom: 4px;
}

.drink-name {
  font-size: 13.5px;
  font-weight: 600;
  color: var(--ink);
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.drink-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.drink-stars {
  font-size: 12px;
  color: var(--yellow);
}

.drink-like {
  font-size: 11.5px;
  color: var(--muted);
}
</style>
