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
import FindIdView from "@/views/auth/FindIdView.vue"
import ResetPasswordView from "@/views/auth/ResetPasswordView.vue"
import NotFoundView from "@/views/NotFoundView.vue"


const routes = [
  { path: "/", name: "home", component: HomeView },
  { path: "/drinks", name: "drink-list", component: DrinkListView },
  { path: "/drinks/:id", name: "drink-detail", component: DrinkDetailView },
  { path: "/notes/:id", name: "note-detail", component: NoteDetailView },

  { path: "/write/:drinkId", name: "note-write", component: NoteWriteView, meta: { requiresAuth: true } },
  { path: "/notes/:noteId/edit", name: "note-edit", component: NoteWriteView, meta: { requiresAuth: true } },
  { path: "/mypage", name: "mypage", component: MyPageView, meta: { requiresAuth: true } },

  { path: "/login", name: "login", component: LoginView, meta: { guestOnly: true }},
  { path: "/signup", name: "signup", component: SignupView, meta: { guestOnly: true } },
  { path: "/find-id", name: "find-id", component: FindIdView, meta: { guestOnly: true } },
  { path: "/reset-password", name: "reset-password", component: ResetPasswordView, meta: { guestOnly: true } },

  { path: "/404", name: "not-found", component: NotFoundView },
  { path: "/:pathMatch(.*)*", redirect: "/404" }
]

const router = createRouter({
  history: createWebHistory(),
  routes,

  // 페이지 이동 시 항상 스크롤을 맨 위로
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach(async (to) => {
  const authStore = useAuthStore()

  if (!authStore.initialized) {
    await authStore.fetchMe()
  }

  // 이미 로그인한 상태에서 로그인 관련 페이지 접근 시 홈으로 이동
  if (to.meta.guestOnly && authStore.isLogin) {
    alert('이미 로그인 한 상태입니다')
    return "/"
  }

  //로그인이 필요한 페이지 이동 시 로그인으로 이동
  if (to.meta.requiresAuth && !authStore.isLogin) {
    alert('로그인이 필요한 페이지입니다 로그인 페이지로 이동합니다')
    return "/login"
  }
})

export default router