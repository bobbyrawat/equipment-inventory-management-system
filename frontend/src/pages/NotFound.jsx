import { ArrowLeft, CircleHelp } from 'lucide-react'
import { Link } from 'react-router-dom'
export default function NotFound() {
  return <main className="not-found"><span className="not-found-icon"><CircleHelp size={24} /></span><span className="eyebrow">404 / NOT FOUND</span><h1>This page isn't on the inventory map.</h1><p>The route may have moved or the address may be incorrect.</p><Link className="button button-primary" to="/dashboard"><ArrowLeft size={17} /> Back to overview</Link></main>
}
