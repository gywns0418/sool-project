import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import DrinkListView from '../views/DrinkListView.vue'
import DrinkDetailView from '../views/DrinkDetailView.vue'
import NoteDetailView from '../views/NoteDetailView.vue'
import NoteWriteView from '../views/NoteWriteView.vue'
import MyPageView from '../views/MyPageView.vue'
import LoginView from "../views/auth/LoginView.vue"
import SignupView from "../views/auth/SignupView.vue"

const routes = [
  { path: '/', name: 'home', component: HomeView },
  { path: '/drinks', name: 'drink-list', component: DrinkListView },
  { path: '/drinks/:id', name: 'drink-detail', component: DrinkDetailView },
  { path: '/notes/:id', name: 'note-detail', component: NoteDetailView },
  { path: '/notes/write', name: 'note-write', component: NoteWriteView },
  { path: '/mypage', name: 'mypage', component: MyPageView },
  { path: "/login", name: "login", component: LoginView },
  { path: "/signup", name: "signup", component: SignupView }
]

export default createRouter({
  history: createWebHistory(),
  routes,

  //라우터 이동시 스크롤 위치 위에서 시작
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    }
    return { top: 0, left: 0 }
  }
})
