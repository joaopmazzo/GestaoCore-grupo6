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

### - LoginController
#####  POST /login
Autentica um usuário e retorna um token JWT.

**Request Body**:
```json
{
  "email": "string",
  "senha": "string"
}
```

| Campo | Tipo   | Obrigatório | Descrição                          |
|-------|--------|-------------|------------------------------------|
| email | String | Sim         | Email do usuário para autenticação |
| senha | String | Sim         | Senha do usuário                   |

**Response Body**: Sucesso (200 OK)
```json
{
  "accessToken": "string",
  "expiresIn": "string"
}
```
| Campo       | Tipo   | Descrição                                          |
|-------------|--------|----------------------------------------------------|
| accessToken | String | Token JWT para autenticação em requisições futuras |
| expiresIn   | String | Data/hora de expiração do token                    |

**Response Body**: Erro (400 Bad Request)
```json
"string"
```
Retorna uma mensagem de erro descrevendo o problema ocorrido durante a autenticação (credenciais inválidas, usuário não encontrado, etc.)

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
