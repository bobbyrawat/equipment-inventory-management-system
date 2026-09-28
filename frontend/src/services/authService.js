import api from './api'
export const login = (request) => api.post('/auth/login', request).then(({ data }) => data)
export const register = (request) => api.post('/auth/register', request).then(({ data }) => data)
