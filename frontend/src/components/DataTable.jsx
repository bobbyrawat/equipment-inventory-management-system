import { Search } from 'lucide-react'
import { useMemo, useState } from 'react'
import Pagination from './Pagination'
import Loading from './Loading'
import ErrorMessage from './ErrorMessage'
export default function DataTable({ columns, rows = [], loading = false, error, emptyTitle = 'Nothing here yet', emptyText = 'Records will appear here when they are available.', onRetry, searchable = true, searchPlaceholder = 'Filter records', rowKey = 'id' }) {
  const [query, setQuery] = useState('')
  const [page, setPage] = useState(1)
  const filtered = useMemo(() => rows.filter((row) => !query || Object.values(row).some((value) => String(value ?? '').toLowerCase().includes(query.toLowerCase()))), [rows, query])
  const pages = Math.max(1, Math.ceil(filtered.length / 10))
  const pageRows = filtered.slice((page - 1) * 10, page * 10)
  if (loading) return <div className="table-frame"><Loading /></div>
  if (error) return <div className="table-frame"><ErrorMessage message={error} onRetry={onRetry} /></div>
  return <div className="table-frame">{searchable && <div className="table-tools"><label className="search-field"><Search size={17} /><input aria-label={searchPlaceholder} placeholder={searchPlaceholder} value={query} onChange={(event) => { setQuery(event.target.value); setPage(1) }} /></label><span className="result-count">{filtered.length} shown</span></div>}{filtered.length ? <><div className="table-scroll"><table><thead><tr>{columns.map((column) => <th key={column.key}>{column.label}</th>)}</tr></thead><tbody>{pageRows.map((row, index) => <tr key={row[rowKey] ?? index}>{columns.map((column) => <td key={column.key}>{column.render ? column.render(row) : row[column.key] ?? '—'}</td>)}</tr>)}</tbody></table></div><Pagination page={page} pages={pages} total={filtered.length} onChange={setPage} /></> : <div className="empty-state"><span className="empty-rule" /><h3>{query ? 'No matching records' : emptyTitle}</h3><p>{query ? 'Try another search term.' : emptyText}</p></div>}</div>
}
