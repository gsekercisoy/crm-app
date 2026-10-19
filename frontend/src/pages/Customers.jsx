import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { api } from '../api.js';

const STATUSES = ['LEAD', 'ACTIVE', 'INACTIVE', 'LOST'];
const STATUS_LABELS = { LEAD: 'Potansiyel', ACTIVE: 'Aktif', INACTIVE: 'Pasif', LOST: 'Kayıp' };

const emptyForm = { name: '', email: '', phone: '', company: '', status: 'LEAD', notes: '' };

export default function Customers() {
  const nav = useNavigate();
  const [list, setList] = useState(null);
  const [q, setQ] = useState('');
  const [editing, setEditing] = useState(null); // null | {} | customer
  const [err, setErr] = useState('');

  const load = (query) => {
    api.get('/customers' + (query ? `?q=${encodeURIComponent(query)}` : ''))
      .then(setList)
      .catch(e => setErr(e.message));
  };

  useEffect(() => { load(''); }, []);

  const search = (e) => {
    e.preventDefault();
    load(q);
  };

  const save = async (form) => {
    try {
      if (form.id) {
        await api.put(`/customers/${form.id}`, form);
      } else {
        await api.post('/customers', form);
      }
      setEditing(null);
      load(q);
    } catch (e) {
      setErr(e.message);
    }
  };

  const remove = async (c) => {
    if (!window.confirm(`"${c.name}" silinsin mi? İlişkili kişi, fırsat ve aktiviteler de silinir.`)) return;
    try {
      await api.del(`/customers/${c.id}`);
      load(q);
    } catch (e) {
      setErr(e.message);
    }
  };

  if (err && !list) return <div className="error">{err}</div>;
  if (!list) return <div className="loading">Yükleniyor…</div>;

  return (
    <div>
      <div className="page-head">
        <h1 className="page-title">Müşteriler</h1>
        <button className="btn primary" onClick={() => setEditing({ ...emptyForm })}>+ Yeni Müşteri</button>
      </div>

      <form className="search" onSubmit={search}>
        <input
          value={q}
          onChange={e => setQ(e.target.value)}
          placeholder="Ad, e-posta veya firma ara…"
        />
        <button className="btn">Ara</button>
        {q && <button type="button" className="btn ghost" onClick={() => { setQ(''); load(''); }}>Temizle</button>}
      </form>

      {err && <div className="error">{err}</div>}

      <table className="table">
        <thead>
          <tr>
            <th>Ad</th><th>Firma</th><th>E-posta</th><th>Telefon</th><th>Durum</th><th></th>
          </tr>
        </thead>
        <tbody>
          {list.map(c => (
            <tr key={c.id}>
              <td><Link to={`/customers/${c.id}`} className="link-strong">{c.name}</Link></td>
              <td>{c.company || '—'}</td>
              <td>{c.email}</td>
              <td>{c.phone || '—'}</td>
              <td><span className={`pill s-${c.status.toLowerCase()}`}>{STATUS_LABELS[c.status]}</span></td>
              <td className="row-actions">
                <button className="btn small" onClick={() => setEditing({ ...c })}>Düzenle</button>
                <button className="btn small danger" onClick={() => remove(c)}>Sil</button>
              </td>
            </tr>
          ))}
          {list.length === 0 && <tr><td colSpan="6" className="muted">Kayıt bulunamadı.</td></tr>}
        </tbody>
      </table>

      {editing && (
        <CustomerModal
          form={editing}
          onClose={() => setEditing(null)}
          onSave={save}
          onOpenDetail={() => nav(`/customers/${editing.id}`)}
        />
      )}
    </div>
  );
}

function CustomerModal({ form, onClose, onSave, onOpenDetail }) {
  const [f, setF] = useState(form);
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });

  return (
    <div className="modal-backdrop" onMouseDown={e => { if (e.target === e.currentTarget) onClose(); }}>
      <div className="modal">
        <h2>{f.id ? 'Müşteriyi Düzenle' : 'Yeni Müşteri'}</h2>
        <label>Ad *
          <input value={f.name} onChange={set('name')} autoFocus />
        </label>
        <label>E-posta *
          <input value={f.email} onChange={set('email')} />
        </label>
        <label>Telefon
          <input value={f.phone || ''} onChange={set('phone')} />
        </label>
        <label>Firma
          <input value={f.company || ''} onChange={set('company')} />
        </label>
        <label>Durum
          <select value={f.status} onChange={set('status')}>
            {STATUSES.map(s => <option key={s} value={s}>{STATUS_LABELS[s]}</option>)}
          </select>
        </label>
        <label>Notlar
          <textarea value={f.notes || ''} onChange={set('notes')} rows={3} />
        </label>
        <div className="modal-actions">
          {f.id && <button className="btn ghost" onClick={onOpenDetail}>Detay</button>}
          <span className="spacer" />
          <button className="btn ghost" onClick={onClose}>Vazgeç</button>
          <button className="btn primary" onClick={() => onSave(f)}>Kaydet</button>
        </div>
      </div>
    </div>
  );
}
