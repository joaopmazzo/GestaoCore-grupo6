import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import api from '../api/axios';

export default function UsuarioModal({ usuario, onClose, onSaved }) {
  const isEdit = !!usuario;
  const { register, handleSubmit, formState: { errors, isSubmitting }, reset } = useForm({
    defaultValues: usuario ? { ...usuario, senha: '' } : {},
  });

  useEffect(() => { reset(usuario ? { ...usuario, senha: '' } : {}); }, [usuario]);

  const onSubmit = async (data) => {
    const payload = { ...data };
    if (!payload.senha) delete payload.senha;
    try {
      if (isEdit) {
        await api.put(`/usuario/${usuario.id}`, payload);
      } else {
        await api.post('/usuario', payload);
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
          <h2 className="modal-title">{isEdit ? 'Editar Usuário' : 'Novo Usuário'}</h2>
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
              <input className={`form-input${errors.nome ? ' error' : ''}`} placeholder="Nome completo"
                {...register('nome', { required: 'Obrigatório' })} />
              {errors.nome && <span className="form-error">{errors.nome.message}</span>}
            </div>
            <div className="form-group">
              <label className="form-label">Email *</label>
              <input className={`form-input${errors.email ? ' error' : ''}`} type="email" placeholder="email@exemplo.com"
                {...register('email', { required: 'Obrigatório' })} />
              {errors.email && <span className="form-error">{errors.email.message}</span>}
            </div>
            <div className="form-group">
              <label className="form-label">Senha {isEdit ? '(deixe vazio para não alterar)' : '*'}</label>
              <input className={`form-input${errors.senha ? ' error' : ''}`} type="password" placeholder="••••••••"
                {...register('senha', { required: isEdit ? false : 'Obrigatório' })} />
              {errors.senha && <span className="form-error">{errors.senha.message}</span>}
            </div>
            <div className="form-group">
              <label className="form-label">Role *</label>
              <select className={`form-input${errors.role ? ' error' : ''}`}
                {...register('role', { required: 'Obrigatório' })}>
                <option value="">Selecione...</option>
                <option value="ADMIN">ADMIN</option>
                <option value="USER">USER</option>
              </select>
              {errors.role && <span className="form-error">{errors.role.message}</span>}
            </div>
          </div>

          <div className="modal-footer">
            <button type="button" className="btn btn-ghost" onClick={onClose}>Cancelar</button>
            <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
              {isSubmitting ? <><span className="spinner" style={{ width: 14, height: 14 }} /> Salvando...</> : (isEdit ? 'Salvar Alterações' : 'Cadastrar Usuário')}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
