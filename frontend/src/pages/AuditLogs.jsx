import { useEffect, useState } from 'react'
import { getAuditLogs } from '../services/auditService'
import { errorMessage } from '../services/api'
import DataTable from '../components/DataTable'
import Loading from '../components/Loading'
export default function AuditLogs() {
  const [rows, setRows] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [refresh, setRefresh] = useState(0)
  useEffect(() => {
    let active = true
    getAuditLogs().then((data) => { if (active) setRows(data) }).catch((caught) => { if (active) setError(errorMessage(caught)) }).finally(() => { if (active) setLoading(false) })
    return () => { active = false }
  }, [refresh])
  function reload() { setLoading(true); setError(''); setRefresh((value) => value + 1) }
  if (loading) return <Loading label="Loading audit history" />
  return <section className="resource-page"><div className="resource-toolbar"><div><p>Read-only history returned by the audit service.</p><span className="resource-count">{rows.length} events</span></div><button className="button button-quiet" onClick={reload}>Refresh</button></div><DataTable rows={rows} loading={false} error={error} onRetry={reload} searchable emptyTitle="No audit events" emptyText="Audit records will appear here after activity is recorded." columns={[{ key: 'timestamp', label: 'Timestamp', render: (row) => row.timestamp ? new Date(row.timestamp).toLocaleString() : '—' }, { key: 'username', label: 'User' }, { key: 'action', label: 'Action' }, { key: 'entityType', label: 'Entity type' }, { key: 'entityId', label: 'Entity ID' }, { key: 'description', label: 'Description' }]} /></section>
}
