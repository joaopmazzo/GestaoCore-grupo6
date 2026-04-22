import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useAuth } from '../contexts/AuthContext';
import api from '../api/axios';

export default function Login() {
  const { login } = useAuth();
  const [error, setError] = useState('');
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm();

  const onSubmit = async (data) => {
    setError('');
    try {
      const res = await api.post('usuario/login', { email: data.email, senha: data.senha });
      login(res.data.accessToken);
    } catch (err) {
      setError('Email ou senha inválidos. Tente novamente.');
    }
  };

  return (
    <div style={{
      minHeight: '100vh',
      background: 'var(--bg-primary)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: 24,
    }}>
      {/* Background glow */}
      <div style={{
        position: 'fixed', top: '20%', left: '50%', transform: 'translateX(-50%)',
        width: 600, height: 300,
        background: 'radial-gradient(ellipse, rgba(96,165,250,0.08) 0%, transparent 70%)',
        pointerEvents: 'none',
      }} />

      <div style={{ width: '100%', maxWidth: 420, position: 'relative' }}>
        {/* Logo */}
        <div style={{ textAlign: 'center', marginBottom: 40 }}>
          <div style={{
            width: 56, height: 56,
            background: 'linear-gradient(135deg, #60a5fa, #a78bfa)',
            borderRadius: 16,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontWeight: 800, fontSize: 20, color: '#fff',
            margin: '0 auto 16px',
            boxShadow: '0 0 32px rgba(96,165,250,0.25)',
          }}>GC</div>
          <h1 style={{ fontSize: 26, fontWeight: 700, letterSpacing: '-0.02em' }}>GestaoCore</h1>
          <p style={{ color: 'var(--text-secondary)', marginTop: 6, fontSize: 14 }}>
            Sistema de Gestão — Grupo 6
          </p>
        </div>

        {/* Card */}
        <div className="card" style={{ padding: 32 }}>
          <h2 style={{ fontSize: 18, fontWeight: 600, marginBottom: 6 }}>Entrar</h2>
          <p style={{ color: 'var(--text-secondary)', fontSize: 14, marginBottom: 24 }}>
            Acesse com suas credenciais
          </p>

          {error && <div className="alert alert-error">{error}</div>}

          <form onSubmit={handleSubmit(onSubmit)} style={{ display: 'flex', flexDirection: 'column', gap: 18 }}>
            <div className="form-group">
              <label className="form-label">Email</label>
              <input
                className={`form-input${errors.email ? ' error' : ''}`}
                type="email"
                placeholder="seu@email.com"
                {...register('email', { required: 'Email é obrigatório' })}
              />
              {errors.email && <span className="form-error">{errors.email.message}</span>}
            </div>

            <div className="form-group">
              <label className="form-label">Senha</label>
              <input
                className={`form-input${errors.senha ? ' error' : ''}`}
                type="password"
                placeholder="••••••••"
                {...register('senha', { required: 'Senha é obrigatória' })}
              />
              {errors.senha && <span className="form-error">{errors.senha.message}</span>}
            </div>

            <button
              type="submit"
              className="btn btn-primary"
              disabled={isSubmitting}
              style={{ width: '100%', justifyContent: 'center', marginTop: 4, padding: '12px 18px', fontSize: 15 }}
            >
              {isSubmitting ? (
                <>
                  <span className="spinner" style={{ width: 16, height: 16 }} />
                  Entrando...
                </>
              ) : 'Entrar no sistema'}
            </button>
          </form>
        </div>

        <p style={{ textAlign: 'center', color: 'var(--text-muted)', fontSize: 12, marginTop: 24 }}>
          GestaoCore © 2025 — Grupo 6
        </p>
      </div>
    </div>
  );
}
