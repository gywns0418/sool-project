import { createRouter, createWebHistory } from "vue-router"

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
  { path: "/write/:drinkId", name: "note-write", component: NoteWriteView },
  { path: "/notes/:noteId/edit", name: "note-edit", component: NoteWriteView },
  { path: "/mypage", name: "mypage", component: MyPageView },
  { path: "/login", name: "login", component: LoginView },
  { path: "/signup", name: "signup", component: SignupView },
  { path: "/find-id", name: "find-id", component: FindIdView },
  { path: "/reset-password", name: "reset-password", component: ResetPasswordView },
  { path: "/:pathMatch(.*)*", name: "not-found", component: NotFoundView }
]

const router = createRouter({
  history: createWebHistory(),
  routes,

  // 페이지 이동 시 항상 스크롤을 맨 위로
  scrollBehavior() {
    return { top: 0 }
  }
})


export default router