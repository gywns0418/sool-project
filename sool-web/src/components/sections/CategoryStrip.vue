<template>
  <div class="cat-strip">
    <h3>카테고리로 빠르게 탐색</h3>
    <div class="cat-pills">
      <router-link
        v-for="item in categoryListWithEmoji"
        :key="item.code"
        class="cat-pill"
        :to="{ path: '/drinks', query: { categoryCode: item.code } }"
      >
        <span class="cat-emoji">{{ item.emoji }}</span>
        <span class="cat-name" :title="item.codeName">{{ item.codeName }}</span>
        <span class="cat-count">{{ item.drinkCount }}종</span>
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue"
import { categories } from "@/mock/soolData"

const props = defineProps({
  items: {
    type: Array,
    default: () => []
  }
})

const categoryListWithEmoji = computed(() => {
  return props.items.map(item => {
    const emojiData = categories.find(e => e.name === item.code)

    return {
      ...item,
      emoji: emojiData ? emojiData.emoji : "🍹"
    }
  })
})
</script>

<style scoped>
.cat-strip {
  padding: 40px 60px;
  border-bottom: 1px solid var(--border);
  background: var(--white);
}

.cat-strip h3 {
  font-family: 'Playfair Display', serif;
  font-size: 20px;
  font-weight: 400;
  color: var(--ink);
  letter-spacing: -0.3px;
  margin-bottom: 20px;
}

.cat-pills {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 10px;
}

.cat-pill {
  min-width: 0;
  min-height: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 18px 12px 14px;
  border-radius: 12px;
  border: 1.5px solid var(--border);
  font-size: 13px;
  font-weight: 500;
  color: var(--sub);
  background: var(--surface);
  cursor: pointer;
  text-align: center;
  overflow: hidden;
}

.cat-pill:hover {
  border-color: var(--point);
  color: var(--point);
  background: #fdf3ef;
  font-weight: 600;
}

.cat-emoji {
  font-size: 28px;
  line-height: 1;
  flex-shrink: 0;
}

.cat-name {
  display: block;
  width: 100%;
  max-width: 100%;
  color: inherit;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  word-break: keep-all;
}

.cat-count {
  font-size: 10px;
  color: var(--muted);
  margin-top: 2px;
  white-space: nowrap;
  flex-shrink: 0;
}

@media (max-width: 1200px) {
  .cat-pills {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .cat-strip {
    padding: 32px 20px;
  }

  .cat-pills {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>