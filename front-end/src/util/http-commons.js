import axios from 'axios'

const VITE_APP_API_URL = import.meta.env.VITE_APP_API_URL

const localAxios = () => {
  const instance = axios.create({
    baseURL: `${VITE_APP_API_URL}/api`,
    headers: {
      'Content-Type': 'application/json;charset=utf-8'
    }
  })

  return instance
}

export { localAxios }
