import api from "./apiClient"

export function uploadImage(formData) {
  return api.post('/image/upload', formData,{
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function deleteImage(fileKey) {
  return api.post('/image/delete', {fileKey})
}