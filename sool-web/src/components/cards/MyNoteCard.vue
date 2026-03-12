<template>
  <div class="my-note-card">
    <router-link class="my-note-link" :to="`/notes/${item.id}`">
      <div class="mn-thumb">{{ item.emoji }}</div>
      <div class="mn-content">
        <div class="mn-drink">{{ item.category }}</div>
        <div class="mn-title">{{ item.title }}</div>
        <div class="mn-date">{{ item.date }}</div>
      </div>
    </router-link>
    <div class="mn-actions">
      <button class="mn-btn" @click="$emit('edit', item)">수정</button>
      <button class="mn-btn del" @click="$emit('delete', item.id)">삭제</button>
    </div>
  </div>
</template>

<script setup>
defineEmits(['edit', 'delete'])

defineProps({
  item: {
    type: Object,
    required: true
  }
})
</script>

<style scoped>
.my-note-card {
  background: var(--white);
  border: 1px solid var(--border);
  border-radius: 10px;
  padding: 16px 18px;
}

.my-note-link {
  display: flex;
  gap: 14px;
}

.mn-thumb {
  width: 52px;
  height: 52px;
  border-radius: 8px;
  background: var(--surface);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
  border: 1px solid var(--border);
}

.mn-drink {
  font-size: 10px;
  text-transform: uppercase;
  letter-spacing: 1px;
  color: var(--point);
  font-weight: 600;
  margin-bottom: 2px;
}

.mn-title {
  font-size: 13.5px;
  font-weight: 600;
  color: var(--ink);
  margin-bottom: 4px;
}

.mn-date {
  font-size: 11px;
  color: var(--muted);
  margin-bottom: 8px;
}

.mn-actions {
  display: flex;
  gap: 6px;
  margin-top: 8px;
}

.mn-btn {
  padding: 3px 10px;
  font-size: 11px;
  border-radius: 4px;
  border: 1px solid var(--border);
  background: white;
  color: var(--sub);
  cursor: pointer;
}

.mn-btn.del {
  border-color: #fbc;
  color: #c0392b;
}
</style>
