<template>
  <div class="comment-item">
    <div class="comment">
      <div class="cm-head">
        <div class="avatar">{{ item.authorInitial }}</div>
        <span class="cm-name">{{ item.author }}</span>
        <span class="cm-date">{{ item.date }}</span>
      </div>
      <div class="cm-text">{{ item.text }}</div>
      <div class="cm-actions">
        <button class="cm-btn reply-btn" @click="showReply = !showReply">답글</button>
      </div>
      <div v-if="showReply" class="reply-box">
        <form class="comment-inp" @submit.prevent="submitComment">
          <input v-model="newComment" placeholder="댓글을 입력하세요" />
          <button class="cm-send">등록</button>
        </form>
      </div>
    </div>

    <div v-if="item.replies?.length" class="replies">
      <div v-for="reply in item.replies" :key="reply.id" class="reply">
        <div class="cm-head">
          <div class="avatar small">{{ reply.authorInitial }}</div>
          <span class="cm-name">{{ reply.author }}</span>
          <span class="cm-date">{{ reply.date }}</span>
        </div>
        <div class="cm-text">{{ reply.text }}</div>
        <div class="cm-actions">
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const liked = ref(false)
const showReply = ref(false)

defineProps({
  item: {
    type: Object,
    required: true
  }
})
</script>

<style scoped>
.comment {
  padding: 12px 0 4px;
  border-bottom: 1px solid var(--border);
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

.avatar.small {
  width: 20px;
  height: 20px;
  font-size: 9px;
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

.reply {
  padding: 10px 0 4px;
  border-bottom: 1px solid #f0ede8;
}

.reply:last-child {
  border-bottom: none;
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
