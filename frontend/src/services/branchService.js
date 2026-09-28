import api from './api'
const path = '/branches'
export const getBranches = () => api.get(path).then(({ data }) => data)
export const getBranch = (id) => api.get(`${path}/${id}`).then(({ data }) => data)
export const createBranch = (body) => api.post(path, body).then(({ data }) => data)
export const updateBranch = (id, body) => api.put(`${path}/${id}`, body).then(({ data }) => data)
export const deleteBranch = (id) => api.delete(`${path}/${id}`)
