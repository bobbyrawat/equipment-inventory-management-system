import api from './api'
const path = '/transfers'
export const getTransfers = () => api.get(path).then(({ data }) => data)
export const getTransfer = (id) => api.get(`${path}/${id}`).then(({ data }) => data)
export const createTransfer = (body) => api.post(path, body).then(({ data }) => data)
export const updateTransferStatus = (id, status) => api.patch(`${path}/${id}/status`, { status }).then(({ data }) => data)
