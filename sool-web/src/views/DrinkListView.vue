<template>
    <div class="page-wrap">
      <PageNav :links="navLinks" />

      <div class="list-body">
        <aside class="filter-panel">
          <h4>카테고리</h4>
          <div class="filter-cats">
            <button
              v-for="category in categoryOptions"
              :key="category.name"
              class="filter-cat-item"
              :class="{ sel: selectedCategory === category.name }"
              @click="selectedCategory = category.name"
            >
              {{ category.label }} <span class="filter-count">{{ category.count }}</span>
            </button>
          </div>

          <h4>도수 (%)</h4>
          <div class="range-row">
            <input v-model="minAbv" class="range-inp" />
            <span class="range-sep">—</span>
            <input v-model="maxAbv" class="range-inp" />
          </div>

          <h4>가격대 (원)</h4>
          <div class="range-row">
            <input value="0" class="range-inp" />
            <span class="range-sep">—</span>
            <input value="500,000" class="range-inp" />
          </div>
        </aside>

        <section class="list-main">
          <div class="list-top">
            <h2>{{ titleLabel }} <span class="sub-count">{{ filteredDrinks.length }}개</span></h2>
            <div class="list-controls">
              <form @submit.prevent class="list-search-form">
                <input v-model="searchKeyword" class="search-inp" placeholder="술 이름 검색" />
              </form>
              <select v-model="sortBy" class="sort-select">
                <option value="latest">최신순</option>
                <option value="rating">별점 높은순</option>
                <option value="likes">좋아요순</option>
              </select>
            </div>
          </div>

          <div class="drinks-grid">
            <DrinkGridCard v-for="drink in filteredDrinks" :key="drink.id" :item="drink" />
          </div>
        </section>
      </div>
    </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageNav from '../components/common/PageNav.vue'
import DrinkGridCard from '../components/cards/DrinkGridCard.vue'
import { drinkGrid } from '../mock/soolData'

const navLinks = [
  { label: '홈', to: '/' },
  { label: '술 목록', to: '/drinks', active: true }
]

const route = useRoute()
const router = useRouter()
const selectedCategory = ref('전체')
const searchKeyword = ref('')
const sortBy = ref('latest')
const minAbv = ref('0')
const maxAbv = ref('60')

const categoryOptions = [
  { name: '전체', label: '전체', count: 382 },
  { name: '와인', label: '🍷 와인', count: 124 },
  { name: '위스키', label: '🥃 위스키', count: 98 },
  { name: '맥주', label: '🍺 맥주', count: 86 },
  { name: '전통주', label: '🍶 전통주', count: 54 },
  { name: '기타', label: '🍸 기타', count: 20 }
]

watch(
  () => route.query,
  (query) => {
    searchKeyword.value = typeof query.q === 'string' ? query.q : ''
    selectedCategory.value = typeof query.category === 'string' ? query.category : '전체'
  },
  { immediate: true }
)

watch([searchKeyword, selectedCategory], ([q, category]) => {
  const nextQuery = {}
  if (q.trim()) nextQuery.q = q.trim()
  if (category && category !== '전체') nextQuery.category = category
  router.replace({ path: '/drinks', query: nextQuery })
})

const parseRating = (rating) => Number((rating.match(/(\d+(?:\.\d+)?)/) || [0])[1])
const parseCountryCategory = (drink) => {
  const map = {
    와인: ['와인', '샴페인', '화이트', '로제'],
    위스키: ['몰트', '위스키'],
    맥주: ['에일', '맥주', '라거'],
    전통주: ['전통주'],
    기타: ['칵테일', '기타']
  }
  return Object.entries(map).find(([, keywords]) => keywords.some((word) => drink.category.includes(word)))?.[0] || '기타'
}
const parseAbv = (meta) => Number(meta.split('%')[0])

const filteredDrinks = computed(() => {
  let items = [...drinkGrid].filter((drink) => {
    const matchedCategory = selectedCategory.value === '전체' || parseCountryCategory(drink) === selectedCategory.value
    const matchedKeyword = !searchKeyword.value.trim() || drink.name.toLowerCase().includes(searchKeyword.value.trim().toLowerCase())
    const abv = parseAbv(drink.meta)
    const matchedAbv = abv >= Number(minAbv.value || 0) && abv <= Number(maxAbv.value || 100)
    return matchedCategory && matchedKeyword && matchedAbv
  })

  if (sortBy.value === 'rating') items.sort((a, b) => parseRating(b.rating) - parseRating(a.rating))
  if (sortBy.value === 'likes') items.sort((a, b) => b.likes - a.likes)
  return items
})

const titleLabel = computed(() => (selectedCategory.value === '전체' ? '전체 술' : selectedCategory.value))
</script>

<style scoped>
.page-wrap {
  background: var(--bg);
  overflow: hidden;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.12);
}

.list-body {
  display: grid;
  grid-template-columns: 240px 1fr;
  min-height: calc(100vh - 60px);
}

.filter-panel {
  background: var(--white);
  border-right: 1px solid var(--border);
  padding: 28px 22px;
}

.filter-panel h4 {
  font-size: 11px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  color: var(--muted);
  font-weight: 600;
  margin-bottom: 14px;
  margin-top: 22px;
}

.filter-panel h4:first-child {
  margin-top: 0;
}

.filter-cats {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.filter-cat-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 10px;
  border-radius: 7px;
  font-size: 13px;
  color: var(--sub);
  background: transparent;
  border: none;
  text-align: left;
  cursor: pointer;
}

.filter-cat-item.sel {
  background: #fdf3ef;
  color: var(--point);
  font-weight: 600;
}

.filter-count {
  font-size: 11px;
  color: var(--muted);
}

.range-row {
  display: flex;
  gap: 6px;
  align-items: center;
  margin-bottom: 6px;
}

.range-inp {
  width: 72px;
  height: 32px;
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 0 8px;
  font-size: 12px;
  background: var(--surface);
  color: var(--ink);
}

.range-sep {
  font-size: 12px;
  color: var(--muted);
}

.list-main {
  padding: 28px 32px;
  background: var(--bg);
}

.list-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 22px;
}

.list-top h2 {
  font-family: 'Playfair Display', serif;
  font-size: 22px;
  font-weight: 400;
  color: var(--ink);
}

.sub-count {
  font-size: 14px;
  color: var(--muted);
  font-family: inherit;
  font-weight: 400;
}

.list-controls {
  display: flex;
  gap: 8px;
  align-items: center;
}

.sort-select,
.search-inp {
  height: 34px;
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 0 12px;
  font-size: 12.5px;
  background: var(--white);
}

.search-inp {
  width: 180px;
}

.drinks-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
}
</style>
