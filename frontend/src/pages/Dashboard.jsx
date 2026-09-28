import { useEffect, useState } from 'react'
import { ArrowDownLeft, ArrowLeftRight, ArrowUpRight, Boxes, Building2, CircleAlert, Clock3, PackageCheck, RefreshCw } from 'lucide-react'
import { getDashboard } from '../services/dashboardService'
import { errorMessage } from '../services/api'
import Loading from '../components/Loading'
import ErrorMessage from '../components/ErrorMessage'
import StatCard from '../components/StatCard'
import DataTable from '../components/DataTable'
export default function Dashboard() {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [refresh, setRefresh] = useState(0)
  useEffect(() => {
    let active = true
    getDashboard().then((result) => { if (active) setData(result) }).catch((caught) => { if (active) setError(errorMessage(caught)) }).finally(() => { if (active) setLoading(false) })
    return () => { active = false }
  }, [refresh])
  function reload() { setLoading(true); setError(''); setRefresh((value) => value + 1) }
  if (loading) return <Loading label="Loading operational overview" />
  if (error) return <div className="dashboard-error"><ErrorMessage message={error} onRetry={reload} /></div>
  const balances = data?.inventory || []
  return <section className="dashboard-page"><div className="dashboard-intro"><div><span className="eyebrow">LIVE INVENTORY POSITION</span><p>Operational snapshot as of <strong>{data?.asOfDate || '—'}</strong></p></div><button className="icon-button" onClick={reload} title="Refresh overview" aria-label="Refresh overview"><RefreshCw size={17} /></button></div><div className="stats-grid"><StatCard label="Equipment types" value={data?.equipmentTypes} icon={Boxes} accent="teal" /><StatCard label="Branches" value={data?.branches} icon={Building2} accent="blue" /><StatCard label="Units in stock" value={data?.totalUnits} icon={PackageCheck} accent="green" /><StatCard label="Low-stock items" value={data?.lowStockItems} icon={CircleAlert} accent="amber" /><StatCard label="Pending transfers" value={data?.pendingTransfers} icon={Clock3} accent="rose" /></div><div className="section-title-row"><div><span className="eyebrow">STOCK LEDGER</span><h2>Inventory balances</h2></div><span className="section-count">{balances.length} rows</span></div><DataTable rows={balances} searchable emptyTitle="No inventory balances" emptyText="The dashboard will show stock as equipment records become available." columns={[{ key: 'equipmentName', label: 'Equipment', render: (row) => <div className="equipment-cell"><b>{row.equipmentName}</b><small>{row.category}</small></div> }, { key: 'branchName', label: 'Branch' }, { key: 'openingBalance', label: 'Opening' }, { key: 'purchases', label: 'Purchases', render: (row) => <span className="movement-positive"><ArrowDownLeft size={14} />{row.purchases}</span> }, { key: 'transferIn', label: 'Transfer in', render: (row) => <span className="movement-positive"><ArrowDownLeft size={14} />{row.transferIn}</span> }, { key: 'transferOut', label: 'Transfer out', render: (row) => <span className="movement-negative"><ArrowUpRight size={14} />{row.transferOut}</span> }, { key: 'assigned', label: 'Assigned' }, { key: 'expended', label: 'Expended' }, { key: 'closingBalance', label: 'Closing', render: (row) => <strong className="balance-value">{row.closingBalance}</strong> }]} />{balances.length > 0 && <div className="movement-footnote"><ArrowLeftRight size={15} /> Movement values are returned directly by the backend balance ledger.</div>}</section>
}
