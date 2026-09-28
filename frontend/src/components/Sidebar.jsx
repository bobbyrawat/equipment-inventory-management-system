import { Activity, ArrowLeftRight, Boxes, Building2, ClipboardList, LayoutDashboard, PackageSearch, ReceiptText, Users, X } from 'lucide-react'
import { NavLink } from 'react-router-dom'
import { useAuth } from '../context/useAuth'
const links = [
  { to: '/dashboard', label: 'Overview', icon: LayoutDashboard },
  { to: '/equipment', label: 'Equipment', icon: Boxes },
  { to: '/branches', label: 'Branches', icon: Building2 },
  { to: '/purchases', label: 'Purchases', icon: ReceiptText },
  { to: '/transfers', label: 'Transfers', icon: ArrowLeftRight },
  { to: '/assignments', label: 'Assignments', icon: ClipboardList },
  { to: '/expenditures', label: 'Expenditures', icon: PackageSearch },
  { to: '/users', label: 'Users', icon: Users, roles: ['ADMIN'] },
  { to: '/audit-logs', label: 'Audit trail', icon: Activity, roles: ['ADMIN'] },
]
export default function Sidebar({ open, onClose }) {
  const { user } = useAuth()
  return <><div className={`sidebar-scrim${open ? ' is-visible' : ''}`} onClick={onClose} /><aside className={`sidebar${open ? ' sidebar-open' : ''}`}><div className="side-label">Workspace</div><nav>{links.filter((link) => !link.roles || link.roles.includes(user?.role)).map(({ to, label, icon: Icon }) => <NavLink key={to} to={to} onClick={onClose} className={({ isActive }) => `nav-link${isActive ? ' nav-active' : ''}`}><Icon size={18} /><span>{label}</span></NavLink>)}</nav><div className="sidebar-bottom"><span className="connection-dot" />Connected to inventory API</div><button className="icon-button sidebar-close" onClick={onClose} aria-label="Close navigation"><X size={18} /></button></aside></>
}
