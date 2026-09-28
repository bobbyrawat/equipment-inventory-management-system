import { useCallback, useState } from 'react'
import { Check, CircleAlert, X } from 'lucide-react'
import { ToastContext } from './ToastStore'

export function ToastProvider({ children }) {
  const [items, setItems] = useState([])
  const dismiss = useCallback((id) => setItems((current) => current.filter((item) => item.id !== id)), [])
  const notify = useCallback((message, type = 'success') => {
    const id = Date.now() + Math.random()
    setItems((current) => [...current, { id, message, type }])
    window.setTimeout(() => dismiss(id), 4500)
  }, [dismiss])
  return <ToastContext.Provider value={notify}>{children}<div className="toast-stack" aria-live="polite">{items.map((item) => <div className={`toast toast-${item.type}`} key={item.id}><span className="toast-icon">{item.type === 'success' ? <Check size={17} /> : <CircleAlert size={17} />}</span><span>{item.message}</span><button className="icon-button toast-close" onClick={() => dismiss(item.id)} aria-label="Dismiss notification"><X size={16} /></button></div>)}</div></ToastContext.Provider>
}
