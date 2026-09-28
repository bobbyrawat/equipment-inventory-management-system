import { useContext } from 'react'
import { ToastContext } from './ToastStore'
export function useToast() { return useContext(ToastContext) }
