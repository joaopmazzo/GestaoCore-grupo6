import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/axios';

const LOW_STOCK_THRESHOLD = 10;

function StatCard({ label, value, sub, accent, icon }) {
  return (
    <div className="card" style={{ display: 'flex', alignItems: 'center', gap: 18 }}>
      <div style={{
        width: 52, height: 52, borderRadius: 14, flexShrink: 0,
        background: accent + '22',
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        color: accent,
      }}>{icon}</div>
      <div>
        <div style={{ fontSize: 28, fontWeight: 700, letterSpacing: '-0.02em', color: accent }}>{value}</div>
        <div style={{ fontSize: 13, fontWeight: 500, color: 'var(--text-primary)' }}>{label}</div>
        {sub && <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 2 }}>{sub}</div>}
      </div>
    </div>
  );
}

export default function Dashboard() {
  const [clientes, setClientes] = useState([]);
  const [produtos, setProdutos] = useState([]);
  const [usuarios, setUsuarios] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAll = async () => {
      try {
        const [c, p, u] = await Promise.all([
          api.get('/cliente'), api.get('/produto'), api.get('/usuario'),
        ]);
        setClientes(c.data); setProdutos(p.data); setUsuarios(u.data);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchAll();
  }, []);

  const lowStock = produtos.filter(p => p.qtdEstoque < LOW_STOCK_THRESHOLD);
  const totalEstoque = produtos.reduce((acc, p) => acc + (p.qtdEstoque || 0), 0);

  return (
    <div>
      <div className="page-header" style={{ marginBottom: 28 }}>
        <div>
          <h1 className="page-title">Dashboard</h1>
          <p className="page-subtitle">Visão geral do sistema</p>
        </div>
      </div>

      {loading ? (
        <div style={{ display: 'flex', alignItems: 'center', gap: 12, color: 'var(--text-muted)', justifyContent: 'center', padding: 80 }}>
          <span className="spinner" />Carregando...
        </div>
      ) : (
        <>
          {/* Stat Cards */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: 16, marginBottom: 28 }}>
            <StatCard
              label="Total de Clientes" value={clientes.length}
              sub="Clientes ativos"
              accent="var(--accent-blue)"
              icon={<svg width="24" height="24" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/></svg>}
            />
            <StatCard
              label="Total de Produtos" value={produtos.length}
              sub={lowStock.length > 0 ? `${lowStock.length} com estoque baixo` : 'Estoque saudável'}
              accent={lowStock.length > 0 ? 'var(--accent-orange)' : 'var(--accent-green)'}
              icon={<svg width="24" height="24" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/><polyline points="3.27 6.96 12 12.01 20.73 6.96"/><line x1="12" y1="22.08" x2="12" y2="12"/></svg>}
            />
            <StatCard
              label="Estoque Total" value={totalEstoque.toFixed(0)}
              sub="Unidades em estoque"
              accent="var(--accent-purple)"
              icon={<svg width="24" height="24" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><line x1="8" y1="6" x2="21" y2="6"/><line x1="8" y1="12" x2="21" y2="12"/><line x1="8" y1="18" x2="21" y2="18"/><line x1="3" y1="6" x2="3.01" y2="6"/><line x1="3" y1="12" x2="3.01" y2="12"/><line x1="3" y1="18" x2="3.01" y2="18"/></svg>}
            />
            <StatCard
              label="Usuários" value={usuarios.length}
              sub={`${usuarios.filter(u => u.role === 'ADMIN').length} admin • ${usuarios.filter(u => u.role === 'USER').length} user`}
              accent="var(--accent-green)"
              icon={<svg width="24" height="24" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>}
            />
          </div>

          {/* Tables */}
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 20 }}>
            {/* Recent clients */}
            <div className="card" style={{ padding: 0 }}>
              <div style={{ padding: '18px 20px', borderBottom: '1px solid var(--border)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <h3 style={{ fontWeight: 600, fontSize: 15 }}>Últimos Clientes</h3>
                <Link to="/clientes" style={{ fontSize: 13, color: 'var(--accent-blue)' }}>Ver todos →</Link>
              </div>
              <table>
                <thead>
                  <tr><th>Nome</th><th>Cidade</th></tr>
                </thead>
                <tbody>
                  {clientes.slice(-5).reverse().map(c => (
                    <tr key={c.id}>
                      <td style={{ fontWeight: 500 }}>{c.nome}</td>
                      <td style={{ color: 'var(--text-secondary)', fontSize: 13 }}>{c.cidade}/{c.estado}</td>
                    </tr>
                  ))}
                  {clientes.length === 0 && (
                    <tr><td colSpan={2} style={{ textAlign: 'center', color: 'var(--text-muted)', padding: 24, fontSize: 13 }}>Nenhum cliente</td></tr>
                  )}
                </tbody>
              </table>
            </div>

            {/* Low stock */}
            <div className="card" style={{ padding: 0 }}>
              <div style={{ padding: '18px 20px', borderBottom: '1px solid var(--border)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <h3 style={{ fontWeight: 600, fontSize: 15 }}>Estoque Baixo</h3>
                <Link to="/produtos" style={{ fontSize: 13, color: 'var(--accent-blue)' }}>Ver produtos →</Link>
              </div>
              <table>
                <thead>
                  <tr><th>Produto</th><th>Qtd.</th></tr>
                </thead>
                <tbody>
                  {lowStock.length === 0 ? (
                    <tr><td colSpan={2} style={{ textAlign: 'center', color: 'var(--accent-green)', padding: 24, fontSize: 13 }}>✓ Estoque saudável</td></tr>
                  ) : (
                    lowStock.slice(0, 5).map(p => (
                      <tr key={p.id}>
                        <td style={{ fontWeight: 500 }}>{p.nome}</td>
                        <td><span className="badge badge-orange">{p.qtdEstoque} un.</span></td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
