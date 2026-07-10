/*
 * Axios instance config.
 * Base URL points to /api, proxied to backend by Vite in dev mode.
 * Request interceptor attaches JWT token from localStorage.
 */
import axios from 'axios'

const http = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('rg_token')
  if (token) {
    config.headers.Authorization = 'Bearer ' + token
  }
  return config
})

export default http
