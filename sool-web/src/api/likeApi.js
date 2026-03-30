import api from "./apiClient"
//주류 좋아요 정보0
export const getDrinkLike = (drinkId) => {
  return api.get(`/drinks/${drinkId}/like`)
}
//주류 좋아요 추가
export const insertDrinkLike = (drinkId) => {
  return api.post(`/drinks/${drinkId}/like`)
}
//주류 좋아요 삭제
export const deleteDrinkLike = (drinkId) => {
  return api.delete(`/drinks/${drinkId}/like`)
}
//노트 좋아요 정보
export const getNoteLike = (noteId) => {
  return api.get(`/notes/${noteId}/like`)
}
//노트 좋아요 추가
export const insertNoteLike = (noteId) => {
  return api.post(`/notes/${noteId}/like`)
}
//노트 좋아요 삭제
export const deleteNoteLike = (noteId) => {
  return api.delete(`/notes/${noteId}/like`)
}
