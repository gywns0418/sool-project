<template>
  <div class="chat-room-list-page">
    <h2>채팅</h2>

    <div v-if="roomList.length > 0" class="room-list">
      <ChatRoomItem
        v-for="room in roomList"
        :key="room.roomId"
        :room="room"
        @click="goRoom(room.roomId)"
      />
    </div>

    <p v-else>채팅방이 없습니다.</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ChatRoomItem from '@/components/chat/ChatRoomItem.vue'

const router = useRouter()
const roomList = ref([])

const fetchRoomList = async () => {
  roomList.value = [
    {
      roomId: 1,
      targetName: '테스트유저',
      lastMessage: '안녕하세요',
      lastMessageAt: '2026-05-12 15:30',
      unreadCount: 2
    }
  ]
}

const goRoom = (roomId) => {
  router.push(`/chat/rooms/${roomId}`)
}

onMounted(() => {
  fetchRoomList()
})
</script>

<style scoped>
.chat-room-list-page {
  max-width: 720px;
  margin: 0 auto;
  padding: 24px;
}

.chat-room-list-page h2 {
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: 700;
}

.room-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.chat-room-item {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 16px;
  border: 1px solid #e5e5e5;
  border-radius: 14px;
  background: #fff;
  cursor: pointer;
  transition: 0.2s;
}

.chat-room-item:hover {
  background: #faf7f4;
}

.room-info strong {
  display: block;
  margin-bottom: 6px;
  font-size: 16px;
}

.room-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.room-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
  font-size: 12px;
  color: #888;
}

.room-meta em {
  min-width: 22px;
  height: 22px;
  padding: 0 7px;
  border-radius: 999px;
  background: #6b2d3a;
  color: #fff;
  font-style: normal;
  font-size: 12px;
  line-height: 22px;
  text-align: center;
}
</style>