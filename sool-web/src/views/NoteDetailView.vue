<template>
  <div class="page-wrap">
    <PageNav :links="navLinks" />

    <div class="nd-wrap">
      <main class="nd-main">
        <div class="nd-top">
          <div>
            <div class="nd-author-row">
              <div class="avatar">{{ authorInitial }}</div>
              <div>
                <div class="nd-author-name">{{ authorName }}</div>
                <div class="nd-date">{{ formattedDate }}</div>
              </div>
            </div>
          </div>
        </div>

        <div class="nd-drink-pill">{{ drinkEmoji }} {{ drinkName }}</div>
        <div class="nd-title">{{ noteTitle }}</div>
        <div class="nd-stars">{{ star }}</div>

        <div v-if="noteImageUrl" class="nd-photo nd-photo-img-wrap">
          <img :src="noteImageUrl" alt="테이스팅 노트 이미지" class="nd-photo-img" />
        </div>
        <div v-else class="nd-photo">{{ drinkEmoji }}</div>

        <div class="nd-text">{{ noteContent }}</div>

        <div class="nd-like-row">
          <button
            class="like-btn"
            :class="{ liked }"
            :disabled="likeLoading"
            @click="toggleLike"
          >
            {{ liked ? '♥' : '♡' }} {{ likeCount }}
          </button>
          <button class="report-btn" @click="openReportModal">
            신고
          </button>
          <template v-if="isOwner">
              <button type="button" class="nd-action-btn" @click="goEdit">
                수정
              </button>

              <button type="button" class="nd-action-btn danger" @click="removeNote">
                삭제
              </button>
          </template>
        </div>
      </main>

      <aside class="nd-side">
        <section class="flavor-section">
          <div class="nd-flavor-title">맛 프로파일</div>

          <template v-if="metricList.length > 0">
            <div v-for="item in metricList" :key="item.metricCode" class="nd-flavor-row">
              <span class="nfl">{{ item.metricName }}</span>
              <div class="nfb">
                <div class="nff" :style="{ width: (item.score / 5) * 100 + '%' }"></div>
              </div>
              <span class="nfv">{{ item.score }}</span>
            </div>
          </template>

          <div v-else class="nd-flavor-empty">
              등록된 맛 프로파일이 없습니다.
          </div>
        </section>
        <div class="nd-comments">
          <h4>댓글</h4>
          <CommentItem v-for="comment in commentList" :key="comment.id" :item="comment" />
          <form class="comment-inp" @submit.prevent="submitComment">
            <input v-model="newComment" placeholder="댓글을 입력하세요" />
            <button class="cm-send">등록</button>
          </form>
        </div>
      </aside>
    </div>
  </div>
  
  <ReportModal
    v-model="reportModalOpen"
    objType="NOTE"
    :objId="Number(route.params.id)"
    @success="fetchNoteDetail"
  />
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import PageNav from '../components/common/PageNav.vue'
import CommentItem from '../components/cards/CommentItem.vue'
import { categories, comments } from '../mock/soolData'
import { getNoteDetail } from '@/api/noteApi'
import { getNoteLike, insertNoteLike, deleteNoteLike } from '@/api/likeApi'
import ReportModal from '../components/common/ReportModal.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const liked = ref(false)
const likeLoading = ref(false)
const likeCount = ref(0)

const metricList = ref([])

const newComment = ref('')
const commentList = ref([...comments])

const noteDetail = ref(null)

const navLinks = computed(() => [
  { label: '홈', to: '/' },
  { label: `${noteDetail.value?.drinkName || ''}`, to: `/drinks/${noteDetail.value?.drinkId}` },
  { label: `${noteDetail.value?.drinkName || ''} 테이스팅 노트`, to: route.fullPath, active: true }
])

const authorName = computed(() => {
  return (
    noteDetail.value?.userName ||
    noteDetail.value?.name ||
    noteDetail.value?.loginId ||
    '작성자 미상'
  )
})

const authorInitial = computed(() => {
  return String(authorName.value || '작').trim().charAt(0) || '작'
})

const formattedDate = computed(() => {
  const value = noteDetail.value?.createdAt || noteDetail.value?.created_at
  if (!value) return '날짜 정보 없음'

  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '날짜 정보 없음'

  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')

  return `${year}. ${month}. ${day}`
})

const drinkName = computed(() => {
  return (
    noteDetail.value?.drinkName ||
    noteDetail.value?.drink_name ||
    '주류 정보 없음'
  )
})

const noteTitle = computed(() => {
  return noteDetail.value?.title || '제목이 없는 테이스팅 노트'
})

const noteContent = computed(() => {
  return (
    noteDetail.value?.content ||
    '아직 작성된 테이스팅 노트 내용이 없습니다.'
  )
})

const star = computed(() => {
  const rating = Number(noteDetail.value?.rating ?? 0)
  if (!rating) return '☆☆☆☆☆'

  const full = Math.max(0, Math.min(5, Math.round(rating)))
  return '★'.repeat(full) + '☆'.repeat(5 - full)
})

const noteImageUrl = computed(() => {
  return (
    noteDetail.value?.imageUrl ||
    noteDetail.value?.image_path ||
    noteDetail.value?.noteImage ||
    ''
  )
})

const fetchLikeStatus = async () => {
  const noteId = route.params.id
  if (!noteId || !authStore.isLogin) return

  try {
    const res = await getNoteLike(noteId)
    liked.value = !!res.data.liked
  } catch (error) {
    console.log('노트 좋아요 상태 조회 실패', error)
    liked.value = false
  }
}

const toggleLike = async () => {
  const noteId = route.params.id
  if (!noteId || likeLoading.value) return

  if (!authStore.isLogin) {
    alert('로그인을 먼저 해주세요.')
    router.push({
      path: '/login',
      query: { redirect: route.fullPath }
    })
    return
  }

  likeLoading.value = true

  try {
    if (liked.value) {
      await deleteNoteLike(noteId)
      likeCount.value = Math.max(0, likeCount.value - 1)
    } else {
      await insertNoteLike(noteId)
      likeCount.value += 1
    }

    liked.value = !liked.value
  } catch (error) {
    console.log('노트 좋아요 처리 실패', error)
  } finally {
    likeLoading.value = false
  }
}

//노트 디테일 정보
const fetchNoteDetail = async () => {
  try {
    const noteId = route.params.id
    console.log('noteId : ' + noteId)

    const res = await getNoteDetail(noteId)
    console.log(res.data)

    noteDetail.value = res.data || null
    metricList.value = res.data.metricList || []
    likeCount.value = Number(res.data?.likeCount ?? 0)

    if (authStore.isLogin) {
      await fetchLikeStatus()
    }
  } catch (error) {
    console.log('노트 상세 조회 실패', error)
    noteDetail.value = null
    likeCount.value = 0
  }
}

const drinkEmoji = computed(() => {
  const code = noteDetail.value?.categoryCode

  const emojiData = categories.find(e => e.name === code)

  return emojiData ? emojiData.emoji : '🍹'
})

const submitComment = () => {
  const text = newComment.value.trim()
  if (!text) return

  commentList.value.push({
    id: Date.now(),
    authorInitial: '나',
    author: 'me',
    date: '방금',
    text,
    likes: 0,
    replies: []
  })

  newComment.value = ''
}

const reportModalOpen = ref(false)

const openReportModal = () => {
  if (!authStore.isLogin) {
    alert('로그인을 먼저 해주세요.')
    router.push({
      path: '/login',
      query: { redirect: route.fullPath }
    })
    return
  }

  reportModalOpen.value = true
}

const isOwner = computed(() => {
  if (!authStore.isLogin) return false
  if (!noteDetail.value) return false

  return Number(authStore.user?.userId) === Number(noteDetail.value.userId)
})

function goEdit() {
  if (!noteDetail.value?.noteId) return
  router.push(`/notes/${noteDetail.value.noteId}/edit`)
}

async function removeNote() {
  if (!noteDetail.value?.noteId) return

  const ok = window.confirm('정말 삭제하시겠습니까?')
  if (!ok) return

  try {
    await deleteNote(noteDetail.value.noteId)
    alert('삭제되었습니다.')
    router.push(`/drinks/${noteDetail.value.drinkId}`)
  } catch (error) {
    console.log('노트 삭제 실패', error)
    alert('삭제에 실패했습니다.')
  }
}

onMounted(() => {
  fetchNoteDetail()
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

.nd-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--ink);
  margin-bottom: 12px;
  line-height: 1.4;
}

.nd-stars {
  font-size: 18px;
  color: var(--yellow);
  margin-bottom: 20px;
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

.nd-photo-img-wrap {
  padding: 0;
  overflow: hidden;
}

.nd-photo-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
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

.flavor-section {
  padding-bottom: 32px;
  margin-bottom: 32px;
  border-bottom: 3px solid var(--border);
}

.nd-flavor-empty {
  min-height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px dashed var(--border);
  border-radius: 10px;
  background: white;
  font-size: 13px;
  color: var(--muted);
}

.nd-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nd-action-btn {
  border: 1px solid #ddd;
  background: #fff;
  border-radius: 8px;
  padding: 8px 12px;
  font-size: 14px;
  cursor: pointer;
}

.nd-action-btn.danger {
  color: #c0392b;
  border-color: #f1b5ae;
}
</style>