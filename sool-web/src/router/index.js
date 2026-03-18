import { createRouter, createWebHistory } from "vue-router"
import { useAuthStore } from "@/stores/authStore"

import HomeView from "@/views/HomeView.vue"
import DrinkListView from "@/views/DrinkListView.vue"
import DrinkDetailView from "@/views/DrinkDetailView.vue"
import NoteDetailView from "@/views/NoteDetailView.vue"
import NoteWriteView from "@/views/NoteWriteView.vue"
import MyPageView from "@/views/MyPageView.vue"
import LoginView from "@/views/auth/LoginView.vue"
import SignupView from "@/views/auth/SignupView.vue"

const routes = [
  { path: "/", name: "home", component: HomeView },
  { path: "/drinks", name: "drink-list", component: DrinkListView },
  { path: "/drinks/:id", name: "drink-detail", component: DrinkDetailView },
  { path: "/notes/:id", name: "note-detail", component: NoteDetailView },
  // 로그인 필요 페이지
  {
    path: "/notes/write",
    name: "note-write",
    component: NoteWriteView,
    meta: { requiresAuth: true }
  },
  // 로그인 필요 페이지
  {
    path: "/mypage",
    name: "mypage",
    component: MyPageView,
    meta: { requiresAuth: true }
  },
  { path: "/login", name: "login", component: LoginView },
  { path: "/signup", name: "signup", component: SignupView }
]

const router = createRouter({
  history: createWebHistory(),
  routes,

  // 페이지 이동 시 항상 스크롤을 맨 위로
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to) => {
  const authStore = useAuthStore()

  // 로그인 필요한 페이지인데 로그인 안했으면 로그인 페이지 이동
  if (to.meta.requiresAuth && !authStore.isLogin) {
    return "/login"
  }
})

export default router