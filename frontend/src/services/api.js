import axios from 'axios'

const api = axios.create({ baseURL: import.meta.env.VITE_API_BASE_URL, headers: { 'Content-Type': 'application/json' } })

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('inventory.token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use((response) => response, (error) => {
  if (error.response?.status === 401) {
    localStorage.removeItem('inventory.token')
    localStorage.removeItem('inventory.auth')
    if (window.location.pathname !== '/login') window.location.assign('/login')
  }
  return Promise.reject(error)
})

export function errorMessage(error) {
  const status = error.response?.status
  const body = error.response?.data
  const backendMessage = body?.message || body?.detail || body?.error
  if (backendMessage) return backendMessage
  if (body?.errors && typeof body.errors === 'object') return Object.values(body.errors).flat().join(' ')
  if (!error.response) return 'Cannot reach the inventory server. Check your connection and try again.'
  return ({ 400: 'The request was invalid. Review the entered values.', 401: 'Your session has expired. Sign in again.', 403: 'You do not have permission to perform this action.', 404: 'The requested record could not be found.', 409: 'This change conflicts with existing data.', 500: 'The server encountered an error. Try again later.' })[status] || `Request failed (${status}).`
}

export default api
