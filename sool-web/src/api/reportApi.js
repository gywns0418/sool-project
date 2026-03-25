import api from '@/api/apiClient'

export const getReportReasons = () => {
  return api.get('/report/reasons')
}

export const insertReport = (data) => {
  return api.post('/report', data)
}