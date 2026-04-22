<template>
  <div class="profile-wrap">
    <div class="profile-head">
      <h2 class="my-main-title">회원 정보 수정</h2>
      <p class="profile-sub">기본 정보와 비밀번호를 각각 수정할 수 있습니다.</p>
    </div>

    <section class="profile-card">
      <h3 class="section-title">기본 정보 수정</h3>

      <div class="form-grid">
        <div class="form-group full">
          <label>아이디</label>

          <div class="inline-field">
            <input
              v-model.trim="profileForm.loginId"
              type="text"
              maxlength="20"
              placeholder="아이디를 입력하세요"
              @input="onChangeLoginId"
            />

            <button
              type="button"
              class="check-btn"
              :disabled="!canCheckLoginId || profileLoading"
              @click="checkLoginIdDuplicate"
            >
              아이디 확인
            </button>
          </div>

          <p
            class="msg"
            :class="{
              ok: loginIdValid && loginIdChecked,
              error: profileForm.loginId && !loginIdValid
            }"
          >
            {{ loginIdMsg }}
          </p>
        </div>

        <div class="form-group full">
          <label>닉네임</label>
          <input
            v-model.trim="profileForm.name"
            type="text"
            maxlength="10"
            placeholder="닉네임을 입력하세요"
            @input="onChangeName"
          />
          <p
            class="msg"
            :class="{
              ok: nameValid,
              error: profileForm.name && !nameValid
            }"
          >
            {{ nameMsg }}
          </p>
        </div>

        <div class="form-group full">
          <label>이메일</label>
          <input
            v-model.trim="profileForm.email"
            type="email"
            maxlength="100"
            placeholder="이메일은 변경할 수 없습니다"
            disabled
          />
          <p class="msg">
            이메일은 변경할 수 없습니다.
          </p>
        </div>
      </div>

      <div class="profile-actions">
        <button
          type="button"
          class="btn btn-line"
          :disabled="profileLoading"
          @click="resetProfileForm"
        >
          취소
        </button>
        <button
          type="button"
          class="btn btn-fill"
          :disabled="profileLoading"
          @click="saveProfile"
        >
          {{ profileLoading ? '저장 중...' : '기본 정보 저장' }}
        </button>
      </div>
    </section>

    <section class="profile-card">
      <h3 class="section-title">비밀번호 변경</h3>

      <div class="form-grid">
        <div class="form-group full">
          <label>현재 비밀번호</label>
          <input
            v-model="passwordForm.currentPassword"
            type="password"
            maxlength="20"
            placeholder="현재 비밀번호를 입력하세요"
          />
        </div>

        <div class="form-group">
          <label>새 비밀번호</label>
          <input
            v-model="passwordForm.newPassword"
            type="password"
            maxlength="20"
            placeholder="새 비밀번호를 입력하세요"
            @input="validateNewPassword"
          />
          <p
            class="msg"
            :class="{ ok: passwordValid, error: passwordForm.newPassword && !passwordValid }"
          >
            {{ passwordMsg }}
          </p>
        </div>

        <div class="form-group">
          <label>새 비밀번호 확인</label>
          <input
            v-model="passwordForm.newPasswordConfirm"
            type="password"
            maxlength="20"
            placeholder="새 비밀번호를 다시 입력하세요"
            @input="validatePasswordConfirm"
          />
          <p
            class="msg"
            :class="{ ok: passwordConfirmValid, error: passwordForm.newPasswordConfirm && !passwordConfirmValid }"
          >
            {{ passwordConfirmMsg }}
          </p>
        </div>
      </div>

      <div class="profile-actions">
        <button
          type="button"
          class="btn btn-line"
          :disabled="passwordLoading"
          @click="resetPasswordForm"
        >
          취소
        </button>
        <button
          type="button"
          class="btn btn-fill"
          :disabled="passwordLoading"
          @click="savePassword"
        >
          {{ passwordLoading ? '저장 중...' : '비밀번호 저장' }}
        </button>
      </div>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import {
  updateMyProfile,
  updateMyPassword,
  checkLoginIdApi
} from '@/api/mypageApi'

const emit = defineEmits(['updateProfile'])

const props = defineProps({
  userInfo: {
    type: Object,
    default: () => ({})
  }
})

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const profileLoading = ref(false)
const passwordLoading = ref(false)

const originalProfile = reactive({
  loginId: '',
  name: '',
  email: ''
})

const profileForm = reactive({
  loginId: '',
  name: '',
  email: ''
})

const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  newPasswordConfirm: ''
})

const loginIdRegex = /^[a-zA-Z0-9]{4,20}$/
const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>/?])[A-Za-z\d!@#$%^&*()_+\-=\[\]{};':"\\|,.<>/?]{8,20}$/
const koreanRegex = /^[가-힣0-9]{1,10}$/
const koreanJamoRegex = /[ㄱ-ㅎㅏ-ㅣ]/

const loginIdValid = ref(false)
const loginIdMsg = ref('')
const loginIdChecked = ref(false)
const checkedLoginIdValue = ref('')

const nameValid = ref(true)
const nameMsg = ref('')

const passwordValid = ref(true)
const passwordMsg = ref('')

const passwordConfirmValid = ref(true)
const passwordConfirmMsg = ref('')

const canCheckLoginId = computed(() => {
  return !!profileForm.loginId && loginIdValid.value
})

function validateLoginId() {
  if (!profileForm.loginId) {
    loginIdValid.value = false
    loginIdMsg.value = ''
    return
  }

  if (!loginIdRegex.test(profileForm.loginId)) {
    loginIdValid.value = false
    loginIdMsg.value = '아이디는 영문 또는 숫자 4~20자입니다.'
    return
  }

  loginIdValid.value = true
  loginIdMsg.value = '형식이 올바릅니다. 아이디 확인을 진행해주세요.'
}

function validateName() {
  if (!profileForm.name) {
    nameValid.value = false
    nameMsg.value = ''
    return
  }

  if (/\s/.test(profileForm.name)) {
    nameValid.value = false
    nameMsg.value = '닉네임에는 띄어쓰기를 사용할 수 없습니다.'
    return
  }

  if (koreanJamoRegex.test(profileForm.name)) {
    nameValid.value = false
    nameMsg.value = '닉네임은 완성된 한글과 숫자만 입력 가능합니다.'
    return
  }

  if (!koreanRegex.test(profileForm.name)) {
    nameValid.value = false
    nameMsg.value = '닉네임은 한글과 숫자 1~10자만 입력 가능합니다.'
    return
  }

  nameValid.value = true
  nameMsg.value = '사용 가능한 닉네임 형식입니다.'
}

function onChangeName(event) {
  const rawValue = event.target.value

  if (!rawValue) {
    profileForm.name = ''
    nameValid.value = false
    nameMsg.value = ''
    return
  }

  if (/\s/.test(rawValue)) {
    profileForm.name = rawValue.replace(/\s/g, '')
    nameValid.value = false
    nameMsg.value = '닉네임에는 띄어쓰기를 사용할 수 없습니다.'
    return
  }

  profileForm.name = rawValue
  validateName()
}

function validateNewPassword() {
  if (!passwordForm.newPassword) {
    passwordValid.value = true
    passwordMsg.value = ''
    validatePasswordConfirm()
    return
  }

  if (!passwordRegex.test(passwordForm.newPassword)) {
    passwordValid.value = false
    passwordMsg.value = '비밀번호는 영문 대/소문자, 숫자, 특수문자를 포함한 8~20자여야 합니다.'
    validatePasswordConfirm()
    return
  }

  passwordValid.value = true
  passwordMsg.value = '사용 가능한 비밀번호 형식입니다.'
  validatePasswordConfirm()
}

function validatePasswordConfirm() {
  if (!passwordForm.newPassword && !passwordForm.newPasswordConfirm) {
    passwordConfirmValid.value = true
    passwordConfirmMsg.value = ''
    return
  }

  if (passwordForm.newPassword !== passwordForm.newPasswordConfirm) {
    passwordConfirmValid.value = false
    passwordConfirmMsg.value = '새 비밀번호가 일치하지 않습니다.'
    return
  }

  passwordConfirmValid.value = true
  passwordConfirmMsg.value = '새 비밀번호가 일치합니다.'
}

function onChangeLoginId() {
  validateLoginId()
  loginIdChecked.value = false
  checkedLoginIdValue.value = ''
}

async function checkLoginIdDuplicate() {
  validateLoginId()

  if (!loginIdValid.value) return

  if (profileForm.loginId === originalProfile.loginId) {
    loginIdChecked.value = true
    checkedLoginIdValue.value = profileForm.loginId
    loginIdMsg.value = '현재 사용 중인 아이디입니다.'
    return
  }

  try {
    const res = await checkLoginIdApi(profileForm.loginId)

    if (res.data.available) {
      loginIdChecked.value = true
      checkedLoginIdValue.value = profileForm.loginId
      loginIdMsg.value = '사용 가능한 아이디입니다.'
    } else {
      loginIdChecked.value = false
      checkedLoginIdValue.value = ''
      loginIdMsg.value = '이미 사용 중인 아이디입니다.'
    }
  } catch (e) {

    console.log('아이디 확인 실패', e)
    loginIdChecked.value = false
    checkedLoginIdValue.value = ''
    loginIdMsg.value = '아이디 확인 중 오류가 발생했습니다.'
  }
}

function applyProfile(data) {
  profileForm.loginId = data?.loginId || ''
  profileForm.name = data?.name || ''
  profileForm.email = data?.email || ''

  originalProfile.loginId = data?.loginId || ''
  originalProfile.name = data?.name || ''
  originalProfile.email = data?.email || ''

  validateLoginId()
  validateName()

  loginIdChecked.value = !!profileForm.loginId
  checkedLoginIdValue.value = profileForm.loginId
  loginIdMsg.value = profileForm.loginId ? '현재 사용 중인 아이디입니다.' : ''
}

function resetProfileForm() {
  applyProfile(originalProfile)
}

function resetPasswordForm() {
  passwordForm.currentPassword = ''
  passwordForm.newPassword = ''
  passwordForm.newPasswordConfirm = ''
  passwordValid.value = true
  passwordMsg.value = ''
  passwordConfirmValid.value = true
  passwordConfirmMsg.value = ''
}

async function saveProfile() {
  if (
    profileForm.loginId === originalProfile.loginId &&
    profileForm.name === originalProfile.name
  ) {
    alert('변경된 정보가 없습니다.')
    return
  }

  validateLoginId()
  validateName()

  if (!profileForm.name) {
    alert('닉네임을 입력해주세요.')
    return
  }

  if (!nameValid.value) {
    alert('닉네임 형식을 확인해주세요.')
    return
  }

  if (!loginIdValid.value) {
    alert('아이디 형식을 확인해주세요.')
    return
  }

  if (!loginIdChecked.value || checkedLoginIdValue.value !== profileForm.loginId) {
    alert('아이디 확인을 완료해주세요.')
    return
  }

  profileLoading.value = true

  try {
    await updateMyProfile({
      loginId: profileForm.loginId,
      name: profileForm.name
    })

    alert('기본 정보가 수정되었습니다.')

    emit('updateProfile', {
      name: profileForm.name
    })

    applyProfile({
      loginId: profileForm.loginId,
      name: profileForm.name,
      email: profileForm.email
    })
  } catch (e) {

    console.log('기본 정보 수정 실패', e)
    alert(e?.response?.data || '기본 정보 수정에 실패했습니다.')
  } finally {
    profileLoading.value = false
  }
}

async function savePassword() {
  validateNewPassword()
  validatePasswordConfirm()

  if (!passwordForm.currentPassword) {
    alert('현재 비밀번호를 입력해주세요.')
    return
  }

  if (!passwordForm.newPassword) {
    alert('새 비밀번호를 입력해주세요.')
    return
  }

  if (!passwordValid.value) {
    alert('새 비밀번호 형식을 확인해주세요.')
    return
  }

  if (!passwordConfirmValid.value) {
    alert('새 비밀번호 확인을 확인해주세요.')
    return
  }

  if (passwordForm.currentPassword === passwordForm.newPassword) {
    alert('새 비밀번호는 현재 비밀번호와 다르게 입력해주세요.')
    return
  }

  passwordLoading.value = true

  try {
    await updateMyPassword({
      currentPassword: passwordForm.currentPassword,
      newPassword: passwordForm.newPassword
    })

    alert('비밀번호가 변경되었습니다.')
    resetPasswordForm()
  } catch (e) {

    console.log('비밀번호 변경 실패', e)

    const errorData = e?.response?.data
    const errorMessage =
      typeof errorData === 'string'
        ? errorData
        : errorData?.message || '비밀번호 변경에 실패했습니다.'

    alert(errorMessage)
  } finally {
    passwordLoading.value = false
  }
}

watch(
  () => props.userInfo,
  (newValue) => {
    if (!newValue) return
    if (!newValue.loginId && !newValue.name && !newValue.email) return

    applyProfile(newValue)
  },
  { immediate: true, deep: true }
)
</script>

<style scoped>
.profile-wrap {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.profile-head {
  padding-bottom: 14px;
  border-bottom: 1px solid var(--border);
}

.my-main-title {
  font-family: 'Playfair Display', serif;
  font-size: 20px;
  font-weight: 400;
  color: var(--ink);
  margin-bottom: 8px;
}

.profile-sub {
  font-size: 13px;
  color: var(--muted);
}

.profile-card {
  background: var(--white);
  border: 1px solid var(--border);
  border-radius: 16px;
  padding: 24px;
}

.section-title {
  font-size: 15px;
  font-weight: 700;
  color: var(--ink);
  margin-bottom: 18px;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group.full {
  grid-column: 1 / -1;
}

.form-group label {
  font-size: 13px;
  font-weight: 600;
  color: var(--sub);
}

.form-group input {
  width: 100%;
  height: 44px;
  border: 1px solid var(--border);
  border-radius: 10px;
  padding: 0 14px;
  font-size: 14px;
  color: var(--ink);
  background: #fff;
  outline: none;
}

.form-group input:focus {
  border-color: var(--point);
  box-shadow: 0 0 0 3px rgba(194, 120, 92, 0.12);
}

.form-group input:disabled {
  background: #f8f6f3;
  color: #888;
  cursor: not-allowed;
}

.inline-field {
  display: flex;
  gap: 8px;
}

.inline-field input {
  flex: 1;
}

.check-btn {
  height: 44px;
  padding: 0 14px;
  border-radius: 10px;
  border: 1px solid var(--border);
  background: #fff;
  color: var(--sub);
  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
  cursor: pointer;
}

.check-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.msg {
  min-height: 18px;
  font-size: 12px;
  color: var(--muted);
}

.msg.ok {
  color: #2e7d32;
}

.msg.error {
  color: #c0392b;
}

.profile-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 18px;
}

.btn {
  min-width: 120px;
  height: 42px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
}

.btn-line {
  border: 1px solid var(--border);
  background: #fff;
  color: var(--sub);
}

.btn-fill {
  border: 1px solid var(--point);
  background: var(--point);
  color: #fff;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .profile-card {
    padding: 18px;
  }

  .inline-field {
    flex-direction: column;
  }

  .profile-actions {
    flex-direction: column-reverse;
  }

  .btn,
  .check-btn {
    width: 100%;
  }
}
</style>