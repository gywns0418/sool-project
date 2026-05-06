<template>
  <div class="page-wrap">
    <PageNav :links="navLinks" />

    <section class="write-hero">
      <div class="write-hero-drink-thumb">
          <img v-if="drink?.image?.fileUrl" :src="drink.image.fileUrl" alt="주류 이미지" />
          <div v-else class="detail-placeholder">{{ drinkEmoji }}</div>
      </div>
      <div>
        <div class="write-hero-tag">{{ isEditMode ? 'Edit Note' : 'Tasting Note' }}</div>
        <div class="write-hero-title">{{ drink.drinkName || '-' }}</div>
        <div v-if="drink.drinkNameEn" class="write-hero-title">{{ drink.drinkNameEn }}</div>
        <div class="write-hero-sub">
          <span>{{ drink.categoryName || '-' }}</span>
          <span class="write-hero-dot"></span>
          <span>{{ drink.typeName || '-' }}</span>
          <span class="write-hero-dot"></span>
          <span>{{ drink.country || '-' }}</span>
        </div>
      </div>
    </section>

    <div class="write-single">
      <div class="form-card">
        <div class="form-card-header">
          <div class="form-card-icon">✎</div>
          <div>
            <div class="form-card-title">
              {{ isEditMode ? '노트 수정' : '노트 제목' }}
            </div>
            <div class="form-card-sub">
              {{ isEditMode ? '작성한 테이스팅 노트를 수정하세요' : '테이스팅 노트의 제목을 작성하세요' }}
            </div>
          </div>
        </div>

        <div class="form-section">
          <label class="form-label required">제목</label>
          <input
            v-model="title"
            type="text"
            class="text-input"
            maxlength="100"
            placeholder="예: 부드러운 베리향이 인상적인 와인"
            @input="updateTitleCount"
            @compositionupdate="updateTitleCount"
            @compositionend="updateTitleCount"
          />
        </div>
        <div class="text-count" :class="{ danger: titleCount >= 90 }">
          {{ titleCount }}/100
        </div>
      </div>

      <div class="note-top-section">
        <div class="form-card">
          <div class="form-card-header">
            <div class="form-card-icon">★</div>
            <div>
              <div class="form-card-title required">별점</div>
              <div class="form-card-sub">전체적인 만족도를 남겨보세요</div>
            </div>
          </div>

          <div class="form-section">
            <label class="form-label">별점</label>
            <div class="star-input">
              <button
                v-for="star in 5"
                :key="star"
                type="button"
                class="star-inp-item"
                :class="{ on: star <= selectedStar }"
                @click="selectedStar = star"
              >
                ★
              </button>
              <span class="star-label">{{ selectedStar }}점</span>
            </div>
          </div>
        </div>

        <div class="form-card">
          <div class="form-card-header">
            <div class="form-card-icon green">◎</div>
            <div>
              <div class="form-card-title required">맛 프로파일</div>
              <div class="form-card-sub">항목별로 1점부터 5점까지 선택하세요</div>
            </div>
          </div>

          <div class="score-grid">
            <div v-if="scores.length === 0" class="empty-metric">
              선택 가능한 맛 프로파일이 없습니다.
            </div>

            <div v-for="item in scores" :key="item.code" class="score-row">
              <div class="score-row-head">
                <div class="score-label-wrap">
                  <span class="score-label">{{ item.codeName }}</span>

                  <button
                    v-if="item.codeDesc"
                    type="button"
                    class="info-toggle-btn"
                    @click="toggleScoreDesc(item.code)"
                  >
                    i
                  </button>
                </div>

                <span class="score-val-text">{{ item.value }}점</span>
              </div>

              <p
                v-if="item.codeDesc && openScoreDescCode === item.code"
                class="score-desc"
              >
                {{ item.codeDesc }}
              </p>

              <div class="seg-bar">
                <button
                  v-for="n in 5"
                  :key="n"
                  type="button"
                  class="seg"
                  :class="{ lit: n <= item.value }"
                  @click="item.value = n"
                ></button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="form-card span2">
        <div class="form-card-header">
          <div class="form-card-icon blue">✎</div>
          <div>
            <div class="form-card-title">상세 기록</div>
            <div class="form-card-sub">느낀 향과 맛, 페어링, 분위기를 자유롭게 적어보세요</div>
          </div>
        </div>

        <div class="form-section">
          <label class="form-label required">테이스팅 내용</label>
          <textarea
            v-model="memo"
            class="write-textarea"
            maxlength="500"
            placeholder="향, 맛, 피니시, 함께 먹은 음식 등을 기록해보세요."
            @input="updateMemoCount"
            @compositionupdate="updateMemoCount"
            @compositionend="updateMemoCount"
          ></textarea>
          <div class="text-count" :class="{ danger: memoCount >= 450 }">
            {{ memoCount }}/500
          </div>
        </div>

        <div class="form-section">
          <label class="form-label required">사진 업로드</label>

          <input
            ref="fileInput"
            type="file"
            accept="image/png,image/jpeg,image/webp"
            style="display:none"
            @change="handleImageUpload"
          />

          <button type="button" class="upload-zone" @click="openImageUpload">

            <template v-if="!imagePreview">
              <div class="upload-icon-wrap">📷</div>

              <p>
                <em>클릭해서 사진 업로드</em><br />
                테이스팅 순간을 함께 남겨보세요
              </p>

              <div class="up-small">
                JPG, JPEG, PNG, WEBP 파일 5MB까지 업로드 가능
              </div>
            </template>

            <template v-else>
              <img :src="imagePreview" class="upload-preview" />
            </template>

          </button>
        </div>
      </div>

      <div class="submit-row">
        <button
          v-if="isEditMode"
          type="button"
          class="cancel-btn"
          :disabled="isSaving"
          @click="goBack"
        >
          취소
        </button>

        <button
          type="button"
          class="submit-btn"
          :disabled="isSaving"
          @click="saveNote"
        >
          {{ isSaving ? (isEditMode ? '수정 중...' : '저장 중...') : (isEditMode ? '노트 수정하기' : '노트 저장하기') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageNav from '../components/common/PageNav.vue'
import { categories } from '@/mock/soolData'
import { getNoteWriteForm, createNote, getNoteUpdateForm, updateNote } from '@/api/noteApi'
import { uploadImage, deleteImage } from '@/api/imageApi'
import { useAuthStore } from '@/stores/authStore'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const errorMsg = ref('')

const drink = ref({})
const title = ref('')
const titleCount = ref(0)
const selectedStar = ref(3)
const memo = ref('')
const memoCount = ref(0)
const scores = ref([])
const openScoreDescCode = ref('')
const isSaving = ref(false)

const fileInput = ref(null)
const selectedImageFile = ref(null)
const imagePreview = ref('')
const uploaded = ref(false)

const noteImage = ref(null)

let uploadedFileKey = ''
let uploadedFileUrl = ''

const ALLOWED_IMAGE_TYPES = ['image/png', 'image/jpeg', 'image/webp']
const ALLOWED_IMAGE_EXTENSIONS = ['png', 'jpg', 'jpeg', 'webp']
const MAX_IMAGE_SIZE = 5 * 1024 * 1024

const isEditMode = computed(() => !!route.params.noteId)
const drinkId = computed(() => Number(route.params.drinkId))
const noteId = computed(() => Number(route.params.noteId))

const navLinks = computed(() => {
  if (isEditMode.value) {
    return [
      { label: '홈', to: '/' },
      { label: drink.value.drinkName || '주류', to: drink.value.drinkId ? `/drinks/${drink.value.drinkId}` : '/' },
      { label: '노트 수정', to: route.fullPath, active: true }
    ]
  }

  return [
    { label: '홈', to: '/' },
    { label: drink.value.drinkName || '주류', to: drink.value.drinkId ? `/drinks/${drink.value.drinkId}` : '/'  },
    { label: '노트 작성', to: route.fullPath, active: true }
  ]
})

const TITLE_MAX_LENGTH = 100
const MEMO_MAX_LENGTH = 500

function updateTitleCount(e) {
  const value = e.target.value.slice(0, TITLE_MAX_LENGTH)

  if (title.value !== value) {
    title.value = value
  }

  titleCount.value = value.length
}

function updateMemoCount(e) {
  const value = e.target.value.slice(0, MEMO_MAX_LENGTH)

  if (memo.value !== value) {
    memo.value = value
  }

  memoCount.value = value.length
}

function syncTextCounts() {
  titleCount.value = title.value.length
  memoCount.value = memo.value.length
}

function resetPreviewUrl() {
  if (imagePreview.value && imagePreview.value.startsWith('blob:')) {
    URL.revokeObjectURL(imagePreview.value)
  }
}

function clearSelectedImage() {
  resetPreviewUrl()
  selectedImageFile.value = null
  uploaded.value = false

  if (isEditMode.value && noteImage.value?.fileUrl) {
    imagePreview.value = noteImage.value.fileUrl
  } else {
    imagePreview.value = ''
  }

  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

function extractErrorMessage(error, defaultMessage) {
  const data = error?.response?.data

  if (typeof data === 'string' && data.trim()) {
    return data
  }

  if (data?.message) {
    return data.message
  }

  if (data?.error) {
    return data.error
  }

  if (Array.isArray(data?.errors) && data.errors.length > 0) {
    const firstError = data.errors[0]
    if (typeof firstError === 'string') return firstError
    if (firstError?.defaultMessage) return firstError.defaultMessage
    if (firstError?.message) return firstError.message
  }

  return defaultMessage
}

function resetForm() {
  drink.value = {}
  title.value = ''
  titleCount.value = 0
  selectedStar.value = 3
  memo.value = ''
  memoCount.value = 0
  scores.value = []
  openScoreDescCode.value = ''
  clearSelectedImage()
}


function mergeMetricList(metricList, defaultMetricList) {
  const map = new Map()

  metricList.forEach(m => {
    const code = m.code || m.metricCode

    map.set(code, {
      code,
      codeName: m.codeName || m.metricName,
      codeDesc: m.codeDesc || '',
      value: Number(m.value ?? m.score ?? 1),
      sortSeq: Number(m.sortSeq ?? 9999)
    })
  })

  defaultMetricList.forEach(d => {
    const code = d.code || d.metricCode

    if (!map.has(code)) {
      map.set(code, {
        code,
        codeName: d.codeName || d.metricName,
        codeDesc: d.codeDesc || '',
        value: 1,
        sortSeq: Number(d.sortSeq ?? 9999)
      })
    }
  })

  return Array.from(map.values()).sort((a, b) => {
    return Number(a.sortSeq ?? 9999) - Number(b.sortSeq ?? 9999)
  })
}

function toggleScoreDesc(code) {
  openScoreDescCode.value = openScoreDescCode.value === code ? '' : code
}

async function fetchWriteForm() {
  if (!drinkId.value || Number.isNaN(drinkId.value)) {
    router.replace('/404')
    return
  }

  try {
    const res = await getNoteWriteForm(drinkId.value)

    if (!res.data || !res.data.drink) {
      router.replace('/404')
      return
    }

    drink.value = res.data.drink
    scores.value = mergeMetricList([], res.data.metricList || [])
    syncTextCounts()
  } catch (error) {
    const status = error.response?.status

    if (status === 409) {
      alert(extractErrorMessage(error, '이미 작성한 노트가 있습니다.'))
      router.replace(`/drinks/${drinkId.value}`)
      return
    }

    router.replace('/404')
  }
}

async function fetchEditForm() {
  if (!noteId.value || Number.isNaN(noteId.value)) {
    router.replace('/404')
    return
  }

  try {
    const res = await getNoteUpdateForm(noteId.value)

    if (!res.data || !res.data.note || !res.data.drink) {
      router.replace('/404')
      return
    }

    const drinkData = res.data.drink || {}
    const noteData = res.data.note || {}
    const metricList = res.data.metricList || []
    const defaultMetricList = res.data.defaultMetricList || []

    noteImage.value = res.data.image || null
    imagePreview.value = res.data.image?.fileUrl || ''

    drink.value = {
      drinkId: drinkData.drinkId,
      drinkName: drinkData.drinkName,
      drinkNameEn: drinkData.drinkNameEn,
      categoryCode: drinkData.categoryCode,
      categoryName: drinkData.categoryName,
      typeName: drinkData.typeName,
      country: drinkData.country,
      image: drinkData.image
    }

    title.value = noteData.title || ''
    selectedStar.value = Number(noteData.rating ?? 3)
    memo.value = noteData.content || ''

    scores.value = mergeMetricList(metricList, defaultMetricList)

    syncTextCounts()
  } catch (error) {
    const status = error.response?.status

    if (status === 403) {
      alert(extractErrorMessage(error, '수정할 수 없습니다.'))
      router.replace(`/notes/${noteId.value}`)
      return
    }

    router.replace('/404')
  }
}

const drinkEmoji = computed(() => {
  const code = drink.value?.categoryCode
  const emojiData = categories.find(e => e.name === code)
  return emojiData ? emojiData.emoji : '🍹'
})

async function fetchPageData() {
  resetForm()

  if (isEditMode.value) {
    await fetchEditForm()
    return
  }

  await fetchWriteForm()
}

async function validateLoginSession() {
  await authStore.fetchMe()

  if (!authStore.isLogin) {
    alert('로그인이 필요합니다. 다시 로그인해주세요.')
    router.replace({
      path: '/login',
      query: { redirect: route.fullPath }
    })
    return false
  }

  return true
}

function validateImageFile(file) {
  const fileName = file?.name || ''
  const extension = fileName.includes('.') ? fileName.split('.').pop().toLowerCase() : ''

  const isValidType = ALLOWED_IMAGE_TYPES.includes(file.type)
  const isValidExtension = ALLOWED_IMAGE_EXTENSIONS.includes(extension)

  if (!isValidType || !isValidExtension) {
    return '이미지는 PNG, JPG, JPEG, WEBP 파일만 업로드할 수 있습니다.'
  }

  if (file.size > MAX_IMAGE_SIZE) {
    return '파일 크기는 5MB 이하만 업로드할 수 있습니다.'
  }

  return ''
}

async function validateImageSignature(file) {
  const buffer = await file.slice(0, 12).arrayBuffer()
  const bytes = new Uint8Array(buffer)

  const isPng =
    bytes.length >= 4 &&
    bytes[0] === 0x89 &&
    bytes[1] === 0x50 &&
    bytes[2] === 0x4E &&
    bytes[3] === 0x47

  const isJpeg =
    bytes.length >= 3 &&
    bytes[0] === 0xFF &&
    bytes[1] === 0xD8 &&
    bytes[2] === 0xFF

  const isWebp =
    bytes.length >= 12 &&
    bytes[0] === 0x52 &&
    bytes[1] === 0x49 &&
    bytes[2] === 0x46 &&
    bytes[3] === 0x46 &&
    bytes[8] === 0x57 &&
    bytes[9] === 0x45 &&
    bytes[10] === 0x42 &&
    bytes[11] === 0x50

  return isPng || isJpeg || isWebp
}

const saveNote = async () => {
  if (isSaving.value) return

  const isValidSession = await validateLoginSession()

  if (!isValidSession) {
    return
  }

  if (!title.value.trim()) {
    alert('제목을 입력해주세요')
    return
  }

  if (!memo.value.trim()) {
    alert('내용을 입력해주세요')
    return
  }

  if (!imagePreview.value) {
    alert('이미지를 추가해주세요')
    return
  }

  errorMsg.value = ''
  uploadedFileKey = ''
  uploadedFileUrl = ''

  try {
    isSaving.value = true

    if (selectedImageFile.value) {
      const invalidImageMessage = validateImageFile(selectedImageFile.value)

      if (invalidImageMessage) {
        alert(invalidImageMessage)
        return
      }

      const isRealImage = await validateImageSignature(selectedImageFile.value)

      if (!isRealImage) {
        alert('실제 이미지 파일만 업로드할 수 있습니다.')
        clearSelectedImage()
        return
      }

      const formData = new FormData()
      formData.append('file', selectedImageFile.value)
      formData.append('dirName', 'NOTE')

      try {
        const uploadRes = await uploadImage(formData)
        uploadedFileKey = uploadRes.data.fileKey
        uploadedFileUrl = uploadRes.data.fileUrl
      } catch (uploadError) {
        uploadError.message = extractErrorMessage(uploadError, '이미지 업로드 중 오류가 발생했습니다.')
        throw uploadError
      }
    }

    const payload = {
      drinkId: drink.value.drinkId,
      title: title.value.trim(),
      content: memo.value.trim(),
      rating: selectedStar.value,
      metricList: scores.value.map(m => ({
        metricCode: m.code,
        score: m.value
      })),
      image: {
        fileUrl: uploadedFileUrl || noteImage.value?.fileUrl || '',
        fileKey: uploadedFileKey || noteImage.value?.fileKey || ''
      }
    }

    if (isEditMode.value) {
      await updateNote(noteId.value, payload)

      if (uploadedFileKey && noteImage.value?.fileKey) {
        try {
          await deleteImage(noteImage.value.fileKey)
        } catch (error) {
          console.log('기존 이미지 삭제 실패', error)
        }
      }

      alert('노트가 수정되었습니다.')
      router.push(`/notes/${noteId.value}`)
      return
    }

    const res = await createNote(payload)
    const createdNoteId = res.data?.noteId

    alert('노트가 저장되었습니다.')

    if (createdNoteId) {
      router.push(`/notes/${createdNoteId}`)
    }
  } catch (e) {
    if (uploadedFileKey) {
      try {
        await deleteImage(uploadedFileKey)
      } catch (deleteError) {
        console.log('업로드 이미지 삭제 실패', deleteError)
      }
    }

    errorMsg.value = extractErrorMessage(e, '노트 저장 중 오류가 발생했습니다.')
    alert(errorMsg.value)
  } finally {
    isSaving.value = false
  }
}

function goBack() {
  if (isEditMode.value && noteId.value) {
    router.push(`/notes/${noteId.value}`)
    return
  }

  if (drink.value?.drinkId) {
    router.push(`/drinks/${drink.value.drinkId}`)
    return
  }

  router.back()
}

function openImageUpload() {
  fileInput.value.click()
}

async function handleImageUpload(e) {
  const file = e.target.files[0]

  if (!file) return

  const invalidImageMessage = validateImageFile(file)

  if (invalidImageMessage) {
    alert(invalidImageMessage)
    clearSelectedImage()
    return
  }

  const isRealImage = await validateImageSignature(file)

  if (!isRealImage) {
    alert('실제 이미지 파일만 업로드할 수 있습니다.')
    clearSelectedImage()
    return
  }

  resetPreviewUrl()
  selectedImageFile.value = file
  imagePreview.value = URL.createObjectURL(file)
  uploaded.value = true
}

onMounted(async () => {
  const wasLogin = authStore.isLogin

  await authStore.fetchMe()

  if (!authStore.isLogin) {
    if (wasLogin) {
      alert('로그인이 필요합니다. 다시 로그인해주세요.')
    }
    router.replace(`/login?redirect=${encodeURIComponent(route.fullPath)}`)
    return
  }

  await fetchPageData()
})

onBeforeUnmount(() => {
  resetPreviewUrl()
})

watch(
  () => [route.params.drinkId, route.params.noteId],
  async () => {
    if (!authStore.isLogin) return
    await fetchPageData()
  }
)
</script>

<style scoped>
.page-wrap {
  background: var(--bg);
  overflow: hidden;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.12);
}

.write-hero {
  background: var(--white);
  border-bottom: 1px solid var(--border);
  padding: 32px 60px 28px;
  display: flex;
  align-items: center;
  gap: 24px;
}

.write-hero-drink-thumb {
  width: 72px;
  height: 88px;
  background: linear-gradient(145deg, #fdf3ef, #f5e8e0);
  border: 1px solid #f0d0c0;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
}

.write-hero-drink-thumb img {
  width: 100%;
  height: 100;
  object-fit: cover;
  border-radius: 12px;
  border: 1px solid var(--border);
  display: block;
}

.write-hero-tag {
  font-size: 10px;
  letter-spacing: 2px;
  text-transform: uppercase;
  color: var(--point);
  font-weight: 600;
  margin-bottom: 5px;
}

.write-hero-title {
  font-family: 'Playfair Display', serif;
  font-size: 26px;
  font-weight: 400;
  color: var(--ink);
  margin-bottom: 4px;
}

.write-hero-sub {
  font-size: 12.5px;
  color: var(--muted);
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.write-hero-dot {
  width: 3px;
  height: 3px;
  border-radius: 50%;
  background: var(--border);
}

.write-single {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 28px 40px 48px;
  max-width: 900px;
  margin: 0 auto;
}

.note-top-section {
  display: grid;
  grid-template-columns: 1fr 1.2fr;
  gap: 20px;
}

.form-card {
  background: var(--white);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 22px 24px;
}

.form-card.span2 {
  grid-column: 1 / -1;
}

.form-card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 18px;
  padding-bottom: 14px;
  border-bottom: 1px solid var(--border);
}

.form-card-icon {
  width: 32px;
  height: 32px;
  border-radius: 10px;
  background: #fdf3ef;
  color: var(--point);
  display: flex;
  align-items: center;
  justify-content: center;
}

.form-card-icon.green {
  background: #eef6f1;
  color: var(--point2);
}

.form-card-icon.blue {
  background: #eef4f8;
  color: #40698c;
}

.form-card-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--ink);
}

.form-card-sub {
  font-size: 12px;
  color: var(--muted);
  margin-top: 2px;
}

.form-section + .form-section {
  margin-top: 18px;
}

.form-label {
  display: block;
  font-size: 12px;
  color: var(--sub);
  margin-bottom: 10px;
}

.text-input {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
}

.star-input {
  display: flex;
  align-items: center;
  gap: 8px;
}

.star-inp-item {
  border: none;
  background: transparent;
  font-size: 28px;
  color: #ddd3c6;
  cursor: pointer;
}

.star-inp-item.on {
  color: var(--yellow);
}

.star-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--point);
  margin-left: 8px;
}

.score-grid {
  display: grid;
  gap: 16px;
}

.empty-metric {
  min-height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px dashed var(--border);
  border-radius: 10px;
  background: white;
  font-size: 13px;
  color: var(--muted);
}

.score-row-head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  flex-wrap: nowrap;
}

.score-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--ink);
}

.score-val-text {
  font-size: 12px;
  color: var(--muted);
  margin-left: auto;
  white-space: nowrap;
}

.seg-bar {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 6px;
}

.seg {
  height: 12px;
  border: none;
  border-radius: 99px;
  background: var(--border);
  cursor: pointer;
}

.seg.lit {
  background: var(--point2);
}

.write-textarea {
  width: 100%;
  min-height: 180px;
  border: 1px solid var(--border);
  border-radius: 10px;
  padding: 14px 16px;
  font-size: 13px;
  line-height: 1.75;
  resize: none;
}

.upload-zone {
  width: 100%;
  border: 1.5px dashed var(--border);
  border-radius: 12px;
  padding: 24px;
  text-align: center;
  background: var(--surface);
  cursor: pointer;
}

.upload-icon-wrap {
  font-size: 24px;
  margin-bottom: 10px;
}

.upload-zone p {
  font-size: 13px;
  color: var(--sub);
  line-height: 1.7;
}

.upload-zone em {
  font-style: normal;
  color: var(--point);
  font-weight: 600;
}

.up-small {
  margin-top: 8px;
  font-size: 11px;
  color: var(--muted);
}

.submit-row {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.submit-btn,
.cancel-btn {
  padding: 14px 24px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
}

.submit-btn {
  border: none;
  background: var(--point);
  color: white;
}

.cancel-btn {
  border: 1px solid var(--border);
  background: white;
  color: var(--sub);
}

.submit-btn:disabled,
.cancel-btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.upload-preview{
  width:  400px;
  height: 400px;
  object-fit: contain;
  display: block;
  margin: 0 auto;
}

@media (max-width: 900px) {
  .write-hero {
    padding: 24px 20px;
  }

  .write-single {
    padding: 20px 16px 36px;
  }

  .note-top-section {
    grid-template-columns: 1fr;
  }
}

.form-label.required::after {
  content: " *";
  color: #e74c3c;
  font-weight: 600;
}

.form-card-title.required::after {
  content: " *";
  color: #e74c3c;
  font-weight: 600;
}

.score-label-wrap {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.info-toggle-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #eef6f1;
  color: var(--point2);
  font-size: 10px;
  font-weight: 700;
  cursor: pointer;
  border: none;
  padding: 0;
}

.score-desc {
  margin: 4px 0 10px;
  font-size: 12px;
  color: var(--muted);
  line-height: 1.5;
  padding-left: 2px;
}

.text-count {
  margin-top: 6px;
  text-align: right;
  font-size: 11px;
  color: var(--muted);
}

.text-count.danger {
  color: #e74c3c;
  font-weight: 600;
}
</style>