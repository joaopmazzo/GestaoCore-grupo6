import { useState, useEffect } from 'react';
import api from '../api/axios';


function formatCurrency(value) {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
}

export default function Vendas() {
  const [produtos, setProdutos] = useState([]);
  const [clientes, setClientes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  
  // Cart & Checkout state
  const [cart, setCart] = useState([]);
  const [cartModal, setCartModal] = useState(null); // Which product to add
  const [qtdToAdd, setQtdToAdd] = useState(1);
  const [checkoutModal, setCheckoutModal] = useState(false);
  const [selectedCliente, setSelectedCliente] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [resProd, resCli] = await Promise.all([
        api.get('/produto'),
        api.get('/cliente')
      ]);
      setProdutos(resProd.data);
      setClientes(resCli.data);
    } catch (err) {
      console.error('Erro ao buscar dados do PDV:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchData(); }, []);

  const addToCart = () => {
    if (!cartModal || qtdToAdd < 1) return;
    const existing = cart.find(i => i.produto.id === cartModal.id);
    if (existing) {
      setCart(cart.map(i => i.produto.id === cartModal.id 
        ? { ...i, quantidade: i.quantidade + qtdToAdd, subtotal: (i.quantidade + qtdToAdd) * cartModal.preco } 
        : i));
    } else {
      setCart([...cart, {
        produto: cartModal,
        quantidade: qtdToAdd,
        subtotal: qtdToAdd * cartModal.preco
      }]);
    }
    setCartModal(null);
    setQtdToAdd(1);
  };

  const removeFromCart = (produtoId) => {
    setCart(cart.filter(i => i.produto.id !== produtoId));
  };

  const totalCart = cart.reduce((acc, item) => acc + item.subtotal, 0);

  const handleCheckout = async () => {
    if (!selectedCliente || cart.length === 0) return;
    setSubmitting(true);
    try {
      const payload = {
        clienteId: selectedCliente,
        dataVenda: new Date().toISOString(),
        itens: cart.map(item => ({
          produtoId: item.produto.id,
          quantidade: item.quantidade,
          valorNaVenda: item.produto.preco
        }))
      };
      // Try to post to Venda (adjust endpoint if needed by backend)
      await api.post('/venda', payload);
      alert('Venda finalizada com sucesso!');
      setCart([]);
      setCheckoutModal(false);
      setSelectedCliente('');
      fetchData(); // Refresh stock
    } catch (err) {
      console.error(err);
      alert('Erro ao finalizar venda, verifique a conexão com a API.');
    } finally {
      setSubmitting(false);
    }
  };

  const filtered = produtos.filter(p =>
    p.nome?.toLowerCase().includes(search.toLowerCase()) ||
    p.categoria?.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div>
      <div className="page-header">
        <div>
          <h1 className="page-title">Ponto de Venda</h1>
          <p className="page-subtitle">Selecione os produtos para realizar a venda</p>
        </div>
        <div className="toolbar">
          <div className="search-bar">
            <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
              <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
            </svg>
            <input className="search-input" placeholder="Buscar produto..." value={search} onChange={e => setSearch(e.target.value)} />
          </div>
          <button className="btn btn-primary" onClick={() => setCheckoutModal(true)}>
            <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2.5" viewBox="0 0 24 24">
              <circle cx="9" cy="21" r="1"></circle><circle cx="20" cy="21" r="1"></circle>
              <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
            </svg>
            Ver Carrinho ({cart.reduce((a, b) => a + b.quantidade, 0)})
          </button>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '24px', padding: '8px 0' }}>
        {loading ? (
          Array.from({ length: 4 }).map((_, i) => (
            <div key={`skel-${i}`} className="card" style={{ height: 320, animation: 'pulse 2s infinite ease-in-out', background: 'var(--bg-secondary)' }} />
          ))
        ) : filtered.length === 0 ? (
          <div className="card" style={{ gridColumn: '1 / -1', textAlign: 'center', padding: '60px 20px' }}>
            <h3>Nenhum produto disponível</h3>
            <p>Não há produtos para exibir no PDV.</p>
          </div>
        ) : (
          filtered.map(p => (
            <div key={p.id} className="card" style={{ padding: 0, overflow: 'hidden', display: 'flex', flexDirection: 'column' }}>
              <div style={{
                height: 160, background: p.image ? `url(${p.image}) center/cover` : 'linear-gradient(135deg, var(--accent-blue-dim), var(--accent-purple-dim))',
                display: 'flex', alignItems: 'center', justifyContent: 'center', position: 'relative'
              }}>
                {!p.image && (
                  <svg width="48" height="48" fill="none" stroke="var(--accent-blue)" strokeWidth="1" viewBox="0 0 24 24">
                    <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
                    <polyline points="3.27 6.96 12 12.01 20.73 6.96"/><line x1="12" y1="22.08" x2="12" y2="12"/>
                  </svg>
                )}
                <div style={{ position: 'absolute', top: 12, right: 12, borderRadius: 'var(--radius-sm)', background: 'rgba(0,0,0,0.6)', backdropFilter: 'blur(4px)', color: '#fff', fontSize: 12, fontWeight: 600, padding: '4px 8px' }}>
                  Estoque: {p.qtdEstoque}
                </div>
              </div>
              <div style={{ padding: '20px', flex: 1, display: 'flex', flexDirection: 'column' }}>
                <h3 style={{ fontSize: 16, fontWeight: 600, margin: '0 0 8px 0', lineHeight: 1.3 }}>{p.nome}</h3>
                <span className="badge badge-blue" style={{ alignSelf: 'flex-start', marginBottom: 12 }}>{p.categoria}</span>
                {p.description && <p style={{ fontSize: 13, color: 'var(--text-muted)', lineHeight: 1.5, marginBottom: 16 }}>{p.description}</p>}
                
                <div style={{ marginTop: 'auto', display: 'flex', alignItems: 'center', justifyContent: 'space-between', borderTop: '1px solid var(--border)', paddingTop: 16 }}>
                  <div style={{ fontSize: 20, fontWeight: 700, color: 'var(--accent-green)' }}>{formatCurrency(p.preco)}</div>
                  <button className="btn btn-primary" style={{ padding: '8px 12px' }} onClick={() => { setCartModal(p); setQtdToAdd(1); }} disabled={p.qtdEstoque <= 0}>
                    <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2.5" viewBox="0 0 24 24">
                      <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
                    </svg>
                  </button>
                </div>
              </div>
            </div>
          ))
        )}
      </div>

      {cartModal && (
        <div className="modal-backdrop">
          <div className="modal" style={{ maxWidth: 420 }}>
            <div className="modal-header">
              <h2 className="modal-title">Adicionar Item</h2>
              <button className="btn btn-icon" onClick={() => setCartModal(null)}>
                <svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
              </button>
            </div>
            <div style={{ display: 'flex', gap: 16, marginBottom: 24, marginTop: 16, alignItems: 'center' }}>
              <div style={{ width: 80, height: 80, borderRadius: 'var(--radius-md)', background: cartModal.image ? `url(${cartModal.image}) center/cover` : 'var(--bg-secondary)' }} />
              <div>
                <h3 style={{ margin: 0, fontSize: 16 }}>{cartModal.nome}</h3>
                <div style={{ color: 'var(--accent-green)', fontWeight: 600, marginTop: 4 }}>{formatCurrency(cartModal.preco)}</div>
                <div style={{ fontSize: 13, color: 'var(--text-muted)', marginTop: 4 }}>Disponível: {cartModal.qtdEstoque} un</div>
              </div>
            </div>
            <div className="form-group">
              <label>Quantidade desejada</label>
              <input type="number" className="form-input" value={qtdToAdd} onChange={e => setQtdToAdd(Number(e.target.value))} min="1" max={cartModal.qtdEstoque} />
            </div>
            <div className="modal-footer" style={{ borderTop: 'none', paddingTop: 8 }}>
              <button className="btn btn-primary" style={{ width: '100%' }} onClick={addToCart}>
                Confirmar ({formatCurrency(cartModal.preco * qtdToAdd)})
              </button>
            </div>
          </div>
        </div>
      )}

      {checkoutModal && (
        <div className="modal-backdrop">
          <div className="modal" style={{ maxWidth: 600, width: '100%' }}>
            <div className="modal-header">
              <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                <svg width="24" height="24" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                  <circle cx="9" cy="21" r="1"></circle><circle cx="20" cy="21" r="1"></circle>
                  <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
                </svg>
                <h2 className="modal-title">Carrinho de Compras</h2>
              </div>
              <button className="btn btn-icon" onClick={() => setCheckoutModal(false)}>
                <svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
              </button>
            </div>

            <div style={{ padding: '0 24px', maxHeight: '50vh', overflowY: 'auto' }}>
              {cart.length === 0 ? (
                <div style={{ textAlign: 'center', padding: '40px 0', color: 'var(--text-muted)' }}>O carrinho está vazio.</div>
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: 12, marginTop: 16 }}>
                  {cart.map((item, i) => (
                    <div key={i} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '12px 16px', background: 'var(--bg-secondary)', borderRadius: 'var(--radius-md)' }}>
                      <div>
                        <div style={{ fontWeight: 500, color: 'var(--text-primary)' }}>{item.produto.nome}</div>
                        <div style={{ fontSize: 13, color: 'var(--text-muted)', marginTop: 4 }}>
                          {item.quantidade}x {formatCurrency(item.produto.preco)}
                        </div>
                      </div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
                        <div style={{ fontWeight: 600, color: 'var(--accent-green)' }}>{formatCurrency(item.subtotal)}</div>
                        <button className="btn btn-icon btn-delete" onClick={() => removeFromCart(item.produto.id)}>
                          <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                            <polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14H6L5 6"/><path d="M10 11v6M14 11v6M9 6V4h6v2"/>
                          </svg>
                        </button>
                      </div>
                    </div>
                  ))}
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '16px 0', borderTop: '1px solid var(--border)', marginTop: 8 }}>
                    <div style={{ fontSize: 16, fontWeight: 500, color: 'var(--text-secondary)' }}>Total da Venda</div>
                    <div style={{ fontSize: 24, fontWeight: 700, color: 'var(--accent-green)' }}>{formatCurrency(totalCart)}</div>
                  </div>
                </div>
              )}
            </div>

            <div style={{ padding: '24px', background: 'var(--bg-secondary)', borderBottomLeftRadius: 'var(--radius-lg)', borderBottomRightRadius: 'var(--radius-lg)' }}>
              <div className="form-group" style={{ marginBottom: 20 }}>
                <label>Vincular a um Cliente</label>
                <select className="form-input" value={selectedCliente} onChange={e => setSelectedCliente(e.target.value)}>
                  <option value="">-- Selecione o Cliente --</option>
                  {clientes.map(c => (
                    <option key={c.id} value={c.id}>{c.nome} - {c.cpfCnpj}</option>
                  ))}
                  {clientes.length === 0 && <option disabled>Nenhum cliente cadastrado.</option>}
                </select>
              </div>
              <div style={{ display: 'flex', gap: 12 }}>
                <button className="btn btn-ghost" style={{ flex: 1 }} onClick={() => setCheckoutModal(false)}>Voltar</button>
                <button className="btn btn-primary" style={{ flex: 2 }} onClick={handleCheckout} disabled={cart.length === 0 || !selectedCliente || submitting}>
                  {submitting ? 'Processando...' : 'Finalizar Venda'}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
