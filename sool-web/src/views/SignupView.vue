<template>
  <div class="signup-page">
    <h1>회원가입</h1>

    <form @submit.prevent="signup">

      <div class="form-group">
        <label>아이디</label>
        <input v-model="loginId" type="text" required />
      </div>

      <div class="form-group">
        <label>이름</label>
        <input v-model="name" type="text" required />
      </div>

      <div class="form-group">
        <label>이메일</label>
        <input v-model="email" type="email" required />
      </div>

      <div class="form-group">
        <label>비밀번호</label>
        <input v-model="password" type="password" required />
      </div>

      <button type="submit">회원가입</button>

    </form>

  </div>
</template>

<script setup>
import { ref } from 'vue'

const loginId = ref('')
const name = ref('')
const email = ref('')
const password = ref('')

const signup = () => {
  console.log('signup', loginId.value, name.value, email.value, password.value)
}
</script>

<style scoped>
.page-wrap {
  background: var(--white);
  overflow: hidden;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.12);
}

.signup-page {
  max-width: 400px;
  margin: 60px auto;
}

.form-group {
  margin-bottom: 16px;
}

input {
  width: 100%;
  padding: 8px;
}

button {
  width: 100%;
  padding: 10px;
}

</style>