import api from "./apiClient"

//댓글 정보
export function getComments(noteId){
  return api.get(`/notes/${noteId}/comments`)
}

//댓글 작성
export function createComment(noteId, data) {
  return api.post(`/notes/${noteId}/comments`, data)
}

//대댓글 작성
export function createReply(noteId, data) {
  return api.post(`/notes/${noteId}/comments/reply`, data)
}

//댓글 수정
export function updateCommentApi(commentId, data) {
  return api.post(`/comments/${commentId}/update`, data)
}

//댓글 삭제
export function deleteCommentApi(commentId) {
  return api.post(`/comments/${commentId}/delete`)
}