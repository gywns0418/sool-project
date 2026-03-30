import api from "./apiClient"

export const getMySidebarInfo = () => {
  return api.get('/mypage/sidebar')
}

export const updateMyProfile = (data) => {
  return api.put('/mypage/profile', data)
}

export const updateMyPassword = (data) => {
  return api.put('/mypage/password', data)
}

export const checkLoginIdDuplicateApi = (loginId) => {
  return api.get('/user/check-loginId', {
    params: { loginId }
  })
}

export const checkEmailDuplicateApi = (email) => {
  return api.get('/user/check-email', {
    params: { email }
  })
}

export const getMyTastingNote = () => {
  return api.get('/mypage/myTastingNote')
}

//마이페이지 좋아요
export const getMyLikes = () => {
  return api.get('/mypage/myLikes')
}

//마이페이지 좋아요 삭제
export function deleteMyLike(data) {
  return api.post('/likes/delete',data)
}

//마이페이지 신고목록
export const getMyReports = () => {
  return api.get('/report/myReport')
}