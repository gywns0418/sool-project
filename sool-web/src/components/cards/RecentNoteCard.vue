<template>
  <router-link class="feed-card" :to="`/notes/${item.id}`">
    <div class="feed-card-top">
      <div class="feed-drink-thumb">{{ item.emoji }}</div>
      <div class="feed-drink-info">
        <div class="feed-drink-cat">{{ item.category }}</div>
        <div class="feed-drink-name">{{ item.drinkName }}</div>
      </div>
    </div>
    <div class="feed-stars">{{ item.stars }}</div>
    <div class="feed-text">{{ item.text }}</div>
    <div class="feed-footer">
      <div class="feed-author">
        <div class="feed-avatar" :class="item.avatarClass">{{ item.authorInitial }}</div>
        <span class="feed-name">{{ item.author }}</span>
      </div>
      <span class="feed-date">{{ item.date }}</span>
    </div>
  </router-link>
</template>

<script setup>
defineProps({
  item: {
    type: Object,
    required: true
  }
})
</script>

<style scoped>
.feed-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: box-shadow 0.18s, transform 0.18s;
  display: block;
}

.feed-card:hover {
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.feed-card-top {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
}

.feed-drink-thumb {
  width: 42px;
  height: 42px;
  border-radius: 8px;
  background: linear-gradient(135deg, #fdf3ef, #f5e5da);
  border: 1px solid var(--border);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.feed-drink-cat {
  font-size: 9.5px;
  letter-spacing: 1.2px;
  text-transform: uppercase;
  color: var(--point);
  font-weight: 600;
  margin-bottom: 2px;
}

.feed-drink-name {
  font-size: 12.5px;
  font-weight: 600;
  color: var(--ink);
}

.feed-stars {
  font-size: 12px;
  color: var(--yellow);
  margin-bottom: 8px;
}

.feed-text {
  font-size: 12.5px;
  color: var(--sub);
  line-height: 1.7;
  font-weight: 300;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 14px;
}

.feed-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.feed-author {
  display: flex;
  align-items: center;
  gap: 7px;
}

.feed-avatar {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: var(--ink);
  color: white;
  font-size: 9px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
}

.feed-avatar.av2 {
  background: var(--point2);
}

.feed-avatar.av3 {
  background: var(--point);
}

.feed-name {
  font-size: 11.5px;
  font-weight: 500;
  color: var(--sub);
}

.feed-date {
  font-size: 10.5px;
  color: var(--muted);
}
</style>
