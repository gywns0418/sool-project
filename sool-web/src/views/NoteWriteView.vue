<template>
    <div class="page-wrap">
      <PageNav :links="navLinks" />

      <section class="write-hero">
        <div class="write-hero-drink-thumb">🍷</div>
        <div>
          <div class="write-hero-tag">Tasting Note</div>
          <div class="write-hero-title">Château Margaux 2018</div>
          <div class="write-hero-sub">
            <span>레드 와인</span>
            <span class="write-hero-dot"></span>
            <span>프랑스 · 보르도</span>
          </div>
        </div>
      </section>

      <div class="write-single">
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
              <button v-for="star in 5" :key="star" type="button" class="star-inp-item" :class="{ on: star <= selectedStar }" @click="selectedStar = star">★</button>
              <span class="star-label">{{ selectedStar }}점</span>
            </div>
          </div>
        </div>

        <div class="form-card">
          <div class="form-card-header">
            <div class="form-card-icon green">◎</div>
            <div>
              <div class="form-card-title">맛 점수</div>
              <div class="form-card-sub">항목별로 1점부터 5점까지 선택하세요</div>
            </div>
          </div>

          <div class="score-grid">
            <div v-for="item in scores" :key="item.key" class="score-row">
              <div class="score-row-head">
                <span class="score-label">{{ item.label }}</span>
                <span class="score-en">{{ item.en }}</span>
                <span class="score-val-text">{{ item.value }}점</span>
              </div>
              <div class="seg-bar">
                <button v-for="n in 5" :key="n" type="button" class="seg" :class="{ lit: n <= item.value }" @click="item.value = n"></button>
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
            <label class="form-label">테이스팅 메모</label>
            <textarea v-model="memo" class="write-textarea" placeholder="향, 맛, 피니시, 함께 먹은 음식 등을 기록해보세요."></textarea>
          </div>

          <div class="form-section">
            <label class="form-label">사진 업로드</label>
            <button type="button" class="upload-zone" @click="uploaded = !uploaded">
              <div class="upload-icon-wrap">📷</div>
              <p><em>{{ uploaded ? '사진 선택 완료' : '클릭해서 사진 업로드' }}</em><br />테이스팅 순간을 함께 남겨보세요</p>
              <div class="up-small">JPG, PNG 파일 업로드 가능</div>
            </button>
          </div>
        </div>

        <div class="write-submit-row">
          <button class="submit-btn" @click="saveNote">노트 저장하기</button>
        </div>
      </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import PageNav from '../components/common/PageNav.vue'

const router = useRouter()

const navLinks = [
  { label: '홈', to: '/' },
  { label: '노트 작성', to: '/notes/write', active: true }
]

const selectedStar = ref(4)
const uploaded = ref(false)
const memo = ref('')
const scores = ref([
  { key: 'sweet', label: '당도', en: 'Sweet', value: 2 },
  { key: 'acid', label: '산도', en: 'Acid', value: 4 },
  { key: 'body', label: '바디', en: 'Body', value: 5 },
  { key: 'tannin', label: '탄닌', en: 'Tannin', value: 4 }
])

const saveNote = () => {
  localStorage.setItem(
    'sool-note-draft',
    JSON.stringify({
      selectedStar: selectedStar.value,
      memo: memo.value,
      scores: scores.value,
      uploaded: uploaded.value
    })
  )
  router.push('/notes/1')
}
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
}

.write-hero-dot {
  width: 3px;
  height: 3px;
  border-radius: 50%;
  background: var(--border);
}

.write-progress {
  display: flex;
  align-items: center;
  padding: 0 60px;
  background: var(--white);
  border-bottom: 1px solid var(--border);
}

.wp-step {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 20px 14px 0;
  font-size: 12px;
  color: var(--muted);
}

.wp-step.active {
  color: var(--point);
  font-weight: 600;
}

.wp-num {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  border: 1.5px solid var(--border);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: 700;
  background: white;
  color: var(--muted);
}

.wp-step.active .wp-num {
  border-color: var(--point);
  background: var(--point);
  color: white;
}

.wp-arrow {
  color: var(--border);
  font-size: 12px;
  margin-right: 20px;
}

.write-single {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  padding: 28px 40px 48px;
  max-width: 90%;
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

.score-en,
.score-val-text {
  font-size: 12px;
  color: var(--muted);
}

.score-val-text {
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
</style>
