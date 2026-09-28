import { useMemo } from 'react'
import EntityManager from '../components/EntityManager'
import { useAuth } from '../context/useAuth'
import * as branches from '../services/branchService'
const baseConfig = {
  singular: 'Branch', plural: 'Branches', description: 'View branch locations and manage organization sites.',
  list: branches.getBranches, detail: branches.getBranch, create: branches.createBranch, update: branches.updateBranch, remove: branches.deleteBranch,
  fields: [
    { key: 'name', label: 'Branch name', required: true, maxLength: 120 },
    { key: 'location', label: 'Location', required: true, maxLength: 200 },
    { key: 'description', label: 'Description', type: 'textarea', maxLength: 1000, nullable: true, full: true },
  ],
  emptyText: 'No branch records are visible for this account.',
}
export default function Branches() {
  const { user } = useAuth()
  const config = useMemo(() => ({ ...baseConfig, canCreate: user?.role === 'ADMIN', canEdit: user?.role === 'ADMIN', canDelete: user?.role === 'ADMIN' }), [user?.role])
  return <EntityManager config={config} />
}
