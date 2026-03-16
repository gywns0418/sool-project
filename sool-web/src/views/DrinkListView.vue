<template>
  <div class="page-wrap">
    <PageNav :links="navLinks" />

    <div class="list-body">
      <aside class="filter-panel">
        <h4>카테고리</h4>
        <div class="filter-cats">
          <button
            v-for="category in categoryOptions"
            :key="category.code"
            class="filter-cat-item"
            :class="{ sel: selectedCategory === category.code }"
            @click="changeCategory(category.code)"
          >
            {{ category.label }}
            <span class="filter-count">{{ category.drinkCount }}</span>
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
          <h2>{{ titleLabel }} <span class="sub-count">{{ totalCount }}개</span></h2>

          <div class="list-controls">
            <form @submit.prevent="applySearch" class="list-search-form">
              <input v-model="searchKeyword" 
              class="search-inp" 
              placeholder="술 이름 검색" />
            </form>

            <select v-model="sortBy" class="sort-select" @change="changeSort">
              <option value="latest">최신순</option>
              <option value="likes">좋아요순</option>
              <option value="name">이름순</option>
            </select>
          </div>
        </div>

        <div class="drinks-grid">
          <DrinkGridCard
            v-for="drink in drinkList"
            :key="drink.drinkId"
            :item="drink"
          />
        </div>

        <div v-if="drinkList.length === 0" class="empty-box">
          검색 결과가 없습니다.
        </div>

        <div class="pagination" v-if="totalPage > 1">
          <button class="page-btn" :disabled="page === 1" @click="movePage(page - 1)">이전</button>

          <button
            v-for="pageNum in visiblePages"
            :key="pageNum"
            class="page-btn"
            :class="{ active: page === pageNum }"
            @click="movePage(pageNum)"
          >
            {{ pageNum }}
          </button>

          <button class="page-btn" :disabled="page === totalPage" @click="movePage(page + 1)">다음</button>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from "vue"
import { useRoute, useRouter } from "vue-router"
import PageNav from "../components/common/PageNav.vue"
import DrinkGridCard from "../components/cards/DrinkGridCard.vue"
import { getDrinkList, getDrinkCategoryList } from "@/api/drinkApi"
import { categories } from "@/mock/soolData"

const navLinks = [
  { label: "홈", to: "/" },
  { label: "술 목록", to: "/drinks", active: true }
]

const route = useRoute()
const router = useRouter()

const drinkList = ref([])
const categoryOptions = ref([])
const totalCount = ref(0)
const totalPage = ref(1)

const selectedCategory = ref("")
const searchKeyword = ref("")
const sortBy = ref("latest")
const minAbv = ref("0")
const maxAbv = ref("60")
const page = ref(1)
const size = ref(12)

const makeCategoryLabel = (item) => {
  if (!item.code) return item.codeName
  const emojiData = categories.find(e => e.name === item.code)
  return emojiData ? `${emojiData.emoji} ${item.codeName}` : item.codeName
}

const loadCategoryList = async () => {
  try {
    const res = await getDrinkCategoryList()
    const list = res.data || []
    
    categoryOptions.value = [
      {
        code: "",
        codeName: "전체",
        label: "전체",
        drinkCount: list.reduce((sum, item) => sum + (item.drinkCount || 0), 0)
      },
      ...list.map(item => ({
        ...item,
        label: makeCategoryLabel(item)
      }))
    ]
  } catch (error) {
    console.log(error)
  }
}

const loadDrinkList = async () => {
  try {
    const params = {
      keyword: searchKeyword.value.trim() || null,
      categoryCode: selectedCategory.value || null,
      minAbv: minAbv.value || null,
      maxAbv: maxAbv.value || null,
      sort: sortBy.value,
      page: page.value,
      size: size.value
    }

    console.log("요청 params =", params)

    const res = await getDrinkList(params)

    drinkList.value = Array.isArray(res.data) ? res.data : (res.data.list || [])
    totalCount.value = Array.isArray(res.data) ? res.data.length : (res.data.totalCount || 0)
    totalPage.value = Array.isArray(res.data) ? 1 : (res.data.totalPage || 1)
  } catch (error) {
    console.log(error)
  }
}

const syncFromRoute = () => {
  selectedCategory.value = typeof route.query.categoryCode === "string" ? route.query.categoryCode : ""
  searchKeyword.value = typeof route.query.keyword === "string" ? route.query.keyword : ""
  sortBy.value = typeof route.query.sort === "string" ? route.query.sort : "latest"
  minAbv.value = typeof route.query.minAbv === "string" ? route.query.minAbv : "0"
  maxAbv.value = typeof route.query.maxAbv === "string" ? route.query.maxAbv : "60"
  page.value = route.query.page ? Number(route.query.page) : 1
}

const updateRoute = () => {
  const query = {}

  if (selectedCategory.value) query.categoryCode = selectedCategory.value
  if (searchKeyword.value.trim()) query.keyword = searchKeyword.value.trim()
  if (sortBy.value && sortBy.value !== "latest") query.sort = sortBy.value
  if (minAbv.value && minAbv.value !== "0") query.minAbv = minAbv.value
  if (maxAbv.value && maxAbv.value !== "60") query.maxAbv = maxAbv.value
  if (page.value > 1) query.page = page.value

  router.replace({ path: "/drinks", query })
}

const changeCategory = (code) => {
  selectedCategory.value = code
  page.value = 1
  updateRoute()
}

const applySearch = () => {
  page.value = 1
  updateRoute()
  
  searchKeyword="" //검색후 초기화
}

const changeSort = () => {
  page.value = 1
  updateRoute()
}

const movePage = (pageNum) => {
  if (pageNum < 1 || pageNum > totalPage.value) return
  page.value = pageNum
  updateRoute()
}

watch(
  () => route.query,
  async () => {
    syncFromRoute()
    await loadDrinkList()
  },
  { immediate: true }
)

loadCategoryList()

const titleLabel = computed(() => {
  if (!selectedCategory.value) return "전체 주류"
  const found = categoryOptions.value.find(item => item.code === selectedCategory.value)
  return found ? found.codeName : "전체 주류"
})

const visiblePages = computed(() => {
  const start = Math.max(1, page.value - 2)
  const end = Math.min(totalPage.value, start + 4)
  const pages = []

  for (let i = start; i <= end; i++) {
    pages.push(i)
  }

  return pages
})
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

.empty-box {
  padding: 40px 0;
  text-align: center;
  color: var(--muted);
  font-size: 14px;
}

.pagination {
  margin-top: 28px;
  display: flex;
  justify-content: center;
  gap: 6px;
}

.page-btn {
  min-width: 34px;
  height: 34px;
  border: 1px solid var(--border);
  background: var(--white);
  border-radius: 6px;
  font-size: 12px;
  cursor: pointer;
}

.page-btn.active {
  background: #fdf3ef;
  border-color: var(--point);
  color: var(--point);
  font-weight: 600;
}

.page-btn:disabled {
  cursor: default;
  opacity: 0.5;
}
</style>