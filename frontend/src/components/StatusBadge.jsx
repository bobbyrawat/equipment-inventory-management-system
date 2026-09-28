const labels = { IN_USE: 'In use', UNDER_MAINTENANCE: 'Maintenance', AVAILABLE: 'Available', RETIRED: 'Retired', PENDING: 'Pending', APPROVED: 'Approved', REJECTED: 'Rejected', COMPLETED: 'Completed' }
export default function StatusBadge({ status }) {
  const tone = String(status || '').toLowerCase().replaceAll('_', '-')
  return <span className={`status-badge status-${tone}`}>{labels[status] || status || '—'}</span>
}
