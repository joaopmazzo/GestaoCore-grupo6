import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import api from '../api/axios';

export default function ProdutoModal({ produto, onClose, onSaved }) {
  const isEdit = !!produto;
  const { register, handleSubmit, formState: { errors, isSubmitting }, reset } = useForm({
    defaultValues: produto || {},
  });

  useEffect(() => { reset(produto || {}); }, [produto]);

  const onSubmit = async (data) => {
    const payload = {
      ...data,
      preco: parseFloat(data.preco),
      qtdEstoque: parseFloat(data.qtdEstoque),
    };
    try {
      if (isEdit) {
        await api.put(`/produto/${produto.id}`, payload);
      } else {
        await api.post('/produto', payload);
      }
      onSaved();
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="modal-backdrop" onClick={(e) => e.target === e.currentTarget && onClose()}>
      <div className="modal">
        <div className="modal-header">
          <h2 className="modal-title">{isEdit ? 'Editar Produto' : 'Novo Produto'}</h2>
          <button className="btn-icon modal-close" onClick={onClose}>
            <svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
              <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>

        <form onSubmit={handleSubmit(onSubmit)}>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
            <div className="form-group">
              <label className="form-label">Nome *</label>
              <input className={`form-input${errors.nome ? ' error' : ''}`} placeholder="Nome do produto"
                {...register('nome', { required: 'Obrigatório' })} />
              {errors.nome && <span className="form-error">{errors.nome.message}</span>}
            </div>
            <div className="form-group">
              <label className="form-label">Categoria *</label>
              <input className={`form-input${errors.categoria ? ' error' : ''}`} placeholder="Ex: Eletrônicos"
                {...register('categoria', { required: 'Obrigatório' })} />
              {errors.categoria && <span className="form-error">{errors.categoria.message}</span>}
            </div>
            <div className="form-grid">
              <div className="form-group">
                <label className="form-label">Preço (R$) *</label>
                <input className={`form-input${errors.preco ? ' error' : ''}`} type="number" step="0.01" placeholder="0,00"
                  {...register('preco', { required: 'Obrigatório', min: { value: 0, message: 'Valor inválido' } })} />
                {errors.preco && <span className="form-error">{errors.preco.message}</span>}
              </div>
              <div className="form-group">
                <label className="form-label">Qtd. Estoque *</label>
                <input className={`form-input${errors.qtdEstoque ? ' error' : ''}`} type="number" step="0.01" placeholder="0"
                  {...register('qtdEstoque', { required: 'Obrigatório', min: { value: 0, message: 'Valor inválido' } })} />
                {errors.qtdEstoque && <span className="form-error">{errors.qtdEstoque.message}</span>}
              </div>
            </div>
          </div>

          <div className="modal-footer">
            <button type="button" className="btn btn-ghost" onClick={onClose}>Cancelar</button>
            <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
              {isSubmitting ? <><span className="spinner" style={{ width: 14, height: 14 }} /> Salvando...</> : (isEdit ? 'Salvar Alterações' : 'Cadastrar Produto')}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
