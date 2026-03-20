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
            <span class="filter-label">{{ category.label }}</span>
            <span class="filter-count">{{ category.drinkCount }}</span>
          </button>
        </div>

        <h4>도수 (%)</h4>
        <div class="range-row">
          <input
            v-model="abvLow"
            type="number"
            min="0"
            class="range-inp"
            @keydown="preventMinusInput"
            @input="sanitizeNumberInput('abvLow')"
            @change="applyFilter"
          />

          <span class="range-sep">—</span>

          <input
            v-model="abvHigh"
            type="number"
            min="0"
            class="range-inp"
            @keydown="preventMinusInput"
            @input="sanitizeNumberInput('abvHigh')"
            @change="applyFilter"
          />
        </div>

        <h4>가격대 (원)</h4>
        <div class="range-row">
          <input
            v-model="priceLow"
            type="number"
            min="0"
            class="range-inp"
            @keydown="preventMinusInput"
            @input="sanitizeNumberInput('priceLow')"
            @change="applyFilter"
          />
          <span class="range-sep">—</span>
          <input
            v-model="priceHigh"
            type="number"
            min="0"
            class="range-inp"
            @keydown="preventMinusInput"
            @input="sanitizeNumberInput('priceHigh')"
            @change="applyFilter"
          />
        </div>

        <button class="filter-apply-btn" @click="applyFilter">필터 적용</button>
        <button class="filter-reset-btn" @click="resetFilter">초기화</button>
      </aside>

      <section class="list-main">
        <div class="list-top">
          <h2>{{ titleLabel }} <span class="sub-count">{{ totalCount }}개</span></h2>

          <div class="list-controls">
            <form @submit.prevent="applySearch" class="list-search-form">
              <input
                v-model="searchKeyword"
                class="search-inp"
                placeholder="주류 이름, 영문명, 카테고리 검색"
              />
            </form>

            <select v-model="sortBy" class="sort-select" @change="changeSort">
              <option value="latest">최신순</option>
              <option value="like">좋아요순</option>
              <option value="name">이름순</option>
            </select>
          </div>
        </div>

        <div class="drinks-grid" v-if="drinkList.length > 0">
          <DrinkGridCard
            v-for="drink in drinkList"
            :key="drink.drinkId || drink.drink_id"
            :item="drink"
            @refresh="loadDrinkList"
          />
        </div>

        <div v-else class="empty-box">
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
  { label: "주류 목록", to: "/drinks", active: true }
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

const abvLow = ref("0")
const abvHigh = ref("60")
const priceLow = ref("0")
const priceHigh = ref("500000")

const page = ref(1)
const size = ref(12)

const makeCategoryLabel = (item) => {
  if (!item.code) return item.codeName
  const emojiData = categories.find(e => e.name === item.code)
  return emojiData ? `${emojiData.emoji} ${item.codeName}` : item.codeName
}

const normalizeRange = () => {
  const lowAbv = Number(abvLow.value || 0)
  const highAbv = Number(abvHigh.value || 0)
  const lowPrice = Number(priceLow.value || 0)
  const highPrice = Number(priceHigh.value || 0)

  if (lowAbv > highAbv) {
    abvLow.value = String(highAbv)
    abvHigh.value = String(lowAbv)
  }

  if (lowPrice > highPrice) {
    priceLow.value = String(highPrice)
    priceHigh.value = String(lowPrice)
  }
}

const preventMinusInput = (e) => {
  if (e.key === "-" || e.key === "e" || e.key === "E") {
    e.preventDefault()
    alert("0보다 작은 값은 입력할 수 없습니다.")
  }
}

const sanitizeNumberInput = (field) => {
  if (field === "abvLow" && Number(abvLow.value) < 0) {
    abvLow.value = "0"
    alert("0보다 작은 값은 입력할 수 없습니다.")
  }

  if (field === "abvHigh" && Number(abvHigh.value) < 0) {
    abvHigh.value = "0"
    alert("0보다 작은 값은 입력할 수 없습니다.")
  }

  if (field === "priceLow" && Number(priceLow.value) < 0) {
    priceLow.value = "0"
    alert("0보다 작은 값은 입력할 수 없습니다.")
  }

  if (field === "priceHigh" && Number(priceHigh.value) < 0) {
    priceHigh.value = "0"
    alert("0보다 작은 값은 입력할 수 없습니다.")
  }
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
    normalizeRange()

    const params = {
      page: page.value,
      size: size.value,
      sort: sortBy.value
    }

    const keyword = searchKeyword.value.trim()

    if (keyword) {
      params.keyword = keyword
    } else {
      params.categoryCode = selectedCategory.value || null
      params.abvLow = Number(abvLow.value)
      params.abvHigh = Number(abvHigh.value)
      params.priceLow = Number(priceLow.value)
      params.priceHigh = Number(priceHigh.value)
    }

    const res = await getDrinkList(params)
    console.log(res.data)

    drinkList.value = res.data.list || []
    totalCount.value = res.data.totalCount || 0
    totalPage.value = res.data.totalPage || 1
  } catch (error) {
    console.log(error)
  }
}

const syncFromRoute = () => {
  selectedCategory.value = typeof route.query.categoryCode === "string" ? route.query.categoryCode : ""
  searchKeyword.value = typeof route.query.keyword === "string" ? route.query.keyword : ""
  sortBy.value = typeof route.query.sort === "string" ? route.query.sort : "latest"
  abvLow.value = typeof route.query.abvLow === "string" ? route.query.abvLow : "0"
  abvHigh.value = typeof route.query.abvHigh === "string" ? route.query.abvHigh : "60"
  priceLow.value = typeof route.query.priceLow === "string" ? route.query.priceLow : "0"
  priceHigh.value = typeof route.query.priceHigh === "string" ? route.query.priceHigh : "500000"
  page.value = route.query.page ? Number(route.query.page) : 1
}

const updateRoute = () => {
  normalizeRange()

  const query = {}
  const keyword = searchKeyword.value.trim()

  if (keyword) {
    query.keyword = keyword
  } else {
    if (selectedCategory.value) query.categoryCode = selectedCategory.value

    if (abvLow.value && abvLow.value !== "0")
      query.abvLow = abvLow.value

    if (abvHigh.value && abvHigh.value !== "60")
      query.abvHigh = abvHigh.value

    if (priceLow.value && priceLow.value !== "0")
      query.priceLow = priceLow.value

    if (priceHigh.value && priceHigh.value !== "500000")
      query.priceHigh = priceHigh.value
  }

  if (sortBy.value && sortBy.value !== "latest")
    query.sort = sortBy.value

  if (page.value > 1)
    query.page = page.value

  router.replace({ path: "/drinks", query })
}

const changeCategory = (code) => {
  selectedCategory.value = code
  searchKeyword.value = ""
  page.value = 1
  updateRoute()
}

const applySearch = () => {
  selectedCategory.value = ""
  page.value = 1
  updateRoute()
}

const applyFilter = () => {
  searchKeyword.value = ""
  page.value = 1
  updateRoute()
}

const resetFilter = () => {
  selectedCategory.value = ""
  searchKeyword.value = ""
  sortBy.value = "latest"
  abvLow.value = "0"
  abvHigh.value = "60"
  priceLow.value = "0"
  priceHigh.value = "500000"
  page.value = 1
  updateRoute()
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
  if (searchKeyword.value.trim()) {
    return "검색 결과"
  }

  if (!selectedCategory.value) {
    return "전체 주류"
  }

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
  gap: 8px;
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

.filter-label {
  flex: 1;
}

.filter-count {
  font-size: 11px;
  color: var(--muted);
}

.range-row {
  display: flex;
  gap: 6px;
  align-items: center;
  margin-bottom: 10px;
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

.filter-apply-btn,
.filter-reset-btn {
  width: 100%;
  height: 34px;
  border-radius: 8px;
  font-size: 12px;
  cursor: pointer;
}

.filter-apply-btn {
  margin-top: 8px;
  border: 1px solid var(--point);
  background: #fdf3ef;
  color: var(--point);
  font-weight: 600;
}

.filter-reset-btn {
  margin-top: 8px;
  border: 1px solid var(--border);
  background: var(--white);
  color: var(--sub);
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
  width: 220px;
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