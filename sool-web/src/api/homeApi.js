import api from "./apiClient"

export const getHome = () => {
  return api.get("/home")
}