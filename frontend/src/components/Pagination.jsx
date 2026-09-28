import { ChevronLeft, ChevronRight } from 'lucide-react'
export default function Pagination({ page, pages, total, onChange }) {
  if (pages < 2) return <div className="table-footer"><span>{total} record{total === 1 ? '' : 's'}</span></div>
  return <div className="table-footer"><span>{total} records · page {page} of {pages}</span><div className="pagination-controls"><button className="icon-button" disabled={page <= 1} onClick={() => onChange(page - 1)} aria-label="Previous page"><ChevronLeft size={17} /></button><button className="icon-button" disabled={page >= pages} onClick={() => onChange(page + 1)} aria-label="Next page"><ChevronRight size={17} /></button></div></div>
}
