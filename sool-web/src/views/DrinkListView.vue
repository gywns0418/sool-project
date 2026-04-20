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
            max="99.9"
            :placeholder="0"
            class="range-inp"
            @keydown="preventMinusInput"
            @input="sanitizeNumberInput('abvLow')"
            @keydown.enter="applyFilter"
          />

          <span class="range-sep">—</span>

          <input
            v-model="abvHigh"
            type="number"
            min="0"
            max="99.9"
            :placeholder="60"
            class="range-inp"
            @keydown="preventMinusInput"
            @input="sanitizeNumberInput('abvHigh')"
            @keydown.enter="applyFilter"
          />
        </div>

        <h4>가격대 (원)</h4>
        <div class="range-row">
          <input
            v-model="priceLow"
            type="number"
            min="0"
            :placeholder="0"
            class="range-inp"
            @keydown="preventMinusInput"
            @input="sanitizeNumberInput('priceLow')"
            @keydown.enter="applyFilter"
          />
          <span class="range-sep">—</span>
          <input
            v-model="priceHigh"
            type="number"
            min="0"
            :placeholder="500000"
            class="range-inp"
            @keydown="preventMinusInput"
            @input="sanitizeNumberInput('priceHigh')"
            @keydown.enter="applyFilter"
          />
        </div>

        <button class="filter-apply-btn" @click="applyFilter">필터 적용</button>
        <button class="filter-reset-btn" @click="resetFilter">초기화</button>
      </aside>

      <section class="list-main">
        <div class="list-top">
          <h2>검색된 {{ titleLabel }} <span class="sub-count">{{ totalCount }}개</span></h2>

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

// 상단 네비게이션 링크
const navLinks = [
  { label: "홈", to: "/" },
  { label: "주류 목록", to: "/drinks", active: true }
]

const route = useRoute()
const router = useRouter()

// 목록 데이터
const drinkList = ref([])

// 카테고리 필터 목록
const categoryOptions = ref([])

// 전체 개수 / 전체 페이지
const totalCount = ref(0)
const totalPage = ref(1)

// 검색 / 필터 상태
const selectedCategory = ref("")
const searchKeyword = ref("")
const sortBy = ref("latest")

// 도수 범위
const abvLow = ref("0")
const abvHigh = ref("60")

// 가격 범위
const priceLow = ref("0")
const priceHigh = ref("500000")

// 페이지 정보
const page = ref(1)
const size = ref(12)
const pageErrorMsg = ref('')
const emptyMsg = ref('')


// 카테고리 코드에 맞는 이모지 + 이름 생성
const makeCategoryLabel = (item) => {
  if (!item.code) return item.codeName

  const emojiData = categories.find(e => e.name === item.code)

  return emojiData
    ? `${emojiData.emoji} ${item.codeName}`
    : "🍹 " + item.codeName
}


// 도수 / 가격 범위가 뒤집혀 있을 경우 자동 교정
// ex) 30 ~ 10 입력 → 10 ~ 30으로 변경
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

// 음수 입력 방지
// - , e 입력을 막아서 음수나 지수표기 방지
const preventMinusInput = (e) => {

  if (e.key === "-" || e.key === "e" || e.key === "E") {
    e.preventDefault()
    alert("0보다 작은 값은 입력할 수 없습니다.")
  }
}

// 숫자 입력값 검증
// 음수 방지 / 도수 최대값 제한 / 소수점 처리
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

  let value = parseFloat(eval(field).value)

  if (isNaN(value)) return

  // 도수 최대값 제한
  if (field === "abvLow" || field === "abvHigh") {

    if (value > 99.9) value = 99.9

    value = Math.floor(value * 10) / 10
  }

  eval(field).value = value
}


// 카테고리 목록 조회
// 필터 영역에 표시할 카테고리 데이터 로드
const loadCategoryList = async () => {
  try {
    const res = await getDrinkCategoryList()
    const list = res.data || []

    categoryOptions.value = [
      {
        code: "",
        codeName: "전체",
        label: "전체",
        drinkCount: list.reduce(
          (sum, item) => sum + (item.drinkCount || 0),
          0
        )
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


// 주류 목록 조회
// 키워드 검색 / 필터 검색 조건에 따라 API 호출
const loadDrinkList = async () => {
  try {
    normalizeRange()

    const params = {
      page: page.value,
      size: size.value,
      sort: sortBy.value
    }

    const keyword = searchKeyword.value.trim()

    params.keyword = keyword
    params.categoryCode = selectedCategory.value || null
    params.abvLow = Number(abvLow.value)
    params.abvHigh = Number(abvHigh.value)
    params.priceLow = Number(priceLow.value)
    params.priceHigh = Number(priceHigh.value)

    const res = await getDrinkList(params)

    const list = res.data.list || []
    const total = res.data.totalCount || 0
    const totalP = res.data.totalPage || 1

    if (total === 0) {
      drinkList.value = []
      totalCount.value = 0
      totalPage.value = 1
      return
    }

    if (page.value > totalP) {
      alert('존재하지 않는 페이지입니다. 마지막 페이지로 이동합니다.')

      router.replace({
        path: '/drinks',
        query: {
          ...route.query,
          page: totalP
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



const normalizeAbv = (value, defaultValue) => {
  const num = Number(value)

  if (value == null || value === '') return defaultValue
  if (isNaN(num)) return defaultValue
  if (num < 0) return '0'
  if (num > 99.9) return '99.9'

  return String(Math.floor(num * 10) / 10)
}

const normalizePrice = (value, defaultValue) => {
  const num = Number(value)

  if (value == null || value === '') return defaultValue
  if (isNaN(num)) return defaultValue
  if (num < 0) return '0'

  return String(Math.floor(num))
}

const syncFromRoute = () => {
  const pageQuery = Number(route.query.page)

  if (route.query.page && (isNaN(pageQuery) || pageQuery < 1)) {
    alert('존재하지 않는 페이지입니다. 첫 페이지로 이동합니다.')

    router.replace({
      path: '/drinks',
      query: {
        ...route.query,
        page: 1
      }
    })
    return
  }

  const normalizedCategoryCode =
    typeof route.query.categoryCode === 'string' ? route.query.categoryCode : ''

  const normalizedKeyword =
    typeof route.query.keyword === 'string' ? route.query.keyword : ''

  const normalizedSort =
    typeof route.query.sort === 'string' ? route.query.sort : 'latest'

  const normalizedAbvLow = normalizeAbv(route.query.abvLow, '0')
  const normalizedAbvHigh = normalizeAbv(route.query.abvHigh, '60')

  let normalizedPriceLow = normalizePrice(route.query.priceLow, '0')
  let normalizedPriceHigh = normalizePrice(route.query.priceHigh, '500000')

  if (Number(normalizedAbvLow) > Number(normalizedAbvHigh)) {
    const temp = normalizedAbvLow
    normalizedAbvLow = normalizedAbvHigh
    normalizedAbvHigh = temp
  }

  if (Number(normalizedPriceLow) > Number(normalizedPriceHigh)) {
    const temp = normalizedPriceLow
    normalizedPriceLow = normalizedPriceHigh
    normalizedPriceHigh = temp
  }

  const normalizedPage = route.query.page ? String(pageQuery) : '1'

  const normalizedQuery = {}

  if (normalizedKeyword) normalizedQuery.keyword = normalizedKeyword
  if (normalizedCategoryCode) normalizedQuery.categoryCode = normalizedCategoryCode
  if (normalizedAbvLow !== '0') normalizedQuery.abvLow = normalizedAbvLow
  if (normalizedAbvHigh !== '60') normalizedQuery.abvHigh = normalizedAbvHigh
  if (normalizedPriceLow !== '0') normalizedQuery.priceLow = normalizedPriceLow
  if (normalizedPriceHigh !== '500000') normalizedQuery.priceHigh = normalizedPriceHigh
  if (normalizedSort !== 'latest') normalizedQuery.sort = normalizedSort
  if (normalizedPage !== '1') normalizedQuery.page = normalizedPage

  const currentQuery = { ...route.query }

  const currentQueryString = JSON.stringify(currentQuery)
  const normalizedQueryString = JSON.stringify(normalizedQuery)

  if (currentQueryString !== normalizedQueryString) {
    router.replace({
      path: '/drinks',
      query: normalizedQuery
    })
    return
  }

  selectedCategory.value = normalizedCategoryCode
  searchKeyword.value = normalizedKeyword
  sortBy.value = normalizedSort
  abvLow.value = normalizedAbvLow
  abvHigh.value = normalizedAbvHigh
  priceLow.value = normalizedPriceLow
  priceHigh.value = normalizedPriceHigh
  page.value = Number(normalizedPage)
}

// 현재 필터 상태 → URL에 반영
const updateRoute = () => {
  normalizeRange()

  const query = {}
  const keyword = searchKeyword.value.trim()

  if (keyword)
    query.keyword = keyword

  if (selectedCategory.value)
    query.categoryCode = selectedCategory.value

  if (abvLow.value !== '' && abvLow.value !== '0')
    query.abvLow = abvLow.value

  if (abvHigh.value !== '' && abvHigh.value !== '60')
    query.abvHigh = abvHigh.value

  if (priceLow.value !== '' && priceLow.value !== '0')
    query.priceLow = priceLow.value

  if (priceHigh.value !== '' && priceHigh.value !== '500000')
    query.priceHigh = priceHigh.value

  if (sortBy.value && sortBy.value !== "latest")
    query.sort = sortBy.value

  if (page.value > 1)
    query.page = page.value

  router.replace({ path: "/drinks", query })
}


// 카테고리 클릭 시 필터 변경
const changeCategory = (code) => {
  selectedCategory.value = code
  page.value = 1

  updateRoute()
}

// 검색 실행
const applySearch = () => {
  page.value = 1

  updateRoute()
}


// 필터 적용
const applyFilter = () => {
  page.value = 1

  updateRoute()
}


// 필터 초기화
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


// 정렬 변경
const changeSort = () => {
  page.value = 1

  updateRoute()
}


// 페이지 이동
const movePage = (pageNum) => {
  if (pageNum < 1 || pageNum > totalPage.value) return
  page.value = pageNum

  updateRoute()

  goTop()
}


// route query 변경 감지 → 목록 다시 조회
watch(
  () => route.query,
  async () => {
    syncFromRoute()
    await loadDrinkList()
  },
  { immediate: true }
)

// 카테고리 목록 최초 로딩
loadCategoryList()


// 페이지 제목 계산
const titleLabel = computed(() => {
  if (searchKeyword.value.trim()) {
    return "검색 결과"
  }

  if (!selectedCategory.value) {
    return "전체 주류"
  }

  const found =
    categoryOptions.value.find(
      item => item.code === selectedCategory.value
    )

  return found
    ? found.codeName
    : "전체 주류"
})


// 페이지네이션 버튼 목록 계산
const visiblePages = computed(() => {

  const start = Math.max(1, page.value - 2)
  const end = Math.min(totalPage.value, start + 4)

  const pages = []

  for (let i = start; i <= end; i++) {
    pages.push(i)
  }

  return pages
})


// 목록 영역 스크롤 최상단 이동
const goTop = () => {
  const el = document.querySelector('.list-main')

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
  flex: 1;
  overflow-y: auto;
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

.list-main::-webkit-scrollbar {
  width: 6px;
}

.list-main::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 3px;
}
</style>