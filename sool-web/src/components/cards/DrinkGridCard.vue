<template>
  <router-link class="drink-card2" :to="`/drinks/${item.id}`">
    <div class="thumb2">{{ item.emoji }}</div>
    <div class="info2">
      <div class="cat2">{{ item.category }}</div>
      <div class="name2">{{ item.name }}</div>
      <div class="abv2">{{ item.meta }}</div>
      <div class="meta2">
        <span class="stars2">{{ item.rating }}</span>
        <button class="like2" :class="{ liked }" @click.prevent="toggleLike">
          {{ liked ? '♥' : '♡' }} {{ likeCount }}
        </button>
      </div>
    </div>
  </router-link>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  item: {
    type: Object,
    required: true
  }
})

const liked = ref(!!props.item.liked)
const likeCount = ref(props.item.likes)

const toggleLike = () => {
  liked.value = !liked.value
  likeCount.value += liked.value ? 1 : -1
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
