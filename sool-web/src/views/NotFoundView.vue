<template>
  <div class="not-found">
    <h1>404</h1>
    <p>존재하지 않는 페이지입니다.</p>
    <button @click="goHome">홈으로 이동</button>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

function goHome() {
  router.push('/')
}
</script>

<style scoped>
.not-found{
  display:flex;
  flex-direction:column;
  align-items:center;
  justify-content:center;
  height:70vh;
  gap:10px;
}

button{
  padding:8px 16px;
  cursor:pointer;
}
</style>