<template>
  
  <article class="liked-card">
    <router-link class="note-card" :to="`/notes/${displayItem.noteId}`">
      <div class="liked-card-top">
        <div class="thumb-wrap">
          <img
            v-if="displayItem.imageUrl"
            :src="displayItem.imageUrl"
            :alt="displayItem.title || displayItem.drinkName"
            class="thumb"
          />
          <div v-else class="thumb empty-thumb">
            <span class="empty-emoji">{{ displayItem.emoji }}</span>
          </div>
        </div>

        <div class="card-body">
          <p class="sub-text">{{ displayItem.drinkName || '주류 이름 없음' }}</p>
          <h4 class="card-title">{{ displayItem.title || '제목 없음' }}</h4>

          <div class="meta-row">
            <span class="rating">★ {{ formatNumber(displayItem.rating) }}</span>
            <span class="date">{{ formatDate(displayItem.likedAt || displayItem.createdAt) }}</span>
          </div>
        </div>
      </div>
    </router-link>

    <div class="card-actions">
      <button
        type="button"
        class="action-btn unlike-btn"
        @click.stop="$emit('unlike', displayItem.noteId)"
      >
        좋아요 취소
      </button>
    </div>
  </article>
  
</template>

<script setup>
import { computed } from 'vue'
import { categories } from '@/mock/soolData'

const props = defineProps({
  item: {
    type: Object,
    default: () => ({})
  }
})

defineEmits(['unlike'])

const displayItem = computed(() => {
  const categoryData =
    categories.find(e => e.name === props.item.categoryCode)

  return {
    ...props.item,
    emoji: categoryData ? categoryData.emoji : '🍹'
  }
})

function formatNumber(value) {
  if (value === null || value === undefined || value === '') return '0.0'
  return Number(value).toFixed(1)
}

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
.liked-card {
  background: #fff;
  border: 1px solid #ece3da;
  border-radius: 18px;
  overflow: hidden;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.04);
}

.liked-card-top {
  cursor: pointer;
}

.thumb-wrap {
  width: 100%;
  aspect-ratio: 16 / 10;
  background: #f6f2ed;
  overflow: hidden;
}

.thumb {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.empty-thumb {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #aa9d91;
  font-size: 13px;
}

.empty-emoji {
  font-size: 40px;
}

.card-body {
  padding: 14px;
}

.sub-text {
  margin: 0 0 6px;
  font-size: 13px;
  color: #9a8f84;
}

.card-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--ink);
  line-height: 1.4;
  min-height: 44px;
}

.meta-row {
  margin-top: 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.rating {
  font-size: 14px;
  font-weight: 600;
  color: #7a5c45;
}

.date {
  font-size: 12px;
  color: #a59a8f;
}

.card-actions {
  padding: 0 14px 14px;
}

.action-btn {
  width: 100%;
  height: 38px;
  border: 0;
  border-radius: 10px;
  font-size: 13px;
  cursor: pointer;
  transition: 0.2s ease;
}

.unlike-btn {
  background: #f3eee8;
  color: #7a5c45;
}

.unlike-btn:hover {
  background: #eadfd3;
}
</style>