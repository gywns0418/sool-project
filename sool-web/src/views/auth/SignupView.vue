<template>
  <div class="page-wrap">
    <PageNav :links="navLinks" show-search />

    <div class="signup-body">
      <div class="form-shell">
        <div class="form-eyebrow">
          <span class="eyebrow-line"></span>
          <span>CREATE ACCOUNT</span>
        </div>

        <h1 class="form-title">회원가입</h1>
        <p class="form-sub">Sool과 함께 나만의 술 여정을 시작하세요</p>

        <div class="step-bar">
          <div class="step-item" :class="{ active: step === 1, done: step > 1 }">
            <span class="step-no">01</span>
            <span class="step-text">기본정보</span>
          </div>
          <div class="step-item" :class="{ active: step === 2, done: step > 2 }">
            <span class="step-no">02</span>
            <span class="step-text">이메일 인증</span>
          </div>
          <div class="step-item" :class="{ active: step === 3 }">
            <span class="step-no">03</span>
            <span class="step-text">비밀번호</span>
          </div>
        </div>

        <form class="form-body" @submit.prevent>
          <template v-if="step === 1">
            <div class="field-group">
              <label class="field-label">아이디</label>
              <div class="field-inline">
                <input
                  v-model="loginId"
                  type="text"
                  class="field-input"
                  maxlength="20"
                  placeholder="아이디"
                  @input="onChangeLoginId"
                  :disabled="emailCodeSent"
                />
                <button type="button" class="btn-side" @click="handleCheckLoginId">
                  중복확인
                </button>
              </div>
              <p v-if="loginIdMsg" class="field-msg" :class="{ success: loginIdChecked, error: !loginIdChecked }">
                {{ loginIdMsg }}
              </p>
            </div>

            <div class="field-group">
              <label class="field-label">닉네임</label>
              <div class="field-inline">
                <input
                  v-model.trim="name"
                  type="text"
                  class="field-input"
                  maxlength="10"
                  placeholder="닉네임"
                  @input="onChangeName"
                  :disabled="emailCodeSent"
                />
              </div>
              <p v-if="nameMsg" class="field-msg" :class="{ success: nameChecked, error: !nameChecked }">
                {{ nameMsg }}
              </p>
            </div>

            <div class="field-group">
              <label class="field-label">이메일</label>
              <input
                v-model.trim="email"
                type="email"
                class="field-input"
                maxlength="100"
                placeholder="이메일 주소를 입력하세요"
                @input="onChangeEmail"
                :disabled="emailCodeSent"
              />
              <p v-if="emailMsg" class="field-msg" :class="{ success: emailValid, error: !emailValid }">
                {{ emailMsg }}
              </p>
            </div>

            <button
              type="button"
              class="btn-submit"
              :disabled="!canSendEmail || loading"
              @click="handleSendEmailCode"
            >
              인증번호 보내기 <span class="btn-arrow">→</span>
            </button>
          </template>
<!--------------------------------------------------------------------------------------------------------------------------------------->
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
              />
              <p v-if="emailVerifyMsg" class="field-msg error">
                {{ emailVerifyMsg }}
              </p>
            </div>

            <div class="btn-row">
              <button type="button" class="btn-light" @click="step = 1">
                이전
              </button>

              <button
                type="button"
                class="btn-light"
                :disabled="loading || resendSeconds > 0"
                @click="handleResendEmailCode"
              >
                {{ resendSeconds > 0 ? `${resendSeconds}초 후 재전송 가능` : '재전송' }}
              </button>

              <button
                type="button"
                class="btn-submit btn-submit-small"
                :disabled="loading || !emailCode"
                @click="handleVerifyEmailCode"
              >
                인증번호 확인 <span class="btn-arrow">→</span>
              </button>
            </div>
          </template>

          <template v-else>
            <div class="info-box success-box">
              이메일 인증이 완료되었습니다.
            </div>

            <div class="field-group">
              <label class="field-label">비밀번호</label>
              <input
                v-model="password"
                type="password"
                class="field-input"
                maxlength="20"
                placeholder="비밀번호를 입력하세요"
                @input="validatePassword"
              />
              <p v-if="passwordMsg" class="field-msg error">
                {{ passwordMsg }}
              </p>
              <p v-else-if="password" class="field-msg success">
                사용 가능한 비밀번호 형식입니다.
              </p>
            </div>

            <div class="field-group">
              <label class="field-label">비밀번호 확인</label>
              <input
                v-model="passwordConfirm"
                type="password"
                class="field-input"
                placeholder="비밀번호를 다시 입력하세요"
                @input="checkPasswordMatch"
              />
              <p v-if="passwordConfirmMsg" class="field-msg error">
                {{ passwordConfirmMsg }}
              </p>
              <p v-else-if="passwordConfirm" class="field-msg success">
                비밀번호가 일치합니다.
              </p>
            </div>

            <div class="btn-row">
              <button type="button" class="btn-light" @click="step = 2">
                이전
              </button>
              <button
                type="button"
                class="btn-submit btn-submit-small"
                :disabled="!canSignup || loading"
                @click="handleSignup"
              >
                가입하기 <span class="btn-arrow">→</span>
              </button>
            </div>
          </template>
        </form>

        <p class="form-footer">
          이미 계정이 있으신가요?
          <router-link to="/login" class="form-link">로그인</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import PageNav from '../../components/common/PageNav.vue'
import { computed, ref, onBeforeUnmount} from 'vue'
import { useRouter } from 'vue-router'
import {
  checkLoginId,
  sendEmailCode,
  resendEmailCode,
  verifyEmailCode,
  signUp
} from '@/api/authApi'

const router = useRouter()

const navLinks = [
  { label: '홈', to: '/', active: true }
]

const step = ref(1)
const loading = ref(false)

const loginId = ref('')
const loginIdChecked = ref(false)
const loginIdMsg = ref('')

const name = ref('')
const nameChecked=ref(false)
const nameValid = ref(false)
const nameMsg = ref('')

const email = ref('')
const emailValid = ref(false)
const emailMsg = ref('')
const emailCode = ref('')
const emailVerified = ref(false)
const emailVerifyMsg = ref('')

const emailCodeSent = ref(false)

const resendSeconds = ref(0)
let resendTimer = null

const password = ref('')
const passwordMsg = ref('')
const passwordConfirm = ref('')
const passwordConfirmMsg = ref('')

const loginIdRegex = /^[a-z0-9]{1,20}$/
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>/?])[A-Za-z\d!@#$%^&*()_+\-=\[\]{};':"\\|,.<>/?]{8,20}$/
const koreanRegex = /^[가-힣0-9]{1,10}$/

function resetEmailFlow() {
  emailVerified.value = false
  emailCode.value = ''
  emailVerifyMsg.value = ''
  password.value = ''
  passwordMsg.value = ''
  passwordConfirm.value = ''
  passwordConfirmMsg.value = ''
  if (step.value > 1) {
    step.value = 1
  }
}

function onChangeLoginId(event) {
  const rawValue = event.target.value

  loginIdChecked.value = false
  resetEmailFlow()

  if (!rawValue) {
    loginId.value = ''
    loginIdMsg.value = ''
    return
  }

  if (/\s/.test(rawValue)) {
    loginId.value = rawValue.replace(/\s/g, '')
    loginIdMsg.value = '아이디에는 공백을 사용할 수 없습니다.'
    return
  }

  if (!loginIdRegex.test(rawValue)) {
    loginId.value = rawValue
    loginIdMsg.value = '아이디는 영문 소문자와 숫자로 최대 20자까지 사용할 수 있습니다.'
    return
  }

  loginId.value = rawValue
  loginIdMsg.value = ''
}

function onChangeName() {
  validateName()
  resetEmailFlow()
}

function validateName() {
  if (!name.value) {
    nameValid.value = false
    nameChecked.value = false
    nameMsg.value = ''
    return
  }

  if (!koreanRegex.test(name.value)) {
    nameValid.value = false
    nameChecked.value = false
    nameMsg.value = '닉네임은 한글과 숫자 1~10자만 입력 가능합니다.'
    return
  }

  nameValid.value = true
  nameChecked.value = true
  nameMsg.value = '사용 가능한 이름 형식입니다.'
}

function onChangeEmail() {
  validateEmail()
  resetEmailFlow()
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

async function handleCheckLoginId() {

  if (!loginId.value) {
    loginIdChecked.value = false
    loginIdMsg.value = '아이디를 입력하세요.'
    return
  }

  if (!loginIdRegex.test(loginId.value)) {
    loginIdChecked.value = false
    loginIdMsg.value = '아이디는 영문 소문자와 숫자로 최대 20자까지 사용할 수 있습니다.'
    return
  }

  loading.value = true
  try {
    const res = await checkLoginId({ loginId: loginId.value })

    if (res.data.available) {
      loginIdChecked.value = true
      loginIdMsg.value = '사용 가능한 아이디입니다.'
      console.log("사용 가능한 아이디")
    } else {
      loginIdChecked.value = false
      loginIdMsg.value = '이미 사용 중인 아이디입니다.'
    }

  } catch (e) {
    loginIdChecked.value = false
    loginIdMsg.value =
      e.response?.data?.message || '아이디 중복확인 중 오류가 발생했습니다.'
  } finally {
    loading.value = false
  }
}

//재전송 카운트다운
function startResendCountdown(seconds = 10) {
  if (resendTimer) {
    clearInterval(resendTimer)
    resendTimer = null
  }

  resendSeconds.value = seconds

  resendTimer = setInterval(() => {
    if (resendSeconds.value > 0) {
      resendSeconds.value -= 1
    }

    if (resendSeconds.value <= 0) {
      clearInterval(resendTimer)
      resendTimer = null
    }
  }, 1000)
}

const canSendEmail = computed(() => {
  return !!loginId.value && loginIdChecked.value && !!name.value && emailValid.value
})

async function handleSendEmailCode() {
  if (!canSendEmail.value) return

  loading.value = true
  emailCodeSent.value = true
  try {
    await sendEmailCode({
      loginId: loginId.value,
      name: name.value,
      email: email.value
    })
    emailVerifyMsg.value = ''
    step.value = 2
    startResendCountdown(10)
    
    alert('인증번호를 발송했습니다.')
  } catch (e) {
    emailVerifyMsg.value = e.response?.data?.message || ''
    alert(e.response?.data?.message || '인증번호 발송에 실패했습니다.')
  } finally {
    loading.value = false
  }
}

async function handleResendEmailCode() {
  loading.value = true
  try {
    const res = await resendEmailCode({
      email: email.value
    })

    emailVerifyMsg.value = ''
    startResendCountdown(10)
    alert(res.data.message || '인증번호가 재발송되었습니다.')
  } catch (e) {
    emailVerifyMsg.value = e.response?.data?.message || ''
    alert(e.response?.data?.message || '인증번호 재전송에 실패했습니다.')
  } finally {
    loading.value = false
  }
}

async function handleVerifyEmailCode() {
  if (!emailCode.value) {
    emailVerifyMsg.value = '인증번호를 입력하세요.'
    return
  }

  loading.value = true
  try {
    const res = await verifyEmailCode({
      email: email.value,
      code: emailCode.value
    })

    if (res.data.verified) {
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
    loading.value = false
  }
}



function validatePassword() {
  if (!password.value) {
    passwordMsg.value = ''
    checkPasswordMatch()
    return
  }

  if (!passwordRegex.test(password.value)) {
    passwordMsg.value = '비밀번호는 영문 대/소문자, 숫자, 특수문자를 포함한 8~20자여야 합니다.'
  } else {
    passwordMsg.value = ''
  }

  checkPasswordMatch()
}

function checkPasswordMatch() {
  if (!passwordConfirm.value) {
    passwordConfirmMsg.value = ''
    return
  }

  if (password.value !== passwordConfirm.value) {
    passwordConfirmMsg.value = '비밀번호가 일치하지 않습니다.'
  } else {
    passwordConfirmMsg.value = ''
  }
}

const canSignup = computed(() => {
  return (
    emailVerified.value &&
    !!password.value &&
    !!passwordConfirm.value &&
    !passwordMsg.value &&
    !passwordConfirmMsg.value
  )
})

async function handleSignup() {
  if (!canSignup.value) return

  loading.value = true
  try {
    await signUp({
      loginId: loginId.value,
      name: name.value,
      email: email.value,
      password: password.value
    })

    alert('회원가입이 완료되었습니다.')
    router.push('/login')
  } catch (e) {
    alert(e.response?.data?.message || '회원가입 중 오류가 발생했습니다.')
  } finally {
    loading.value = false
  }
}

//카운트다운 정리
onBeforeUnmount(() => {
  if (resendTimer) {
    clearInterval(resendTimer)
    resendTimer = null
  }
})

</script>

<style scoped>
.page-wrap {
  background: var(--bg);
  overflow: hidden;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.12);
  min-height: 1000px;
}

.signup-body {
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

.field-inline {
  display: flex;
  gap: 10px;
}

.field-inline .field-input {
  flex: 1;
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

.btn-submit,
.btn-side,
.btn-light {
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  font-family: 'Pretendard', sans-serif;
  cursor: pointer;
  transition: opacity 0.18s, transform 0.12s;
}

.btn-submit:active,
.btn-side:active,
.btn-light:active {
  transform: scale(0.98);
}

.btn-submit:disabled,
.btn-side:disabled,
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

.btn-side {
  min-width: 92px;
  height: 46px;
  border: none;
  background: var(--ink);
  color: var(--white);
  padding: 0 14px;
}

.btn-side:hover:not(:disabled) {
  opacity: 0.88;
}

.btn-light {
  height: 46px;
  border: 1px solid var(--border);
  background: var(--white);
  color: var(--ink);
  padding: 0 16px;
  flex: 1;
}

.btn-row {
  display: flex;
  gap: 10px;
  align-items: center;
}

.btn-arrow {
  font-size: 15px;
}

.form-footer {
  margin-top: 22px;
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

@media (max-width: 640px) {
  .signup-body {
    padding: 20px 14px;
  }

  .form-shell {
    padding: 32px 20px;
  }

  .form-title {
    font-size: 30px;
  }

  .step-bar {
    flex-direction: column;
  }

  .field-inline,
  .btn-row {
    flex-direction: column;
  }

  .btn-submit-small,
  .btn-light,
  .btn-side {
    width: 100%;
  }
}
</style>