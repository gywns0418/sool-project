<template>
  <div class="page-wrap">
    <PageNav :links="navLinks" />

    <div class="nd-wrap">
      <main class="nd-main" :class="{ reported: isReportedNote }">
        <template v-if="isReportedNote">
          <div class="reported-box">
            <div class="reported-title">{{ getReportedMessage(noteDetail?.objType) }}</div>
            <div class="reported-meta">작성자: {{ authorName }}</div>
            <div class="reported-meta">작성일: {{ formattedDate }}</div>
          </div>

          <div class="nd-drink-pill reported-pill">{{ drinkEmoji }} {{ drinkName }}</div>

          <div v-if="noteImageUrl" class="nd-photo nd-photo-img-wrap reported-photo">
            <img :src="noteImageUrl" alt="테이스팅 노트 이미지" class="nd-photo-img" />
          </div>
          <div v-else class="nd-photo reported-photo">{{ drinkEmoji }}</div>

          <div class="nd-like-row">
            <button class="like-btn reported-like" disabled>
              ♥ -
            </button>
          </div>
        </template>

        <template v-else>
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

            <template v-if="!isOwner">
              <button
                class="report-btn"
                @click="openReportModal({
                  objType: 'NOTE',
                  objId: route.params.id
                })"
              >
                신고
              </button>
            </template>

            <template v-if="isOwner">
              <button type="button" class="nd-action-btn" @click="goEdit">
                수정
              </button>

              <button type="button" class="nd-action-btn danger" @click="removeNote">
                삭제
              </button>
            </template>
          </div>
        </template>
      </main>

      <aside class="nd-side">
        <template v-if="isReportedNote">
          <section class="flavor-section">
            <div class="nd-flavor-title">맛 프로파일</div>

            <div class="comment-reported-box">
              <div class="reported-title">🚫 신고 처리된 노트입니다</div>
              <div class="reported-meta">
                신고 처리된 노트의 맛 프로파일은 표시되지 않습니다.
              </div>
            </div>
          </section>

          <div class="nd-comments">
            <h4>댓글</h4>

            <div class="comment-reported-box">
              <div class="reported-title">🚫 신고 처리된 노트입니다</div>
              <div class="reported-meta">
                신고 처리된 노트의 댓글은 표시되지 않습니다.
              </div>
            </div>
          </div>
        </template>

        <template v-else>
          <section class="flavor-section">
            <div class="nd-flavor-title">맛 프로파일</div>

            <template v-if="metricList.length > 0">
              <div v-for="item in metricList" :key="item.metricCode" class="nd-flavor-row">
                <span class="nfl">{{ item.metricName }}</span>
                <div class="nfb">
                  <div class="nff" :style="{ width: (Number(item.score || 0) / 5) * 100 + '%' }"></div>
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

            <div class="comment-list">
              <div v-if="commentTree.length === 0" class="nd-comment-empty">
                아직 댓글이 없습니다.
              </div>

              <template v-else>
                <div
                  v-for="comment in commentTree"
                  :key="comment.commentId"
                  class="comment-block"
                >
                  <CommentItem
                    :item="comment"
                    :noteId="Number(route.params.id)"
                    @refresh="fetchComments"
                    @report="openReportModal"
                  />
                </div>
              </template>
            </div>

            <form class="comment-inp" @submit.prevent="submitComment">
              <input
                v-model="newComment"
                type="text"
                maxlength="100"
                placeholder="댓글을 입력하세요"
              />
              <button class="cm-send" :disabled="commentSubmitting">
                등록
              </button>
            </form>
          </div>
        </template>
      </aside>
    </div>
  </div>

  <ReportModal
    v-model="reportModalOpen"
    :objType="reportObjType"
    :objId="reportObjId"
    @success="handleReportSuccess"
  />
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import PageNav from '../components/common/PageNav.vue'
import CommentItem from '../components/cards/CommentItem.vue'
import { categories } from '../mock/soolData'
import { getNoteDetail, deleteNote } from '@/api/noteApi'
import { getNoteLike, insertNoteLike, deleteNoteLike } from '@/api/likeApi'
import { getComments, createComment } from '@/api/commentApi'
import ReportModal from '../components/common/ReportModal.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const liked = ref(false)
const likeLoading = ref(false)
const likeCount = ref(0)

const metricList = ref([])
const noteDetail = ref(null)

const newComment = ref('')
const commentList = ref([])
const commentSubmitting = ref(false)

const reportModalOpen = ref(false)
const reportObjType = ref('')
const reportObjId = ref(null)

const navLinks = computed(() => {
  const links = [
    { label: '홈', to: '/' }
  ]

  if (noteDetail.value?.drinkId && noteDetail.value?.drinkIsDeleted !== 'Y') {
    links.push({
      label: noteDetail.value?.drinkName || '',
      to: `/drinks/${noteDetail.value.drinkId}`
    })
  } else {
    links.push({
      label: '삭제된 주류'
    })
  }

  links.push({
    label: `${noteDetail.value?.drinkName || ''} 테이스팅 노트`,
    to: route.fullPath,
    active: true
  })

  return links
})

const isReportedNote = computed(() => {
  return !!noteDetail.value?.reported
})

const authorName = computed(() => {
  return noteDetail.value?.userName || noteDetail.value?.loginId || '작성자 미상'
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
  return noteDetail.value?.drinkName || '주류 정보 없음'
})

const noteTitle = computed(() => {
  return noteDetail.value?.title || '제목이 없는 테이스팅 노트'
})

const noteContent = computed(() => {
  return noteDetail.value?.content || '아직 작성된 테이스팅 노트 내용이 없습니다.'
})

const star = computed(() => {
  const rating = Number(noteDetail.value?.rating ?? 0)
  if (!rating) return '☆☆☆☆☆'

  const full = Math.max(0, Math.min(5, Math.round(rating)))
  return '★'.repeat(full) + '☆'.repeat(5 - full)
})

const noteImageUrl = computed(() => {
  return noteDetail.value?.image?.fileUrl || ''
})

const drinkEmoji = computed(() => {
  const code = noteDetail.value?.categoryCode
  const emojiData = categories.find(e => e.name === code)
  return emojiData ? emojiData.emoji : '🍹'
})

const commentTree = computed(() => {
  const map = new Map()
  const roots = []

  commentList.value.forEach(comment => {
    map.set(comment.commentId, {
      ...comment,
      replies: []
    })
  })

  commentList.value.forEach(comment => {
    const current = map.get(comment.commentId)

    if (Number(comment.depth) === 1 && comment.parentCommentId) {
      const parent = map.get(comment.parentCommentId)
      if (parent) {
        parent.replies.push(current)
      } else {
        roots.push(current)
      }
    } else {
      roots.push(current)
    }
  })

  roots.sort((a, b) => {
    if (b.groupId !== a.groupId) return Number(b.groupId) - Number(a.groupId)
    return new Date(a.createdAt) - new Date(b.createdAt)
  })

  roots.forEach(root => {
    root.replies.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
  })

  return roots
})

const getReportedMessage = (objType) => {
  return objType === 'COMMENT'
    ? '🚫 신고 처리된 댓글입니다'
    : '🚫 신고 처리된 노트입니다'
}

const moveToLogin = async () => {
  alert('세션이 만료되었습니다. 다시 로그인해주세요.')

  authStore.user = null
  authStore.initialized = true

  router.push({
    path: '/login',
    query: { redirect: route.fullPath }
  })
}

const handleForbidden = async (error) => {
  if (error?.response?.status === 403) {
    await moveToLogin()
    return true
  }

  return false
}

const requireLogin = () => {
  if (!authStore.isLogin) {
    alert('로그인을 먼저 해주세요.')
    router.push({
      path: '/login',
      query: { redirect: route.fullPath }
    })
    return false
  }

  return true
}

const fetchLikeStatus = async () => {
  const noteId = route.params.id

  if (!noteId || !authStore.isLogin || isReportedNote.value) return

  try {
    const res = await getNoteLike(noteId)
    liked.value = !!res.data.liked
  } catch (error) {
    const handled = await handleForbidden(error)
    if (handled) return

    console.log('노트 좋아요 상태 조회 실패', error)
    liked.value = false
  }
}

const toggleLike = async () => {
  const noteId = route.params.id

  if (!noteId || likeLoading.value || isReportedNote.value) return
  if (!requireLogin()) return

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
    const handled = await handleForbidden(error)
    if (handled) return

    console.log('노트 좋아요 처리 실패', error)

    const message =
      error?.response?.data?.message ||
      error?.response?.data ||
      '좋아요 처리에 실패했습니다.'

    alert(message)
  } finally {
    likeLoading.value = false
  }
}

const fetchNoteDetail = async () => {
  try {
    const noteId = route.params.id

    if (!noteId) {
      router.replace('/404')
      return
    }

    const res = await getNoteDetail(noteId)

    if (!res.data) {
      router.replace('/404')
      return
    }

    noteDetail.value = res.data
    console.log(noteDetail.value)
    metricList.value = res.data?.metricList || []

    likeCount.value = Number(
      res.data?.noteDetail?.likeCount ??
      res.data?.likeCount ??
      0
    )

    if (isReportedNote.value) {
      liked.value = false
      return
    }

    if (authStore.isLogin) {
      await fetchLikeStatus()
    }
  } catch (error) {
    console.log('노트 상세 조회 실패', error)
    router.replace('/404')
  }
}

const fetchComments = async () => {
  try {
    const noteId = route.params.id
    const res = await getComments(noteId)
    commentList.value = res.data || []
  } catch (error) {
    console.log('댓글 조회 실패', error)
    commentList.value = []
  }
}

const submitComment = async () => {
  if (isReportedNote.value) return

  const content = newComment.value.trim()
  if (!content) return
  if (!requireLogin()) return

  try {
    commentSubmitting.value = true

    await createComment(Number(route.params.id), { content })

    newComment.value = ''
    await fetchComments()
  } catch (error) {
    const handled = await handleForbidden(error)
    if (handled) return

    console.log('댓글 등록 실패', error)
    alert('댓글 등록에 실패했습니다.')
  } finally {
    commentSubmitting.value = false
  }
}

const openReportModal = (payload) => {
  if (!requireLogin()) return

  reportObjType.value = payload.objType
  reportObjId.value = payload.objId
  reportModalOpen.value = true
}

const handleReportSuccess = async () => {
  await fetchNoteDetail()
  await fetchComments()
}

const isOwner = computed(() => {
  if (!authStore.isLogin) return false
  if (!noteDetail.value) return false
  return authStore.user?.userId === noteDetail.value.userId
})

function goEdit() {
  if (!noteDetail.value?.noteId || isReportedNote.value) return
  if (!requireLogin()) return

  router.push(`/notes/${noteDetail.value.noteId}/edit`)
}

async function removeNote() {
  if (!noteDetail.value?.noteId || isReportedNote.value) return
  if (!requireLogin()) return

  const ok = window.confirm('정말 삭제하시겠습니까?')
  if (!ok) return

  try {
    await deleteNote(noteDetail.value.noteId)
    alert('삭제되었습니다.')
    router.push(`/drinks/${noteDetail.value.drinkId}`)
  } catch (error) {
    const handled = await handleForbidden(error)
    if (handled) return

    console.log('노트 삭제 실패', error)
    alert('삭제에 실패했습니다.')
  }
}

onMounted(() => {
  fetchNoteDetail()
  fetchComments()
})

watch(
  () => route.params.id,
  () => {
    fetchNoteDetail()
    fetchComments()
  }
)
</script>

<style scoped>
.page-wrap {
  background: var(--white);
  overflow: hidden;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.12);
}

.nd-wrap {
  display: grid;
  grid-template-columns: 1fr 460px;
  min-height: 1000px;
}

.nd-main {
  padding: 40px 48px;
  border-right: 1px solid var(--border);
}

.nd-main.reported {
  background: #f7f7f7;
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

.reported-pill {
  background: #ececec;
  border-color: #d8d8d8;
  color: #777;
}

.nd-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--ink);
  margin-bottom: 12px;
  line-height: 1.4;
  word-break: break-word;
  overflow-wrap: anywhere;
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
  width: 80%;
  height: 400px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nd-photo-img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  display: block;
}

.reported-photo {
  background: #ececec;
  border-color: #d7d7d7;
  opacity: 0.8;
}

.nd-text {
  font-size: 14.5px;
  color: var(--sub);
  line-height: 1.8;
  font-weight: 300;
  margin-bottom: 24px;
  white-space: pre-wrap;
  word-break: break-word;
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

.reported-like,
.reported-like:hover {
  border-color: #d7d7d7;
  background: #ececec;
  color: #999;
  cursor: default;
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
  padding: 32px 30px;
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
  display: flex;
  flex-direction: column;
}

.comment-list {
  max-height: 340px;
  overflow-y: auto;
  padding-right: 6px;
}

.comment-list:has(.nd-comment-empty) {
  max-height: none;
}

.nd-comment-empty {
  min-height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px dashed var(--border);
  border-radius: 10px;
  background: white;
  font-size: 13px;
  color: var(--muted);
}

.comment-block {
  margin-bottom: 10px;
}

.reply-wrap {
  margin-left: 28px;
  margin-top: 8px;
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
  cursor: pointer;
}

.cm-send:disabled {
  opacity: 0.6;
  cursor: default;
}

.flavor-section {
  padding-bottom: 32px;
  margin-bottom: 32px;
  border-bottom: 3px solid var(--border);
}

.nd-flavor-empty{
  min-height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px dashed var(--border);
  border-radius: 10px;
  background: white;
  font-size: 13px;
  color: var(--muted);
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

.reported-box,
.comment-reported-box {
  background: #e7e7e7;
  border: 1px solid #d6d6d6;
  border-radius: 8px;
  padding: 14px 16px;
}

.comment-reported-box {
  background: #f1f1f1;
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