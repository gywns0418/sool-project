import api from "./apiClient"


export const checkLoginId = (params) => {
  return api.get("/auth/check-login-id",{params})
}

/* 인증번호 발송 */
export const sendEmailCode = (params) => {
  return api.post("/auth/email/send-code", params)
}

/* 인증번호 재발송 */
export const resendEmailCode = (params) => {
  return api.post("/auth/email/resend-code", params)
}

/* 인증번호 확인 */
export const verifyEmailCode = (params) => {
  return api.post("/auth/email/verify-code", params)
}

export const signUp = (params) => {
  return api.post("/auth/signup", params)
}



/* 아이디 찾기 */

/* 아이디 찾기 인증번호 발송 */
export const sendFindLoginIdEmailCode = (params) => {
  return api.post("/auth/email/id-send-code", params)
}

/* 아이디 찾기 */
export const findLoginId = (params) => {
  return api.post("/auth/find-id", params)
}



/* 비밀번호 재설정 */

/* 비밀번호 재설정 이메일 코드 전송 */
export function sendResetPasswordEmailCode(params) {
  return api.post('/auth/email/password-send-code', params)
}


/* 비밀번호 변경 */
export const resetPassword = (params) => {
  return api.post("/auth/reset-password", params)
}