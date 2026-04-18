import { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import api from '../api/axios';

export default function ClienteModal({ cliente, onClose, onSaved }) {
  const isEdit = !!cliente;
  const { register, handleSubmit, formState: { errors, isSubmitting }, reset } = useForm({
    defaultValues: cliente || {},
  });

  useEffect(() => { reset(cliente || {}); }, [cliente]);

  const onSubmit = async (data) => {
    try {
      if (isEdit) {
        await api.put(`/cliente/${cliente.id}`, data);
      } else {
        await api.post('/cliente', data);
      }
      onSaved();
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="modal-backdrop" onClick={(e) => e.target === e.currentTarget && onClose()}>
      <div className="modal modal-wide">
        <div className="modal-header">
          <h2 className="modal-title">{isEdit ? 'Editar Cliente' : 'Novo Cliente'}</h2>
          <button className="btn-icon modal-close" onClick={onClose}>
            <svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
              <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>

        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="form-grid" style={{ marginBottom: 16 }}>
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
              <label className="form-label">Telefone *</label>
              <input className={`form-input${errors.telefone ? ' error' : ''}`} placeholder="(11) 90000-0000"
                {...register('telefone', { required: 'Obrigatório' })} />
              {errors.telefone && <span className="form-error">{errors.telefone.message}</span>}
            </div>
            <div className="form-group">
              <label className="form-label">CEP *</label>
              <input className={`form-input${errors.cep ? ' error' : ''}`} placeholder="00000-000"
                {...register('cep', { required: 'Obrigatório' })} />
              {errors.cep && <span className="form-error">{errors.cep.message}</span>}
            </div>
            <div className="form-group">
              <label className="form-label">Rua *</label>
              <input className={`form-input${errors.rua ? ' error' : ''}`} placeholder="Nome da rua"
                {...register('rua', { required: 'Obrigatório' })} />
              {errors.rua && <span className="form-error">{errors.rua.message}</span>}
            </div>
            <div className="form-group">
              <label className="form-label">Número *</label>
              <input className={`form-input${errors.numero ? ' error' : ''}`} placeholder="123"
                {...register('numero', { required: 'Obrigatório' })} />
              {errors.numero && <span className="form-error">{errors.numero.message}</span>}
            </div>
            <div className="form-group">
              <label className="form-label">Cidade *</label>
              <input className={`form-input${errors.cidade ? ' error' : ''}`} placeholder="São Paulo"
                {...register('cidade', { required: 'Obrigatório' })} />
              {errors.cidade && <span className="form-error">{errors.cidade.message}</span>}
            </div>
            <div className="form-group">
              <label className="form-label">Estado *</label>
              <input className={`form-input${errors.estado ? ' error' : ''}`} placeholder="SP"
                {...register('estado', { required: 'Obrigatório' })} />
              {errors.estado && <span className="form-error">{errors.estado.message}</span>}
            </div>
          </div>
          <div className="form-group" style={{ marginBottom: 0 }}>
            <label className="form-label">Complemento</label>
            <input className="form-input" placeholder="Apto, bloco, etc." {...register('complemento')} />
          </div>

          <div className="modal-footer">
            <button type="button" className="btn btn-ghost" onClick={onClose}>Cancelar</button>
            <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
              {isSubmitting ? <><span className="spinner" style={{ width: 14, height: 14 }} /> Salvando...</> : (isEdit ? 'Salvar Alterações' : 'Cadastrar Cliente')}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
