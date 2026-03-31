import api from "./apiClient"

//마이페이지 사이드바 정보
export const getMySidebarInfo = () => {
  return api.get('/mypage/sidebar')
}

//마이페이지 회원 기본 정보 수정
export const updateMyProfile = (data) => {
  return api.post('/mypage/profile', data)
}

//로그인 아이디 확인
export const checkLoginIdApi = (loginId) => {
  return api.get('/auth/check-login-id', {
    params: { loginId }
  })
}

//이메일 확인
export const checkEmailApi = (email) => {
  return api.get('/mypage/email/check-email', {
    params: { email }
  })
}

//비밀번호 확인 및 변경
export const updateMyPassword = (data) => {
  return api.post('/mypage/password', {
    currentPassword: data.currentPassword,
    newPassword: data.newPassword
  })
}


//마이페이지 내 테이스팅 노트
export const getMyTastingNote = () => {
  return api.get('/mypage/myTastingNote')
}

//마이페이지 내 테이스팅 노트 삭제
export function deleteMyTastingNote(noteId) {
  return api.post(`/notes/delete/${noteId}`)
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

//회원 탈퇴
export function deleteUser(){
  return api.post('/mypage/users/delete')
}