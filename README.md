# API da Biblioteca

API REST para gerenciar livros, autores, categorias, usuários e empréstimos.
Projeto final de Desenvolvimento de Web Services (Senac) - Parte 1.

**Aluno:** SEU NOME

## Tecnologias
- Java 17
- Spring Boot 4.1.1
- Spring Data JPA + H2 (banco em memória)
- Bean Validation
- Spring HATEOAS
- Springdoc OpenAPI (Swagger)
- Maven

## Como executar
1. Requisitos: JDK 17+ instalado.
2. Clone o repositório e entre na pasta do projeto.
3. Execute: `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`)
4. A API sobe em `http://localhost:8080`.

## Links úteis
- Swagger UI: http://localhost:8080/swagger-ui.html
- Console H2: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:bibliotecadb`, usuário `sa`, senha vazia)

## Modelo de dados
- **Usuario** 1-1 **PerfilUsuario**
- **Usuario** 1-N **Emprestimo**
- **Livro** 1-N **Emprestimo**
- **Categoria** 1-N **Livro**
- **Livro** N-N **Autor**
- Enum: `StatusEmprestimo` (ATIVO, DEVOLVIDO, ATRASADO)

## Endpoints principais
| Recurso | Base | Extras |
|---|---|---|
| Categorias | `/categorias` | `/busca?nome=` |
| Autores | `/autores` | `/busca?nome=` |
| Livros | `/livros` | `/busca?titulo=` |
| Usuários | `/usuarios` | `/busca?nome=` |
| Perfis | `/perfis` | `/busca?telefone=` |
| Empréstimos | `/emprestimos` | `/status/{status}`, `PATCH /{id}/devolucao` |

Todas as listagens são paginadas (`?page=0&size=10&sort=nome,asc`) e retornam links HATEOAS.

## Tratamento de erros
- 400: dados inválidos
- 404: recurso não encontrado
- 409: conflito (valor duplicado ou registro em uso)
- 500: erro interno

## Arquitetura
Controller → Service → Repository → Entity, com tratamento global de exceções (`@RestControllerAdvice`).

## Testes
A coleção do Postman está em `Biblioteca API.postman_collection.json`.
