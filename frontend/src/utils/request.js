import axios from 'axios'

const request = axios.create({
  baseURL: 'http://localhost:8081',
  timeout: 5000
})

request.interceptors.request.use(
  config => {
    const userStr = localStorage.getItem('user')
    if (userStr) {
      const user = JSON.parse(userStr)
      // 只设置用户ID作为Authorization，避免中文字符问题
      config.headers['Authorization'] = user.id
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    return Promise.reject(error)
  }
)

export default request
