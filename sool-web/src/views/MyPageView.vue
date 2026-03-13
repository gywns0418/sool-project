<template>
  <div class="page-wrap">
    <PageNav :links="navLinks" />

    <div class="my-wrap">
      <aside class="my-sidebar">
        <div class="my-avatar">K</div>
        <div class="my-name">김효준</div>
        <div class="my-email">hyojun@example.com</div>

        <div class="my-stats">
          <div class="my-stat">
            <div class="my-stat-n">{{ noteList.length }}</div>
            <div class="my-stat-l">노트</div>
          </div>
          <div class="my-stat">
            <div class="my-stat-n">61</div>
            <div class="my-stat-l">좋아요</div>
          </div>
        </div>

        <div class="my-nav">
          <div class="my-nav-item" :class="{ sel: activeTab === 'notes' }" @click="activeTab = 'notes'">
            내 테이스팅 노트
          </div>
          <div class="my-nav-item" :class="{ sel: activeTab === 'likes' }" @click="activeTab = 'likes'">
            좋아요한 술
          </div>
          <div class="my-nav-item" :class="{ sel: activeTab === 'profile' }" @click="activeTab = 'profile'">
            회원 정보 수정
          </div>
          <div class="my-nav-item" :class="{ sel: activeTab === 'reports' }" @click="activeTab = 'reports'">
            신고 목록
          </div>
          <div class="my-nav-sep"></div>
          <div class="my-nav-item" @click="goHome">회원 탈퇴</div>
        </div>
      </aside>

      <main class="my-main">
        <MyNotesSection
          v-if="activeTab === 'notes'"
          :note-list="noteList"
          @edit="editNote"
          @delete="deleteNote"
        />

        <MyLikedDrinksSection v-else-if="activeTab === 'likes'" />

        <MyProfileSection v-else-if="activeTab === 'profile'" />

        <MyReportsSection v-else-if="activeTab === 'reports'" />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

import PageNav from '../components/common/PageNav.vue'

import MyNotesSection from '../components/mypage/MyNotesSection.vue'
import MyLikedDrinksSection from '../components/mypage/MyLikedDrinksSection.vue'
import MyProfileSection from '../components/mypage/MyProfileSection.vue'
import MyReportsSection from '../components/mypage/MyReportsSection.vue'

import { myNotes } from '../mock/soolData'

const router = useRouter()
const activeTab = ref('notes')
const noteList = ref([...myNotes])

const navLinks = [
  { label: '홈', to: '/' },
  { label: '마이페이지', to: '/mypage', active: true }
]

const goHome = () => router.push('/')

const editNote = (item) => {
  router.push({ path: '/notes/write', query: { edit: item.id } })
}

const deleteNote = (id) => {
  noteList.value = noteList.value.filter((note) => note.id !== id)
}
</script>

<style scoped>
.page-wrap {
  background: var(--bg);
  overflow: hidden;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.12);
}

.my-wrap {
  display: grid;
  grid-template-columns: 240px 1fr;
  min-height: 700px;
}

.my-sidebar {
  background: var(--white);
  border-right: 1px solid var(--border);
  padding: 32px 22px;
}

.my-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: var(--ink);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 12px;
}

.my-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--ink);
  margin-bottom: 3px;
}

.my-email {
  font-size: 12px;
  color: var(--muted);
  margin-bottom: 24px;
}

.my-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
  margin-bottom: 28px;
}

.my-stat {
  background: var(--surface);
  border-radius: 8px;
  padding: 12px 10px;
  text-align: center;
  border: 1px solid var(--border);
}

.my-stat-n {
  font-size: 20px;
  font-weight: 700;
  color: var(--point);
}

.my-stat-l {
  font-size: 10px;
  color: var(--muted);
  margin-top: 2px;
}

.my-nav {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.my-nav-item {
  padding: 9px 12px;
  border-radius: 7px;
  font-size: 13.5px;
  color: var(--sub);
  cursor: pointer;
}

.my-nav-item.sel {
  background: #fdf3ef;
  color: var(--point);
  font-weight: 600;
}

.my-nav-item:hover {
  background: #fdf3ef;
  color: var(--point);
  font-weight: 600;
}

.my-nav-sep {
  height: 1px;
  background: var(--border);
  margin: 10px 0;
}

.my-main {
  background: var(--bg);
  padding: 32px;
}

.my-main-title {
  font-family: 'Playfair Display', serif;
  font-size: 20px;
  font-weight: 400;
  color: var(--ink);
  margin-bottom: 20px;
}

.my-notes-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}
</style>
