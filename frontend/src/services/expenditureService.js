import api from './api'
const path = '/expenditures'
export const getExpenditures = () => api.get(path).then(({ data }) => data)
export const getExpenditure = (id) => api.get(`${path}/${id}`).then(({ data }) => data)
export const createExpenditure = (body) => api.post(path, body).then(({ data }) => data)
