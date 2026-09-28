import { useMemo } from 'react'
import EntityManager from '../components/EntityManager'
import { useAuth } from '../context/useAuth'
import { errorMessage } from '../services/api'
import { getBranches } from '../services/branchService'
import { getEquipment } from '../services/equipmentService'
import * as transfers from '../services/transferService'
import StatusBadge from '../components/StatusBadge'
const baseConfig = {
  singular: 'Transfer', plural: 'Transfers', description: 'Move equipment between branches and track each request.',
  list: transfers.getTransfers, detail: transfers.getTransfer, create: transfers.createTransfer,
  lookups: [
    { key: 'equipment', load: () => getEquipment().then((rows) => rows.map((item) => ({ value: item.id, label: `${item.name} · ${item.category} (${item.unit})` }))) },
    { key: 'branches', load: () => getBranches().then((rows) => rows.map((item) => ({ value: item.id, label: item.name }))) },
  ],
  fields: [
    { key: 'equipmentId', label: 'Equipment', type: 'select', lookup: 'equipment', required: true },
    { key: 'fromBranchId', label: 'From branch', type: 'select', lookup: 'branches', required: true },
    { key: 'toBranchId', label: 'To branch', type: 'select', lookup: 'branches', required: true },
    { key: 'quantity', label: 'Quantity', type: 'number', min: 1, step: 1, required: true },
    { key: 'transferDate', label: 'Transfer date', type: 'date', required: true },
    { key: 'remarks', label: 'Remarks', type: 'textarea', maxLength: 2000, nullable: true, full: true },
  ],
  columns: [
    { key: 'id', label: 'ID' }, { key: 'transferDate', label: 'Date' }, { key: 'equipmentId', label: 'Equipment' },
    { key: 'fromBranchId', label: 'From' }, { key: 'toBranchId', label: 'To' }, { key: 'quantity', label: 'Quantity' },
    { key: 'status', label: 'Status', render: (row) => <StatusBadge status={row.status} /> },
  ],
  rowAction: (row, { refresh, notify }) => {
    if (row.status === 'COMPLETED' || row.status === 'REJECTED') return null
    const options = row.status === 'PENDING' ? ['APPROVED', 'REJECTED'] : ['COMPLETED', 'REJECTED']
    return <select className="row-status-select" aria-label={`Update transfer ${row.id} status`} value="" onChange={async (event) => { const status = event.target.value; if (!status) return; try { await transfers.updateTransferStatus(row.id, status); notify(`Transfer ${row.id} marked ${status.toLowerCase()}.`); refresh() } catch (error) { notify(errorMessage(error), 'error') } }}><option value="">Update</option>{options.map((status) => <option value={status} key={status}>{status}</option>)}</select>
  },
  emptyText: 'Create a transfer request to track stock between branches.',
}
export default function Transfers() {
  const { user } = useAuth()
  const config = useMemo(() => ({
    ...baseConfig,
    fields: baseConfig.fields.map((field) => field.key === 'toBranchId' && user?.role !== 'ADMIN'
      ? { ...field, type: 'number', lookup: undefined, label: 'Destination branch ID', min: 1, step: 1, help: 'Enter the existing destination branch ID. Branch lookup is scoped to your own branch.' }
      : field),
  }), [user?.role])
  return <EntityManager config={config} />
}
