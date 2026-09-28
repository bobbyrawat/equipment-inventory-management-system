import { useState } from 'react'
import { login as loginRequest, register as registerRequest } from '../services/authService'
import { AuthContext } from './AuthStore'
const STORAGE_KEY = 'inventory.auth'
const TOKEN_KEY = 'inventory.token'

function readStoredAuth() {
  try { return JSON.parse(localStorage.getItem(STORAGE_KEY) || 'null') } catch { return null }
}

export function AuthProvider({ children }) {
  const [auth, setAuth] = useState(readStoredAuth)
  const [loading, setLoading] = useState(false)

  function acceptSession(response) {
    localStorage.setItem(TOKEN_KEY, response.token)
    localStorage.setItem(STORAGE_KEY, JSON.stringify(response))
    setAuth(response)
  }

  async function signIn(credentials) {
    setLoading(true)
    try { const response = await loginRequest(credentials); acceptSession(response); return response }
    finally { setLoading(false) }
  }

  async function signUp(request) {
    setLoading(true)
    try { const response = await registerRequest(request); acceptSession(response); return response }
    finally { setLoading(false) }
  }

  function signOut() {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(STORAGE_KEY)
    setAuth(null)
  }

  return <AuthContext.Provider value={{ auth, user: auth?.user || null, loading, signIn, signUp, signOut }}>{children}</AuthContext.Provider>
}
