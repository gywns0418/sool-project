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
    },

    clearUser() {
      this.user = null
    },

    //로그인
    async login(loginId, password) {
      try {
        const res = await api.post("/auth/login", {
          loginId,
          password
        })

        this.user = res.data
        return res.data
      } catch (error) {
        this.user = null
        throw error
      }
    },

    //로그아웃
    async logout() {
      try {
        await api.post("/auth/logout")
      } finally {
        this.user = null
      }
    },

    //로그인 시 확인
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