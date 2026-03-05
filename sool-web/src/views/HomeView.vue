<script setup>
import axios from "axios"
import { ref } from "vue"

const msg = ref("")

const ping = async () => {
  const { data } = await axios.get("/api/test")
  msg.value = data
}
</script>

<template>
  <div>
    <h1>Spring 연결 테스트</h1>
    <button @click="ping">API 호출</button>
    <p>{{ msg }}</p>
  </div>
</template>