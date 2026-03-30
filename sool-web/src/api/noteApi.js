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

export function createNote(params) {
  return api.post('/notes/writeform', params)
}

export const getNoteUpdateForm = (noteId) => {
  return api.get(`/notes/edit/${noteId}`)
}

export function updateNote(noteId, params) {
  return api.post(`/notes/edit/${noteId}`, params)
}

export function deleteNote(noteId){
  return api.post(`/notes/delete/${noteId}`)
}


/* **************************************** */

//댓글

export const getCommentList = (noteId) => {
  return api.get(`/notes/${noteId}/comments`)
}

export const insertComment = (params) => {
  return api.post('/comments', params)
}