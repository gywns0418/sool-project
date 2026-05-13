<template>
  <div class="chat-room-item" @click="$emit('click')">
    <div class="room-info">
      <strong>{{ room.title }}</strong>
      <p>{{ room.lastMessage || '메시지가 없습니다.' }}</p>
    </div>

    <div class="room-meta">
      <span>{{ room.lastMessageAt }}</span>
      <em v-if="room.unreadCount > 0">{{ room.unreadCount }}</em>
    </div>
  </div>
</template>

<script setup>
defineProps({
  room: {
    type: Object,
    required: true
  }
})

defineEmits(['click'])
</script>

<style scoped>
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

.room-info {
  min-width: 0;
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
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.room-meta {
  flex-shrink: 0;
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