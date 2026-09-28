import { AlertTriangle } from 'lucide-react'
export default function ErrorMessage({ message, onRetry }) {
  if (!message) return null
  return <div className="error-message" role="alert"><AlertTriangle size={18} /><span>{message}</span>{onRetry && <button className="text-button" onClick={onRetry}>Try again</button>}</div>
}
