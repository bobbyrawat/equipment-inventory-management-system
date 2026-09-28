import api from './api'
const path = '/users'
export const getUsers = () => api.get(path).then(({ data }) => data)
export const getUser = (id) => api.get(`${path}/${id}`).then(({ data }) => data)
export const createUser = (body) => api.post(path, body).then(({ data }) => data)
export const updateUser = (id, body) => api.put(`${path}/${id}`, body).then(({ data }) => data)
export const deleteUser = (id) => api.delete(`${path}/${id}`)
