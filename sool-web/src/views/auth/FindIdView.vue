<template>
  <PageNav :links="navLinks" show-search />
    <div class="auth-wrap">
      
        <h2 class="auth-title">아이디 찾기</h2>

    <div class="auth-form">

      <input
        v-model="name"
        type="text"
        placeholder="이름"
        class="auth-input"
      />

      <input
        v-model="email"
        type="email"
        placeholder="이메일"
        class="auth-input"
      />

      <div class="auth-row">
        <input
          v-model="code"
          type="text"
          placeholder="인증번호"
          class="auth-input"
        />

        <button
          class="auth-btn"
          :disabled="cooldown > 0"
          @click="sendCode"
        >
          {{ cooldown > 0 ? cooldown + '초 후 재전송' : '인증번호 발송' }}
        </button>
      </div>

      <button
        class="auth-submit"
        @click="findId"
      >
        아이디 찾기
      </button>

      <div v-if="resultId" class="result-box">
        회원님의 아이디 : <b>{{ resultId }}</b>
      </div>

    </div>

  </div>
</template>

<script setup>
import PageNav from '@/components/common/PageNav.vue'
import { ref } from "vue"
import api from "@/api/apiClient"

const navLinks = [
  { label: '홈', to: '/'}
]

const name = ref("")
const email = ref("")
const code = ref("")
const resultId = ref("")

const cooldown = ref(0)
let timer = null

const startCooldown = () => {

  cooldown.value = 10

  timer = setInterval(() => {

    cooldown.value--

    if (cooldown.value <= 0) {
      clearInterval(timer)
    }

  }, 1000)

}

const sendCode = async () => {

  await api.post("/auth/send-find-id-code", {
    name: name.value,
    email: email.value
  })

  startCooldown()

}

const findId = async () => {

  const res = await api.post("/auth/find-id", {
    name: name.value,
    email: email.value,
    code: code.value
  })

  resultId.value = res.data.loginId

}


</script>

<style scoped>

.auth-wrap {
  width: 420px;
  margin: 0 auto;
  padding-top: 80px;
}

.auth-title {
  font-size: 26px;
  margin-bottom: 30px;
  text-align: center;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.auth-input {
  padding: 12px;
  border: 1px solid #ddd;
}

.auth-row {
  display: flex;
  gap: 8px;
}

.auth-btn {
  width: 140px;
}

.auth-submit {
  margin-top: 10px;
  padding: 12px;
}

.result-box {
  margin-top: 20px;
  text-align: center;
}

</style>