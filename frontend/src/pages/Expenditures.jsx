import EntityManager from '../components/EntityManager'
import { getBranches } from '../services/branchService'
import { getEquipment } from '../services/equipmentService'
import * as expenditures from '../services/expenditureService'
const config = {
  singular: 'Expenditure', plural: 'Expenditures', description: 'Record equipment written off or consumed from stock.',
  list: expenditures.getExpenditures, detail: expenditures.getExpenditure, create: expenditures.createExpenditure,
  lookups: [
    { key: 'equipment', load: () => getEquipment().then((rows) => rows.map((item) => ({ value: item.id, label: `${item.name} · ${item.category} (${item.unit})` }))) },
    { key: 'branches', load: () => getBranches().then((rows) => rows.map((item) => ({ value: item.id, label: item.name }))) },
  ],
  fields: [
    { key: 'equipmentId', label: 'Equipment', type: 'select', lookup: 'equipment', required: true },
    { key: 'branchId', label: 'Branch', type: 'select', lookup: 'branches', required: true },
    { key: 'quantity', label: 'Quantity', type: 'number', min: 1, step: 1, required: true },
    { key: 'expenditureDate', label: 'Expenditure date', type: 'date', required: true },
    { key: 'reason', label: 'Reason', type: 'textarea', maxLength: 500, required: true, full: true },
    { key: 'remarks', label: 'Remarks', type: 'textarea', maxLength: 2000, nullable: true, full: true },
  ],
  columns: [{ key: 'id', label: 'ID' }, { key: 'expenditureDate', label: 'Date' }, { key: 'equipmentId', label: 'Equipment' }, { key: 'branchId', label: 'Branch' }, { key: 'quantity', label: 'Quantity' }, { key: 'reason', label: 'Reason' }],
  emptyText: 'No expenditures have been recorded.',
}
export default function Expenditures() { return <EntityManager config={config} /> }
