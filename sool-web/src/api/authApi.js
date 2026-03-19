import api from "./apiClient"


export const checkLoginId = (params) => {
  return api.get("/auth/check-login-id",{params})
}

export const sendEmailCode = (params) => {
  return api.post("/auth/email/send-code", params)
}

export const resendEmailCode = (params) => {
  return api.post("/auth/email/resend-code", params)
}

export const verifyEmailCode = (params) => {
  return api.post("/auth/email/verify-code", params)
}

export const signUp = (params) => {
  return api.post("/auth/signup", params)
}
