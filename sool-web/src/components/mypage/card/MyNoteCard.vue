<template>
  <div class="my-note-card">
    <router-link class="my-note-link" :to="`/notes/${displayItem.noteId}`">
      <div class="mn-thumb">{{ displayItem.emoji }}</div>
      <div class="mn-content">
        <div class="mn-drink">{{ displayItem.typeName }}</div>
        <div class="mn-title">{{ displayItem.title }}</div>
        <div class="mn-date">{{ formatDate(displayItem.createdAt) }}</div>
      </div>
    </router-link>
    <div class="mn-actions">
      <button class="mn-btn" @click="$emit('edit', displayItem.noteId)">수정</button>
      <button class="mn-btn del" @click="$emit('delete', displayItem.noteId)">삭제</button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { categories } from '@/mock/soolData'

defineEmits(['edit', 'delete'])

const props = defineProps({
  item: {
    type: Object,
    required: true
  }
})

const displayItem = computed(() => {
  const categoryData =
    categories.find(e => e.name === props.item.categoryCode)

  return {
    ...props.item,
    emoji: categoryData ? categoryData.emoji : '🍹'
  }
})

function formatDate(value) {
  if (!value) return ''

  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value

  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}.${month}.${day}`
}
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
