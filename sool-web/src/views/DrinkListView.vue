<template>
  <div class="page-wrap">
    <PageNav :links="navLinks" />

    <div class="scroll-progress">
      <div
        class="scroll-progress-bar"
        :style="{ width: scrollProgress + '%' }"
      ></div>
    </div>

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

        <div class="filter-divider">
          <span></span>
        </div>

        <h4>도수 (%)</h4>
        <div class="range-row">
          <input
            v-model="abvLow"
            type="number"
            min="0"
            :max="ABV_MAX"
            step="0.1"
            :placeholder="ABV_DEFAULT_LOW"
            class="range-inp"
            @keydown="preventMinusInput"
            @input="sanitizeAbvInput('abvLow')"
            @keydown.enter="applyFilter"
          />

          <span class="range-sep">—</span>

          <input
            v-model="abvHigh"
            type="number"
            min="0"
            :max="ABV_MAX"
            step="0.1"
            :placeholder="ABV_DEFAULT_HIGH"
            class="range-inp"
            @keydown="preventMinusInput"
            @input="sanitizeAbvInput('abvHigh')"
            @keydown.enter="applyFilter"
          />
        </div>

        <h4>가격대 (원)</h4>
        <div class="range-row">
          <input
            v-model="priceLow"
            type="number"
            min="0"
            :max="PRICE_MAX"
            step="1000"
            :placeholder="PRICE_DEFAULT_LOW"
            class="range-inp"
            @keydown="preventInput"
            @input="sanitizePriceInput('priceLow')"
            @blur="formatPriceInput('priceLow')"
            @keydown.enter="applyFilter"
          />
          <span class="range-sep">—</span>
          <input
            v-model="priceHigh"
            type="number"
            min="0"
            :max="PRICE_MAX"
            step="1000"
            :placeholder="PRICE_DEFAULT_HIGH"
            class="range-inp"
            @keydown="preventInput"
            @input="sanitizePriceInput('priceHigh')"
            @blur="formatPriceInput('priceHigh')"
            @keydown.enter="applyFilter"
          />
        </div>
        <p v-if="priceMsg" class="input-msg">{{ priceMsg }}</p>

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
                placeholder="주류 한글명, 영문명, 종류 검색"
              />
            </form>

            <select v-model="sortBy" class="sort-select" @change="changeSort">
              <option value="latest">최신순</option>
              <option value="like">좋아요 높은순</option>
              <option value="name">이름순</option>
              <option value="ratingHigh">평균 별점 높은순</option>
            </select>
          </div>
        </div>

        <div class="list-scroll">
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
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch, onMounted, onUnmounted, nextTick  } from "vue"
import { useRoute, useRouter } from "vue-router"
import PageNav from "../components/common/PageNav.vue"
import DrinkGridCard from "../components/cards/DrinkGridCard.vue"
import { getDrinkList, getDrinkCategoryList } from "@/api/drinkApi"
import { categories } from "@/mock/soolData"


const scrollProgress = ref(0)

const updateScrollProgress = () => {
  const el = document.querySelector('.list-scroll')

  if (!el) return

  const maxScroll = el.scrollHeight - el.clientHeight

  if (maxScroll <= 0) {
    scrollProgress.value = 0
    return
  }

  scrollProgress.value = (el.scrollTop / maxScroll) * 100
}

onMounted(async () => {
  await nextTick()

  const el = document.querySelector('.list-scroll')

  if (el) {
    el.addEventListener('scroll', updateScrollProgress)
  }

  updateScrollProgress()
})

onUnmounted(() => {
  const el = document.querySelector('.list-scroll')

  if (el) {
    el.removeEventListener('scroll', updateScrollProgress)
  }
})


const ABV_MIN = 0
const ABV_MAX = 99.9
const ABV_DEFAULT_LOW = "0"
const ABV_DEFAULT_HIGH = "99.9"

const PRICE_MIN = 0
const PRICE_MAX = 10000000
const PRICE_DEFAULT_LOW = "0"
const PRICE_DEFAULT_HIGH = "10000000"

const ALLOWED_SORTS = ["latest", "like", "name","ratingHigh"]

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

const abvLow = ref(ABV_DEFAULT_LOW)
const abvHigh = ref(ABV_DEFAULT_HIGH)

const priceLow = ref(PRICE_DEFAULT_LOW)
const priceHigh = ref(PRICE_DEFAULT_HIGH)

const priceMsg = ref('')

const page = ref(1)
const size = ref(12)

const isCategoryLoaded = ref(false)

const makeCategoryLabel = (item) => {
  if (!item.code) return item.codeName

  const emojiData = categories.find(e => e.name === item.code)

  return emojiData
    ? `${emojiData.emoji} ${item.codeName}`
    : `🍹 ${item.codeName}`
}

const isValidCategoryCode = (code) => {
  if (!code) return true

  return categoryOptions.value.some(item => item.code === code)
}

const normalizeRange = () => {
  const lowAbv = Number(abvLow.value || ABV_DEFAULT_LOW)
  const highAbv = Number(abvHigh.value || ABV_DEFAULT_HIGH)

  const lowPrice = Number(priceLow.value || PRICE_DEFAULT_LOW)
  const highPrice = Number(priceHigh.value || PRICE_DEFAULT_HIGH)

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
    alert('음수나 지수 표기는 입력할 수 없습니다.')
    e.preventDefault()
  }
}

const preventInput = (e) => {
  if (e.key === "-" || e.key === "e" || e.key === "E" || e.key === ".") {
    alert('가격은 정수만 입력할 수 있습니다.')
    e.preventDefault()
  }
}

const sanitizeAbvInput = (field) => {
  const target = field === "abvLow" ? abvLow : abvHigh

  if (target.value === "" || target.value == null) return

  if (!/^\d*\.?\d?$/.test(target.value)) {
    alert('도수는 소수점 1자리까지만 입력할 수 있습니다.')
    target.value = target.value.slice(0, -1)
    return
  }

  let value = Number(target.value)

  if (!isNaN(value)) {
    if (value < ABV_MIN) {
      alert(`도수는 ${ABV_MIN} 이상만 입력할 수 있습니다.`)
      target.value = String(ABV_MIN)
    }
    if (value > ABV_MAX){
      alert(`도수는 ${ABV_MAX} 이하만 입력할 수 있습니다.`)
      target.value = String(ABV_MAX)
    }
  }
}

const sanitizePriceInput = (field) => {
  const target = field === "priceLow" ? priceLow : priceHigh

  if (target.value === "" || target.value == null) return

  let value = Number(target.value)

  if (isNaN(value)) {
    alert('가격은 숫자만 입력할 수 있습니다.')
    target.value = ""
    return
  }

  if (value < PRICE_MIN){
    alert(`가격은 ${PRICE_MIN}원 이상만 입력할 수 있습니다.`)
    value = PRICE_MIN
  }
  if (value > PRICE_MAX){
    alert(`가격은 ${PRICE_MAX}원 이하만 입력할 수 있습니다.`)
    value = PRICE_MAX
  }

  target.value = String(Math.floor(value))
}

const formatPriceInput = (field) => {
  const target = field === "priceLow" ? priceLow : priceHigh

  if (target.value === "" || target.value == null) return

  let value = Number(target.value)

  if (isNaN(value)) {
    target.value = ""
    return
  }

  if (value < PRICE_MIN) value = PRICE_MIN
  if (value > PRICE_MAX) value = PRICE_MAX

  const formattedValue = Math.floor(value / 100) * 100

  if (value !== formattedValue) {
    priceMsg.value = '100원 단위로 자동 조정되었습니다.'
    setTimeout(() => {
      priceMsg.value = ''
    }, 2000)
  }

  target.value = String(formattedValue)
}

const normalizeAbv = (value, defaultValue) => {
  if (value == null || value === "") return defaultValue

  const num = Number(value)

  if (isNaN(num)) return defaultValue
  if (num < ABV_MIN) return ABV_DEFAULT_LOW
  if (num > ABV_MAX) return ABV_DEFAULT_HIGH

  return String(Math.floor(num * 10) / 10)
}

const normalizePrice = (value, defaultValue) => {
  if (value == null || value === "") return defaultValue

  const num = Number(value)

  if (isNaN(num)) return defaultValue
  if (num < PRICE_MIN) return PRICE_DEFAULT_LOW
  if (num > PRICE_MAX) return PRICE_DEFAULT_HIGH

  return String(Math.floor(num / 100) * 100)
}

const normalizeSort = (value) => {
  if (typeof value !== "string") return "latest"
  return ALLOWED_SORTS.includes(value) ? value : "latest"
}

const normalizePage = (value) => {
  const num = Number(value)

  if (!value) return 1
  if (isNaN(num) || num < 1) return 1

  return Math.floor(num)
}

const clearDrinkList = () => {
  drinkList.value = []
  totalCount.value = 0
  totalPage.value = 1
}

const removePageQuery = () => {
  const query = { ...route.query }
  delete query.page

  router.replace({
    path: "/drinks",
    query
  })
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

    isCategoryLoaded.value = true
  } catch (error) {
    console.log(error)
    isCategoryLoaded.value = true
  }
}

const loadDrinkList = async () => {
  try {
    normalizeRange()

    const params = {
      page: page.value,
      size: size.value,
      sort: sortBy.value,
      keyword: searchKeyword.value.trim(),
      categoryCode: selectedCategory.value || null,
      abvLow: Number(abvLow.value),
      abvHigh: Number(abvHigh.value),
      priceLow: Number(priceLow.value),
      priceHigh: Number(priceHigh.value)
    }

    const res = await getDrinkList(params)

    const list = res.data.list || []
    const total = res.data.totalCount || 0
    const totalP = res.data.totalPage || 1

    if (total === 0) {
      clearDrinkList()

      if (page.value !== 1) {
        page.value = 1
        removePageQuery()
        return
      }

      return
    }

    if (page.value > totalP) {
      router.replace({
        path: "/drinks",
        query: {
          ...route.query,
          page: String(totalP)
        }
      })
      return
    }

    drinkList.value = list
    totalCount.value = total
    totalPage.value = totalP
  } catch (error) {
    console.log(error)
  }
}

const syncFromRoute = () => {
  const normalizedCategoryCode =
    typeof route.query.categoryCode === "string" ? route.query.categoryCode : ""

  const normalizedKeyword =
    typeof route.query.keyword === "string" ? route.query.keyword.trim() : ""

  const normalizedSortValue = normalizeSort(route.query.sort)
  const normalizedAbvLow = normalizeAbv(route.query.abvLow, ABV_DEFAULT_LOW)
  const normalizedAbvHigh = normalizeAbv(route.query.abvHigh, ABV_DEFAULT_HIGH)

  let normalizedPriceLow = normalizePrice(route.query.priceLow, PRICE_DEFAULT_LOW)
  let normalizedPriceHigh = normalizePrice(route.query.priceHigh, PRICE_DEFAULT_HIGH)

  let fixedAbvLow = normalizedAbvLow
  let fixedAbvHigh = normalizedAbvHigh
  let fixedCategoryCode = normalizedCategoryCode

  if (Number(fixedAbvLow) > Number(fixedAbvHigh)) {
    const temp = fixedAbvLow
    fixedAbvLow = fixedAbvHigh
    fixedAbvHigh = temp
  }

  if (Number(normalizedPriceLow) > Number(normalizedPriceHigh)) {
    const temp = normalizedPriceLow
    normalizedPriceLow = normalizedPriceHigh
    normalizedPriceHigh = temp
  }

  if (isCategoryLoaded.value && !isValidCategoryCode(normalizedCategoryCode)) {
    fixedCategoryCode = ""
  }

  const normalizedPageValue = normalizePage(route.query.page)

  selectedCategory.value = fixedCategoryCode
  searchKeyword.value = normalizedKeyword
  sortBy.value = normalizedSortValue
  abvLow.value = fixedAbvLow
  abvHigh.value = fixedAbvHigh
  priceLow.value = normalizedPriceLow
  priceHigh.value = normalizedPriceHigh
  page.value = normalizedPageValue

  const normalizedQuery = {}

  if (normalizedKeyword) normalizedQuery.keyword = normalizedKeyword
  if (fixedCategoryCode) normalizedQuery.categoryCode = fixedCategoryCode
  if (fixedAbvLow !== ABV_DEFAULT_LOW) normalizedQuery.abvLow = fixedAbvLow
  if (fixedAbvHigh !== ABV_DEFAULT_HIGH) normalizedQuery.abvHigh = fixedAbvHigh
  if (normalizedPriceLow !== PRICE_DEFAULT_LOW) normalizedQuery.priceLow = normalizedPriceLow
  if (normalizedPriceHigh !== PRICE_DEFAULT_HIGH) normalizedQuery.priceHigh = normalizedPriceHigh
  if (normalizedSortValue !== "latest") normalizedQuery.sort = normalizedSortValue
  if (String(normalizedPageValue) !== "1") normalizedQuery.page = String(normalizedPageValue)

  const currentQueryString = JSON.stringify(route.query)
  const normalizedQueryString = JSON.stringify(normalizedQuery)

  if (currentQueryString !== normalizedQueryString) {
    router.replace({
      path: "/drinks",
      query: normalizedQuery
    })
  }
}

const updateRoute = () => {
  normalizeRange()

  const query = {}
  const keyword = searchKeyword.value.trim()

  if (keyword) query.keyword = keyword
  if (selectedCategory.value) query.categoryCode = selectedCategory.value
  if (abvLow.value !== ABV_DEFAULT_LOW) query.abvLow = abvLow.value
  if (abvHigh.value !== ABV_DEFAULT_HIGH) query.abvHigh = abvHigh.value
  if (priceLow.value !== PRICE_DEFAULT_LOW) query.priceLow = priceLow.value
  if (priceHigh.value !== PRICE_DEFAULT_HIGH) query.priceHigh = priceHigh.value
  if (sortBy.value !== "latest") query.sort = sortBy.value
  if (page.value > 1) query.page = String(page.value)

  router.replace({
    path: "/drinks",
    query
  })
}

const changeCategory = (code) => {
  selectedCategory.value = code
  page.value = 1
  updateRoute()
}

const applySearch = () => {
  page.value = 1
  updateRoute()
}

const applyFilter = () => {
  formatPriceInput("priceLow")
  formatPriceInput("priceHigh")

  page.value = 1
  updateRoute()
}

const resetFilter = () => {
  selectedCategory.value = ""
  searchKeyword.value = ""
  sortBy.value = "latest"
  abvLow.value = ABV_DEFAULT_LOW
  abvHigh.value = ABV_DEFAULT_HIGH
  priceLow.value = PRICE_DEFAULT_LOW
  priceHigh.value = PRICE_DEFAULT_HIGH
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
  goTop()
}

watch(
  () => route.query,
  async () => {
    if (!isCategoryLoaded.value) return

    syncFromRoute()
    await loadDrinkList()
  },
  { immediate: true }
)

const init = async () => {
  await loadCategoryList()
  syncFromRoute()
  await loadDrinkList()
}

init()

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

const goTop = () => {
  const el = document.querySelector(".list-scroll")

  if (el) {
    el.scrollTop = 0
  }
}
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
  height: calc(100vh - 60px);
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
  width: 90px;
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
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
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

.list-scroll::-webkit-scrollbar {
  width: 6px;
}

.list-scroll::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 3px;
}

.filter-divider {
  margin: 18px 0 8px;
  display: flex;
  align-items: center;
}

.filter-divider span {
  display: block;
  width: 100%;
  height: 1px;
  background: linear-gradient(
    to right,
    transparent 0%,
    #e5ddd6 15%,
    #e5ddd6 85%,
    transparent 100%
  );
}

.input-msg {
  margin-top: 4px;
  font-size: 11px;
  color: #c2785c;
  animation: fadeInOut 2s ease;
}

@keyframes fadeInOut {
  0% { opacity: 0; transform: translateY(-3px); }
  10% { opacity: 1; transform: translateY(0); }
  80% { opacity: 1; }
  100% { opacity: 0; }
}

.list-scroll {
  flex: 1;
  overflow-y: auto;
  padding-right: 6px;
}

.scroll-progress {
  position: sticky;
  top: 0;
  z-index: 20;
  height: 2px;
  background: transparent;
}

.scroll-progress-bar {
  height: 100%;
  width: 0%;
  background: linear-gradient(
    to right,
    #c2785c,
    #e0b38f
  );
  transition: width 0.08s linear;
  border-radius: 999px;
}
</style>