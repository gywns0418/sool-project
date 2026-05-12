<template>
  <form class="chat-message-input" @submit.prevent="handleSend">
    <input
      v-model="message"
      type="text"
      maxlength="500"
      placeholder="메시지를 입력하세요"
    />

    <button type="submit" :disabled="!message.trim()">
      전송
    </button>
  </form>
</template>

<script setup>
import { ref } from 'vue'

const emit = defineEmits(['send'])

const message = ref('')

const handleSend = () => {
  const text = message.value.trim()

  if (!text) return

  emit('send', text)
  message.value = ''
}
</script>

<style scoped>
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