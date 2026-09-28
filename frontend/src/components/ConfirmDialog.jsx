import { AlertTriangle } from 'lucide-react'
import Modal from './Modal'
export default function ConfirmDialog({ open, onClose, onConfirm, title = 'Confirm deletion', message, busy = false }) {
  return <Modal open={open} onClose={onClose} title={title}><div className="confirm-content"><span className="confirm-mark"><AlertTriangle size={20} /></span><p>{message}</p></div><footer className="modal-actions"><button className="button button-quiet" onClick={onClose} disabled={busy}>Cancel</button><button className="button button-danger" onClick={onConfirm} disabled={busy}>{busy ? 'Deleting…' : 'Delete record'}</button></footer></Modal>
}
