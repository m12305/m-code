import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000,
  transformResponse: [
    (data) => {
      if (typeof data === 'string') {
        // JS MAX_SAFE_INTEGER = 9007199254740991 (16 digits)
        // MyBatis-Plus ASSIGN_ID generates 19-digit snowflake IDs
        // Wrap >=16 digit integers in quotes to preserve precision
        data = data.replace(/(?<=[:[,])\s*(-?\d{16,})(?=\s*[,\}\]])/g, '"$1"')
        try {
          return JSON.parse(data)
        } catch {
          return data
        }
      }
      return data
    },
  ],
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res.data
  },
  (error) => {
    if (error.response) {
      const status = error.response.status
      if (status === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        window.location.href = '/login'
        return
      }
      ElMessage.error(error.response.data?.message || '网络错误')
    } else {
      ElMessage.error('网络连接失败')
    }
    return Promise.reject(error)
  },
)

export default request
