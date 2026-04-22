<template>
  <div class="page-wrap">
    <PageNav :links="navLinks" />

    <div class="detail-wrap">
      <div class="detail-header">
        <div class="detail-img">
          <img v-if="drink?.image?.fileUrl" :src="drink.image.fileUrl" alt="주류 이미지" />
          <div v-else class="detail-placeholder">{{ drinkEmoji }}</div>
        </div>

        <div class="detail-info">
          <div class="detail-cat">
            {{ drink?.categoryCode || '-' }} - {{ drink?.typeCode || '-' }}
          </div>

          <h1 class="detail-name">
            {{ drink?.drinkName || '-' }}
          </h1>

          <h2 class="detail-name-en">
            {{ drink?.drinkNameEn || '-' }}
          </h2>

          <div class="detail-origin">
            {{ drink?.country || '-' }}
          </div>

          <div class="detail-chips">
            <span class="chip">{{ drink?.abv ?? '-' }}%</span>
            <span class="chip">{{ formatPrice(drink?.price) }}</span>
            <span class="chip rating">★ {{ drink?.avgRating ?? 0 }}</span>
            <span class="chip">노트 {{ totalCount }}개</span>
          </div>

          <p class="detail-desc">
            {{ drink?.description || '설명이 없습니다.' }}
          </p>

          <div class="detail-actions">
            <button
              v-if="authStore.isLogin && hasMyNote"
              class="btn-primary disabled"
              disabled
            >
              이미 노트를 작성했습니다
            </button>

            <router-link
              v-else-if="authStore.isLogin"
              :to="`/write/${drink?.drinkId}`"
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
        <FlavorBars :avgMetric="avgMetric" />
      </div>
    </div>

    <section class="notes-section">
      <div class="notes-head">
        <h3>테이스팅 노트</h3>

        <select v-model="sortBy" class="sort-select">
          <option value="latest">최신순</option>
          <option value="ratingDesc">별점 높은순</option>
          <option value="ratingAsc">별점 낮은순</option>
        </select>
      </div>

      <div v-if="noteList.length > 0" class="note-list">
        <NoteListCard
          v-for="note in noteList"
          :key="note.noteId"
          :item="note"
        />
      </div>

      <div v-else class="empty-note">
        아직 등록된 테이스팅 노트가 없습니다.
      </div>

      <div v-if="totalPage > 1" class="pagination">
        <button
          class="page-btn"
          :disabled="page === 1"
          @click="movePage(page - 1)"
        >
          이전
        </button>

        <button
          v-for="pageNum in visiblePages"
          :key="pageNum"
          class="page-btn"
          :class="{ active: page === pageNum }"
          @click="movePage(pageNum)"
        >
          {{ pageNum }}
        </button>

        <button
          class="page-btn"
          :disabled="page === totalPage"
          @click="movePage(page + 1)"
        >
          다음
        </button>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import PageNav from '@/components/common/PageNav.vue'
import FlavorBars from '@/components/sections/FlavorBars.vue'
import NoteListCard from '@/components/cards/NoteListCard.vue'
import { getDrinkDetail } from '@/api/drinkApi'
import { getDrinkLike, insertDrinkLike, deleteDrinkLike } from '@/api/likeApi'
import { getNoteList } from '@/api/noteApi'
import { categories } from '@/mock/soolData'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const liked = ref(false)
const likeLoading = ref(false)
const drink = ref(null)

const noteList = ref([])
const totalCount = ref(0)
const page = ref(1)
const size = ref(5)
const totalPage = ref(0)
const sortBy = ref('latest')
const avgMetric = ref([])

const authRedirecting = ref(false)

const currentDrinkId = computed(() => {
  const id = Number(route.params.id)
  return Number.isInteger(id) && id > 0 ? id : null
})

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

const drinkEmoji = computed(() => {
  const code = drink.value?.categoryCode
  const emojiData = categories.find(item => item.name === code)
  return emojiData ? emojiData.emoji : '🍹'
})

const visiblePages = computed(() => {
  const pages = []
  const startPage = Math.max(1, page.value - 2)
  const endPage = Math.min(totalPage.value, startPage + 4)

  for (let i = startPage; i <= endPage; i += 1) {
    pages.push(i)
  }

  return pages
})

const hasMyNote = computed(() => {
  if (!authStore.isLogin) return false
  if (!Array.isArray(noteList.value)) return false

  const loginUserId = Number(authStore.user?.userId)
  if (!loginUserId) return false

  return noteList.value.some(note => Number(note.userId) === loginUserId)
})

const clearAuthState = () => {
  authStore.user = null
  authStore.initialized = true
}

const getErrorMessage = (error, defaultMessage) => {
  return error?.response?.data?.message || defaultMessage
}

const getSafeRedirectPath = () => {
  if (route.path === '/login') {
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/'
    return redirect || '/'
  }

  return route.fullPath || '/'
}

const moveToLogin = async (message) => {
  if (authRedirecting.value) return

  authRedirecting.value = true

  alert(message || '로그인 세션이 만료되었습니다. 다시 로그인해주세요.')

  clearAuthState()

  await router.replace({
    path: '/login',
    query: {
      redirect: getSafeRedirectPath()
    }
  })
}

const handleAuthError = async (error) => {
  const status = error?.response?.status

  if (status === 401 || status === 403) {
    await moveToLogin(getErrorMessage(error, '로그인 세션이 만료되었습니다. 다시 로그인해주세요.'))
    return true
  }

  return false
}

const fetchDrinkDetail = async () => {
  if (!currentDrinkId.value) {
    router.replace('/404')
    return
  }

  try {
    const res = await getDrinkDetail(currentDrinkId.value)

    if (!res.data) {
      drink.value = null
      router.replace('/404')
      return
    }

    drink.value = res.data
  } catch (e) {
    drink.value = null
    router.replace('/404')
  }
}

const fetchLikeStatus = async () => {
  if (!currentDrinkId.value) return
  if (!authStore.isLogin) {
    liked.value = false
    return
  }

  try {
    const res = await getDrinkLike(currentDrinkId.value)
    liked.value = !!res.data?.liked
  } catch (e) {
    const handled = await handleAuthError(e)
    if (handled) return

    liked.value = false
  }
}

const fetchTastingNote = async () => {
  if (!currentDrinkId.value) return

  try {
    const res = await getNoteList(currentDrinkId.value, {
      page: page.value,
      size: size.value,
      sort: sortBy.value
    })

    noteList.value = Array.isArray(res.data?.list) ? res.data.list : []
    totalCount.value = Number(res.data?.totalCount ?? 0)
    totalPage.value = Number(res.data?.totalPage ?? 0)
    page.value = Number(res.data?.page ?? 1)
    size.value = Number(res.data?.size ?? 5)
    avgMetric.value = Array.isArray(res.data?.avgMetric) ? res.data.avgMetric : []
  } catch (e) {
    noteList.value = []
    totalCount.value = 0
    totalPage.value = 0
    avgMetric.value = []
  }
}

const movePage = async (pageNum) => {
  if (pageNum < 1 || pageNum > totalPage.value || pageNum === page.value) return

  page.value = pageNum
  await fetchTastingNote()
}

const toggleLike = async () => {
  if (!currentDrinkId.value || likeLoading.value) return

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
      await deleteDrinkLike(currentDrinkId.value)
    } else {
      await insertDrinkLike(currentDrinkId.value)
    }

    await fetchDrinkDetail()
    await fetchLikeStatus()
  } catch (e) {
    const handled = await handleAuthError(e)
    if (handled) return

    alert(getErrorMessage(e, '좋아요 처리 중 오류가 발생했습니다.'))
  } finally {
    likeLoading.value = false
  }
}

const resetNoteState = () => {
  noteList.value = []
  totalCount.value = 0
  totalPage.value = 0
  avgMetric.value = []
}

const initPage = async () => {
  page.value = 1
  liked.value = false
  drink.value = null
  authRedirecting.value = false
  resetNoteState()

  await fetchDrinkDetail()
  await fetchTastingNote()

  if (authStore.isLogin) {
    await fetchLikeStatus()
  }
}

watch(
  () => route.params.id,
  async () => {
    await initPage()
  }
)

watch(
  () => authStore.isLogin,
  async (isLogin) => {
    if (isLogin) {
      await fetchLikeStatus()
    } else {
      liked.value = false
    }
  }
)

watch(sortBy, async () => {
  page.value = 1
  await fetchTastingNote()
})

onMounted(async () => {
  await initPage()
})
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

.detail-name-en {
  font-size: 22px;
  font-weight: 400;
  color: var(--muted);
  margin-bottom: 10px;
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
  flex-wrap: wrap;
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

.btn-primary.disabled {
  background: #d9d9d9;
  color: #888;
  cursor: not-allowed;
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

.note-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.empty-note {
  padding: 40px 0;
  text-align: center;
  font-size: 14px;
  color: var(--muted);
  border: 1px solid var(--border);
  border-radius: 12px;
  background: var(--surface);
}

.pagination {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: 24px;
}

.page-btn {
  min-width: 36px;
  height: 36px;
  padding: 0 12px;
  border: 1px solid var(--border);
  background: var(--white);
  color: var(--sub);
  border-radius: 8px;
  cursor: pointer;
  font-size: 13px;
}

.page-btn.active {
  background: var(--point);
  color: white;
  border-color: var(--point);
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>