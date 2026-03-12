import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import DrinkListView from '../views/DrinkListView.vue'
import DrinkDetailView from '../views/DrinkDetailView.vue'
import NoteDetailView from '../views/NoteDetailView.vue'
import NoteWriteView from '../views/NoteWriteView.vue'
import MyPageView from '../views/MyPageView.vue'
import LoginView from "../views/LoginView.vue"
import SignupView from "../views/SignupView.vue"

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
  routes
})
