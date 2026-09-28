import { LogOut, Menu, Package, X } from 'lucide-react'
import { useAuth } from '../context/useAuth'
export default function Navbar({ onMenu, menuOpen }) {
  const { user, signOut } = useAuth()
  return <header className="topbar"><button className="icon-button mobile-menu" onClick={onMenu} aria-label={menuOpen ? 'Close navigation' : 'Open navigation'}>{menuOpen ? <X size={20} /> : <Menu size={20} />}</button><a className="brand-mark" href="/dashboard"><span><Package size={19} /></span><b>Fieldstock</b></a><div className="topbar-right"><span className="topbar-date">Inventory operations</span><span className="topbar-divider" /><span className="user-chip"><span className="avatar">{user?.name?.slice(0, 1)?.toUpperCase() || 'U'}</span><span className="user-label"><b>{user?.name}</b><small>{user?.role?.replaceAll('_', ' ')}</small></span></span><button className="icon-button logout-button" onClick={signOut} title="Sign out" aria-label="Sign out"><LogOut size={18} /></button></div></header>
}
