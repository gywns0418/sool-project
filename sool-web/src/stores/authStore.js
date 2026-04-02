import { defineStore } from "pinia"
import api from "@/api/apiClient"

export const useAuthStore = defineStore("auth", {
  state: () => ({
    user: null,
    initialized: false
  }),

  getters: {
    isLogin: (state) => !!state.user
  },

  actions: {
    setUser(user) {
      this.user = user
      this.initialized = true
    },

    clearUser() {
      this.user = null
      this.initialized = true
    },

    //로그인
    async login(loginId, password) {
      try {
        const res = await api.post("/auth/login", {
          loginId,
          password
        })

        this.user = res.data
        this.initialized = true
        return res.data
      } catch (error) {
        this.user = null
        this.initialized = true
        throw error
      }
    },

    //로그아웃
    async logout() {
      try {
        await api.post("/auth/logout")
      } finally {
        this.clearUser()
      }
    },

    //로그인 상태 확인
    async fetchMe() {
      try {
        const res = await api.get("/auth/me")
        this.user = res.data
      } catch (error) {
        this.user = null
      } finally {
        this.initialized = true
      }
    }
  }
})