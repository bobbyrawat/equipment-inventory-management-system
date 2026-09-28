export default function StatCard({ label, value, detail, icon: Icon, accent = 'teal' }) {
  return <article className={`stat-card stat-${accent}`}><div className="stat-top"><span>{label}</span>{Icon && <span className="stat-icon"><Icon size={19} /></span>}</div><strong>{value ?? '—'}</strong>{detail && <small>{detail}</small>}</article>
}
