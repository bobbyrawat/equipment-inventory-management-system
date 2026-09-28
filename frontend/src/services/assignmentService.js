import api from './api'
const path = '/assignments'
export const getAssignments = () => api.get(path).then(({ data }) => data)
export const getAssignment = (id) => api.get(`${path}/${id}`).then(({ data }) => data)
export const createAssignment = (body) => api.post(path, body).then(({ data }) => data)
