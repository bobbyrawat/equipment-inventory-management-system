import EntityManager from '../components/EntityManager'
import { getBranches } from '../services/branchService'
import * as users from '../services/userService'
const config = {
  singular: 'User', plural: 'Users', description: 'Administer account roles and branch membership.',
  list: users.getUsers, detail: users.getUser, create: users.createUser, update: users.updateUser, remove: users.deleteUser,
  lookups: [{ key: 'branches', load: () => getBranches().then((rows) => rows.map((item) => ({ value: item.id, label: `${item.name} · ${item.location}` }))) }],
  fields: [
    { key: 'name', label: 'Full name', required: true, maxLength: 120 },
    { key: 'username', label: 'Username', required: true, min: 3, maxLength: 60 },
    { key: 'email', label: 'Email', type: 'email', required: true, maxLength: 160 },
    { key: 'password', label: 'Password', type: 'password', required: true, min: 8, maxLength: 100, help: 'Required by the AdminCreateUserRequest on both create and update.' },
    { key: 'role', label: 'Role', type: 'select', required: true, options: ['ADMIN', 'BRANCH_MANAGER', 'LOGISTICS_OFFICER'].map((value) => ({ value, label: value.replaceAll('_', ' ') })) },
    { key: 'branchId', label: 'Branch', type: 'select', lookup: 'branches', nullable: true, placeholder: 'No branch' },
  ],
  columns: [{ key: 'id', label: 'ID' }, { key: 'name', label: 'Name' }, { key: 'username', label: 'Username' }, { key: 'email', label: 'Email' }, { key: 'role', label: 'Role' }, { key: 'branchId', label: 'Branch' }],
  emptyText: 'No user accounts were returned by the backend.',
}
export default function Users() { return <EntityManager config={config} /> }
