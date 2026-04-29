<template>
  <div
    class="my-note-card"
    :class="{ reported: displayItem.reported }"
  >
    <component
      :is="displayItem.reported ? 'div' : 'router-link'"
      class="my-note-link"
      v-bind="displayItem.reported ? {} : { to: `/notes/${displayItem.noteId}` }"
    >
      <div class="mn-thumb" :class="{ reported: displayItem.reported }">
        <img
          v-if="displayItem.image?.fileUrl && !displayItem.reported"
          :src="displayItem.image?.fileUrl"
          :alt="displayItem.drinkName"
          class="mn-thumb-img"
        />
        <span v-else>{{ displayItem.reported ? '🚫' : displayItem.emoji }}</span>
      </div>

      <div class="mn-content">
        <template v-if="displayItem.reported">
          <div class="reported-box">
            <div class="reported-title">신고 처리된 노트입니다</div>
            <div class="reported-meta">{{ displayItem.typeName }} - {{ displayItem.drinkName }}</div>
            <div class="reported-meta">작성일: {{ formatDate(displayItem.createdAt) }}</div>
          </div>
        </template>

        <template v-else>
          <div class="mn-drink">{{ displayItem.typeName }} - {{ displayItem.drinkName }}</div>
          <div class="mn-title">{{ displayItem.title }}</div>
          <div class="mn-date">{{ formatDate(displayItem.createdAt) }}</div>
        </template>
      </div>
    </component>

    <div v-if="!displayItem.reported" class="mn-actions">
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
    reported: props.item.reported === true || props.item.reported === 1,
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
  display: flex;
  justify-content: space-between;
  align-items: center;

  background: var(--white);
  border: 1px solid var(--border);
  border-radius: 10px;
  padding: 16px 18px;
}

.my-note-card.reported {
  background: #f3f3f3;
  border-color: #d9d9d9;
}

.my-note-link {
  display: flex;
  gap: 14px;
  flex: 1;
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

.mn-thumb.reported {
  background: #e3e3e3;
  color: #8a8a8a;
}

.mn-thumb-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.mn-content {
  flex: 1;
}

.mn-drink {
  font-size: 10px;
  text-transform: uppercase;
  letter-spacing: 1px;
  color: var(--point);
  font-weight: 600;
  margin-bottom: 2px;
  white-space: pre-wrap;
  word-break: break-word;
  overflow-wrap: anywhere;
}

.mn-title {
  font-size: 13.5px;
  font-weight: 600;
  color: var(--ink);
  margin-bottom: 4px;
  white-space: pre-wrap;
  word-break: break-word;
  overflow-wrap: anywhere;
}

.mn-date {
  font-size: 11px;
  color: var(--muted);
  margin-bottom: 8px;
}

.mn-actions {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 6px;
  margin-left: 12px;
  flex-shrink: 0;
  white-space: nowrap;
  min-width: 90px;
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

.reported-box {
  background: #e7e7e7;
  border: 1px solid #d6d6d6;
  border-radius: 8px;
  padding: 10px 12px;
}

.reported-title {
  font-size: 13px;
  font-weight: 700;
  color: #666;
  margin-bottom: 4px;
}

.reported-meta {
  font-size: 11px;
  color: #7b7b7b;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
  overflow-wrap: anywhere;
}
</style>