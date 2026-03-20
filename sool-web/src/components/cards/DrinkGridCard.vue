<template>
  <router-link class="drink-card2" :to="`/drinks/${item.drinkId}`">
    <div class="thumb2">{{ emoji }}</div>

    <div class="info2">
      <div class="cat2">{{ item.typeName || item.typeCode }}</div>

      <div class="name2">{{ item.drinkName }}</div>

      <div class="abv2">{{ item.abv }}%</div>

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

.thumb2 {
  height: 130px;
  background: var(--surface);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 50px;
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
</style>