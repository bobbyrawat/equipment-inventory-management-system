import api from './api'
const path = '/purchases'
export const getPurchases = (params) => api.get(path, { params }).then(({ data }) => data)
export const getPurchase = (id) => api.get(`${path}/${id}`).then(({ data }) => data)
export const createPurchase = (body) => api.post(path, body).then(({ data }) => data)
