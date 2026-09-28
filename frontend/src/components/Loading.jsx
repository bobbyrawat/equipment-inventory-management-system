export default function Loading({ label = 'Loading data' }) {
  return <div className="loading-state" role="status"><span className="spinner" /><span>{label}</span></div>
}
