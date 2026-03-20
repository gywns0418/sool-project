<template>
  <div class="page-wrap">
    <PageNav :links="navLinks" />

    <div class="nd-wrap">
      <main class="nd-main">
        <div v-if="loading" class="state-box">불러오는 중입니다.</div>
        <div v-else-if="!noteDetail" class="state-box">노트 정보를 찾을 수 없습니다.</div>

        <template v-else>
          <div class="nd-top">
            <div class="nd-author-row">
              <div class="avatar">{{ authorInitial }}</div>
              <div>
                <div class="nd-author-name">{{ authorName }}</div>
                <div class="nd-date">{{ formattedDate }}</div>
              </div>
            </div>
          </div>

          <div class="nd-drink-pill">{{ drinkLabel }}</div>
          <div class="nd-stars">{{ starText }}</div>

          <div v-if="noteImageUrl" class="nd-photo nd-photo-img-wrap">
            <img :src="noteImageUrl" alt="테이스팅 노트 이미지" class="nd-photo-img" />
          </div>
          <div v-else class="nd-photo">🍷</div>

          <div class="nd-text">{{ noteContent }}</div>

          <div class="nd-like-row">
            <button
              class="like-btn"
              :class="{ liked: liked }"
              :disabled="likeLoading"
              @click="toggleLike"
            >
              {{ liked ? '♥' : '♡' }} {{ likeCount }}
            </button>

            <button
              class="report-btn"
              :disabled="reportLoading || reported"
              @click="reportNote"
            >
              {{ reported ? '신고 완료' : '신고' }}
            </button>
          </div>
        </template>
      </main>

      <aside class="nd-side">
        <div class="nd-flavor-title">맛 프로파일</div>

        <div v-if="flavorList.length === 0" class="empty-text">
          등록된 맛 프로파일이 없습니다.
        </div>

        <div v-for="item in flavorList" :key="item.label" class="nd-flavor-row">
          <span class="nfl">{{ item.label }}</span>
          <div class="nfb">
            <div class="nff" :style="{ width: item.width }"></div>
          </div>
          <span class="nfv">{{ item.value }}</span>
        </div>

        <div class="nd-comments">
          <h4>댓글</h4>

          <div v-if="commentLoading" class="empty-text">댓글을 불러오는 중입니다.</div>
          <div v-else-if="commentList.length === 0" class="empty-text">아직 댓글이 없습니다.</div>

          <CommentItem
            v-else
            v-for="comment in commentList"
            :key="comment.id"
            :item="comment"
          />

          <form class="comment-inp" @submit.prevent="submitComment">
            <input v-model="newComment" placeholder="댓글을 입력하세요" />
            <button class="cm-send" :disabled="commentSubmitLoading">등록</button>
          </form>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import PageNav from '../components/common/PageNav.vue'
import CommentItem from '../components/cards/CommentItem.vue'
import {
  getNoteDetail,
  getNoteFlavors,
  getNoteComments,
  createNoteComment,
  likeNote,
  unlikeNote,
  reportNoteApi
} from '@/api/noteApi'

const route = useRoute()

const loading = ref(false)
const likeLoading = ref(false)
const reportLoading = ref(false)
const commentLoading = ref(false)
const commentSubmitLoading = ref(false)

const noteDetail = ref(null)
const flavorList = ref([])
const commentList = ref([])
const liked = ref(false)
const reported = ref(false)
const likeCount = ref(0)
const newComment = ref('')

const noteId = computed(() => Number(route.params.noteId || route.params.id))

const navLinks = computed(() => [
  { label: '홈', to: '/' },
  { label: `테이스팅 노트 #${noteId.value || 0}`, to: route.fullPath, active: true }
])

const authorName = computed(() => {
  return (
    noteDetail.value?.userName ||
    noteDetail.value?.name ||
    noteDetail.value?.authorName ||
    noteDetail.value?.loginId ||
    noteDetail.value?.writerName ||
    '알 수 없음'
  )
})

const authorInitial = computed(() => {
  return String(authorName.value).trim().charAt(0) || '?'
})

const formattedDate = computed(() => {
  return formatDate(noteDetail.value?.createdAt || noteDetail.value?.created_at)
})

const drinkLabel = computed(() => {
  const drinkName =
    noteDetail.value?.drinkName ||
    noteDetail.value?.drink_name ||
    noteDetail.value?.drinkNameKr ||
    noteDetail.value?.drink_name_kr ||
    '주류 정보 없음'

  return `🍷 ${drinkName}`
})

const noteImageUrl = computed(() => {
  return (
    noteDetail.value?.imageUrl ||
    noteDetail.value?.image_path ||
    noteDetail.value?.photoUrl ||
    noteDetail.value?.noteImage ||
    ''
  )
})

const noteContent = computed(() => {
  return (
    noteDetail.value?.content ||
    noteDetail.value?.noteContent ||
    noteDetail.value?.review ||
    ''
  )
})

const starText = computed(() => {
  const rating = Number(noteDetail.value?.rating ?? 0)
  const full = Math.max(0, Math.min(5, Math.round(rating)))
  return '★'.repeat(full) + '☆'.repeat(5 - full)
})

function formatDate(value) {
  if (!value) return ''

  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)

  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')

  return `${year}. ${month}. ${day}`
}

function mapFlavorList(items) {
  return (items || []).map((item) => {
    const label = item.label || item.metricName || item.profileName || item.name || ''
    const rawValue = Number(item.value ?? item.score ?? item.amount ?? 0)
    const clampedValue = Math.max(0, Math.min(5, rawValue))

    return {
      label,
      value: clampedValue,
      width: `${(clampedValue / 5) * 100}%`
    }
  })
}

function mapComment(comment) {
  const replies = comment.replies || comment.children || []

  return {
    id: comment.commentId || comment.comment_id || comment.id,
    authorInitial: String(
      comment.userName || comment.authorName || comment.loginId || comment.writerName || '?'
    ).trim().charAt(0),
    author:
      comment.userName ||
      comment.authorName ||
      comment.loginId ||
      comment.writerName ||
      '알 수 없음',
    date: formatDate(comment.createdAt || comment.created_at),
    text: comment.content || comment.comment || '',
    likes: Number(comment.likeCount ?? comment.like_count ?? 0),
    replies: replies.map(mapComment)
  }
}

async function fetchNoteDetailData() {
  loading.value = true

  try {
    const { data } = await getNoteDetail(noteId.value)
    noteDetail.value = data
    liked.value = Boolean(data?.liked)
    reported.value = Boolean(data?.reported)
    likeCount.value = Number(data?.likeCount ?? data?.like_count ?? 0)
  } catch (error) {
    noteDetail.value = null
  } finally {
    loading.value = false
  }
}

async function fetchFlavorList() {
  try {
    const { data } = await getNoteFlavors(noteId.value)
    flavorList.value = mapFlavorList(Array.isArray(data) ? data : data?.list || [])
  } catch (error) {
    flavorList.value = []
  }
}

async function fetchComments() {
  commentLoading.value = true

  try {
    const { data } = await getNoteComments(noteId.value)
    const list = Array.isArray(data) ? data : data?.list || []
    commentList.value = list.map(mapComment)
  } catch (error) {
    commentList.value = []
  } finally {
    commentLoading.value = false
  }
}

async function fetchAll() {
  if (!noteId.value) return
  await Promise.all([fetchNoteDetailData(), fetchFlavorList(), fetchComments()])
}

async function toggleLike() {
  if (!noteDetail.value || likeLoading.value) return

  likeLoading.value = true

  try {
    if (liked.value) {
      await unlikeNote(noteId.value)
      liked.value = false
      likeCount.value = Math.max(0, likeCount.value - 1)
    } else {
      await likeNote(noteId.value)
      liked.value = true
      likeCount.value += 1
    }
  } finally {
    likeLoading.value = false
  }
}

async function reportNote() {
  if (!noteDetail.value || reportLoading.value || reported.value) return

  reportLoading.value = true

  try {
    await reportNoteApi(noteId.value)
    reported.value = true
  } finally {
    reportLoading.value = false
  }
}

async function submitComment() {
  const content = newComment.value.trim()
  if (!content || commentSubmitLoading.value) return

  commentSubmitLoading.value = true

  try {
    await createNoteComment(noteId.value, { content })
    newComment.value = ''
    await fetchComments()
  } finally {
    commentSubmitLoading.value = false
  }
}

watch(
  () => route.params.id,
  () => {
    fetchAll()
  }
)

watch(
  () => route.params.noteId,
  () => {
    fetchAll()
  }
)

onMounted(() => {
  fetchAll()
})
</script>

<style scoped>
.page-wrap {
  background: var(--white);
  overflow: hidden;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.12);
}

.nd-wrap {
  display: grid;
  grid-template-columns: 1fr 400px;
  min-height: 700px;
}

.nd-main {
  padding: 40px 48px;
  border-right: 1px solid var(--border);
}

.nd-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 24px;
}

.nd-author-row {
  display: flex;
  align-items: center;
  gap: 10px;
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

.nd-author-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--ink);
}

.nd-date {
  font-size: 12px;
  color: var(--muted);
}

.nd-drink-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  background: #fdf3ef;
  border: 1px solid #f0c9b9;
  border-radius: 100px;
  font-size: 12.5px;
  color: var(--point);
  font-weight: 500;
  margin-bottom: 16px;
}

.nd-stars {
  font-size: 18px;
  color: var(--yellow);
  margin-bottom: 16px;
}

.nd-photo {
  width: 100%;
  height: 220px;
  background: var(--surface);
  border-radius: 10px;
  border: 1px solid var(--border);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 60px;
  margin-bottom: 20px;
}

.nd-text {
  font-size: 14.5px;
  color: var(--sub);
  line-height: 1.8;
  font-weight: 300;
  margin-bottom: 24px;
}

.nd-like-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.like-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 18px;
  border: 1.5px solid var(--border);
  background: white;
  border-radius: 100px;
  font-size: 13px;
  color: var(--muted);
  font-weight: 600;
  cursor: pointer;
}

.like-btn.liked {
  border: 1.5px solid var(--point);
  background: #fdf3ef;
  color: var(--point);
}

.report-btn {
  font-size: 12px;
  color: var(--muted);
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px 12px;
}

.nd-side {
  background: var(--surface);
  padding: 32px 26px;
}

.nd-flavor-title,
.nd-comments h4 {
  font-size: 11px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  color: var(--muted);
  font-weight: 600;
  margin-bottom: 16px;
}

.nd-flavor-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
}

.nfl {
  font-size: 12.5px;
  color: var(--sub);
  width: 48px;
}

.nfb {
  flex: 1;
  height: 6px;
  background: var(--border);
  border-radius: 3px;
  overflow: hidden;
}

.nff {
  height: 100%;
  background: var(--point2);
  border-radius: 3px;
}

.nfv {
  font-size: 12px;
  font-weight: 700;
  color: var(--ink);
}

.nd-comments {
  margin-top: 28px;
}

.comment-inp {
  margin-top: 14px;
  display: flex;
  gap: 6px;
}

.comment-inp input {
  flex: 1;
  height: 36px;
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 0 12px;
  font-size: 12.5px;
  background: white;
  color: var(--ink);
}

.cm-send {
  height: 36px;
  padding: 0 14px;
  background: var(--point);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}
</style>
