import axios from "axios"
import router from "@/router"
import { useAuthStore } from "@/stores/authStore"

const api = axios.create({
  baseURL: "/api",
  headers: {
    "Content-Type": "application/json"
  },
  withCredentials: true
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const authStore = useAuthStore()
    const status = error.response?.status
    const url = error.config?.url || ""

    if (status === 401) {
      authStore.clearUser()

      // 앱 시작 시 /auth/me 401은 정상적인 비로그인 상태이므로 alert 안 띄움
      if (url.includes("/auth/me")) {
        return Promise.reject(error)
      }

      if (router.currentRoute.value.path !== "/login") {
        alert("세션이 만료되었습니다. 다시 로그인해주세요.")
        router.push("/login")
      }
    }

    return Promise.reject(error)
  }
)

export default api