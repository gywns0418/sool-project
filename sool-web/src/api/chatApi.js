import api from "./apiClient"

export const chatRoomList = () => {
  return api.get("/chat")
}