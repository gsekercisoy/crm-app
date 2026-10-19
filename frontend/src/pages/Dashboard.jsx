import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api.js';

const STAGE_LABELS = {
  NEW: 'Yeni', QUALIFIED: 'Nitelenmiş', PROPOSAL: 'Teklif', WON: 'Kazanıldı', LOST: 'Kaybedildi'
};

export default function Dashboard() {
  const [stats, setStats] = useState(null);
  const [err, setErr] = useState('');

  useEffect(() => {
    api.get('/stats').then(setStats).catch(e => setErr(e.message));
  }, []);

  if (err) return <div className="error">{err}</div>;
  if (!stats) return <div className="loading">Yükleniyor…</div>;

  const maxStage = Math.max(
    ...Object.values(stats.byStage).map(v => Number(v)),
    1
  );

  return (
    <div>
      <h1 className="page-title">Dashboard</h1>

      <div className="cards">
        <div className="card">
          <div className="card-num">{stats.totalCustomers}</div>
          <div className="card-label">Müşteri</div>
          <div className="card-sub"> {stats.customersACTIVE} aktif · {stats.customersLEAD} potansiyel</div>
        </div>
        <div className="card">
          <div className="card-num">{stats.totalDeals}</div>
          <div className="card-label">Fırsat</div>
          <div className="card-sub">Kazanılan: {TRY(stats.wonValue / 1)}</div>
        </div>
        <div className="card highlight">
          <div className="card-num">{TRY(stats.openPipeline / 1)}</div>
          <div className="card-label">Açık Pipeline</div>
          <div className="card-sub">Kazanılan + kayıp hariç bekleyen toplam</div>
        </div>
        <div className="card">
          <div className="card-num">{stats.totalContacts}</div>
          <div className="card-label">Kişi</div>
          <div className="card-sub">{stats.totalActivities} aktivite kaydı</div>
        </div>
      </div>

      <div className="panel">
        <h2>Hunideki Fırsat Değerleri</h2>
        <div className="bars">
          {Object.entries(stats.byStage).map(([stage, val]) => (
            <div className="bar-row" key={stage}>
              <span className="bar-label">{STAGE_LABELS[stage] || stage}</span>
              <div className="bar-track">
                <div className="bar-fill" style={{ width: `${Math.max(Number(val) / maxStage * 100, Number(val) > 0 ? 4 : 0)}%` }} />
              </div>
              <span className="bar-val">{TRY(Number(val) / 1)}</span>
            </div>
          ))}
        </div>
      </div>

      <div className="panel">
        <h2>Son Aktiviteler</h2>
        <ul className="feed">
          {(stats.latestActivities || []).map(a => (
            <li key={a.id}>
              <span className={`pill t-${a.type.toLowerCase()}`}>{a.type}</span>
              <span className="feed-body">{a.body}</span>
              <Link className="feed-cust" to={`/customers/${a.customerId}`}>{a.customerName}</Link>
              <span className="feed-date">{(a.createdAt || '').slice(0, 10)}</span>
            </li>
          ))}
          {(stats.latestActivities || []).length === 0 && <li className="muted">Henüz aktivite yok.</li>}
        </ul>
      </div>
    </div>
  );
}
