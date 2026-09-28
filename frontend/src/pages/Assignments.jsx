import EntityManager from '../components/EntityManager'
import { getBranches } from '../services/branchService'
import { getEquipment } from '../services/equipmentService'
import * as assignments from '../services/assignmentService'
const config = {
  singular: 'Assignment', plural: 'Assignments', description: 'Allocate available equipment to a user at its branch.',
  list: assignments.getAssignments, detail: assignments.getAssignment, create: assignments.createAssignment,
  lookups: [
    { key: 'equipment', load: () => getEquipment().then((rows) => rows.map((item) => ({ value: item.id, label: `${item.name} · ${item.category} (${item.unit})` }))) },
    { key: 'branches', load: () => getBranches().then((rows) => rows.map((item) => ({ value: item.id, label: item.name }))) },
  ],
  fields: [
    { key: 'equipmentId', label: 'Equipment', type: 'select', lookup: 'equipment', required: true },
    { key: 'branchId', label: 'Branch', type: 'select', lookup: 'branches', required: true },
    { key: 'assignedToId', label: 'Assigned user ID', type: 'number', min: 1, step: 1, required: true, help: 'The backend requires an existing user at the selected branch.' },
    { key: 'quantity', label: 'Quantity', type: 'number', min: 1, step: 1, required: true },
    { key: 'assignmentDate', label: 'Assignment date', type: 'date', required: true },
    { key: 'remarks', label: 'Remarks', type: 'textarea', maxLength: 2000, nullable: true, full: true },
  ],
  columns: [{ key: 'id', label: 'ID' }, { key: 'assignmentDate', label: 'Date' }, { key: 'equipmentId', label: 'Equipment' }, { key: 'branchId', label: 'Branch' }, { key: 'assignedToId', label: 'Assigned user' }, { key: 'quantity', label: 'Quantity' }],
  emptyText: 'No equipment assignments have been recorded.',
}
export default function Assignments() { return <EntityManager config={config} /> }
