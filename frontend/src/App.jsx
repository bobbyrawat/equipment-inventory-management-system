import { Navigate, Route, Routes } from 'react-router-dom'
import Layout from './components/Layout'
import ProtectedRoute from './components/ProtectedRoute'
import Assignments from './pages/Assignments'
import AuditLogs from './pages/AuditLogs'
import Branches from './pages/Branches'
import Dashboard from './pages/Dashboard'
import Equipment from './pages/Equipment'
import Expenditures from './pages/Expenditures'
import Login from './pages/Login'
import NotFound from './pages/NotFound'
import Purchases from './pages/Purchases'
import Register from './pages/Register'
import Transfers from './pages/Transfers'
import Users from './pages/Users'

export default function App() {
  return <Routes>
    <Route path="/login" element={<Login />} />
    <Route path="/register" element={<Register />} />
    <Route element={<ProtectedRoute />}>
      <Route element={<Layout />}>
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={<Dashboard />} />
        <Route path="equipment" element={<Equipment />} />
        <Route path="branches" element={<Branches />} />
        <Route path="purchases" element={<Purchases />} />
        <Route path="transfers" element={<Transfers />} />
        <Route path="assignments" element={<Assignments />} />
        <Route path="expenditures" element={<Expenditures />} />
        <Route element={<ProtectedRoute roles={['ADMIN']} />}>
          <Route path="users" element={<Users />} />
          <Route path="audit-logs" element={<AuditLogs />} />
        </Route>
      </Route>
    </Route>
    <Route path="*" element={<NotFound />} />
  </Routes>
}
