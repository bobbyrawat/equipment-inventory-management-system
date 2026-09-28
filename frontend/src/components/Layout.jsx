import { useState } from 'react'
import { Outlet, useLocation } from 'react-router-dom'
import Navbar from './Navbar'
import Sidebar from './Sidebar'
const routeTitles = { dashboard: 'Overview', equipment: 'Equipment', branches: 'Branches', purchases: 'Purchases', transfers: 'Transfers', assignments: 'Assignments', expenditures: 'Expenditures', users: 'User access', 'audit-logs': 'Audit trail' }
export default function Layout() {
  const [menuOpen, setMenuOpen] = useState(false)
  const location = useLocation()
  const title = routeTitles[location.pathname.split('/')[1]] || 'Inventory'
  return <div className="app-shell"><Navbar menuOpen={menuOpen} onMenu={() => setMenuOpen((open) => !open)} /><Sidebar open={menuOpen} onClose={() => setMenuOpen(false)} /><main className="main-area"><div className="page-heading"><div><span className="eyebrow">EQUIPMENT INVENTORY</span><h1>{title}</h1></div><span className="heading-date">Operational workspace</span></div><Outlet /></main></div>
}
