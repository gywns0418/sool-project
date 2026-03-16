import api from "./apiClient"

export const getDrinkList = (params) => {
  return api.get("/drinks", { params })
}

export const getDrinkCategoryList = () => {
  return api.get("/drinks/categories")
}