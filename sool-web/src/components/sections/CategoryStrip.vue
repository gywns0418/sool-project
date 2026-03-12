<template>
  <div class="cat-strip">
    <h3>카테고리로 빠르게 탐색</h3>
    <div class="cat-pills">
      <router-link
        v-for="category in items"
        :key="category.id"
        class="cat-pill"
        :to="{ path: '/drinks', query: { category: category.name } }"
      >
        <span class="cat-emoji">{{ category.emoji }}</span>
        {{ category.name }}
        <span class="cat-count">{{ category.count }}</span>
      </router-link>
    </div>
  </div>
</template>

<script setup>
defineProps({
  items: {
    type: Array,
    default: () => []
  }
})
</script>

<style scoped>
.cat-strip {
  padding: 40px 60px;
  border-bottom: 1px solid var(--border);
  background: var(--white);
}

.cat-strip h3 {
  font-family: 'Playfair Display', serif;
  font-size: 20px;
  font-weight: 400;
  color: var(--ink);
  letter-spacing: -0.3px;
  margin-bottom: 20px;
}

.cat-pills {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;
}

.cat-pill {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 18px 12px 14px;
  border-radius: 12px;
  border: 1.5px solid var(--border);
  font-size: 13px;
  font-weight: 500;
  color: var(--sub);
  background: var(--surface);
  cursor: pointer;
  text-align: center;
}

.cat-pill:hover {
  border-color: var(--point);
  color: var(--point);
  background: #fdf3ef;
  font-weight: 600;
}

.cat-emoji {
  font-size: 28px;
  line-height: 1;
}

.cat-count {
  font-size: 10px;
  color: var(--muted);
  margin-top: 2px;
}
</style>
