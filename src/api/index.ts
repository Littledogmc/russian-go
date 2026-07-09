/*
 * axios 实例配置
 * 统一管理后端 API 的基础路径和请求/响应拦截
 * 开发环境通过 Vite proxy 转发到 localhost:8080
 */
import axios from 'axios'

const http = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
})

export default http
