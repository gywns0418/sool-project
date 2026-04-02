<template>
  <div class="page-wrap">
    <PageNav :links="navLinks" />

    <div class="my-wrap">
      <aside class="my-sidebar">
        <div class="my-avatar">{{ userInitial }}</div>
        <div class="my-name">{{ sidebarInfo.name || '-' }}</div>
        <div class="my-email">{{ sidebarInfo.email || '-' }}</div>

        <div class="my-stats">
          <div class="my-stat">
            <div class="my-stat-n">{{ sidebarInfo.noteCount }}</div>
            <div class="my-stat-l">노트</div>
          </div>
          <div class="my-stat">
            <div class="my-stat-n">{{ sidebarInfo.likeCount }}</div>
            <div class="my-stat-l">좋아요</div>
          </div>
        </div>

        <div class="my-nav">
          <div class="my-nav-item" :class="{ sel: activeTab === 'notes' }" @click="changeTab('notes')">
            내 테이스팅 노트
          </div>
          <div class="my-nav-item" :class="{ sel: activeTab === 'likes' }" @click="changeTab('likes')">
            나의 좋아요
          </div>
          <div class="my-nav-item" :class="{ sel: activeTab === 'profile' }" @click="changeTab('profile')">
            회원 정보 수정
          </div>
          <div class="my-nav-item" :class="{ sel: activeTab === 'reports' }" @click="changeTab('reports')">
            신고 목록
          </div>
          <div class="my-nav-sep"></div>
          <div class="my-nav-item" @click="handleUserDelete">회원 탈퇴</div>
        </div>
      </aside>

      <main class="my-main">
        <MyNotesSection v-if="activeTab === 'notes'"
          @refreshSidebar="fetchSidebarInfo"
        />

        <MyLikedDrinksSection v-else-if="activeTab === 'likes'" @refreshSidebar="fetchSidebarInfo"/>

        <MyProfileSection
          v-else-if="activeTab === 'profile'"
          :user-info="sidebarInfo"
        />

        <MyReportsSection v-else-if="activeTab === 'reports'" />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

import { useAuthStore } from '@/stores/authStore'
import PageNav from '../components/common/PageNav.vue'

import MyNotesSection from '../components/mypage/MyNotesSection.vue'
import MyLikedDrinksSection from '../components/mypage/MyLikedSection.vue'
import MyProfileSection from '../components/mypage/MyProfileSection.vue'
import MyReportsSection from '../components/mypage/MyReportsSection.vue'

import { getMySidebarInfo, deleteUser } from '@/api/mypageApi'


const authStore = useAuthStore()
const router = useRouter()
const activeTab = ref('notes')

const sidebarInfo = ref({
  name: '',
  loginId: '',
  email: '',
  noteCount: 0,
  likeCount: 0
})

const userInitial = computed(() => {
  return sidebarInfo.value.name ? sidebarInfo.value.name.charAt(0) : 'U'
})

const navLinks = [
  { label: '홈', to: '/' },
  { label: '마이페이지', to: '/mypage', active: true }
]

const handleUserDelete = async () => {
  if (!confirm('정말 회원을 탈퇴하시겠습니까?')) return

  try {
    await deleteUser()

    alert('회원 탈퇴가 완료되었습니다.')
    await authStore.logout()
    router.replace('/')

  } catch (error) {
    console.log('회원탈퇴 실패', error)
  }
}

const fetchSidebarInfo = async () => {
  try {
    const res = await getMySidebarInfo()
    console.log(res.data)
    sidebarInfo.value = res.data
  } catch (e) {
    console.log('마이페이지 사이드바 조회 실패', e)
  }
}

//스크롤 최상위로
const goTop = () => {
  const el = document.querySelector('.my-main')
  if (el) {
    el.scrollTop = 0
  }
}

const changeTab = (tab) => {
  activeTab.value = tab
  goTop()
}

onMounted(async () => {
  const wasLogin = authStore.isLogin

  await authStore.fetchMe()

  if (!authStore.isLogin) {
    if (wasLogin) {
      alert('세션이 만료되었습니다. 다시 로그인해주세요.')
    }
    router.replace('/login')
    return
  }

  await fetchSidebarInfo()
})
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
  height: calc(100vh - 60px); 
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
  flex: 1;
  overflow-y: auto;
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

.my-main::-webkit-scrollbar {
  width: 6px;
}

.my-main::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 3px;
}
</style>
