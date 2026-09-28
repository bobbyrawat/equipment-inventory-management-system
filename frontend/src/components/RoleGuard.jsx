import { useAuth } from '../context/useAuth'
export default function RoleGuard({ roles, children, fallback = null }) {
  const { user } = useAuth()
  return roles.includes(user?.role) ? children : fallback
}
