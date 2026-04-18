# GestaoCore — Frontend

Interface web do sistema de gestão de clientes, produtos e estoque. Desenvolvido com **React + Vite**, consome a API Spring Boot do projeto.

---

## Pré-requisitos

| Ferramenta | Versão mínima |
|------------|--------------|
| Node.js | 18+ |
| npm | 9+ |
| Java (JDK) | 21 |

---

## Como rodar

### 1. Backend (API Spring Boot)

```bash
cd gestaocore-grupo6-api
./gradlew bootRun
```

A API sobe em **`http://localhost:8080`**.

Ao iniciar, o Flyway cria as tabelas automaticamente e insere um usuário admin para login inicial.

> **Credenciais padrão:** confirmar com o responsável pelo repositório (senha em BCrypt no migration SQL).

---

### 2. Frontend (React)

Em um **novo terminal**:

```bash
cd gestaocore-frontend
npm install       # apenas na primeira vez
npm run dev
```

Acesse em **`http://localhost:5173`**.

---

## Telas disponíveis

| Rota | Tela |
|------|------|
| `/login` | Login com autenticação JWT |
| `/dashboard` | Visão geral — cards de resumo + tabelas rápidas |
| `/clientes` | CRUD de clientes |
| `/produtos` | CRUD de produtos com alerta de estoque baixo |
| `/usuarios` | CRUD de usuários com perfis ADMIN/USER |

---

## Variáveis de configuração

O endereço da API fica definido em `src/api/axios.js`:

```js
const api = axios.create({
  baseURL: 'http://localhost:8080', // altere se a API rodar em outra porta
});
```

---

## Build para produção

```bash
npm run build
# Gera os arquivos em dist/
```

---

## Tecnologias

- [React](https://react.dev) — interface
- [Vite](https://vite.dev) — bundler
- [React Router DOM](https://reactrouter.com) — rotas SPA
- [Axios](https://axios-http.com) — chamadas HTTP
- [React Hook Form](https://react-hook-form.com) — formulários
