<template>
    <div class="page-wrap">
      <PageNav :links="navLinks" show-search />

      <section class="hero">
        <div class="hero-left">
          <div class="hero-tag">Tasting Note</div>
          <h1 class="hero-title">당신의 한 잔을<br><em>기록하세요</em></h1>
          <p class="hero-desc">와인, 위스키, 전통주까지. 마셨던 술의 맛과 향을 기록하고 취향을 발견하세요.</p>
          <div class="hero-cta">
            <router-link to="/drinks" class="btn-primary">술 목록 탐색하기 →</router-link>
          </div>
        </div>
      </section>

      <CategoryStrip :items="categories" />

      <section class="popular">
        <div class="section-head">
          <div class="section-title">이번 주 인기 술 ✦</div>
        </div>
        <div class="drinks-row">
          <DrinkRankCard v-for="drink in popularDrinks" :key="drink.id" :item="drink" />
        </div>
      </section>

      <section class="recent-notes">
        <div class="section-head">
          <div class="section-title">최근 테이스팅 노트</div>
        </div>
        <div class="note-feed">
          <RecentNoteCard v-for="note in recentNotes" :key="note.id" :item="note" />
        </div>
      </section>

      <router-link to="/login" class="btn-primary">로그인</router-link>
      <router-link to="/signup" class="btn-primary">signup</router-link>
    </div>
</template>

<script setup>
import PageNav from '../components/common/PageNav.vue'
import CategoryStrip from '../components/sections/CategoryStrip.vue'
import DrinkRankCard from '../components/cards/DrinkRankCard.vue'
import RecentNoteCard from '../components/cards/RecentNoteCard.vue'
import { categories, popularDrinks, recentNotes } from '../mock/soolData'

const navLinks = [
  { label: '홈', to: '/', active: true }
]
</script>

<style scoped>
.page-wrap {
  background: var(--bg);
  overflow: hidden;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.12);
  min-height: 700px;
}

.hero {
  background: var(--white);
  padding: 80px 60px 72px;
  border-bottom: 1px solid var(--border);
  position: relative;
  overflow: hidden;
}

.hero::before {
  content: 'SOOL';
  position: absolute;
  right: -20px;
  top: 50%;
  transform: translateY(-50%);
  font-family: 'Playfair Display', serif;
  font-size: 200px;
  font-style: italic;
  color: var(--border);
  opacity: 0.5;
  line-height: 1;
  pointer-events: none;
  letter-spacing: -8px;
}

.hero-left {
  max-width: 680px;
  position: relative;
  z-index: 1;
}

.hero-tag {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 10px;
  letter-spacing: 2.5px;
  text-transform: uppercase;
  font-weight: 600;
  color: var(--point);
  margin-bottom: 24px;
}

.hero-tag::before {
  content: '';
  width: 28px;
  height: 1px;
  background: var(--point);
}

.hero-title {
  font-family: 'Playfair Display', serif;
  font-size: 68px;
  color: var(--ink);
  line-height: 1.08;
  letter-spacing: -2px;
  margin-bottom: 22px;
}

.hero-title em {
  font-style: italic;
  color: var(--point);
}

.hero-desc {
  font-size: 16px;
  color: var(--sub);
  line-height: 1.75;
  margin-bottom: 36px;
  font-weight: 300;
  max-width: 480px;
}

.hero-cta {
  display: flex;
  gap: 12px;
  align-items: center;
}

.btn-primary {
  display: inline-flex;
  padding: 13px 30px;
  font-size: 14px;
  font-weight: 600;
  color: white;
  background: var(--point);
  border: none;
  border-radius: 8px;
}

.popular {
  padding: 48px 60px;
  background: var(--bg);
}

.section-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 24px;
}

.section-title {
  font-family: 'Playfair Display', serif;
  font-size: 22px;
  font-weight: 400;
  color: var(--ink);
}

.drinks-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
}

.recent-notes {
  padding: 48px 60px 56px;
  background: var(--white);
  border-top: 1px solid var(--border);
}

.note-feed {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-top: 24px;
}
</style>
