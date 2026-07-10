/*
 * Axios instance config.
 * Base URL points to /api, proxied to backend by Vite in dev mode.
 */
import axios from 'axios'

const http = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
})

export default http
