import { useState } from 'react'
import { ArrowRight, Boxes, ShieldCheck } from 'lucide-react'
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/useAuth'
import { errorMessage } from '../services/api'
import ErrorMessage from '../components/ErrorMessage'
export default function Login() {
  const { user, signIn, loading } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [form, setForm] = useState({ usernameOrEmail: '', password: '' })
  const [error, setError] = useState('')
  if (user) return <Navigate to="/dashboard" replace />
  async function submit(event) {
    event.preventDefault(); setError('')
    try { await signIn(form); navigate(location.state?.from?.pathname || '/dashboard', { replace: true }) }
    catch (caught) { setError(errorMessage(caught)) }
  }
  return <main className="auth-page"><div className="auth-art"><div className="auth-orbit orbit-one" /><div className="auth-orbit orbit-two" /><div className="auth-art-content"><span className="auth-brand-icon"><Boxes size={24} /></span><span className="eyebrow">FIELDSTOCK / OPERATIONS</span><h1>Control your<br />equipment flow.</h1><p>A single operational view for inventory, movement, and accountability across every branch.</p><div className="auth-art-note"><ShieldCheck size={17} /> Secured with your organization's access controls</div></div><span className="auth-art-index">01 / INVENTORY CONTROL</span></div><div className="auth-form-side"><form className="auth-card" onSubmit={submit}><div className="auth-kicker">WELCOME BACK</div><h2>Sign in</h2><p className="auth-subtitle">Use your account credentials to continue.</p><ErrorMessage message={error} /><label className="form-field" htmlFor="usernameOrEmail"><span>Username or email <i>*</i></span><input id="usernameOrEmail" name="usernameOrEmail" value={form.usernameOrEmail} onChange={(event) => setForm({ ...form, usernameOrEmail: event.target.value })} required autoComplete="username" /></label><label className="form-field" htmlFor="password"><span>Password <i>*</i></span><input id="password" name="password" type="password" value={form.password} onChange={(event) => setForm({ ...form, password: event.target.value })} required autoComplete="current-password" /></label><button className="button button-primary auth-submit" type="submit" disabled={loading}>{loading ? 'Signing in…' : 'Sign in'} <ArrowRight size={17} /></button><p className="auth-switch">New to Fieldstock? <Link to="/register">Create an account</Link></p></form><span className="auth-foot">EQUIPMENT INVENTORY MANAGEMENT</span></div></main>
}
