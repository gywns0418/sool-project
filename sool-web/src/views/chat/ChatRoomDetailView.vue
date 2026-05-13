<template>
  <div class="chat-room-detail-page">
    <div class="chat-header">
      <button type="button" @click="goBack">뒤로</button>
      <h2>채팅방</h2>
    </div>

    <ChatMessageList :messages="messageList" :my-user-id="myUserId" />

    <ChatMessageInput @send="sendMessage" />
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import ChatMessageList from '@/components/chat/ChatMessageList.vue'
import ChatMessageInput from '@/components/chat/ChatMessageInput.vue'
import { chatMessageList } from '@/api/chatApi'
import {
  connectChatSocket,
  subscribeRoom,
  sendChatMessage,
  disconnectChatSocket
} from '@/socket/chatSocket'
import { useAuthStore } from '@/stores/authStore'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const roomId = Number(route.params.roomId)
const myUserId = authStore.user?.userId

const messageList = ref([])

const fetchMessages = async () => {
  const response = await chatMessageList(roomId)
  messageList.value = response.data
}

const sendMessage = async (message) => {

  sendChatMessage({
    roomId,
    senderId: myUserId,
    message
  })
}

const goBack = () => {
  router.back()
}

onMounted(async () => {

  await fetchMessages()

  connectChatSocket(() => {

    subscribeRoom(roomId, (message) => {

      messageList.value.push(message)
    })
  })
})

onBeforeUnmount(() => {

  disconnectChatSocket()
})
</script>

<style scoped>
.chat-room-detail-page {
  max-width: 720px;
  margin: 0 auto;
  padding: 24px;
}

.chat-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.chat-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
}

.chat-header button {
  border: 0;
  background: #eee;
  padding: 8px 12px;
  border-radius: 8px;
  cursor: pointer;
}

.chat-message-list {
  height: 560px;
  padding: 16px;
  border: 1px solid #e5e5e5;
  border-radius: 16px;
  background: #f8f8f8;
  overflow-y: auto;
}

.message-row {
  display: flex;
  margin-bottom: 12px;
}

.message-row.mine {
  justify-content: flex-end;
}

.message-box {
  max-width: 70%;
  padding: 10px 12px;
  border-radius: 14px;
  background: #fff;
}

.message-row.mine .message-box {
  background: #6b2d3a;
  color: #fff;
}

.message-box strong {
  display: block;
  margin-bottom: 4px;
  font-size: 13px;
}

.message-box p {
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-word;
}

.message-box span {
  display: block;
  margin-top: 6px;
  font-size: 11px;
  color: #999;
}

.message-row.mine .message-box span {
  color: rgba(255, 255, 255, 0.75);
}

.chat-message-input {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

.chat-message-input input {
  flex: 1;
  height: 44px;
  padding: 0 14px;
  border: 1px solid #ddd;
  border-radius: 12px;
  outline: none;
}

.chat-message-input input:focus {
  border-color: #6b2d3a;
}

.chat-message-input button {
  width: 80px;
  border: 0;
  border-radius: 12px;
  background: #6b2d3a;
  color: #fff;
  cursor: pointer;
}

.chat-message-input button:disabled {
  background: #ccc;
  cursor: not-allowed;
}
</style>