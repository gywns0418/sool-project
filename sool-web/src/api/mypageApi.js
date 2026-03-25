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