<template>
  <div class="chat-message-list">
    <div
      v-for="message in messages"
      :key="message.messageId"
      class="message-row"
      :class="{ mine: message.senderId === myUserId }"
    >
      <div class="message-box">
        <strong v-if="message.senderId !== myUserId">
          {{ message.senderName }}
        </strong>

        <p>{{ message.message }}</p>
        <span>{{ formatDate(message.createdAt) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import moment from 'moment/min/moment-with-locales'

moment.locale('ko')

defineProps({
  messages: {
    type: Array,
    required: true
  },
  myUserId: {
    type: Number,
    required: true
  }
})

const formatDate = (datetime) => {
  return moment(datetime).fromNow()
}
</script>

<style scoped>
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
</style>