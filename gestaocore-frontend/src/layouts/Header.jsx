import { useLocation } from 'react-router-dom';

const pageTitles = {
  '/dashboard': { title: 'Dashboard', sub: 'Visão geral do sistema' },
  '/clientes': { title: 'Clientes', sub: 'Gerencie seus clientes' },
  '/produtos': { title: 'Produtos', sub: 'Controle de estoque e produtos' },
  '/usuarios': { title: 'Usuários', sub: 'Administração de usuários' },
};

export default function Header() {
  const location = useLocation();
  const info = pageTitles[location.pathname] || { title: 'GestaoCore', sub: '' };

  return (
    <header style={{
      height: 'var(--header-height)',
      background: 'var(--bg-secondary)',
      borderBottom: '1px solid var(--border)',
      display: 'flex', alignItems: 'center', justifyContent: 'space-between',
      padding: '0 32px',
      position: 'sticky', top: 0, zIndex: 40,
    }}>
      <div>
        <h1 style={{ fontSize: 18, fontWeight: 700 }}>{info.title}</h1>
        {info.sub && <p style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 1 }}>{info.sub}</p>}
      </div>
      <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
        <div style={{
          width: 36, height: 36,
          background: 'linear-gradient(135deg, #60a5fa, #a78bfa)',
          borderRadius: '50%',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          fontSize: 14, fontWeight: 700, color: '#fff',
        }}>U</div>
      </div>
    </header>
  );
}
