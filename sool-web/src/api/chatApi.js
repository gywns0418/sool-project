import api from "./apiClient"

export const chatRoomList = () => {
  return api.get("/chat")
}

export const chatMessageList = (roomId) => {
  return api.get(`/chat/${roomId}/messages`)
}