import api from './api'
const path = '/equipment'
export const getEquipment = (params) => api.get(path, { params }).then(({ data }) => data)
export const getEquipmentItem = (id) => api.get(`${path}/${id}`).then(({ data }) => data)
export const searchEquipment = (q) => api.get(`${path}/search`, { params: { q } }).then(({ data }) => data)
export const createEquipment = (body) => api.post(path, body).then(({ data }) => data)
export const updateEquipment = (id, body) => api.put(`${path}/${id}`, body).then(({ data }) => data)
export const deleteEquipment = (id) => api.delete(`${path}/${id}`)
