# GestaoCore - Grupo 6 API

## Informações do Projeto

**GestaoCore** é uma API REST desenvolvida em Java com Spring Boot para gestão e controle de processos. O projeto utiliza autenticação JWT (JSON Web Token) para segurança e persistência de dados com JPA/Hibernate.

- **Nome do Projeto**: gestaocore-grupo6-api
- **Versão java**: 21
- **Versão Spring Boot**: 4.0.5

---

## Bibliotecas e Dependências

### Web
- **spring-boot-starter-webmvc**: Framework principal para criação de APIs REST seguindo o padrão MVC
- **spring-boot-starter-webmvc-test**: Ferramentas para testes de controllers e endpoints REST
- **spring-boot-devtools**: Ferramentas de desenvolvimento que habilitam hot reload e melhoram a produtividade
- **spring-boot-starter-security**: Implementação de segurança e autenticação/autorização
- **spring-boot-starter-security-test**: Ferramentas para testes de segurança
- **spring-boot-starter-validation**: Validação de dados de entrada com Jakarta Bean Validation
- **java-jwt (Auth0) v4.5.1**: Biblioteca para criação e validação de tokens JWT

### Database
- **spring-boot-h2console**: Console web para gerenciar o banco de dados H2
- **h2database**: Banco de dados relacional em memória, ideal para desenvolvimento e testes
- **spring-boot-starter-data-jpa**: Abstração de persistência de dados usando JPA/Hibernate
- **spring-boot-starter-flyway**: Ferramenta de versionamento e migração de schemas de banco de dados
- **spring-boot-starter-data-jpa-test**: Ferramentas para testes de repositórios e camada de dados
- **spring-boot-starter-flyway-test**: Ferramentas para testes de migrações Flyway

### Outros
- **lombok**: Reduz boilerplate code através de anotações que geram getters, setters, construtores, etc.
- **junit-platform-launcher**: Framework de testes unitários e de integração

---

## Estrutura das Controllers

<details>
<summary><h3>LoginController</h3></summary>

#####  POST /login
Autentica um usuário e retorna um token JWT.

**Request Body**:
```json
{
  "email": "string",
  "senha": "string"
}
```

**Response Body**: Sucesso (200 OK)
```json
{
  "accessToken": "string",
  "expiresIn": "string"
}
```

</details>

---

<details>
<summary><h3>UsuarioController</h3></summary>

#####  GET /usuario
Retorna a lista de todos os usuários cadastrados.

**Response Body**: Sucesso (200 OK)
```json
[
  {
    "id": "uuid",
    "nome": "string",
    "email": "string",
    "role": "string"
  }
]
```

---

#####  GET /usuario/{id}
Retorna os dados de um usuário específico por ID.

**Path Parameters**:

| Parâmetro | Tipo | Obrigatório | Descrição              |
|-----------|------|-------------|------------------------|
| id        | UUID | Sim         | ID do usuário buscado  |

**Response Body**: Sucesso (200 OK)
```json
{
  "id": "uuid",
  "nome": "string",
  "email": "string",
  "role": "string"
}
```

---

#####  POST /usuario
Cadastra um novo usuário no sistema.

**Request Body**:
```json
{
  "nome": "string",
  "email": "string",
  "senha": "string",
  "role": "string"
}
```

**Response Body**: Sucesso (201 Created)
```json
{
  "id": "uuid",
  "nome": "string",
  "email": "string",
  "role": "string"
}
```

---

#####  PUT /usuario/{id}
Atualiza os dados de um usuário existente.

**Path Parameters**:

| Parâmetro | Tipo | Obrigatório | Descrição                 |
|-----------|------|-------------|---------------------------|
| id        | UUID | Sim         | ID do usuário a atualizar |

**Request Body**:
```json
{
  "nome": "string",
  "email": "string",
  "senha": "string",
  "role": "string"
}
```

**Response Body**: Sucesso (200 OK)
```json
{
  "id": "uuid",
  "nome": "string",
  "email": "string",
  "role": "string"
}
```

---

#####  DELETE /usuario/{id}
Remove um usuário do sistema.

**Path Parameters**:

| Parâmetro | Tipo | Obrigatório | Descrição               |
|-----------|------|-------------|-------------------------|
| id        | UUID | Sim         | ID do usuário a deletar |

**Response Body**: Sucesso (204 No Content)

</details>

---

<details>
<summary><h3>ClienteController</h3></summary>

#####  GET /cliente
Retorna a lista de todos os clientes cadastrados.

**Response Body**: Sucesso (200 OK)
```json
[
  {
    "id": "uuid",
    "nome": "string",
    "email": "string",
    "telefone": "string",
    "rua": "string",
    "numero": "string",
    "cidade": "string",
    "estado": "string",
    "cep": "string",
    "complemento": "string"
  }
]
```

---

#####  GET /cliente/{id}
Retorna os dados de um cliente específico por ID.

**Path Parameters**:

| Parâmetro | Tipo | Obrigatório | Descrição             |
|-----------|------|-------------|-----------------------|
| id        | UUID | Sim         | ID do cliente buscado |

**Response Body**: Sucesso (200 OK)
```json
{
    "id": "uuid",
    "nome": "string",
    "email": "string",
    "telefone": "string",
    "rua": "string",
    "numero": "string",
    "cidade": "string",
    "estado": "string",
    "cep": "string",
    "complemento": "string"
}
```

---

#####  POST /cliente
Cadastra um novo cliente no sistema.

**Request Body**:
```json
{
    "nome": "string",       // obrigatorio
    "email": "string",      // obrigatorio
    "telefone": "string",   // obrigatorio
    "rua": "string",        // obrigatorio
    "numero": "string",     // obrigatorio
    "cidade": "string",     // obrigatorio
    "estado": "string",     // obrigatorio
    "cep": "string",        // obrigatorio
    "complemento": "string" // obrigatorio
}
```

**Response Body**: Sucesso (201 Created)
```json
{
  "id": "uuid",
  "nome": "string",
  "email": "string",
  "telefone": "string",
  "rua": "string",
  "numero": "string",
  "cidade": "string",
  "estado": "string",
  "cep": "string",
  "complemento": "string"
}
```

---

#####  PUT /cliente/{id}
Atualiza os dados de um cliente existente.

**Path Parameters**:

| Parâmetro | Tipo | Obrigatório | Descrição                 |
|-----------|------|-------------|---------------------------|
| id        | UUID | Sim         | ID do usuário a atualizar |

**Request Body**:
```json
{
  "nome": "string",
  "email": "string",
  "telefone": "string",
  "rua": "string",
  "numero": "string",
  "cidade": "string",
  "estado": "string",
  "cep": "string",
  "complemento": "string"
}
```

**Response Body**: Sucesso (200 OK)
```json
{
  "id": "uuid",
  "nome": "string",
  "email": "string",
  "telefone": "string",
  "rua": "string",
  "numero": "string",
  "cidade": "string",
  "estado": "string",
  "cep": "string",
  "complemento": "string"
}
```

---

#####  DELETE /cliente/{id}
Remove um cliente do sistema.

**Path Parameters**:

| Parâmetro | Tipo | Obrigatório | Descrição               |
|-----------|------|-------------|-------------------------|
| id        | UUID | Sim         | ID do usuário a deletar |

**Response Body**: Sucesso (204 No Content)

</details>

---

<details>
<summary><h3>ProdutoController</h3></summary>

#####  GET /produto
Retorna a lista de todos os produtos cadastrados.

**Response Body**: Sucesso (200 OK)
```json
[
  {
    "id": "uuid",
    "nome": "string",
    "categoria": "string",
    "preco": "double",
    "qtdEstoque": "double"
  }
]
```

---

#####  GET /produto/{id}
Retorna os dados de um produto específico por ID.

**Path Parameters**:

| Parâmetro | Tipo | Obrigatório | Descrição             |
|-----------|------|-------------|-----------------------|
| id        | UUID | Sim         | ID do produto buscado |

**Response Body**: Sucesso (200 OK)
```json
{
  "id": "uuid",
  "nome": "string",
  "categoria": "string",
  "preco": "double",
  "qtdEstoque": "double"
}
```

---

#####  POST /produto
Cadastra um novo produto no sistema.

**Request Body**:
```json
{
  "nome": "string",         // obrigatorio
  "categoria": "string",    // obrigatorio
  "preco": "double",        // obrigatorio
  "qtdEstoque": "double"    // obrigatorio
}
```

**Response Body**: Sucesso (201 Created)
```json
{
  "id": "uuid",
  "nome": "string",
  "categoria": "string",
  "preco": "double",
  "qtdEstoque": "double"
}
```

---

#####  PUT /produto/{id}
Atualiza os dados de um produto existente.

**Path Parameters**:

| Parâmetro | Tipo | Obrigatório | Descrição                 |
|-----------|------|-------------|---------------------------|
| id        | UUID | Sim         | ID do produto a atualizar |

**Request Body**:
```json
{
  "nome": "string",
  "categoria": "string",
  "preco": "double",
  "qtdEstoque": "double"
}
```

**Response Body**: Sucesso (200 OK)
```json
{
  "id": "uuid",
  "nome": "string",
  "categoria": "string",
  "preco": "double",
  "qtdEstoque": "double"
}
```

---

#####  DELETE /produto/{id}
Remove um produto do sistema.

**Path Parameters**:

| Parâmetro | Tipo | Obrigatório | Descrição               |
|-----------|------|-------------|-------------------------|
| id        | UUID | Sim         | ID do produto a deletar |

**Response Body**: Sucesso (204 No Content)

</details>

---

<details>
<summary><h3>VendaController</h3></summary>

#####  GET /venda
Retorna a lista de todas as vendas cadastradas.

**Response Body**: Sucesso (200 OK)
```json
[
  {
    "id": "uuid",
    "dataVenda": "2024-04-23T10:30:00",
    "clienteId": "uuid",
    "itens": [
      {
        "id": "uuid",
        "produtoId": "uuid",
        "quantidade": 2.0,
        "valorTotal": 100.00,
        "valorNaVenda": 50.00
      }
    ]
  }
]
```

---

#####  GET /venda/{id}
Retorna os dados de uma venda específica por ID.

**Path Parameters**:

| Parâmetro | Tipo | Obrigatório | Descrição             |
|-----------|------|-------------|-----------------------|
| id        | UUID | Sim         | ID da venda buscada   |

**Response Body**: Sucesso (200 OK)
```json
{
  "id": "uuid",
  "dataVenda": "2024-04-23T10:30:00",
  "clienteId": "uuid",
  "itens": [
    {
      "id": "uuid",
      "produtoId": "uuid",
      "quantidade": 2.0,
      "valorTotal": 100.00,
      "valorNaVenda": 50.00
    }
  ]
}
```

---

#####  POST /venda
Cadastra uma nova venda no sistema.

**Request Body**:
```json
{
  "dataVenda": "2024-04-23T10:30:00",      // obrigatorio
  "clienteId": "uuid",                     // obrigatorio
  "itens": [                               // obrigatorio
    {
      "produtoId": "uuid",                 // obrigatorio
      "quantidade": 2.0                    // obrigatorio
    }
  ]
}
```

**Response Body**: Sucesso (201 Created)
```json
{
  "id": "uuid",
  "dataVenda": "2024-04-23T10:30:00",
  "clienteId": "uuid",
  "itens": [
    {
      "id": "uuid",
      "produtoId": "uuid",
      "quantidade": 2.0,
      "valorTotal": 100.00,
      "valorNaVenda": 50.00
    }
  ]
}
```

---

#####  PATCH /venda/{id}/cancelar
Cancela uma venda existente.

**Path Parameters**:

| Parâmetro | Tipo | Obrigatório | Descrição              |
|-----------|------|-------------|------------------------|
| id        | UUID | Sim         | ID da venda a cancelar |

**Response Body**: Sucesso (200 OK)
```json
{
  "id": "uuid",
  "dataVenda": "2024-04-23T10:30:00",
  "clienteId": "uuid",
  "itens": [
    {
      "id": "uuid",
      "produtoId": "uuid",
      "quantidade": 2.0,
      "valorTotal": 100.00,
      "valorNaVenda": 50.00
    }
  ]
}
```

---

#####  DELETE /venda/{id}
Remove uma venda do sistema. **(Requer role ADMIN)**

**Path Parameters**:

| Parâmetro | Tipo | Obrigatório | Descrição             |
|-----------|------|-------------|-----------------------|
| id        | UUID | Sim         | ID da venda a deletar |

**Response Body**: Sucesso (204 No Content)

</details>

---

## Documentação de Referência

Para mais informações, consulte as seguintes documentações:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/4.0.5/gradle-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.0.5/gradle-plugin/packaging-oci-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/4.0.5/reference/web/servlet.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/4.0.5/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/4.0.5/reference/using/devtools.html)
* [Spring Security](https://docs.spring.io/spring-boot/4.0.5/reference/web/spring-security.html)
* [Flyway Migration](https://docs.spring.io/spring-boot/4.0.5/how-to/data-initialization.html#howto.data-initialization.migration-tool.flyway)
