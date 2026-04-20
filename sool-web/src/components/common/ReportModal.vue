<template>
  <div v-if="modelValue" class="report-modal-backdrop" @click="closeModal">
    <div class="report-modal" @click.stop>
      <div class="report-modal-head">
        <h3 class="report-modal-title">신고하기</h3>
        <button class="report-close-btn" @click="closeModal">×</button>
      </div>

      <p class="report-modal-desc">
        아래 신고 사유 중 하나를 선택해주세요.
      </p>

      <div class="report-reason-list">
        <label
          v-for="reason in reportReasons"
          :key="reason.code"
          class="report-reason-item"
          :class="{ active: selectedReasonCode === reason.code }"
        >
          <input
            v-model="selectedReasonCode"
            type="radio"
            name="reportReason"
            :value="reason.code"
          />
          <span>{{ reason.codeName }}</span>
        </label>
      </div>

      <div class="report-actions">
        <button class="report-cancel-btn" @click="closeModal">취소</button>
        <button
          class="report-submit-btn"
          :disabled="!selectedReasonCode || reportLoading"
          @click="submitReport"
        >
          {{ reportLoading ? '신고 중...' : '신고하기' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { getReportReasons, insertReport } from '@/api/reportApi'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  objType: {
    type: String,
    required: true
  },
  objId: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const reportReasons = ref([])
const selectedReasonCode = ref('')
const reportLoading = ref(false)
const loaded = ref(false)

const closeModal = () => {
  emit('update:modelValue', false)
  selectedReasonCode.value = ''
}

const moveToLogin = async () => {
  alert('세션이 만료되었습니다. 다시 로그인해주세요.')

  authStore.user = null
  authStore.initialized = true

  emit('update:modelValue', false)
  selectedReasonCode.value = ''

  router.replace(`/login?redirect=${encodeURIComponent(route.fullPath)}`)
}

const handleForbidden = async (error) => {
  if (error?.response?.status === 403) {
    await moveToLogin()
    return true
  }

  return false
}

const fetchReportReasons = async () => {
  try {
    const res = await getReportReasons()
    reportReasons.value = Array.isArray(res.data) ? res.data : []
    loaded.value = true
  } catch (error) {
    const handled = await handleForbidden(error)
    if (handled) return

    console.log('신고 사유 조회 실패', error)
    reportReasons.value = []
  }
}

const submitReport = async () => {
  if (!selectedReasonCode.value || reportLoading.value) return

  reportLoading.value = true

  try {
    await insertReport({
      objType: props.objType,
      objId: props.objId,
      reasonCode: selectedReasonCode.value
    })

    alert('신고가 접수되었습니다.')
    emit('success')
    closeModal()
  } catch (e) {
    const handled = await handleForbidden(e)
    if (handled) return

    console.log('신고 등록 실패', e)
    const message = e.response?.data?.message || '신고 처리에 실패했습니다.'
    alert(message)
  } finally {
    reportLoading.value = false
  }
}

watch(
  () => props.modelValue,
  async (newValue) => {
    if (newValue) {
      selectedReasonCode.value = ''

      if (!loaded.value) {
        await fetchReportReasons()
      }
    }
  }
)
</script>

<style scoped>
.report-modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.report-modal {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border-radius: 18px;
  padding: 24px;
  box-shadow: 0 18px 40px rgba(0, 0, 0, 0.18);
}

.report-modal-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.report-modal-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--ink);
  margin: 0;
}

.report-close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  font-size: 24px;
  line-height: 1;
  color: var(--muted);
  cursor: pointer;
}

.report-modal-desc {
  font-size: 13px;
  color: var(--muted);
  margin: 0 0 18px;
}

.report-reason-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.report-reason-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 16px;
  border: 1px solid var(--border);
  border-radius: 12px;
  cursor: pointer;
  background: #fff;
  transition: all 0.2s ease;
}

.report-reason-item.active {
  border-color: #e1a08b;
  background: #fff7f3;
}

.report-reason-item input {
  margin: 0;
}

.report-reason-item span {
  font-size: 14px;
  color: var(--ink);
}

.report-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 20px;
}

.report-cancel-btn,
.report-submit-btn {
  min-width: 88px;
  height: 40px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
}

.report-cancel-btn {
  border: 1px solid var(--border);
  background: #fff;
  color: var(--sub);
}

.report-submit-btn {
  border: none;
  background: var(--point);
  color: #fff;
}

.report-submit-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>