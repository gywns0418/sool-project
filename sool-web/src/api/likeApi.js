import api from "./apiClient"

export const getDrinkLike = (drinkId) => {
  return api.get(`/drinks/${drinkId}/like`)
}

export const insertDrinkLike = (drinkId) => {
  return api.post(`/drinks/${drinkId}/like`)
}

export const deleteDrinkLike = (drinkId) => {
  return api.delete(`/drinks/${drinkId}/like`)
}