import api from './api'
export const getAuditLogs = () => api.get('/audit-logs').then(({ data }) => data)
