import React, { useEffect, useState } from 'react';
import { api, TRY } from '../api.js';

const STAGES = ['NEW', 'QUALIFIED', 'PROPOSAL', 'WON', 'LOST'];
const STAGE_LABELS = { NEW: 'Yeni', QUALIFIED: 'Nitelenmiş', PROPOSAL: 'Teklif', WON: 'Kazanıldı', LOST: 'Kaybedildi' };

export default function Deals() {
  const [deals, setDeals] = useState(null);
  const [customers, setCustomers] = useState([]);
  const [adding, setAdding] = useState(false);
  const [err, setErr] = useState('');

  const load = () => {
    api.get('/deals').then(setDeals).catch(e => setErr(e.message));
  };

  useEffect(() => {
    load();
    api.get('/customers').then(setCustomers).catch(() => {});
  }, []);

  if (err && !deals) return <div className="error">{err}</div>;
  if (!deals) return <div className="loading">Yükleniyor…</div>;

  const move = async (d, dir) => {
    const i = STAGES.indexOf(d.stage);
    const j = Math.min(Math.max(i + dir, 0), STAGES.length - 1);
    if (i === j) return;
    try {
      const saved = await api.patch(`/deals/${d.id}/stage`, { stage: STAGES[j] });
      setDeals(deals.map(x => x.id === d.id ? saved : x));
    } catch (e2) { setErr(e2.message); }
  };

  const remove = async (d) => {
    if (!window.confirm(`"${d.title}" fırsatı silinsin mi?`)) return;
    try {
      await api.del(`/deals/${d.id}`);
      setDeals(deals.filter(x => x.id !== d.id));
    } catch (e2) { setErr(e2.message); }
  };

  const create = async (form) => {
    try {
      const saved = await api.post(`/customers/${form.customerId}/deals`, {
        title: form.title,
        amount: Number(form.amount || 0),
        stage: form.stage
      });
      setDeals([...deals, saved]);
      setAdding(false);
    } catch (e2) {
      setErr(e2.message);
    }
  };

  return (
    <div>
      <div className="page-head">
        <h1 className="page-title">Fırsat Pipeline'ı</h1>
        <button className="btn primary" onClick={() => setAdding(true)} disabled={customers.length === 0}>
          + Yeni Fırsat
        </button>
      </div>

      {customers.length === 0 && (
        <div className="info">Fırsat açmak için önce en az bir müşteri ekleyin.</div>
      )}
      {err && <div className="error">{err}</div>}

      <div className="board">
        {STAGES.map(stage => {
          const col = deals.filter(d => d.stage === stage);
          const sum = col.reduce((t, d) => t + Number(d.amount), 0);
          return (
            <div className="col" key={stage}>
              <div className="col-head">
                <b>{STAGE_LABELS[stage]}</b>
                <span className="muted">{col.length} · {TRY(sum)}</span>
              </div>
              {col.map(d => (
                <div className="deal" key={d.id}>
                  <div className="deal-title">{d.title}</div>
                  <div className="deal-cust">
                    <a href={`/customers/${d.customerId}`}>{d.customerName}</a>
                  </div>
                  <div className="deal-amount">{TRY(Number(d.amount) / 1)}</div>
                  <div className="deal-actions">
                    <button className="btn tiny" onClick={() => move(d, -1)}>◀</button>
                    <button className="btn tiny danger" onClick={() => remove(d)}>Sil</button>
                    <button className="btn tiny" onClick={() => move(d, +1)}>▶</button>
                  </div>
                </div>
              ))}
              {col.length === 0 && <div className="muted small-col">—</div>}
            </div>
          );
        })}
      </div>

      {adding && <DealModal customers={customers} onClose={() => setAdding(false)} onSave={create} />}
    </div>
  );
}

function DealModal({ customers, onClose, onSave }) {
  const [f, setF] = useState({
    customerId: customers[0] ? customers[0].id : '',
    title: '', amount: '', stage: 'NEW'
  });
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });

  return (
    <div className="modal-backdrop" onMouseDown={e => { if (e.target === e.currentTarget) onClose(); }}>
      <div className="modal">
        <h2>Yeni Fırsat</h2>
        <label>Müşteri *
          <select value={f.customerId} onChange={set('customerId')}>
            {customers.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
          </select>
        </label>
        <label>Fırsat *
          <input value={f.title} onChange={set('title')} autoFocus />
        </label>
        <label>Tutar (₺)
          <input type="number" min="0" step="any" value={f.amount} onChange={set('amount')} />
        </label>
        <label>Aşama
          <select value={f.stage} onChange={set('stage')}>
            {STAGES.map(s => <option key={s} value={s}>{STAGE_LABELS[s]}</option>)}
          </select>
        </label>
        <div className="modal-actions">
          <span className="spacer" />
          <button className="btn ghost" onClick={onClose}>Vazgeç</button>
          <button className="btn primary" onClick={() => onSave(f)}>Kaydet</button>
        </div>
      </div>
    </div>
  );
}
