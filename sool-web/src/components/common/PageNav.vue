<template>
  <nav class="nav">
    <router-link to="/" class="nav-logo">SoolNote</router-link>
    <ul class="nav-links">
      <li v-for="link in links" :key="link.label">
        <router-link :to="link.to" :class="{ 'active-link': isActive(link) }">{{ link.label }}</router-link>
      </li>
    </ul>
    <div class="nav-right">
      <button class="btn-ghost" @click="router.push('/mypage')">마이페이지</button>
      <button class="btn-fill" @click="router.push('/notes/write')">노트 작성</button>
    </div>
  </nav>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const props = defineProps({
  links: {
    type: Array,
    default: () => []
  }
})

const route = useRoute()
const router = useRouter()

const currentPath = computed(() => route.path)

const isActive = (link) => {
  if (link.active) return true
  if (!link.to) return false
  if (link.to === '/') return currentPath.value === '/'
  return currentPath.value.startsWith(link.to)
}

</script>

<style scoped>
.nav {
  background: var(--white);
  border-bottom: 1px solid var(--border);
  padding: 0 40px;
  display: flex;
  align-items: center;
  height: 60px;
  gap: 32px;
  position: sticky;
  top: 0;
  z-index: 10;
}

.nav-logo {
  font-family: 'Playfair Display', serif;
  font-size: 22px;
  letter-spacing: -0.3px;
  flex-shrink: 0;
  font-style: italic;
  color: var(--point);
}


.nav-links {
  display: flex;
  gap: 24px;
  list-style: none;
  flex: 1;
}

.nav-links a {
  font-size: 13.5px;
  color: var(--sub);
  font-weight: 400;
  letter-spacing: 0.1px;
}

.nav-links a.active-link {
  color: var(--ink);
  font-weight: 600;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}


.btn-ghost {
  padding: 7px 14px;
  font-size: 12.5px;
  font-weight: 500;
  color: var(--ink);
  background: transparent;
  border: 1px solid var(--border);
  border-radius: 6px;
  cursor: pointer;
}

.btn-fill {
  padding: 7px 16px;
  font-size: 12.5px;
  font-weight: 600;
  color: white;
  background: var(--point);
  border: none;
  border-radius: 6px;
  cursor: pointer;
}
</style>
