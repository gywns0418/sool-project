import api from './apiClient'

export const getNoteList = (drinkId, params) => {
  return api.get(`/drinks/${drinkId}/notes`, { params })
}

export const getNoteDetail = (noteId) => {
  return api.get(`/notes/${noteId}`)
}

export const getNoteFlavors = (noteId) => {
  return api.get(`/notes/${noteId}/flavors`)
}

export const getNoteComments = (noteId) => {
  return api.get(`/notes/${noteId}/comments`)
}

export const createNoteComment = (noteId, data) => {
  return api.post(`/notes/${noteId}/comments`, data)
}

export const likeNote = (noteId) => {
  return api.post(`/notes/${noteId}/like`)
}

export const unlikeNote = (noteId) => {
  return api.delete(`/notes/${noteId}/like`)
}

export const reportNoteApi = (noteId) => {
  return api.post(`/notes/${noteId}/report`)
}