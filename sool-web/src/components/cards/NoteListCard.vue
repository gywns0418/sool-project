<template>
  <router-link class="note-card" :to="`/notes/${item.noteId}`">
    <div>
      <div class="note-author">
        <div class="avatar" :class="item.avatarClass">{{ item.authorInitial }}</div>
        <div>
          <div class="author-name">{{ item.userName }}</div>
          <div class="note-date">{{ formatDate(item.createdAt) }}</div>
        </div>
      </div>
      <div class="note-stars">{{ getStars(item.rating) }}</div>
      <div class="note-text">{{ item.content }}</div>
    </div>
    <div class="note-side">
      <div class="note-img">{{ item.emoji || '🍷'}}</div>
      <button class="note-like" :class="{ liked }" @click.prevent="toggleLike">{{ liked ? '♥' : '♡' }} {{ likeCount || 0}}</button>
    </div>
  </router-link>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  item: {
    type: Object,
    required: true
  }
})

function getStars(rating) {
  const maxStars = 5
  return '★'.repeat(rating) + '☆'.repeat(maxStars - rating)
}

function formatDate(dateStr) {
  const date = new Date(dateStr)

  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')

  return `${year}.${month}.${day}`
}

const liked = ref(false)
const likeCount = ref(props.item.likes)

const toggleLike = () => {
  liked.value = !liked.value
  likeCount.value += liked.value ? 1 : -1
}
</script>

<style scoped>
.note-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 10px;
  padding: 20px 22px;
  margin-bottom: 12px;
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 16px;
  align-items: flex-start;
}

.note-author {
  display: flex;
  align-items: center;
  gap: 9px;
  margin-bottom: 10px;
}

.avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--ink);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 600;
}

.avatar.a2 {
  background: var(--point2);
}

.author-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--ink);
}

.note-date {
  font-size: 11px;
  color: var(--muted);
}

.note-stars {
  color: var(--yellow);
  font-size: 13px;
  margin-bottom: 7px;
}

.note-text {
  font-size: 13px;
  color: var(--sub);
  line-height: 1.65;
  font-weight: 300;
}

.note-side {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.note-img {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  background: var(--border);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
}

.note-like {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--muted);
  padding: 4px 10px;
  border: 1px solid var(--border);
  border-radius: 100px;
  background: white;
  cursor: pointer;
}

.note-like.liked{
  border-color: var(--point);
  color: var(--point);
  background: #fdf3ef;
}

.note-like:hover{
  border-color: var(--point);
  color: var(--point);
  background: #fdf3ef;
}
</style>
