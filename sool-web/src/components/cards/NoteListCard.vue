<template>
  <router-link class="note-card" :to="`/notes/${item.noteId}`">
    <div>
      <div class="note-author">
        <div class="avatar" :class="item.avatarClass" 
        :style="{backgroundColor: avatarColor}">
          {{ authorInitial }}
        </div>
        <div>
          <div class="author-name">{{ authorName }}</div>
          <div class="note-date">{{ formatDate(item.createdAt) }}</div>
        </div>
      </div>
      <div class="note-stars">{{ getStars(item.rating) }}</div>
      <div class="note-text">{{ item.content }}</div>
    </div>
    <div class="note-side">
      <div class="note-img">
        <img
          v-if="item?.image?.fileUrl"
          :src="item?.image?.fileUrl"
          alt="note image"
        />
        <span v-else>{{ item.emoji || '🍷' }}</span>
      </div>

      <button
        class="note-like"
        :class="{ liked }"
        :disabled="loading"
        @click.prevent="toggleLike"
      >
        {{ liked ? '♥' : '♡' }} {{ likeCount }}
      </button>
    </div>
  </router-link>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useAuthStore } from '@/stores/authStore'
import { useRouter, useRoute } from 'vue-router'
import { getNoteLike, insertNoteLike, deleteNoteLike } from '@/api/likeApi'

const props = defineProps({
  item: {
    type: Object,
    required: true
  }
})

const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()

const liked = ref(false)
const likeCount = ref(props.item.likeCount || 0)
const loading = ref(false)

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

const authorName = computed(() => {
  const name = props.item.userName
  return name && name.trim() !== '' ? name : '작성자 미상'
})

const authorInitial = computed(() => {
  const name = props.item.userName
  if (!name || name.trim() === '') return '?'
  return name.charAt(0)
})

const avatarColor = computed(() => {
  const name = props.item.userName || '작성자 미상'

  let hash = 0
  for (let i = 0; i < name.length; i++) {
    hash = name.charCodeAt(i) + ((hash << 5) - hash)
  }

  const hue = hash % 360

  return `hsl(${hue}, 30%, 40%)`
})

//좋아요 상태
const fetchLikeStatus = async () => {
  try {
    const res = await getNoteLike(props.item.noteId)
    liked.value = !!res.data.liked
  } catch {
    liked.value = false
  }
}

//좋아요 표시
const toggleLike = async () => {
  if (loading.value) return

  if (!authStore.isLogin) {
    alert('로그인을 먼저 해주세요.')
    router.push({
      path: '/login',
      query: { redirect: route.fullPath }
    })
    return
  }

  loading.value = true

  try {
    if (liked.value) {
      await deleteNoteLike(props.item.noteId)
      likeCount.value--
    } else {
      await insertNoteLike(props.item.noteId)
      likeCount.value++
    }

    liked.value = !liked.value
  } catch (e) {
    console.log('노트 좋아요 실패', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (authStore.isLogin) {
    fetchLikeStatus()
  }
})
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
  overflow: hidden;
  font-size: 26px;
}

.note-img img{
  width: 100%;
  height: 100%;
  object-fit: cover;
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
