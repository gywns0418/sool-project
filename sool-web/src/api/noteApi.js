import api from './apiClient'

//테이스팅 노트

export const getNoteList = (drinkId, params) => {
  return api.get(`/drinks/${drinkId}/notes`, { params })
}

export const getNoteDetail = (noteId) => {
  return api.get(`/notes/${noteId}`)
}

export const getNoteWriteForm = (drinkId) => {
  return api.get(`/notes/write/${drinkId}`)
}
/* **************************************** */

//댓글

export const getCommentList = (noteId) => {
  return api.get(`/notes/${noteId}/comments`)
}

export const insertComment = (params) => {
  return api.post('/comments', params)
}