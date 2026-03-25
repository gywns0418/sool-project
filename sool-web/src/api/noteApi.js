import api from './apiClient'

export const getNoteList = (drinkId, params) => {
  return api.get(`/drinks/${drinkId}/notes`, { params })
}

export const getNoteDetail = (noteId) => {
  return api.get(`/notes/${noteId}`)
}
