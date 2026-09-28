import { useEffect, useState } from 'react'
import { ArrowLeft, ArrowRight, Boxes } from 'lucide-react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/useAuth'
import { getBranches } from '../services/branchService'
import { errorMessage } from '../services/api'
import ErrorMessage from '../components/ErrorMessage'
export default function Register() {
  const { signUp, loading } = useAuth()
  const navigate = useNavigate()
  const [branches, setBranches] = useState([])
  const [branchesError, setBranchesError] = useState('')
  const [form, setForm] = useState({ name: '', username: '', email: '', password: '', branchId: '' })
  const [error, setError] = useState('')
  useEffect(() => {
    if (!localStorage.getItem('inventory.token')) return
    getBranches().then(setBranches).catch((caught) => setBranchesError(errorMessage(caught)))
  }, [])
  async function submit(event) {
    event.preventDefault(); setError('')
    try { await signUp({ ...form, branchId: Number(form.branchId) }); navigate('/dashboard', { replace: true }) }
    catch (caught) { setError(errorMessage(caught)) }
  }
  return <main className="auth-page auth-page-register"><div className="auth-art"><div className="auth-orbit orbit-one" /><div className="auth-orbit orbit-two" /><div className="auth-art-content"><span className="auth-brand-icon"><Boxes size={24} /></span><span className="eyebrow">FIELDSTOCK / ACCESS</span><h1>Join the<br />operations desk.</h1><p>Set up your account to work with live equipment inventory and branch operations.</p><div className="auth-art-note"><span className="connection-dot" /> Live backend registration</div></div><span className="auth-art-index">02 / ACCOUNT ACCESS</span></div><div className="auth-form-side"><form className="auth-card register-card" onSubmit={submit}><Link className="back-link" to="/login"><ArrowLeft size={16} /> Back to sign in</Link><div className="auth-kicker">NEW ACCOUNT</div><h2>Register</h2><p className="auth-subtitle">Your account will be assigned to a branch.</p><ErrorMessage message={error} />{branchesError && <div className="inline-note">Branch lookup is unavailable for this session. Enter an existing branch ID.</div>}<div className="form-grid auth-grid"><label className="form-field form-field-full" htmlFor="name"><span>Full name <i>*</i></span><input id="name" required maxLength="120" value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} /></label><label className="form-field" htmlFor="username"><span>Username <i>*</i></span><input id="username" required minLength="3" maxLength="60" autoComplete="username" value={form.username} onChange={(event) => setForm({ ...form, username: event.target.value })} /></label><label className="form-field" htmlFor="email"><span>Email <i>*</i></span><input id="email" type="email" required maxLength="160" autoComplete="email" value={form.email} onChange={(event) => setForm({ ...form, email: event.target.value })} /></label><label className="form-field form-field-full" htmlFor="password"><span>Password <i>*</i></span><input id="password" type="password" required minLength="8" maxLength="100" autoComplete="new-password" value={form.password} onChange={(event) => setForm({ ...form, password: event.target.value })} /></label><label className="form-field form-field-full" htmlFor="branchId"><span>Branch <i>*</i></span>{branches.length ? <select id="branchId" required value={form.branchId} onChange={(event) => setForm({ ...form, branchId: event.target.value })}><option value="">Select a branch</option>{branches.map((branch) => <option key={branch.id} value={branch.id}>{branch.name} · {branch.location}</option>)}</select> : <input id="branchId" type="number" min="1" step="1" required value={form.branchId} onChange={(event) => setForm({ ...form, branchId: event.target.value })} placeholder="Existing branch ID" />}</label></div><button className="button button-primary auth-submit" type="submit" disabled={loading}>{loading ? 'Creating account…' : 'Create account'} <ArrowRight size={17} /></button></form><span className="auth-foot">EQUIPMENT INVENTORY MANAGEMENT</span></div></main>
}
