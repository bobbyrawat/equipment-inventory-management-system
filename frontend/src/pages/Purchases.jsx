import EntityManager from '../components/EntityManager'
import { getBranches } from '../services/branchService'
import { getEquipment } from '../services/equipmentService'
import * as purchases from '../services/purchaseService'
const config = {
  singular: 'Purchase', plural: 'Purchases', description: 'Record incoming stock against its equipment and branch.',
  list: purchases.getPurchases, detail: purchases.getPurchase, create: purchases.createPurchase,
  lookups: [
    { key: 'equipment', load: () => getEquipment().then((rows) => rows.map((item) => ({ value: item.id, label: `${item.name} · ${item.category} (${item.unit})` }))) },
    { key: 'branches', load: () => getBranches().then((rows) => rows.map((item) => ({ value: item.id, label: item.name }))) },
  ],
  fields: [
    { key: 'equipmentId', label: 'Equipment', type: 'select', lookup: 'equipment', required: true },
    { key: 'branchId', label: 'Branch', type: 'select', lookup: 'branches', required: true },
    { key: 'quantity', label: 'Quantity received', type: 'number', min: 1, step: 1, required: true },
    { key: 'purchaseDate', label: 'Purchase date', type: 'date', required: true },
    { key: 'supplier', label: 'Supplier', maxLength: 160, nullable: true },
    { key: 'cost', label: 'Cost', type: 'number', min: 0, step: '0.01', nullable: true },
    { key: 'remarks', label: 'Remarks', type: 'textarea', maxLength: 2000, nullable: true, full: true },
  ],
  columns: [
    { key: 'id', label: 'ID' }, { key: 'purchaseDate', label: 'Date' }, { key: 'equipmentId', label: 'Equipment' },
    { key: 'branchId', label: 'Branch' }, { key: 'quantity', label: 'Quantity' }, { key: 'supplier', label: 'Supplier' }, { key: 'cost', label: 'Cost' },
  ],
  emptyText: 'Purchases recorded through this workspace will appear here.',
}
export default function Purchases() { return <EntityManager config={config} /> }
