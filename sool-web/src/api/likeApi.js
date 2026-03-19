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

export const getNoteLike = (noteId) => {
  return api.get(`/notes/${noteId}/like`)
}

export const insertNoteLike = (noteId) => {
  return api.post(`/notes/${noteId}/like`)
}

export const deleteNoteLike = (noteId) => {
  return api.delete(`/notes/${noteId}/like`)
}