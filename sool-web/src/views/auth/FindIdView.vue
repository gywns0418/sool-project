<template>
  <div class="page-wrap">
    <div v-if="loading" class="loading-overlay">
      <div class="loading-box">
        <div class="spinner-large"></div>
        <p>{{ loadingMessage }}</p>
      </div>
    </div>

    <PageNav :links="navLinks" show-search />

    <div class="resetpassword-body">
      <div class="form-shell">
        <div class="form-eyebrow">
          <span class="eyebrow-line"></span>
          <span>FIND ID</span>
        </div>

        <h1 class="form-title">아이디 찾기</h1>
        <p class="form-sub">Sool과 함께 나만의 술 여정을 시작하세요</p>

        <div class="step-bar">
          <div class="step-item" :class="{ active: step === 1, done: step > 1 }">
            <span class="step-no">01</span>
            <span class="step-text">기본정보 입력</span>
          </div>
          <div class="step-item" :class="{ active: step === 2, done: step > 2 }">
            <span class="step-no">02</span>
            <span class="step-text">이메일 인증</span>
          </div>
          <div class="step-item" :class="{ active: step === 3 }">
            <span class="step-no">03</span>
            <span class="step-text">아이디 확인</span>
          </div>
        </div>

        <form class="form-body" @submit.prevent>
          <template v-if="step === 1">
            <div class="field-group">
              <label class="field-label">이메일</label>
              <input
                v-model.trim="email"
                type="email"
                class="field-input"
                placeholder="이메일 주소를 입력하세요"
                @input="onChangeEmail"
                :disabled="emailCodeSent || loading"
              />
              <p
                v-if="emailMsg"
                class="field-msg"
                :class="{ success: emailValid, error: !emailValid }"
              >
                {{ emailMsg }}
              </p>
            </div>

            <button
              type="button"
              class="btn-submit"
              :disabled="!canSendEmail || loading"
              @click="handleSendEmailCode"
            >
              <span v-if="loading && loadingType === 'sendEmailCode'" class="spinner"></span>
              <span v-else>
                인증번호 보내기 <span class="btn-arrow">→</span>
              </span>
            </button>
          </template>

          <template v-else-if="step === 2">
            <div class="info-box">
              <strong>{{ email }}</strong> 로 인증번호를 보냈습니다.
            </div>

            <div class="field-group">
              <label class="field-label">이메일 인증번호</label>
              <input
                v-model.trim="emailCode"
                type="text"
                class="field-input"
                placeholder="인증번호를 입력하세요"
                maxlength="6"
                :disabled="loading"
              />
              <p v-if="emailVerifyMsg" class="field-msg error">
                {{ emailVerifyMsg }}
              </p>
            </div>

            <div class="btn-row">
              <button
                type="button"
                class="btn-light"
                @click="goStep1"
                :disabled="loading"
              >
                이전
              </button>

              <button
                type="button"
                class="btn-light"
                :disabled="loading || resendSeconds > 0"
                @click="handleResendEmailCode"
              >
                <span v-if="loading && loadingType === 'resendEmailCode'" class="spinner spinner-dark"></span>
                <span v-else>
                  {{ resendSeconds > 0 ? `${resendSeconds}초 후 재전송 가능` : '재전송' }}
                </span>
              </button>

              <button
                type="button"
                class="btn-submit btn-submit-small"
                :disabled="loading || !emailCode"
                @click="handleVerifyEmailCode"
              >
                <span v-if="loading && loadingType === 'verifyEmailCode'" class="spinner"></span>
                <span v-else>
                  인증번호 확인 <span class="btn-arrow">→</span>
                </span>
              </button>
            </div>
          </template>

          <template v-else>
            <div class="info-box success-box">
              이메일 인증이 완료되었습니다.
            </div>

            <div class="field-group">
              <label class="field-label">찾은 아이디</label>
              <div class="field-result">
                {{ foundLoginId }}
              </div>
            </div>

            <div class="btn-row">
              <button
                type="button"
                class="btn-light"
                @click="step = 2"
                :disabled="loading"
              >
                이전
              </button>

              <button
                type="button"
                class="btn-submit btn-submit-small"
                :disabled="loading"
                @click="router.push('/login')"
              >
                로그인 하러가기 <span class="btn-arrow">→</span>
              </button>
            </div>
          </template>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import PageNav from '../../components/common/PageNav.vue'
import { computed, ref, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import {
  sendFindLoginIdEmailCode,
  resendEmailCode,
  verifyEmailCode,
  findLoginId
} from '@/api/authApi'

const router = useRouter()

const navLinks = [
  { label: '홈', to: '/', active: true }
]

const step = ref(1)
const loading = ref(false)
const loadingType = ref('')
const loadingMessage = ref('처리 중입니다...')

const email = ref('')
const emailValid = ref(false)
const emailMsg = ref('')

const emailCode = ref('')
const emailVerified = ref(false)
const emailVerifyMsg = ref('')

const emailCodeSent = ref(false)

const foundLoginId = ref('')

const resendSeconds = ref(0)
let resendTimer = null

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

function setLoading(type, message) {
  loading.value = true
  loadingType.value = type
  loadingMessage.value = message
}

function clearLoading() {
  loading.value = false
  loadingType.value = ''
  loadingMessage.value = '처리 중입니다...'
}

function clearResendTimer() {
  if (resendTimer) {
    clearInterval(resendTimer)
    resendTimer = null
  }
}

function startResendCountdown(seconds = 10) {
  clearResendTimer()
  resendSeconds.value = seconds

  resendTimer = setInterval(() => {
    if (resendSeconds.value > 0) {
      resendSeconds.value -= 1
    }

    if (resendSeconds.value <= 0) {
      clearResendTimer()
    }
  }, 1000)
}

function resetEmailFlow() {
  emailCode.value = ''
  emailVerified.value = false
  emailVerifyMsg.value = ''
  foundLoginId.value = ''
  clearResendTimer()
  resendSeconds.value = 0

  if (step.value > 1) {
    step.value = 1
  }
}

function goStep1() {
  step.value = 1
  emailCodeSent.value = false
  emailCode.value = ''
  emailVerified.value = false
  emailVerifyMsg.value = ''
  foundLoginId.value = ''
  clearResendTimer()
  resendSeconds.value = 0
}

function validateEmail() {
  if (!email.value) {
    emailValid.value = false
    emailMsg.value = ''
    return
  }

  if (!emailRegex.test(email.value)) {
    emailValid.value = false
    emailMsg.value = '올바른 이메일 형식이 아닙니다.'
    return
  }

  emailValid.value = true
  emailMsg.value = '사용 가능한 이메일 형식입니다.'
}

function onChangeEmail() {
  validateEmail()
  resetEmailFlow()
}

const canSendEmail = computed(() => {
  return !!emailValid.value
})

async function handleSendEmailCode() {
  if (!emailValid.value) {
    alert('이메일 형식을 확인하세요.')
    return
  }

  setLoading('sendEmailCode', '인증번호를 발송하고 있습니다.')
  emailCodeSent.value = true

  try {
    const res = await sendFindLoginIdEmailCode({
      email: email.value
    })

    emailVerifyMsg.value = ''
    step.value = 2
    startResendCountdown(10)
    alert(res.data?.message || '인증번호를 발송했습니다.')
  } catch (e) {
    emailCodeSent.value = false
    emailVerifyMsg.value = e.response?.data?.message || ''
    alert(e.response?.data?.message || '인증번호 발송에 실패했습니다.')
  } finally {
    clearLoading()
  }
}

async function handleResendEmailCode() {
  setLoading('resendEmailCode', '인증번호를 다시 발송하고 있습니다.')

  try {
    const res = await resendEmailCode({
      email: email.value
    })

    emailVerifyMsg.value = ''
    startResendCountdown(10)
    alert(res.data?.message || '인증번호가 재전송되었습니다.')
  } catch (e) {
    emailVerifyMsg.value = e.response?.data?.message || ''
    alert(e.response?.data?.message || '인증번호 재전송에 실패했습니다.')
  } finally {
    clearLoading()
  }
}

async function handleVerifyEmailCode() {
  if (!emailCode.value) {
    emailVerifyMsg.value = '인증번호를 입력하세요.'
    return
  }

  setLoading('verifyEmailCode', '인증번호와 아이디를 확인하고 있습니다.')

  try {
    const res = await verifyEmailCode({
      email: email.value,
      code: emailCode.value
    })

    if (res.data?.verified) {
      const findRes = await findLoginId({
        email: email.value
      })

      foundLoginId.value = findRes.data?.loginId || ''
      emailVerified.value = true
      emailVerifyMsg.value = ''
      step.value = 3
    } else {
      emailVerified.value = false
      emailVerifyMsg.value = '인증번호가 올바르지 않습니다.'
    }
  } catch (e) {
    emailVerified.value = false
    emailVerifyMsg.value = e.response?.data?.message || '인증번호 확인 중 오류가 발생했습니다.'
  } finally {
    clearLoading()
  }
}

onBeforeUnmount(() => {
  clearResendTimer()
})
</script>

<style scoped>
.page-wrap {
  background: var(--bg);
  overflow: hidden;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.12);
  min-height: 1000px;
}

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(15, 23, 42, 0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.loading-box {
  min-width: 220px;
  background: var(--white);
  border-radius: 16px;
  padding: 28px 30px;
  text-align: center;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.18);
  border: 1px solid var(--border);
}

.loading-box p {
  margin: 14px 0 0;
  font-size: 14px;
  color: var(--ink);
  font-weight: 500;
}

.resetpassword-body {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px 24px;
}

.form-shell {
  width: 100%;
  max-width: 700px;
  background: var(--white);
  border: 1px solid var(--border);
  border-radius: 16px;
  padding: 48px 80px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.06);
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
  margin-bottom: 24px;
}

.step-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 26px;
}

.step-item {
  flex: 1;
  border: 1px solid var(--border);
  border-radius: 10px;
  padding: 10px 10px 9px;
  background: var(--bg);
  display: flex;
  flex-direction: column;
  gap: 4px;
  opacity: 0.6;
}

.step-item.active,
.step-item.done {
  opacity: 1;
  border-color: rgba(200, 96, 58, 0.35);
  background: rgba(200, 96, 58, 0.06);
}

.step-no {
  font-size: 10px;
  color: var(--point);
  font-weight: 700;
  letter-spacing: 1px;
}

.step-text {
  font-size: 12px;
  color: var(--ink);
  font-weight: 600;
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
  box-sizing: border-box;
}

.field-input::placeholder {
  color: var(--muted);
}

.field-input:focus {
  border-color: var(--point);
  box-shadow: 0 0 0 3px rgba(200, 96, 58, 0.10);
  background: var(--bg);
}

.field-msg {
  font-size: 12px;
  margin: 0;
  line-height: 1.4;
}

.field-msg.success {
  color: #2e8b57;
}

.field-msg.error {
  color: #c0392b;
}

.info-box {
  padding: 14px 16px;
  border-radius: 10px;
  border: 1px solid var(--border);
  background: var(--bg);
  font-size: 13px;
  color: var(--ink);
}

.success-box {
  border-color: rgba(46, 139, 87, 0.25);
  background: rgba(46, 139, 87, 0.07);
}

.field-result {
  min-height: 46px;
  display: flex;
  align-items: center;
  padding: 0 14px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: var(--bg);
  font-size: 15px;
  font-weight: 600;
  color: var(--ink);
}

.btn-submit,
.btn-light {
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  font-family: 'Pretendard', sans-serif;
  cursor: pointer;
  transition: opacity 0.18s, transform 0.12s;
}

.btn-submit:active,
.btn-light:active {
  transform: scale(0.98);
}

.btn-submit:disabled,
.btn-light:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-submit {
  margin-top: 6px;
  width: 100%;
  height: 48px;
  background: var(--point);
  color: white;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-submit:hover:not(:disabled) {
  opacity: 0.88;
}

.btn-submit-small {
  width: auto;
  flex: 1.3;
  margin-top: 0;
}

.btn-light {
  height: 46px;
  border: 1px solid var(--border);
  background: var(--white);
  color: var(--ink);
  padding: 0 16px;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-row {
  display: flex;
  gap: 10px;
  align-items: center;
}

.btn-arrow {
  font-size: 15px;
}

.spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255,255,255,0.35);
  border-top: 2px solid #ffffff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

.spinner-dark {
  border: 2px solid rgba(0,0,0,0.15);
  border-top: 2px solid var(--ink);
}

.spinner-large {
  width: 38px;
  height: 38px;
  border: 3px solid rgba(200, 96, 58, 0.18);
  border-top: 3px solid var(--point);
  border-radius: 50%;
  margin: 0 auto;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 640px) {
  .form-shell {
    padding: 32px 20px;
  }

  .form-title {
    font-size: 30px;
  }

  .step-bar {
    flex-direction: column;
  }

  .btn-row {
    flex-direction: column;
  }

  .btn-submit-small,
  .btn-light {
    width: 100%;
  }

  .loading-box {
    width: calc(100% - 32px);
    min-width: auto;
    padding: 24px 20px;
  }
}
</style>