import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { api, TRY } from '../api.js';

const STAGES = ['NEW', 'QUALIFIED', 'PROPOSAL', 'WON', 'LOST'];
const STAGE_LABELS = { NEW: 'Yeni', QUALIFIED: 'Nitelenmiş', PROPOSAL: 'Teklif', WON: 'Kazanıldı', LOST: 'Kaybedildi' };
const STATUS_LABELS = { LEAD: 'Potansiyel', ACTIVE: 'Aktif', INACTIVE: 'Pasif', LOST: 'Kayıp' };
const A_LABELS = { CALL: 'Arama', EMAIL: 'E-posta', MEETING: 'Görüşme', NOTE: 'Not' };

export default function CustomerDetail() {
  const { id } = useParams();
  const [customer, setCustomer] = useState(null);
  const [contacts, setContacts] = useState([]);
  const [deals, setDeals] = useState([]);
  const [activities, setActivities] = useState([]);
  const [err, setErr] = useState('');

  const [ct, setCt] = useState({ name: '', title: '', email: '', phone: '' });
  const [note, setNote] = useState({ type: 'NOTE', body: '' });
  const [dealForm, setDealForm] = useState({ title: '', amount: '' });

  const loadAll = () => {
    api.get(`/customers/${id}`).then(setCustomer).catch(e => setErr(e.message));
    api.get(`/customers/${id}/contacts`).then(setContacts).catch(() => {});
    api.get(`/deals?customerId=${id}`).then(setDeals).catch(() => {});
    api.get(`/customers/${id}/activities`).then(setActivities).catch(() => {});
  };

  useEffect(() => { loadAll(); }, [id]);

  if (err && !customer) return <div className="error">{err}</div>;
  if (!customer) return <div className="loading">Yükleniyor…</div>;

  const addContact = async (e) => {
    e.preventDefault();
    try {
      const saved = await api.post(`/customers/${id}/contacts`, ct);
      setContacts([...contacts, saved]);
      setCt({ name: '', title: '', email: '', phone: '' });
    } catch (e2) { setErr(e2.message); }
  };

  const delContact = async (c) => {
    try {
      await api.del(`/contacts/${c.id}`);
      setContacts(contacts.filter(x => x.id !== c.id));
    } catch (e2) { setErr(e2.message); }
  };

  const addDeal = async (e) => {
    e.preventDefault();
    try {
      const saved = await api.post(`/customers/${id}/deals`, {
        title: dealForm.title,
        amount: Number(dealForm.amount || 0)
      });
      setDeals([...deals, saved]);
      setDealForm({ title: '', amount: '' });
    } catch (e2) { setErr(e2.message); }
  };

  const moveDeal = async (d, stage) => {
    try {
      const saved = await api.patch(`/deals/${d.id}/stage`, { stage });
      setDeals(deals.map(x => x.id === d.id ? saved : x));
    } catch (e2) { setErr(e2.message); }
  };

  const delDeal = async (d) => {
    try {
      await api.del(`/deals/${d.id}`);
      setDeals(deals.filter(x => x.id !== d.id));
    } catch (e2) { setErr(e2.message); }
  };

  const addActivity = async (e) => {
    e.preventDefault();
    try {
      const saved = await api.post(`/customers/${id}/activities`, note);
      setActivities([saved, ...activities]);
      setNote({ type: 'NOTE', body: '' });
    } catch (e2) { setErr(e2.message); }
  };

  return (
    <div>
      <div className="page-head">
        <div>
          <Link to="/customers" className="back">← Müşteriler</Link>
          <h1 className="page-title">{customer.name}</h1>
          <div className="sub">
            <span className={`pill s-${customer.status.toLowerCase()}`}>{STATUS_LABELS[customer.status]}</span>
            {customer.company && <span className="muted"> · {customer.company}</span>}
          </div>
        </div>
      </div>

      {err && <div className="error">{err}</div>}

      <div className="detail-grid">
        <div className="panel">
          <h2>İletişim Bilgileri</h2>
          <p><b>E-posta:</b> {customer.email}</p>
          <p><b>Telefon:</b> {customer.phone || '—'}</p>
          <p><b>Kayıt tarihi:</b> {(customer.createdAt || '').slice(0, 10)}</p>
          {customer.notes && <p className="notes">{customer.notes}</p>}
        </div>

        <div className="panel">
          <h2>Kişiler</h2>
          <ul className="mini-list">
            {contacts.map(c => (
              <li key={c.id}>
                <div>
                  <b>{c.name}</b>{c.title && <span className="muted"> · {c.title}</span>}
                  <div className="muted">{c.email} {c.phone}</div>
                </div>
                <button className="btn small danger" onClick={() => delContact(c)}>Sil</button>
              </li>
            ))}
            {contacts.length === 0 && <li className="muted">Henüz kişi eklenmemiş.</li>}
          </ul>
          <form onSubmit={addContact} className="inline-form">
            <input required placeholder="Ad *" value={ct.name} onChange={e => setCt({ ...ct, name: e.target.value })} />
            <input placeholder="Ünvan" value={ct.title} onChange={e => setCt({ ...ct, title: e.target.value })} />
            <input placeholder="E-posta" value={ct.email} onChange={e => setCt({ ...ct, email: e.target.value })} />
            <button className="btn primary">Ekle</button>
          </form>
        </div>

        <div className="panel">
          <h2>Fırsatlar</h2>
          <ul className="mini-list">
            {deals.map(d => (
              <li key={d.id}>
                <div>
                  <b>{d.title}</b> <span className="money">{TRY(Number(d.amount) / 1)}</span>
                  <div>
                    <select value={d.stage} onChange={e => moveDeal(d, e.target.value)}>
                      {STAGES.map(s => <option key={s} value={s}>{STAGE_LABELS[s]}</option>)}
                    </select>
                  </div>
                </div>
                <button className="btn small danger" onClick={() => delDeal(d)}>Sil</button>
              </li>
            ))}
            {deals.length === 0 && <li className="muted">Henüz fırsat yok.</li>}
          </ul>
          <form onSubmit={addDeal} className="inline-form">
            <input required placeholder="Fırsat *" value={dealForm.title}
                   onChange={e => setDealForm({ ...dealForm, title: e.target.value })} />
            <input type="number" min="0" step="any" placeholder="₺ tutar" value={dealForm.amount}
                   onChange={e => setDealForm({ ...dealForm, amount: e.target.value })} />
            <button className="btn primary">Ekle</button>
          </form>
        </div>

        <div className="panel">
          <h2>Aktivite Geçmişi</h2>
          <ul className="feed">
            {activities.map(a => (
              <li key={a.id}>
                <span className={`pill t-${a.type.toLowerCase()}`}>{A_LABELS[a.type] || a.type}</span>
                <span className="feed-body">{a.body}</span>
                <span className="feed-date">{(a.createdAt || '').slice(0, 16).replace('T', ' ')}</span>
              </li>
            ))}
            {activities.length === 0 && <li className="muted">Henüz aktivite yok.</li>}
          </ul>
          <form onSubmit={addActivity} className="inline-form">
            <select value={note.type} onChange={e => setNote({ ...note, type: e.target.value })}>
              <option value="NOTE">Not</option>
              <option value="CALL">Arama</option>
              <option value="EMAIL">E-posta</option>
              <option value="MEETING">Görüşme</option>
            </select>
            <input required placeholder="Ne kayıt edilsin? *" value={note.body}
                   onChange={e => setNote({ ...note, body: e.target.value })} />
            <button className="btn primary">Kaydet</button>
          </form>
        </div>
      </div>
    </div>
  );
}
