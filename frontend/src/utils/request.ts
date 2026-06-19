import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
    baseURL: '/api',
    timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`
        }
        return config
    },
    error => Promise.reject(error)
)

// 响应拦截器
request.interceptors.response.use(
    res => {
        const { code, msg, data } = res.data
        if (code === 200) {
            return res
        } else if (code === 401) {
            ElMessage.error(msg || '授权过期，请重新登录')
            localStorage.removeItem('token')
            window.location.href = '/login'
            return Promise.reject(new Error(msg))
        } else {
            ElMessage.error(msg || '系统网络错误')
            return Promise.reject(new Error(msg))
        }
    },
    error => {
        ElMessage.error('网络连接异常或服务端无响应')
        return Promise.reject(error)
    }
)

export default request
