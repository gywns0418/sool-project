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

        <div class="nd-drink-pill">🍷 {{ drinkName }}</div>
        <div class="nd-title">{{ noteTitle }}</div>
        <div class="nd-stars">{{ starText }}</div>

        <div v-if="noteImageUrl" class="nd-photo nd-photo-img-wrap">
          <img :src="noteImageUrl" alt="테이스팅 노트 이미지" class="nd-photo-img" />
        </div>
        <div v-else class="nd-photo">🍷</div>

        <div class="nd-text">{{ noteContent }}</div>

        <div class="nd-like-row">
          <button class="like-btn" :class="{ liked: liked }" @click="liked = !liked">
            {{ liked ? '♥' : '♡' }} {{ likeCount || 0}}
          </button>
          <button class="report-btn" @click="reported = !reported">
            {{ reported ? '신고 완료' : '신고' }}
          </button>
        </div>
      </main>

      <aside class="nd-side">
        <div class="nd-flavor-title">맛 프로파일</div>
        <div v-for="item in noteDetailFlavor" :key="item.label" class="nd-flavor-row">
          <span class="nfl">{{ item.label }}</span>
          <div class="nfb">
            <div class="nff" :style="{ width: item.width }"></div>
          </div>
          <span class="nfv">{{ item.value }}</span>
        </div>

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
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import PageNav from '../components/common/PageNav.vue'
import CommentItem from '../components/cards/CommentItem.vue'
import { comments, noteDetailFlavor } from '../mock/soolData'
import { getNoteDetail } from '@/api/noteApi'

const route = useRoute()

const liked = ref(false)
const reported = ref(false)
const newComment = ref('')
const commentList = ref([...comments])

const noteDetail = ref(null)

const navLinks = computed(() => [
  { label: '홈', to: '/' },
  { label: `테이스팅 노트 #${route.params.id || 1}`, to: route.fullPath, active: true }
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

const starText = computed(() => {
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

const fetchNoteDetail = async () => {
  try {
    const noteId = route.params.id
    console.log("noteId : "+noteId)
    const res = await getNoteDetail(noteId)
    console.log(res.data)
    noteDetail.value = res.data || null
  } catch (error) {
    console.log('노트 상세 조회 실패', error)
    noteDetail.value = null
  }
}

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
</style>