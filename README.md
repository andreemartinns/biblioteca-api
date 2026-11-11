# API da Biblioteca

API REST para gerenciar livros, autores, categorias, usuários e empréstimos. Projeto final de Desenvolvimento de Web Services (Senac) - Partes 1 e 2.

**Aluno:** André Luiz Jesus Martins

## Links úteis

- **Front-end (Vercel):** https://biblioteca-front-two.vercel.app
- **API (Render):** https://biblioteca-api-0ikp.onrender.com
- **Swagger UI (produção):** https://biblioteca-api-0ikp.onrender.com/swagger-docs.html
- **Swagger UI (local):** http://localhost:8080/swagger-docs.html
- **Repositório do front-end:** https://github.com/andreemartinns/biblioteca-front
- Console H2 (local): http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:bibliotecadb`, usuário `sa`, senha vazia)
- Chave ADMIN de demonstração: `admin-demo-key-123`

> O servidor da Render é gratuito e "dorme" após alguns minutos sem uso. A primeira requisição pode levar cerca de 1 minuto.

## Tecnologias

- Java 17
- Spring Boot 4.1.1
- Spring Data JPA + H2 (banco em memória)
- Bean Validation
- Spring HATEOAS
- Springdoc OpenAPI (Swagger)
- Maven
- Docker (deploy na Render)

## Como executar

1. Requisitos: JDK 17+ instalado.
2. Clone o repositório e entre na pasta do projeto.
3. Execute: `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`)
4. A API sobe em `http://localhost:8080`.

## Modelo de dados

- Usuario 1-1 PerfilUsuario
- Usuario 1-N Emprestimo
- Livro 1-N Emprestimo
- Categoria 1-N Livro
- Livro N-N Autor
- Enum: StatusEmprestimo (ATIVO, DEVOLVIDO, ATRASADO)

## Endpoints principais

| Recurso | Base | Extras |
|---|---|---|
| Categorias | `/categorias` | `/busca?nome=` |
| Autores | `/autores` | `/busca?nome=` |
| Livros | `/livros` | `/busca?titulo=` |
| Usuários | `/usuarios` | `/busca?nome=` |
| Perfis | `/perfis` | `/busca?telefone=` |
| Empréstimos | `/emprestimos` | `/status/{status}`, `PATCH /{id}/devolucao` |
| Chaves de API | `/api-keys` | `PATCH /{id}/revogar` |

Todas as listagens são paginadas (`?page=0&size=10&sort=nome,asc`) e retornam links HATEOAS.

## Tratamento de erros

- 400: dados inválidos ou cabeçalho obrigatório ausente
- 401: API key ausente, inválida ou revogada
- 403: chave sem nível de acesso suficiente ou origem CORS não permitida
- 404: recurso ou rota não encontrados
- 405: método HTTP não suportado
- 409: conflito (valor duplicado ou registro em uso)
- 429: limite de requisições excedido
- 500: erro interno

## Arquitetura

Controller → Service → Repository → Entity, com tratamento global de exceções (`@RestControllerAdvice`).
Os recursos da Parte 2 são implementados como filtros (`OncePerRequestFilter`) no pacote `security`, sem bibliotecas extras.

## Parte 2: recursos avançados

Ordem dos filtros: CORS → Rate Limit → API Key → Idempotência → Controller.

### 1. Idempotência (`X-Idempotency-Key`)

Evita que o mesmo POST seja processado duas vezes. Se o cliente repetir a requisição com a mesma chave, a API devolve a resposta original com o cabeçalho `Idempotent-Replayed: true`, sem criar um novo registro.

- **Obrigatório** em `POST /emprestimos` (sem o cabeçalho: `400 Bad Request`).
- Opcional nos demais POSTs.
- As respostas ficam guardadas por 1 hora (`app.idempotency.ttl-seconds`).

### 2. API Key (`X-API-Key`)

| Operação | Exige |
|---|---|
| `GET` | Nada (público) |
| `POST`, `PUT`, `PATCH` | Qualquer chave válida (USUARIO ou ADMIN) |
| `DELETE` | Chave ADMIN |
| `POST /api-keys` | Nada (público, gera uma chave USUARIO) |
| `GET /api-keys`, `PATCH /api-keys/{id}/revogar` | Chave ADMIN |

- Chave ausente ou inválida: `401 Unauthorized`.
- Chave válida sem permissão: `403 Forbidden`.
- Chave de demonstração ADMIN: `admin-demo-key-123` (recriada a cada inicialização).

### 3. Rate Limiting

Limite de 30 requisições por 60 segundos por cliente (identificado pela API Key ou, na falta dela, pelo IP). Ao exceder, a API retorna `429 Too Many Requests` com o cabeçalho `Retry-After`. Todas as respostas trazem `X-RateLimit-Limit` e `X-RateLimit-Remaining`. Os valores são configuráveis em `application.properties`.

### 4. CORS

Origens permitidas: `https://biblioteca-front-two.vercel.app`, `http://localhost:3000`, `http://localhost:5173` e `http://localhost:4200`. Métodos permitidos: GET, POST, PUT, PATCH, DELETE e OPTIONS. Configurável em `app.cors.allowed-origins`.

### 5. Versionamento (`X-API-Version`)

`GET /livros/{id}`:
- Sem o cabeçalho (ou `X-API-Version: 1`): versão 1, completa e com HATEOAS.
- Com `X-API-Version: 2`: versão 2, resumida, com o nome da categoria e a lista de nomes dos autores.

### Limitações

Chaves, idempotência e rate limit ficam em memória e são zerados ao reiniciar a aplicação. Em produção, usaria Redis e um banco persistente.

## Front-end

Interface em HTML, CSS e JavaScript puros, publicada na Vercel: https://biblioteca-front-two.vercel.app

- Lista, cria e exclui livros (com paginação).
- Registra empréstimos (envia `X-Idempotency-Key`) e devoluções.
- Cadastra categorias, autores e usuários.
- Gera uma chave de API pelo botão "Gerar chave" e a envia em `X-API-Key` nas operações de escrita.

## Testes

A coleção do Postman está em `API de Gestão de Biblioteca Comunitária.postman_collection`. Para as requisições de escrita, configure a autorização da coleção (API Key, `X-API-Key`).