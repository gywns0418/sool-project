<template>
  <div class="page-wrap">
    <PageNav :links="navLinks" />

    <section class="write-hero">
      <div class="write-hero-drink-thumb">{{ drinkEmoji }}</div>
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
          <label class="form-label">제목</label>
          <input
            v-model="title"
            type="text"
            class="text-input"
            maxlength="100"
            placeholder="예: 부드러운 베리향이 인상적인 와인"
          />
        </div>
      </div>

      <div class="note-top-section">
        <div class="form-card">
          <div class="form-card-header">
            <div class="form-card-icon">★</div>
            <div>
              <div class="form-card-title">기본 평점</div>
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
              <div class="form-card-title">맛 프로파일</div>
              <div class="form-card-sub">항목별로 1점부터 5점까지 선택하세요</div>
            </div>
          </div>

          <div class="score-grid">
            <div v-if="scores.length === 0" class="empty-metric">
              선택 가능한 맛 프로파일이 없습니다.
            </div>

            <div v-for="item in scores" :key="item.code" class="score-row">
              <div class="score-row-head">
                <span class="score-label">{{ item.codeName }}</span>
                <span class="score-val-text">{{ item.value }}점</span>
              </div>

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
          <label class="form-label">테이스팅 내용</label>
          <textarea
            v-model="memo"
            class="write-textarea"
            placeholder="향, 맛, 피니시, 함께 먹은 음식 등을 기록해보세요."
          ></textarea>
        </div>

        <div class="form-section">
          <label class="form-label">사진 업로드 *</label>

          <input
            ref="fileInput"
            type="file"
            accept="image/png,image/jpeg,image/jpg,image/webp"
            style="display:none"
            @change="handleImageUpload"
          />

          <button type="button" class="upload-zone" @click="openImageUpload">

            <!-- 이미지 없는 상태 -->
            <template v-if="!imagePreview">
              <div class="upload-icon-wrap">📷</div>

              <p>
                <em>클릭해서 사진 업로드</em><br />
                테이스팅 순간을 함께 남겨보세요
              </p>

              <div class="up-small">
                JPG, JPEG, PNG, WEBP 파일 업로드 가능
              </div>
            </template>

            <!-- 이미지 있는 상태 -->
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
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageNav from '../components/common/PageNav.vue'
import { categories } from '@/mock/soolData'
import { getNoteWriteForm, createNote, getNoteUpdateForm, updateNote } from '@/api/noteApi'
import { uploadImage, deleteImage } from '@/api/imageApi'

const route = useRoute()
const router = useRouter()

const errorMsg = ref('')

const drink = ref({})
const title = ref('')
const selectedStar = ref(1)
const memo = ref('')
const scores = ref([])
const isSaving = ref(false)

const fileInput = ref(null)
const selectedImageFile = ref(null)
const imagePreview = ref('')
const uploaded = ref(false)

const noteImage = ref(null)

let uploadedFileKey = ''
let uploadedFileUrl = ''

const isEditMode = computed(() => !!route.params.noteId)
const drinkId = computed(() => Number(route.params.drinkId))
const noteId = computed(() => Number(route.params.noteId))

const navLinks = computed(() => {
  if (isEditMode.value) {
    return [
      { label: '홈', to: '/' },
      { label: drink.value.drinkName || '노트 상세', to: drink.value.drinkId ? `/drinks/${drink.value.drinkId}` : '/' },
      { label: '노트 수정', to: route.fullPath, active: true }
    ]
  }

  return [
    { label: '홈', to: '/' },
    { label: drink.value.drinkName || '노트 작성', to: route.fullPath, active: true }
  ]
})

function resetForm() {
  drink.value = {}
  title.value = ''
  selectedStar.value = 1
  memo.value = ''
  scores.value = []
}

function normalizeMetricList(metricList) {
  return (metricList || []).map(item => ({
    code: item.code || item.metricCode,
    codeName: item.codeName || item.metricName,
    value: Number(item.value ?? item.score ?? 1)
  }))
}

//작성 기초 정보
async function fetchWriteForm() {
  try {
    const res = await getNoteWriteForm(drinkId.value)

    drink.value = res.data?.drink || {}
    scores.value = normalizeMetricList(res.data?.metricList)
  } catch (error) {
    console.log('노트 작성 화면 데이터 조회 실패', error)
    drink.value = {}
    scores.value = []
  }
}

//수정 기초정보 
async function fetchEditForm() {
  try {
    const res = await getNoteUpdateForm(noteId.value)
    console.log(res.data)

    const drinkData = res.data?.drink || {}
    const noteData = res.data?.note || {}
    const metricList = res.data?.metricList || []
    
    noteImage.value = res.data?.image || null
    imagePreview.value = res.data?.image?.fileUrl || ''

    drink.value = {
      drinkId: drinkData.drinkId,
      drinkName: drinkData.drinkName,
      drinkNameEn: drinkData.drinkNameEn,
      categoryCode: drinkData.categoryCode,
      categoryName: drinkData.categoryName,
      typeName: drinkData.typeName,
      country: drinkData.country
    }

    title.value = noteData.title || ''
    selectedStar.value = Number(noteData.rating ?? 1)
    memo.value = noteData.content || ''
    scores.value = normalizeMetricList(metricList)
  } catch (error) {
    console.log('노트 수정 화면 데이터 조회 실패', error)
    alert('수정할 노트 정보를 불러오지 못했습니다.')
  }
}

//주류 이미지 없을때 기본 이모티콘 표시
const drinkEmoji = computed(() => {
  const code = drink.value?.categoryCode
  const emojiData = categories.find(e => e.name === code)
  return emojiData ? emojiData.emoji : '🍹'
})

//페이지 표시
async function fetchPageData() {
  resetForm()

  if (isEditMode.value) {
    await fetchEditForm()
    return
  }

  await fetchWriteForm()
}

//저장하기 버튼
const saveNote = async () => {
  if (isSaving.value) return

  if(!title.value){
    alert("제목을 입력해주세요")
    return
  }

  if(!memo.value){
    alert("내용을 입력해주세요")
    return
  }

  if (!imagePreview.value) {
    alert("이미지를 추가해주세요")
    return
  }

  errorMsg.value = ''

  try {
    isSaving.value = true

    if(selectedImageFile.value){
      const formData = new FormData()
      formData.append('file', selectedImageFile.value)
      formData.append('dirName', 'NOTE')

      const uploadRes = await uploadImage(formData)

      uploadedFileKey = uploadRes.data.fileKey
      uploadedFileUrl = uploadRes.data.fileUrl
    }


    const payload = {
      drinkId: drink.value.drinkId,
      title: title.value,
      content: memo.value,
      rating: selectedStar.value,
      metricList: scores.value.map(m => ({
        metricCode: m.code,
        score: m.value
      })),
      image:{
        fileUrl: uploadedFileUrl || noteImage.value?.fileUrl || '',
        fileKey: uploadedFileKey || noteImage.value?.fileKey || ''
      }
    }

    if (isEditMode.value) {
      await updateNote(noteId.value, payload)

      // 새 이미지 업로드했으면 기존 이미지 삭제
      if (uploadedFileKey && noteImage.value?.fileKey) {
        try {
          console.log("filekey : ",noteImage.value?.fileKey)
          console.log("uploadedFileKey : ",uploadedFileKey)
          await deleteImage(noteImage.value?.fileKey)
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
    
    errorMsg.value = e.response?.data?.message || '노트 저장 중 오류가 발생했습니다.'
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

function handleImageUpload(e) {
  const file = e.target.files[0]
  console.log("file : ",file)

  if (!file) return

  selectedImageFile.value = file
  imagePreview.value = URL.createObjectURL(file)
  uploaded.value = true
}

onMounted(() => {
  fetchPageData()
})
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
  resize: vertical;
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
</style>