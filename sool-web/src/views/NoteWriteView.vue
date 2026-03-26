<template>
  <div class="page-wrap">
    <PageNav :links="navLinks" />

    <section class="write-hero">
      <div class="write-hero-drink-thumb">🍷</div>
      <div>
        <div class="write-hero-tag">Tasting Note</div>
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
            <div class="form-card-title">노트 제목</div>
            <div class="form-card-sub">테이스팅 노트의 제목을 작성하세요</div>
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
          <label class="form-label">사진 업로드</label>
          <button type="button" class="upload-zone" @click="uploaded = !uploaded">
            <div class="upload-icon-wrap">📷</div>
            <p>
              <em>{{ uploaded ? '사진 선택 완료' : '클릭해서 사진 업로드' }}</em><br />
              테이스팅 순간을 함께 남겨보세요
            </p>
            <div class="up-small">JPG, PNG 파일 업로드 가능</div>
          </button>
        </div>
      </div>

      <button class="submit-btn" :disabled="isSaving" @click="saveNote">
        {{ isSaving ? '저장 중...' : '노트 저장하기' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageNav from '../components/common/PageNav.vue'
import { getNoteWriteForm, createNote } from '@/api/noteApi'

const route = useRoute()
const router = useRouter()

const drink = ref({})
const title = ref('')
const selectedStar = ref(1)
const uploaded = ref(false)
const memo = ref('')
const scores = ref([])
const isSaving = ref(false)

const navLinks = computed(() => [
  { label: '홈', to: '/' },
  { label: drink.value.drinkName || '노트 작성', to: route.fullPath, active: true }
])

const fetchNoteWriteForm = async () => {
  try {
    const drinkId = route.params.drinkId
    const res = await getNoteWriteForm(drinkId)

    drink.value = res.data?.drink || {}
    scores.value = (res.data?.metricList || []).map(item => ({
      code: item.code,
      codeName: item.codeName,
      value: 1
    }))
  } catch (error) {
    console.log('노트 작성 화면 데이터 조회 실패', error)
    drink.value = {}
    scores.value = []
  }
}

const saveNote = async () => {
  if (!drink.value?.drinkId) {
    alert('주류 정보가 없습니다.')
    return
  }

  if (!title.value.trim()) {
    alert('노트 제목을 입력해주세요.')
    return
  }

  if (!selectedStar.value) {
    alert('별점을 선택해주세요.')
    return
  }

  if (!memo.value.trim()) {
    alert('테이스팅 메모를 입력해주세요.')
    return
  }

  if (isSaving.value) {
    return
  }

  try {
    isSaving.value = true

    const payload = {
      drinkId: drink.value.drinkId,
      title: title.value.trim(),
      content: memo.value.trim(),
      rating: selectedStar.value,
      metricList: scores.value.map(item => ({
        metricCode: item.code,
        score: item.value
      }))
    }

    const res = await createNote(payload)
    const noteId = res.data?.noteId
    console.log(noteId)

    alert('노트가 저장되었습니다.')

    if (noteId) {
      router.push(`/notes/${noteId}`)
      return
    }

    router.push('/mypage')
  } catch (error) {
    console.log('노트 저장 실패', error)

    const message =
      error.response?.data?.message ||
      error.response?.data ||
      '노트 저장에 실패했습니다.'

    alert(message)
  } finally {
    isSaving.value = false
  }
}

onMounted(() => {
  fetchNoteWriteForm()
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

.write-submit-row {
  grid-column: 1 / -1;
  display: flex;
  justify-content: flex-end;
}

.submit-btn {
  padding: 14px 24px;
  border: none;
  border-radius: 8px;
  background: var(--point);
  color: white;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
}

.text-input {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
}

.text-input:focus {
  border-color: #7c3aed;
}

.note-top-section {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 20px;
}

.empty-metric {
  font-size: 13px;
  color: var(--muted);
  padding: 8px 0;
}
</style>