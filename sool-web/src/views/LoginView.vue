<template>
<PageNav :links="navLinks" show-search />
  <div class="login-page">
    <h1>로그인</h1>

    <form @submit.prevent="login">
      <div class="form-group">
        <label>아이디</label>
        <input v-model="loginId" type="text" required />
      </div>

      <div class="form-group">
        <label>비밀번호</label>
        <input v-model="password" type="password" required />
      </div>

      <button type="submit">로그인</button>
    </form>

    <p class="signup-link">
      계정이 없으신가요?
      <router-link to="/signup">회원가입</router-link>
    </p>
  </div>
</template>

<script setup>
import PageNav from '../components/common/PageNav.vue'
import { ref } from 'vue'

const loginId = ref('')
const password = ref('')

const login = () => {
  console.log('login', loginId.value, password.value)
}
</script>

<style scoped>
.page-wrap {
  background: var(--white);
  overflow: hidden;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.12);
}

.login-page {
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

.signup-link {
  margin-top: 16px;
  text-align: center;
}

</style>