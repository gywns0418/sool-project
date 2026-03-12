<template>
    <div class="page-wrap">
      <PageNav :links="navLinks" />

      <div class="detail-wrap">
        <div class="detail-header">
          <div class="detail-img">🍷</div>
          <div class="detail-info">
            <div class="detail-cat">레드 와인</div>
            <h1 class="detail-name">Château Margaux 2018</h1>
            <div class="detail-origin">프랑스 · 보르도 · Margaux AOC</div>
            <div class="detail-chips">
              <span class="chip">13.5%</span>
              <span class="chip">₩ 420,000</span>
              <span class="chip rating">★ 4.9</span>
              <span class="chip">노트 48개</span>
            </div>
            <p class="detail-desc">마고 와인의 정수라 불리는 2018년 빈티지. 블랙 커런트와 삼나무 향이 복잡하게 어우러지며, 실크처럼 부드러운 타닌과 긴 여운이 특징입니다.</p>
            <div class="detail-actions">
              <router-link to="/notes/write" class="btn-primary">테이스팅 노트 작성</router-link>
              <button class="btn-ghost like-btn" :class="{ active: liked }" @click="liked = !liked">{{ liked ? '♥' : '♡' }} 좋아요 {{ likeCount }}</button>
            </div>
          </div>
        </div>

        <div class="flavor-section">
          <FlavorBars title="평균 맛 프로파일" :items="flavorProfile" />
          <FlavorBars title="별점 분포" :items="ratingDistribution" yellow />
        </div>
      </div>

      <section class="notes-section">
        <div class="notes-head">
          <h3>테이스팅 노트</h3>
          <select class="sort-select">
            <option>최신순</option>
            <option>별점 높은순</option>
          </select>
        </div>
        <NoteListCard v-for="note in detailNotes" :key="note.id" :item="note" />
      </section>
    </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'
import PageNav from '../components/common/PageNav.vue'
import FlavorBars from '../components/sections/FlavorBars.vue'
import NoteListCard from '../components/cards/NoteListCard.vue'
import { detailNotes, flavorProfile, ratingDistribution } from '../mock/soolData'

const route = useRoute()
const liked = ref(false)
const likeCount = computed(() => 128 + (liked.value ? 1 : 0))

const navLinks = computed(() => [
  { label: '홈', to: '/' },
  { label: '술 목록', to: '/drinks' },
  { label: `술 상세 #${route.params.id || 1}`, to: route.fullPath, active: true }
])
</script>

<style scoped>
.page-wrap {
  background: var(--white);
  overflow: hidden;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.12);
}

.detail-wrap {
  padding: 40px 60px;
  background: var(--white);
}

.detail-header {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 40px;
  align-items: flex-start;
  padding-bottom: 36px;
  border-bottom: 1px solid var(--border);
  margin-bottom: 36px;
}

.detail-img {
  width: 160px;
  height: 200px;
  background: var(--surface);
  border-radius: 12px;
  border: 1px solid var(--border);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 72px;
}

.detail-cat {
  font-size: 11px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  color: var(--point);
  font-weight: 600;
  margin-bottom: 8px;
}

.detail-name {
  font-family: 'Playfair Display', serif;
  font-size: 36px;
  font-weight: 400;
  color: var(--ink);
  letter-spacing: -0.5px;
  line-height: 1.2;
  margin-bottom: 6px;
}

.detail-origin {
  font-size: 13px;
  color: var(--muted);
  margin-bottom: 16px;
}

.detail-chips {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 18px;
}

.chip {
  padding: 5px 12px;
  border-radius: 100px;
  border: 1px solid var(--border);
  font-size: 12px;
  color: var(--sub);
  background: var(--surface);
  font-weight: 500;
}

.chip.rating {
  background: #fdc723de;
  color: var(--sub);
  border-color: #fdc723de;
  font-weight: 600;
}

.detail-desc {
  font-size: 14px;
  color: var(--sub);
  line-height: 1.75;
  max-width: 520px;
  margin-bottom: 20px;
  font-weight: 300;
}

.detail-actions {
  display: flex;
  gap: 10px;
}

.btn-primary,
.btn-ghost {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  cursor: pointer;
}

.btn-primary {
  padding: 13px 30px;
  font-size: 14px;
  font-weight: 600;
  color: white;
  background: var(--point);
}

.btn-ghost {
  padding: 7px 14px;
  font-size: 12.5px;
  font-weight: 500;
  color: var(--ink);
  background: transparent;
  border: 1px solid var(--border);
}

.like-btn {
  transition: all 0.2s;
}

.like-btn.active {
  color: #d62828;
  border-color: #d62828;
}

.like-btn:hover {
  color: #d62828;
  border-color: #d62828;
}

.flavor-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  padding-bottom: 36px;
  border-bottom: 1px solid var(--border);
  margin-bottom: 0;
}

.notes-section {
  padding: 0 60px 48px;
}

.notes-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 20px;
}

.notes-head h3 {
  font-family: 'Playfair Display', serif;
  font-size: 20px;
  font-weight: 400;
  color: var(--ink);
}

.sort-select {
  height: 34px;
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 0 10px;
  font-size: 12.5px;
  color: var(--sub);
  background: var(--white);
}
</style>
