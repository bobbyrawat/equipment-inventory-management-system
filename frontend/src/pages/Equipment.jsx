import EntityManager from '../components/EntityManager'
import { useAuth } from '../context/useAuth'
import { getBranches } from '../services/branchService'
import * as equipment from '../services/equipmentService'
const config = {
  singular: 'Equipment', plural: 'Equipment', description: 'Catalog and maintain equipment stock records.', search: equipment.searchEquipment,
  list: equipment.getEquipment, detail: equipment.getEquipmentItem, create: equipment.createEquipment, update: equipment.updateEquipment, remove: equipment.deleteEquipment,
  lookups: [{ key: 'branches', load: () => getBranches().then((rows) => rows.map((item) => ({ value: item.id, label: `${item.name} · ${item.location}` }))) }],
  fields: [
    { key: 'name', label: 'Equipment name', required: true, maxLength: 160 },
    { key: 'category', label: 'Category', required: true, maxLength: 100 },
    { key: 'description', label: 'Description', type: 'textarea', maxLength: 2000, full: true, nullable: true },
    { key: 'quantity', label: 'Quantity', type: 'number', required: true, min: 0, step: 1, lockOnEdit: true, help: 'Stock changes are recorded through purchase, transfer, assignment, and expenditure operations.' },
    { key: 'unit', label: 'Unit', required: true, maxLength: 40 },
    { key: 'status', label: 'Status', type: 'select', required: true, status: true, options: ['AVAILABLE', 'IN_USE', 'UNDER_MAINTENANCE', 'RETIRED'].map((value) => ({ value, label: value.replaceAll('_', ' ') })) },
    { key: 'branchId', label: 'Branch', type: 'select', required: true, lookup: 'branches', lockOnEdit: true },
  ],
  emptyText: 'Add an equipment record to begin tracking stock in your branch.',
}
export default function Equipment() {
  const { user } = useAuth()
  return <EntityManager config={{ ...config, canEdit: true, canDelete: true, canCreate: Boolean(user) }} />
}
