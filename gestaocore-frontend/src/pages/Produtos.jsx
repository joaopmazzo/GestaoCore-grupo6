import { useState, useEffect } from 'react';
import api from '../api/axios';
import ProdutoModal from '../components/ProdutoModal';

const LOW_STOCK_THRESHOLD = 10;

function formatCurrency(value) {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
}

export default function Produtos() {
  const [produtos, setProdutos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [modal, setModal] = useState(null);
  const [deleteConfirm, setDeleteConfirm] = useState(null);

  const fetchProdutos = async () => {
    setLoading(true);
    try {
      const res = await api.get('/produto');
      setProdutos(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchProdutos(); }, []);

  const handleDelete = async (id) => {
    try {
      await api.delete(`/produto/${id}`);
      setDeleteConfirm(null);
      fetchProdutos();
    } catch (err) {
      console.error(err);
    }
  };

  const filtered = produtos.filter(p =>
    p.nome?.toLowerCase().includes(search.toLowerCase()) ||
    p.categoria?.toLowerCase().includes(search.toLowerCase())
  );

  const lowStockCount = produtos.filter(p => p.qtdEstoque < LOW_STOCK_THRESHOLD).length;

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Produtos</h1>
          <p className="page-subtitle">
            {produtos.length} produto{produtos.length !== 1 ? 's' : ''} cadastrado{produtos.length !== 1 ? 's' : ''}
            {lowStockCount > 0 && (
              <span className="badge badge-orange" style={{ marginLeft: 10 }}>
                {lowStockCount} com estoque baixo
              </span>
            )}
          </p>
        </div>
        <div className="toolbar">
          <div className="search-bar">
            <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
              <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
            </svg>
            <input className="search-input" placeholder="Buscar produto..." value={search} onChange={e => setSearch(e.target.value)} />
          </div>
          <button className="btn btn-primary" onClick={() => setModal('create')}>
            <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2.5" viewBox="0 0 24 24">
              <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
            Novo Produto
          </button>
        </div>
      </div>

      <div className="card" style={{ padding: 0 }}>
        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>Nome</th>
                <th>Categoria</th>
                <th>Preço</th>
                <th>Qtd. Estoque</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr className="loading-row"><td colSpan={5}>Carregando...</td></tr>
              ) : filtered.length === 0 ? (
                <tr className="loading-row">
                  <td colSpan={5}>
                    <div className="empty-state">
                      <svg fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
                        <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
                      </svg>
                      <h3>Nenhum produto encontrado</h3>
                      <p>Adicione um produto ou ajuste a busca</p>
                    </div>
                  </td>
                </tr>
              ) : filtered.map(p => (
                <tr key={p.id}>
                  <td style={{ fontWeight: 500 }}>{p.nome}</td>
                  <td><span className="badge badge-blue">{p.categoria}</span></td>
                  <td style={{ fontWeight: 600, color: 'var(--accent-green)' }}>{formatCurrency(p.preco)}</td>
                  <td>
                    <span className={`badge ${p.qtdEstoque < LOW_STOCK_THRESHOLD ? 'badge-orange' : 'badge-green'}`}>
                      {p.qtdEstoque < LOW_STOCK_THRESHOLD && '⚠ '}
                      {p.qtdEstoque} un.
                    </span>
                  </td>
                  <td>
                    <div className="actions-cell">
                      <button className="btn btn-icon btn-edit" title="Editar" onClick={() => setModal(p)}>
                        <svg width="15" height="15" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                          <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                          <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                        </svg>
                      </button>
                      <button className="btn btn-icon btn-delete" title="Excluir" onClick={() => setDeleteConfirm(p)}>
                        <svg width="15" height="15" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                          <polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14H6L5 6"/>
                          <path d="M10 11v6M14 11v6M9 6V4h6v2"/>
                        </svg>
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {modal && (
        <ProdutoModal
          produto={modal === 'create' ? null : modal}
          onClose={() => setModal(null)}
          onSaved={() => { setModal(null); fetchProdutos(); }}
        />
      )}

      {deleteConfirm && (
        <div className="modal-backdrop">
          <div className="modal" style={{ maxWidth: 420 }}>
            <div className="modal-header">
              <h2 className="modal-title">Confirmar exclusão</h2>
            </div>
            <p style={{ color: 'var(--text-secondary)', fontSize: 14, lineHeight: 1.6 }}>
              Tem certeza que deseja excluir o produto <strong style={{ color: 'var(--text-primary)' }}>{deleteConfirm.nome}</strong>?
            </p>
            <div className="modal-footer">
              <button className="btn btn-ghost" onClick={() => setDeleteConfirm(null)}>Cancelar</button>
              <button className="btn btn-danger" onClick={() => handleDelete(deleteConfirm.id)}>Excluir</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
