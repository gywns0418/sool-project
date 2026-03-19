<template>
  <div class="page-wrap">
    <PageNav :links="navLinks" />

    <div class="detail-wrap">
      <div class="detail-header">
        <div class="detail-img">
          <img v-if="drink?.imageUrl" :src="drink.imageUrl" alt="주류 이미지" />
          <div v-else class="detail-placeholder">🍷</div>
        </div>

        <div class="detail-info">
          <div class="detail-cat">
            {{ drink?.categoryCode || '-' }} - {{ drink?.typeCode || '-' }}
          </div>

          <h1 class="detail-name">
            {{ drink?.drinkName || '-' }}
          </h1>
          <h1 class="detail-name">
            {{ drink?.drinkNameEn || '-' }}
          </h1>

          <div class="detail-origin">
            {{ drink?.country || '-' }}
          </div>

          <div class="detail-chips">
            <span class="chip">{{ drink?.abv ?? '-' }}%</span>
            <span class="chip">{{ formatPrice(drink?.price) }}</span>
            <span class="chip rating">★ {{ drink?.avgRating ?? 0 }}</span>
            <span class="chip">노트 {{ drink?.noteCount ?? 0 }}개</span>
          </div>

          <p class="detail-desc">
            {{ drink?.description || '설명이 없습니다.' }}
          </p>

          <div class="detail-actions">
            <router-link
              v-if="authStore.isLogin"
              :to="`/notes/write?drinkId=${drink?.drink_id || ''}`"
              class="btn-primary"
            >
              테이스팅 노트 작성
            </router-link>

            <router-link
              v-else
              :to="`/login?redirect=${route.fullPath}`"
              class="btn-primary"
            >
              로그인하고 노트 작성하기
            </router-link>

            <button
              class="btn-ghost like-btn"
              :class="{ active: liked }"
              :disabled="likeLoading"
              @click="toggleLike"
            >
              {{ liked ? '♥' : '♡' }} 좋아요 {{ drink?.likeCount ?? 0 }}
            </button>
          </div>
        </div>
      </div>

      <div class="flavor-section">
        <FlavorBars title="평균 맛 프로파일" :items="flavorProfile" />
      </div>
    </div>

    <section class="notes-section">
      <div class="notes-head">
        <h3>테이스팅 노트</h3>
        <select class="sort-select">
          <option>최신순</option>
          <option>별점 높은순</option>
          <option>별점 낮은순</option>
        </select>
      </div>

      <NoteListCard
        v-for="note in detailNotes"
        :key="note.id"
        :item="note"
      />
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from "@/stores/authStore"
import PageNav from '@/components/common/PageNav.vue'
import FlavorBars from '@/components/sections/FlavorBars.vue'
import NoteListCard from '@/components/cards/NoteListCard.vue'
import { getDrinkDetail } from '@/api/drinkApi'
import { getDrinkLike, insertDrinkLike, deleteDrinkLike } from '@/api/likeApi'
import { detailNotes, flavorProfile } from '@/mock/soolData'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const liked = ref(false)
const likeLoading = ref(false)
const drink = ref(null)

const navLinks = computed(() => [
  { label: '홈', to: '/' },
  { label: '주류 목록', to: '/drinks' },
  {
    label: drink.value?.drinkNameEn || `주류 상세 #${route.params.id}`,
    to: route.fullPath,
    active: true
  }
])

const formatPrice = (price) => {
  if (price == null || price === '') return '-'
  return `₩ ${Number(price).toLocaleString()}`
}

const fetchDrinkDetail = async () => {
  const drinkId = route.params.id

  if (!drinkId) return

  try {
    const res = await getDrinkDetail(drinkId)
    drink.value = res.data
  } catch (e) {
    console.log('주류 상세 조회 실패', e)
    drink.value = null
  }
}

const fetchLikeStatus = async () => {
  const drinkId = route.params.id

  if (!drinkId) return

  try {
    const res = await getDrinkLike(drinkId)
    liked.value = !!res.data.liked
  } catch (e) {
    liked.value = false
  }
}

const toggleLike = async () => {
  const drinkId = route.params.id

  if (!drinkId || likeLoading.value) return

  if (!authStore.isLogin) {
    alert('로그인을 먼저 해주세요.')
    
    router.push({
      path: '/login',
      query: { redirect: route.fullPath }
    })
    return
  }

  likeLoading.value = true

  try {
    if (liked.value) {
      await deleteDrinkLike(drinkId)
    } else {
      await insertDrinkLike(drinkId)
    }

    await fetchDrinkDetail()
    await fetchLikeStatus()
  } catch (e) {
    console.log('좋아요 처리 실패', e)
  } finally {
    likeLoading.value = false
  }
}

const initPage = async () => {
  await fetchDrinkDetail()
  await fetchLikeStatus()
}

onMounted(() => {
  initPage()
})

watch(
  () => route.params.id,
  () => {
    initPage()
  }
)

watch(
  () => authStore.isLogin,
  () => {
    fetchLikeStatus()
  }
)
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
  grid-template-columns: 220px 1fr;
  gap: 40px;
  align-items: flex-start;
  padding-bottom: 36px;
  border-bottom: 1px solid var(--border);
  margin-bottom: 36px;
}

.detail-img {
  width: 220px;
}

.detail-img img {
  width: 100%;
  height: 280px;
  object-fit: cover;
  border-radius: 12px;
  border: 1px solid var(--border);
  display: block;
}

.detail-placeholder {
  width: 100%;
  height: 280px;
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
  text-decoration: none;
}

.btn-primary {
  padding: 13px 30px;
  font-size: 14px;
  font-weight: 600;
  color: white;
  background: var(--point);
  border: none;
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

.like-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.flavor-section {
  display: grid;
  grid-template-columns: 1fr;
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