<template>
  <div class="page-wrap">
    <PageNav :links="navLinks" show-search />

    <div class="login-body">
      <div class="form-shell">
        <div class="form-eyebrow">
          <span class="eyebrow-line"></span>
          <span>SIGN IN</span>
        </div>

        <h1 class="form-title">로그인</h1>
        <p class="form-sub">계속하려면 계정으로 로그인하세요</p>

        <form @submit.prevent="login" class="form-body">
          <div class="field-group">
            <label class="field-label">아이디</label>
            <input
              v-model="loginId"
              type="text"
              class="field-input"
              placeholder="아이디를 입력하세요"
              required
            />
          </div>

          <div class="field-group">
            <label class="field-label">비밀번호</label>
            <input
              v-model="password"
              type="password"
              class="field-input"
              placeholder="비밀번호를 입력하세요"
              required
            />
          </div>

          <button type="submit" class="btn-submit">
            로그인하기 <span class="btn-arrow">→</span>
          </button>
        </form>

        <div class="sub-links">
          <router-link to="/find-id" class="sub-link">아이디 찾기</router-link>
          <span class="sub-sep">|</span>
          <router-link to="/reset-password" class="sub-link">비밀번호 재설정</router-link>
        </div>

        <p class="form-footer">
          계정이 없으신가요?
          <router-link to="/signup" class="form-link">회원가입</router-link>
        </p>
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref } from 'vue'
import PageNav from '../../components/common/PageNav.vue'

const navLinks = [
  { label: '홈', to: '/', active: true }
]

const loginId = ref('')
const password = ref('')

const login = () => {
  console.log('login', loginId.value, password.value)
}
</script>


<style scoped>
.page-wrap {
  background: var(--bg);
  overflow: hidden;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.12);
  min-height: 700px;
}

.login-body {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px 24px;
}

.form-shell {
  width: 100%;
  max-width: 400px;
  background: var(--white);
  border: 1px solid var(--border);
  border-radius: 16px;
  padding: 48px 44px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
}

.form-eyebrow {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  font-size: 10px;
  letter-spacing: 2.5px;
  text-transform: uppercase;
  font-weight: 600;
  color: var(--point);
  margin-bottom: 14px;
}

.eyebrow-line {
  display: block;
  width: 24px;
  height: 1px;
  background: var(--point);
}

.form-title {
  font-family: 'Playfair Display', serif;
  font-size: 36px;
  color: var(--ink);
  letter-spacing: -1px;
  line-height: 1.1;
  margin-bottom: 6px;
}

.form-sub {
  font-size: 13px;
  color: var(--muted);
  font-weight: 300;
  margin-bottom: 32px;
}

.form-body {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field-label {
  font-size: 11px;
  font-weight: 600;
  color: var(--sub);
  letter-spacing: 0.5px;
  text-transform: uppercase;
}

.field-input {
  width: 100%;
  height: 46px;
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 0 14px;
  font-size: 14px;
  color: var(--ink);
  background: var(--bg);
  font-family: 'Pretendard', sans-serif;
  outline: none;
  transition: border-color 0.18s, box-shadow 0.18s;
}

.field-input::placeholder {
  color: var(--muted);
}

.field-input:focus {
  border-color: var(--point);
  box-shadow: 0 0 0 3px rgba(200, 96, 58, 0.10);
  background: var(--white);
}

.btn-submit {
  margin-top: 6px;
  width: 100%;
  height: 48px;
  background: var(--point);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  font-family: 'Pretendard', sans-serif;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: opacity 0.18s, transform 0.12s;
}

.btn-submit:hover {
  opacity: 0.88;
}

.btn-submit:active {
  transform: scale(0.98);
}

.btn-arrow {
  font-size: 15px;
}

.sub-links {
  margin-top: 18px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.sub-link {
  color: var(--muted);
  text-decoration: none;
}

.sub-link:hover {
  color: var(--point);
  text-decoration: underline;
}

.sub-sep {
  color: var(--border);
}

.form-footer {
  margin-top: 18px;
  text-align: center;
  font-size: 13px;
  color: var(--muted);
}

.form-link {
  color: var(--point);
  font-weight: 600;
  text-decoration: none;
  margin-left: 4px;
}

.form-link:hover {
  text-decoration: underline;
}
</style>