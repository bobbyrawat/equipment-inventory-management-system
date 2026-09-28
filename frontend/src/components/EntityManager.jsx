import { useEffect, useState } from 'react'
import { Eye, Plus, RefreshCw, Search, Trash2 } from 'lucide-react'
import DataTable from './DataTable'
import Modal from './Modal'
import ConfirmDialog from './ConfirmDialog'
import StatusBadge from './StatusBadge'
import { useToast } from '../context/useToast'
import { errorMessage } from '../services/api'

function emptyForm(fields) {
  return Object.fromEntries(fields.map((field) => [field.key, field.defaultValue ?? '']))
}

function toRequest(fields, values) {
  return Object.fromEntries(fields.map((field) => {
    const value = values[field.key]
    if (field.type === 'number' && value !== '') return [field.key, Number(value)]
    if (field.type === 'number' && value === '') return [field.key, null]
    return [field.key, value === '' && field.nullable ? null : value]
  }))
}

function FormField({ field, value, onChange, options, disabled }) {
  const common = { id: field.key, name: field.key, value: value ?? '', required: field.required, disabled, onChange: (event) => onChange(field.key, event.target.value), min: field.min, max: field.max, maxLength: field.maxLength, step: field.step }
  return <label className={`form-field${field.full ? ' form-field-full' : ''}`} htmlFor={field.key}><span>{field.label}{field.required && <i> *</i>}</span>{field.type === 'textarea' ? <textarea {...common} rows={3} placeholder={field.placeholder} /> : field.type === 'select' ? <select {...common}><option value="">{field.placeholder || 'Select an option'}</option>{(field.options || options || []).map((option) => <option key={option.value} value={option.value}>{option.label}</option>)}</select> : <input {...common} type={field.type || 'text'} placeholder={field.placeholder} autoComplete={field.type === 'password' ? 'new-password' : 'off'} />}{field.help && <small>{field.help}</small>}</label>
}

export default function EntityManager({ config }) {
  const notify = useToast()
  const [rows, setRows] = useState([])
  const [lookups, setLookups] = useState({})
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [query, setQuery] = useState('')
  const [refresh, setRefresh] = useState(0)
  const [formOpen, setFormOpen] = useState(false)
  const [formValues, setFormValues] = useState(() => emptyForm(config.fields))
  const [editing, setEditing] = useState(null)
  const [saving, setSaving] = useState(false)
  const [detail, setDetail] = useState(null)
  const [detailLoading, setDetailLoading] = useState(false)
  const [deleteTarget, setDeleteTarget] = useState(null)
  const [deleting, setDeleting] = useState(false)

  useEffect(() => {
    let active = true
    const request = config.search && query.trim() ? config.search(query.trim()) : config.list()
    request.then((data) => { if (active) setRows(Array.isArray(data) ? data : []) })
      .catch((caught) => { if (active) { setError(errorMessage(caught)); setRows([]) } })
      .finally(() => { if (active) setLoading(false) })
    return () => { active = false }
  }, [config, query, refresh])
  useEffect(() => {
    let active = true
    Promise.all((config.lookups || []).map(async (lookup) => [lookup.key, await lookup.load()]))
      .then((entries) => { if (active) setLookups(Object.fromEntries(entries)) })
      .catch((caught) => { if (active) notify(errorMessage(caught), 'error') })
    return () => { active = false }
  }, [config, notify])

  const lookupForField = (field) => lookups[field.lookup] || []
  const columns = (() => {
    const base = config.columns || config.fields.filter((field) => field.table !== false).map((field) => ({ key: field.key, label: field.label }))
    const resolved = base.map((column) => ({ ...column, render: column.render || ((row) => {
      const value = row[column.key]
      const field = config.fields.find((item) => item.key === column.key)
      if (field?.type === 'select' && field.options) return field.options.find((option) => String(option.value) === String(value))?.label || value || '—'
      if (field?.lookup) return lookupForField(field).find((option) => String(option.value) === String(value))?.label || value || '—'
      if (field?.status) return <StatusBadge status={value} />
      if (field?.type === 'date') return value || '—'
      if (typeof value === 'number' && field?.currency) return new Intl.NumberFormat(undefined, { style: 'currency', currency: 'USD' }).format(value)
      return value ?? '—'
    }) }))
    resolved.push({ key: '_actions', label: 'Actions', render: (row) => <div className="row-actions"><button className="icon-button" onClick={() => openDetail(row.id)} title="View details" aria-label={`View record ${row.id}`}><Eye size={16} /></button>{config.update && config.canEdit !== false && <button className="icon-button" onClick={() => openEdit(row)} title="Edit record" aria-label={`Edit record ${row.id}`}>Edit</button>}{config.remove && config.canDelete !== false && <button className="icon-button icon-danger" onClick={() => setDeleteTarget(row)} title="Delete record" aria-label={`Delete record ${row.id}`}><Trash2 size={16} /></button>}{config.rowAction?.(row, { refresh: () => setRefresh((value) => value + 1), notify })}</div> })
    return resolved
  })()

  function refreshRows() {
    setLoading(true)
    setError('')
    setRefresh((value) => value + 1)
  }

  function openCreate() {
    setEditing(null)
    setFormValues(emptyForm(config.fields))
    setFormOpen(true)
  }

  async function openEdit(row) {
    setSaving(false)
    setError('')
    try {
      const current = config.detail ? await config.detail(row.id) : row
      setEditing(current)
      setFormValues({ ...emptyForm(config.fields), ...current, ...(config.editDefaults || {}) })
      setFormOpen(true)
    } catch (caught) { notify(errorMessage(caught), 'error') }
  }

  async function openDetail(id) {
    setDetailLoading(true)
    setDetail({ id })
    try { setDetail(await config.detail(id)) }
    catch (caught) { setDetail({ error: errorMessage(caught) }) }
    finally { setDetailLoading(false) }
  }

  async function submit(event) {
    event.preventDefault()
    setSaving(true)
    try {
      const body = toRequest(config.fields, formValues)
      if (editing) await config.update(editing.id, body)
      else await config.create(body)
      setFormOpen(false)
      notify(`${config.singular} ${editing ? 'updated' : 'created'}.`)
      refreshRows()
    } catch (caught) { notify(errorMessage(caught), 'error') }
    finally { setSaving(false) }
  }

  async function removeRecord() {
    setDeleting(true)
    try {
      await config.remove(deleteTarget.id)
      setDeleteTarget(null)
      notify(`${config.singular} deleted.`)
      refreshRows()
    } catch (caught) { notify(errorMessage(caught), 'error') }
    finally { setDeleting(false) }
  }

  const detailEntries = detail && !detail.error ? Object.entries(detail).filter(([key]) => key !== 'id') : []

  return <section className="resource-page">
    <div className="resource-toolbar"><div><p>{config.description}</p><span className="resource-count">{rows.length} {config.plural.toLowerCase()}</span></div><div className="toolbar-actions">{config.search && <label className="search-field resource-search"><Search size={17} /><input placeholder={config.searchPlaceholder || `Search ${config.plural.toLowerCase()}`} value={query} onChange={(event) => { setLoading(true); setError(''); setQuery(event.target.value) }} /></label>}<button className="icon-button refresh-button" onClick={refreshRows} aria-label="Refresh records" title="Refresh"><RefreshCw size={17} /></button>{config.create && config.canCreate !== false && <button className="button button-primary" onClick={openCreate}><Plus size={17} />{config.createLabel || `New ${config.singular}`}</button>}</div></div>
    <DataTable columns={columns} rows={rows} loading={loading} error={error} onRetry={refreshRows} searchable={!config.search} emptyTitle={config.emptyTitle || `No ${config.plural.toLowerCase()} yet`} emptyText={config.emptyText} />
    <Modal open={formOpen} onClose={() => !saving && setFormOpen(false)} title={`${editing ? 'Edit' : 'New'} ${config.singular}`} wide>
      <form className="record-form" onSubmit={submit}><div className="form-grid">{config.fields.map((field) => <FormField key={field.key} field={field} value={formValues[field.key]} disabled={Boolean(editing && field.lockOnEdit)} options={lookupForField(field)} onChange={(key, value) => setFormValues((current) => ({ ...current, [key]: value }))} />)}</div><footer className="modal-actions"><button className="button button-quiet" type="button" onClick={() => setFormOpen(false)} disabled={saving}>Cancel</button><button className="button button-primary" type="submit" disabled={saving}>{saving ? 'Saving…' : editing ? 'Save changes' : `Create ${config.singular}`}</button></footer></form>
    </Modal>
    <Modal open={Boolean(detail)} onClose={() => setDetail(null)} title={`${config.singular} details`}>
      {detailLoading ? <div className="modal-loading">Loading record…</div> : detail?.error ? <div className="modal-loading error-text">{detail.error}</div> : <dl className="detail-list">{detailEntries.map(([key, value]) => <div key={key}><dt>{key.replace(/([A-Z])/g, ' $1').replace(/^./, (letter) => letter.toUpperCase())}</dt><dd>{key === 'status' ? <StatusBadge status={value} /> : value === null || value === '' ? '—' : String(value)}</dd></div>)}</dl>}
    </Modal>
    <ConfirmDialog open={Boolean(deleteTarget)} onClose={() => setDeleteTarget(null)} onConfirm={removeRecord} busy={deleting} message={`Delete ${config.singular.toLowerCase()} #${deleteTarget?.id}? This action cannot be undone.`} />
  </section>
}
