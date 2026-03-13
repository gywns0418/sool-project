import axios from "axios"

export const getHome = () => {
  return axios.get("/api/home")
}