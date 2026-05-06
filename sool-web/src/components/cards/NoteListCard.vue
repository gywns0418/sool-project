<template>
  <component
    :is="item.reported ? 'div' : 'router-link'"
    class="note-card"
    :class="{ reported: item.reported }"
    v-bind="item.reported ? {} : { to: `/notes/${item.noteId}` }"
  >
    <div>
      <template v-if="item.reported">
        <div class="reported-box">
          <div class="reported-title">🚫 신고 처리된 노트입니다</div>
          <div class="reported-meta">작성자: {{ authorName }}</div>
          <div class="reported-meta">작성일: {{ formatDate(item.createdAt) }}</div>
        </div>
      </template>

      <template v-else>
        <div class="note-author">
          <div
            class="avatar"
            :class="item.avatarClass"
            :style="{ backgroundColor: avatarColor }"
          >
            {{ authorInitial }}
          </div>
          <div>
            <div class="author-name">{{ authorName }}</div>
            <div class="note-date">{{ formatDate(item.createdAt) }}</div>
          </div>
        </div>

        <div class="note-stars">{{ getStars(item.rating) }}</div>
        <div class="note-text">{{ item.content }}</div>
      </template>
    </div>

    <div class="note-side">
      <div class="note-img" :class="{ reported: item.reported }">
        <img
          v-if="item?.image?.fileUrl && !item.reported"
          :src="item?.image?.fileUrl"
          alt="note image"
        />
        <span v-else>{{ drinkEmoji || '🍹' }}</span>
      </div>

      <button
        class="note-like"
        :class="{ liked, reported: item.reported }"
        :disabled="loading || item.reported"
        @click.prevent="toggleLike"
      >
        {{ liked ? '♥' : '♡' }} {{ item.reported ? '-' : likeCount }}
      </button>
    </div>
  </component>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useAuthStore } from '@/stores/authStore'
import { useRouter, useRoute } from 'vue-router'
import { getNoteLike, insertNoteLike, deleteNoteLike } from '@/api/likeApi'
import { categories } from '@/mock/soolData'

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

watch(
  () => props.item,
  (newItem) => {
    liked.value = !!newItem?.liked
    likeCount.value = newItem?.likeCount ?? 0
  },
  { deep: true, immediate: true }
)

const drinkEmoji = computed(() => {
  const code = props.item?.categoryCode
  const emojiData = categories.find(item => item.name === code)
  return emojiData ? emojiData.emoji : '🍹'
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

  const hue = ((hash % 360) + 360) % 360

  return `hsl(${hue}, 30%, 40%)`
})

const moveToLogin = () => {
  alert('로그인이 필요합니다. 다시 로그인해주세요.')

  authStore.user = null
  authStore.initialized = true

  router.replace(`/login?redirect=${encodeURIComponent(route.fullPath)}`)
}

const handleAuthError = (error) => {
  const status = error?.response?.status

  if (status === 401 || status === 403) {
    moveToLogin()
    return true
  }

  return false
}

const fetchLikeStatus = async () => {
  if (props.item.reported) {
    liked.value = false
    return
  }

  try {
    const res = await getNoteLike(props.item.noteId)
    liked.value = !!res.data.liked
  } catch (e) {
    if (handleAuthError(e)) return
    liked.value = false
  }
}

const toggleLike = async () => {
  if (props.item.reported) return
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

  const prevLiked = liked.value
  const prevLikeCount = likeCount.value

  try {
    if (liked.value) {
      liked.value = false
      likeCount.value = Math.max(0, prevLikeCount - 1)
      await deleteNoteLike(props.item.noteId)
    } else {
      liked.value = true
      likeCount.value = prevLikeCount + 1
      await insertNoteLike(props.item.noteId)
    }
  } catch (e) {
    liked.value = prevLiked
    likeCount.value = prevLikeCount

    if (handleAuthError(e)) return

    console.log('노트 좋아요 실패', e)
    alert('좋아요 처리 중 오류가 발생했습니다.')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (authStore.isLogin && !props.item.reported) {
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

.note-card.reported {
  background: #f3f3f3;
  border-color: #d9d9d9;
  cursor: default;
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
  white-space: pre-wrap;
  word-break: break-word;
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

.note-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.note-img.reported {
  background: #e3e3e3;
  color: #9a9a9a;
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

.note-like.liked {
  border-color: var(--point);
  color: var(--point);
  background: #fdf3ef;
}

.note-like:hover {
  border-color: var(--point);
  color: var(--point);
  background: #fdf3ef;
}

.note-like.reported,
.note-like.reported:hover {
  border-color: #d9d9d9;
  color: #9a9a9a;
  background: #ececec;
  cursor: default;
}

.reported-box {
  background: #e7e7e7;
  border: 1px solid #d6d6d6;
  border-radius: 8px;
  padding: 14px 16px;
}

.reported-title {
  font-size: 14px;
  font-weight: 700;
  color: #666;
  margin-bottom: 8px;
}

.reported-meta {
  font-size: 12px;
  color: #7b7b7b;
  line-height: 1.6;
}
</style>