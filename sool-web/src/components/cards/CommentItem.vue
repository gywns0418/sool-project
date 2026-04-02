<template>
  <div class="comment-item">
    <div class="comment" :class="{ deleted: isDeleted }">
      <div class="cm-head">
        <div class="avatar">{{ authorInitial }}</div>
        <span class="cm-name">{{ authorName }}</span>
        <span class="cm-date">{{ formattedDate }}</span>
      </div>

      <div v-if="editing" class="comment-inp edit-inp">
        <input
          v-model="editContent"
          type="text"
          maxlength="500"
          placeholder="댓글을 수정하세요"
        />
        <button class="cm-send" :disabled="editSubmitting" @click="submitEdit">
          저장
        </button>
        <button class="cm-cancel" :disabled="editSubmitting" @click="cancelEdit">
          취소
        </button>
      </div>

      <div v-else class="cm-text">
        {{ displayContent }}
      </div>

      <div class="cm-actions" v-if="!isDeleted && !editing">

        <button v-if="item.depth === 0" class="cm-btn reply-btn" @click="toggleReply">
          {{ showReply ? '취소' : '답글' }}
        </button>

        <button class="cm-btn report-btn" @click="reportComment">
          신고
        </button>

        <template v-if="isOwner">
          <button class="cm-btn reply-btn" @click="startEdit">
            수정
          </button>

          <button class="cm-btn reply-btn" @click="deleteComment">
            삭제
          </button>
        </template>

      </div>

      <div v-if="showReply" class="reply-box">
        <form class="comment-inp" @submit.prevent="submitReply">
          <input
            v-model="replyContent"
            type="text"
            maxlength="100"
            placeholder="답글을 입력하세요"
          />
          <button class="cm-send" :disabled="replySubmitting">
            등록
          </button>
        </form>
      </div>
    </div>

    <div v-if="item.replies?.length" class="replies">
      <CommentItem
        v-for="reply in item.replies"
        :key="reply.commentId"
        :item="reply"
        :noteId="Number(route.params.id)"
        @refresh="$emit('refresh')"
        @report="$emit('report', $event)"
      />
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useAuthStore } from '@/stores/authStore'
import { useRoute, useRouter } from 'vue-router'
import { createReply,updateCommentApi,deleteCommentApi } from '@/api/commentApi'

const props = defineProps({
  item: {
    type: Object,
    required: true
  },
  noteId: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['refresh','report'])

const authStore = useAuthStore()
const route = useRoute()
const router = useRouter()

const showReply = ref(false)
const replyContent = ref('')
const replySubmitting = ref(false)

const editing = ref(false)
const editContent = ref('')
const editSubmitting = ref(false)

const isDeleted = computed(() => props.item.isDeleted === 'Y')

const authorName = computed(() => {
  return props.item.userName || props.item.name || props.item.loginId || '알 수 없음'
})

const authorInitial = computed(() => {
  return String(authorName.value || '익').trim().charAt(0) || '익'
})

//삭제된 댓글 표시
const displayContent = computed(() => {
  if (isDeleted.value) {
    return '삭제된 댓글입니다.'
  }
  return props.item.content || ''
})

const formattedDate = computed(() => {
  const value = props.item.createdAt || props.item.created_at
  if (!value) return ''

  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return ''

  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')

  return `${year}. ${month}. ${day}`
})

//로그인 확인
function requireLogin() {
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


//대댓글 표시
function toggleReply() {
  if (!requireLogin()) return

  showReply.value = !showReply.value
}

//대댓글 작성
async function submitReply() {
  const content = replyContent.value.trim()
  if (!content) return

  if (!requireLogin()) return
  

  try {
    replySubmitting.value = true

    await createReply(props.noteId, {
      parentCommentId: props.item.commentId,
      content
    })

    replyContent.value = ''
    showReply.value = false
    emit('refresh')
  } catch (error) {
    console.log('답글 등록 실패', error)
    alert('답글 등록에 실패했습니다.')
  } finally {
    replySubmitting.value = false
  }
}

//댓글 수정 창 표시
function startEdit() {
  if (!requireLogin()) return
  editing.value = true
  editContent.value = props.item.content || ''
  showReply.value = false
}

//댓글 수정 취소
function cancelEdit() {
  editing.value = false
  editContent.value = props.item.content || ''
}

//댓글 수정
async function submitEdit() {
  const content = editContent.value.trim()
  if (!content) {
    alert('댓글 내용을 입력해주세요.')
    return
  }

  try {
    editSubmitting.value = true

    await updateCommentApi(props.item.commentId, {
      content
    })

    editing.value = false
    emit('refresh')
  } catch (error) {
    console.log('댓글 수정 실패', error)
    alert('댓글 수정에 실패했습니다.')
  } finally {
    editSubmitting.value = false
  }
}

//댓글 삭제
async function deleteComment() {
  if (!confirm('댓글을 삭제하시겠습니까?')) return

  try {
    await deleteCommentApi(props.item.commentId)
    emit('refresh')
  } catch (error) {
    console.log('댓글 삭제 실패', error)
  }
}

//신고 모달을 위한 emit
function reportComment() {
  emit('report', {
    objType: 'COMMENT',
    objId: props.item.commentId
  })
}

//작성자 확인
const isOwner = computed(() => {
  if (!authStore.isLogin) return false
  if (!props.item) return false

  return authStore.user?.userId === props.item.userId
})
</script>

<style scoped>
.comment {
  padding: 12px 0 4px;
  border-bottom: 1px solid var(--border);
}

.comment.deleted .cm-text {
  color: var(--muted);
}

.cm-head {
  display: flex;
  align-items: center;
  gap: 7px;
  margin-bottom: 5px;
}

.avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: var(--ink);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: 600;
}

.cm-name {
  font-size: 12px;
  font-weight: 600;
  color: var(--ink);
}

.cm-date {
  font-size: 11px;
  color: var(--muted);
}

.cm-text {
  font-size: 12.5px;
  color: var(--sub);
  line-height: 1.6;
  font-weight: 300;
  margin-bottom: 6px;
}

.cm-actions {
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
}

.cm-btn {
  font-size: 11px;
  color: var(--muted);
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
}

.reply-btn {
  color: var(--point);
  font-weight: 500;
}

.reply-box {
  font-size: 11px;
  color: var(--muted);
  margin-bottom: 8px;
}

.replies {
  margin-left: 14px;
  padding-left: 12px;
  border-left: 2px solid var(--border);
  margin-top: 4px;
  margin-bottom: 4px;
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

.cm-cancel {
  height: 36px;
  padding: 0 14px;
  background: white;
  color: var(--muted);
  border: 1px solid var(--border);
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
}

.cm-send:disabled,
.cm-cancel:disabled {
  opacity: 0.6;
  cursor: default;
}
</style>