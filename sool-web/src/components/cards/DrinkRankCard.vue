<template>
  <router-link class="drink-card" :to="`/drinks/${displayItem.drinkId}`">
  <div class="drink-thumb">
    <img
      v-if="displayItem.image?.fileUrl"
      :src="displayItem.image?.fileUrl"
      :alt="displayItem.title"
      class="thumb"
    />
    <div v-else class="thumb empty-thumb">
      <span class="empty-emoji">{{ displayItem.emoji }}</span>
    </div>

    <div class="drink-rank" :class="{ gold: rank === 1 }">
      {{ rank }}
    </div>
  </div>

    <div class="drink-info">
      <div class="drink-cat">{{ displayItem.typeCode }}</div>
      <div class="drink-name">{{ displayItem.drinkName }}</div>
      <div class="drink-name">{{ displayItem.drinkNameEn }}</div>
      <div class="drink-meta">
        <span class="drink-like">♥ {{ displayItem.likeCount }}</span>
      </div>
    </div>
  </router-link>
</template>

<script setup>
import { computed } from "vue"
import { categories } from "@/mock/soolData"

const props = defineProps({
  item: {
    type: Object,
    required: true
  },
  rank: {
    type: Number,
    default: 1
  }
})

const displayItem = computed(() => {
  const emojiData = categories.find(e => e.name === props.item.categoryCode)

  return {
    ...props.item,
    emoji: emojiData ? emojiData.emoji : "🍹"
  }
})
</script>

<style scoped>
.drink-card {
  background: var(--white);
  border: 1px solid var(--border);
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.18s, transform 0.18s;
  display: block;
}

.drink-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  transform: translateY(-3px);
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
  font-size: 13px;
}


.drink-rank {
  position: absolute;
  top: 10px;
  left: 10px;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: var(--ink);
  color: white;
  font-size: 10px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}

.drink-rank.gold {
  background: var(--yellow);
  color: var(--ink);
}

.drink-info {
  padding: 14px 16px;
}

.drink-cat {
  font-size: 10px;
  letter-spacing: 1px;
  text-transform: uppercase;
  color: var(--point);
  font-weight: 600;
  margin-bottom: 4px;
}

.drink-name {
  font-size: 13.5px;
  font-weight: 600;
  color: var(--ink);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.drink-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.drink-like {
  font-size: 11.5px;
  color: #d62828;
  border-color: #d62828;
  margin-left: auto;
}
</style>