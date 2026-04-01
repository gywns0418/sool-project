<template>
  <router-link class="drink-card2" :to="`/drinks/${item.drinkId}`">
    <div class="drink-thumb">
      <img
        v-if="item.image?.fileUrl"
        :src="item.image?.fileUrl"
        :alt="item.title"
        class="thumb"
      />
      <div v-else class="thumb empty-thumb">
        <span class="empty-emoji">{{ emoji }}</span>
      </div>
    </div>

    <div class="info2">
      <div class="cat2">{{ item.typeName || item.typeCode }}</div>

      <div class="name2">{{ item.drinkName }}</div>

      <div class="info-list">
          <span v-if="item.abv !== null && item.abv !== undefined">
            도수 {{ item.abv }}%
          </span>
          <span v-if="item.price !== null && item.price !== undefined">
            {{ formatPrice(item.price) }}
          </span>
      </div>

      <div class="meta2">
        <span class="stars2">★{{ item.avgRating ?? "-" }}</span>

        <button
          class="like2"
          :class="{ liked: liked }"
          :disabled="likeLoading"
          @click.prevent.stop="toggleLike"
        >
          {{ liked ? "♥" : "♡" }} {{ likeCount }}
        </button>
      </div>
    </div>
  </router-link>
</template>

<script setup>
import { ref, computed, watch } from "vue"
import { categories } from "@/mock/soolData"
import { useAuthStore } from "@/stores/authStore"
import { useRoute, useRouter } from "vue-router"
import { insertDrinkLike, deleteDrinkLike } from "@/api/likeApi"

const props = defineProps({
  item: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(["refresh"])

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const likeLoading = ref(false)
const liked = ref(!!props.item.liked)
const likeCount = ref(props.item.likeCount ?? 0)

watch(
  () => props.item,
  (newItem) => {
    liked.value = !!newItem?.liked
    likeCount.value = newItem?.likeCount ?? 0
  },
  { deep: true, immediate: true }
)

const emoji = computed(() => {
  const data = categories.find((e) => e.name === props.item.categoryCode)
  return data ? data.emoji : "🍹"
})


function formatPrice(value) {
  return `${Number(value).toLocaleString()}원`
}

const toggleLike = async () => {
  const drinkId = props.item.drinkId

  if (!drinkId || likeLoading.value) return

  if (!authStore.isLogin) {
    alert("로그인을 먼저 해주세요.")
    router.push({
      path: "/login",
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

    emit("refresh")
  } catch (e) {
    console.log("좋아요 처리 실패", e)
  } finally {
    likeLoading.value = false
  }
}
</script>

<style scoped>
.drink-card2 {
  background: var(--white);
  border: 1px solid var(--border);
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  display: block;
}

.drink-thumb {
  height: 130px;
  background: linear-gradient(145deg, var(--surface), #ede8e0);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 50px;
  border-bottom: 1px solid var(--border);
  position: relative;
}

.thumb {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
}

.empty-thumb {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #aa9d91;
  font-size: 40px;
}


.info2 {
  padding: 14px 16px;
}

.cat2 {
  font-size: 10px;
  letter-spacing: 1px;
  text-transform: uppercase;
  color: var(--point);
  font-weight: 600;
  margin-bottom: 4px;
}

.name2 {
  font-size: 14px;
  font-weight: 600;
  color: var(--ink);
  margin-bottom: 3px;
}

.abv2 {
  font-size: 11.5px;
  color: var(--muted);
  margin-bottom: 8px;
}

.meta2 {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stars2 {
  font-size: 12.5px;
  color: var(--yellow);
}

.like2 {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--muted);
  padding: 4px 8px;
  border: 1px solid var(--border);
  border-radius: 100px;
  background: white;
}

.like2:hover {
  border-color: var(--point);
  color: var(--point);
  background: #fdf3ef;
}

.like2.liked {
  border-color: var(--point);
  color: var(--point);
  background: #fdf3ef;
}

.info-list {
  margin-top: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.info-list span {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  background: #f8f4ef;
  color: #7a5c45;
  font-size: 12px;
}
</style>