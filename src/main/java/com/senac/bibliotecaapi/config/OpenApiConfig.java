package com.senac.bibliotecaapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gestão de Biblioteca Comunitária")
                        .version("1.0")
                        .description("# Gestão de Acervo e Empréstimos\n" +
                                "\n" +
                                "API REST desenvolvida para bibliotecários e administradores de bibliotecas comunitárias e escolares gerenciarem o acervo de livros, o cadastro de autores e categorias, os usuários cadastrados e o controle de empréstimos e devoluções. Permite consultar a disponibilidade de livros, acompanhar prazos de devolução e identificar empréstimos em atraso. Os dados são expostos em JSON, com links de navegação (HATEOAS) nas respostas. Operações de escrita exigem o cabeçalho `X-API-Key`.\n" +
                                "\n" +
                                "## Endpoint Details\n" +
                                "\n" +
                                "- A API está publicada em `https://biblioteca-api-0ikp.onrender.com`.\n" +
                                "- Todas as respostas usam codificação UTF-8 e formato JSON.\n" +
                                "- Projeto em Java com Spring Boot e Maven (pacote `com.senac.bibliotecaapi`).\n" +
                                "- O banco de dados é o H2 (em memória), acessível pelo H2 Console em `/h2-console`.\n" +
                                "- A documentação interativa (Swagger UI) fica em `/swagger-docs.html` e a especificação OpenAPI em `/v3/api-docs`.\n" +
                                "\n" +
                                "## Autenticação e Cabeçalhos\n" +
                                "\n" +
                                "| Cabeçalho | Quando usar |\n" +
                                "|---|---|\n" +
                                "| `Content-Type: application/json` | Requisições com corpo (`POST`, `PUT`, `PATCH`) |\n" +
                                "| `X-API-Key` | `POST`, `PUT`, `PATCH` (qualquer chave válida) e `DELETE` (chave ADMIN) |\n" +
                                "| `X-Idempotency-Key` | Obrigatório em `POST /emprestimos`; opcional nos demais `POST` |\n" +
                                "| `X-API-Version` | Use `2` em `GET /livros/{id}` para a resposta resumida (versão 2) |\n" +
                                "\n" +
                                "As consultas `GET` são públicas. Gere uma chave em `POST /api-keys` (rota pública) ou use a chave ADMIN de demonstração. Clique em **Authorize** no topo da página para informar a chave uma única vez.\n" +
                                "\n" +
                                "### Limite de requisições\n" +
                                "\n" +
                                "Cada cliente pode fazer 30 requisições por minuto. Ao exceder, a API retorna `429 Too Many Requests` com o cabeçalho `Retry-After` indicando quantos segundos aguardar.\n" +
                                "\n" +
                                "## Respostas e Links (HATEOAS)\n" +
                                "\n" +
                                "Cada recurso retornado inclui um objeto `_links` com URLs relacionadas. Exemplo de resposta ao criar uma categoria:\n" +
                                "\n" +
                                "```json\n" +
                                "{\n" +
                                "  \"_links\": {\n" +
                                "    \"self\": { \"href\": \"https://biblioteca-api-0ikp.onrender.com/categorias/1\" },\n" +
                                "    \"atualizar\": { \"href\": \"https://biblioteca-api-0ikp.onrender.com/categorias/1\" },\n" +
                                "    \"deletar\": { \"href\": \"https://biblioteca-api-0ikp.onrender.com/categorias/1\" },\n" +
                                "    \"categorias\": { \"href\": \"https://biblioteca-api-0ikp.onrender.com/categorias\" }\n" +
                                "  },\n" +
                                "  \"nome\": \"Romance\",\n" +
                                "  \"id\": 1\n" +
                                "}\n" +
                                "```\n" +
                                "\n" +
                                "## Resumo dos Endpoints\n" +
                                "\n" +
                                "| Recurso | Método | Rota | Descrição |\n" +
                                "|---|---|---|---|\n" +
                                "| Categorias | POST | `/categorias` | Cria uma categoria |\n" +
                                "| Categorias | GET | `/categorias` | Lista todas as categorias |\n" +
                                "| Categorias | GET | `/categorias/{id}` | Busca categoria por ID |\n" +
                                "| Categorias | GET | `/categorias/busca?nome=` | Busca categoria por nome |\n" +
                                "| Categorias | PUT | `/categorias/{id}` | Atualiza uma categoria |\n" +
                                "| Categorias | DELETE | `/categorias/{id}` | Exclui uma categoria |\n" +
                                "| Autores | POST | `/autores` | Cria um autor |\n" +
                                "| Autores | GET | `/autores` | Lista todos os autores |\n" +
                                "| Autores | GET | `/autores/{id}` | Busca autor por ID |\n" +
                                "| Autores | GET | `/autores/busca?nome=` | Busca autor por nome |\n" +
                                "| Autores | PUT | `/autores/{id}` | Atualiza um autor |\n" +
                                "| Autores | DELETE | `/autores/{id}` | Exclui um autor |\n" +
                                "| Livros | POST | `/livros` | Cria um livro |\n" +
                                "| Livros | GET | `/livros` | Lista todos os livros |\n" +
                                "| Livros | GET | `/livros/{id}` | Busca livro por ID (v1 ou v2 via `X-API-Version`) |\n" +
                                "| Livros | GET | `/livros/busca?titulo=` | Busca livro por título |\n" +
                                "| Livros | PUT | `/livros/{id}` | Atualiza um livro |\n" +
                                "| Livros | DELETE | `/livros/{id}` | Exclui um livro |\n" +
                                "| Usuários | POST | `/usuarios` | Cria um usuário (com perfil) |\n" +
                                "| Usuários | GET | `/usuarios` | Lista todos os usuários |\n" +
                                "| Usuários | GET | `/usuarios/{id}` | Busca usuário por ID |\n" +
                                "| Usuários | GET | `/usuarios/busca?nome=` | Busca usuário por nome |\n" +
                                "| Usuários | PUT | `/usuarios/{id}` | Atualiza um usuário |\n" +
                                "| Usuários | DELETE | `/usuarios/{id}` | Exclui um usuário |\n" +
                                "| Perfis | POST | `/perfis` | Cria um perfil |\n" +
                                "| Perfis | GET | `/perfis` | Lista todos os perfis |\n" +
                                "| Perfis | GET | `/perfis/{id}` | Busca perfil por ID |\n" +
                                "| Perfis | GET | `/perfis/busca?telefone=` | Busca perfil por telefone |\n" +
                                "| Perfis | PUT | `/perfis/{id}` | Atualiza um perfil |\n" +
                                "| Perfis | DELETE | `/perfis/{id}` | Exclui um perfil |\n" +
                                "| Empréstimos | POST | `/emprestimos` | Registra um empréstimo (exige `X-Idempotency-Key`) |\n" +
                                "| Empréstimos | GET | `/emprestimos` | Lista todos os empréstimos |\n" +
                                "| Empréstimos | GET | `/emprestimos/{id}` | Busca empréstimo por ID |\n" +
                                "| Empréstimos | GET | `/emprestimos/status/{status}` | Filtra por status (ex.: `ATIVO`) |\n" +
                                "| Empréstimos | PATCH | `/emprestimos/{id}/devolucao` | Registra a devolução |\n" +
                                "| Empréstimos | PUT | `/emprestimos/{id}` | Atualiza um empréstimo |\n" +
                                "| Empréstimos | DELETE | `/emprestimos/{id}` | Exclui um empréstimo |\n" +
                                "| Chaves de API | POST | `/api-keys` | Gera uma chave de API (público) |\n" +
                                "| Chaves de API | GET | `/api-keys` | Lista as chaves (ADMIN) |\n" +
                                "| Chaves de API | PATCH | `/api-keys/{id}/revogar` | Revoga uma chave (ADMIN) |\n" +
                                "\n" +
                                "## Categorias\n" +
                                "\n" +
                                "### Criar categoria\n" +
                                "\n" +
                                "`POST /categorias`\n" +
                                "\n" +
                                "```json\n" +
                                "{\n" +
                                "  \"nome\": \"Romance\"\n" +
                                "}\n" +
                                "```\n" +
                                "\n" +
                                "Resposta: `201 Created` com a categoria criada e seus `_links`.\n" +
                                "\n" +
                                "### Buscar por nome\n" +
                                "\n" +
                                "`GET /categorias/busca?nome=rom`\n" +
                                "\n" +
                                "Retorna as categorias cujo nome corresponde ao texto informado.\n" +
                                "\n" +
                                "### Validação\n" +
                                "\n" +
                                "O campo `nome` é obrigatório. Enviar `\"nome\": \"\"` retorna erro de validação.\n" +
                                "\n" +
                                "## Autores\n" +
                                "\n" +
                                "### Criar autor\n" +
                                "\n" +
                                "`POST /autores`\n" +
                                "\n" +
                                "```json\n" +
                                "{\n" +
                                "  \"nome\": \"Machado de Assis\",\n" +
                                "  \"nacionalidade\": \"Brasileira\"\n" +
                                "}\n" +
                                "```\n" +
                                "\n" +
                                "## Livros\n" +
                                "\n" +
                                "### Criar livro\n" +
                                "\n" +
                                "`POST /livros`\n" +
                                "\n" +
                                "```json\n" +
                                "{\n" +
                                "  \"titulo\": \"Dom Casmurro\",\n" +
                                "  \"isbn\": \"9788535911664\",\n" +
                                "  \"anoPublicacao\": 1899,\n" +
                                "  \"categoria\": { \"id\": 1 },\n" +
                                "  \"autores\": [{ \"id\": 1 }]\n" +
                                "}\n" +
                                "```\n" +
                                "\n" +
                                "A categoria e os autores devem existir antes de criar o livro. Um livro pode ter vários autores.\n" +
                                "\n" +
                                "### Buscar por título\n" +
                                "\n" +
                                "`GET /livros/busca?titulo=dom`\n" +
                                "\n" +
                                "### Versionamento\n" +
                                "\n" +
                                "`GET /livros/{id}` sem cabeçalho (ou com `X-API-Version: 1`) retorna a versão 1, completa e com HATEOAS. Com `X-API-Version: 2` retorna a versão 2, resumida, com o nome da categoria e os nomes dos autores.\n" +
                                "\n" +
                                "## Usuários e Perfis\n" +
                                "\n" +
                                "Cada usuário possui um perfil associado (relação um-para-um), criado junto com o usuário.\n" +
                                "\n" +
                                "### Criar usuário\n" +
                                "\n" +
                                "`POST /usuarios`\n" +
                                "\n" +
                                "```json\n" +
                                "{\n" +
                                "  \"nome\": \"João Silva\",\n" +
                                "  \"email\": \"joao@email.com\",\n" +
                                "  \"perfil\": {\n" +
                                "    \"telefone\": \"11999999999\",\n" +
                                "    \"endereco\": \"Rua A, 100\",\n" +
                                "    \"dataNascimento\": \"2000-05-10\"\n" +
                                "  }\n" +
                                "}\n" +
                                "```\n" +
                                "\n" +
                                "O perfil também pode ser consultado, criado, atualizado e excluído diretamente pelo recurso `/perfis`.\n" +
                                "\n" +
                                "## Empréstimos\n" +
                                "\n" +
                                "### Criar empréstimo\n" +
                                "\n" +
                                "`POST /emprestimos` (exige o cabeçalho `X-Idempotency-Key`)\n" +
                                "\n" +
                                "```json\n" +
                                "{\n" +
                                "  \"usuario\": { \"id\": 1 },\n" +
                                "  \"livro\": { \"id\": 1 },\n" +
                                "  \"dataPrevista\": \"2026-12-20\"\n" +
                                "}\n" +
                                "```\n" +
                                "\n" +
                                "O usuário e o livro devem existir. A data usa o formato `AAAA-MM-DD`. Repetir a mesma `X-Idempotency-Key` não cria um novo empréstimo: a API devolve a resposta original com o cabeçalho `Idempotent-Replayed: true`.\n" +
                                "\n" +
                                "### Filtrar por status\n" +
                                "\n" +
                                "`GET /emprestimos/status/ATIVO`\n" +
                                "\n" +
                                "### Devolver empréstimo\n" +
                                "\n" +
                                "`PATCH /emprestimos/{id}/devolucao`\n" +
                                "\n" +
                                "Não precisa de corpo. Marca o empréstimo como devolvido.\n" +
                                "\n" +
                                "## Códigos de Status\n" +
                                "\n" +
                                "| Código | Significado |\n" +
                                "|---|---|\n" +
                                "| `200 OK` | Requisição bem-sucedida |\n" +
                                "| `201 Created` | Recurso criado |\n" +
                                "| `204 No Content` | Recurso excluído (quando aplicável) |\n" +
                                "| `400 Bad Request` | Dados inválidos, JSON malformado ou cabeçalho obrigatório ausente |\n" +
                                "| `401 Unauthorized` | API key ausente, inválida ou revogada |\n" +
                                "| `403 Forbidden` | Chave válida, mas sem nível de acesso suficiente |\n" +
                                "| `404 Not Found` | Recurso ou rota não encontrados |\n" +
                                "| `405 Method Not Allowed` | Método HTTP não suportado na rota |\n" +
                                "| `409 Conflict` | Conflito com dados existentes (ex.: ISBN ou e-mail duplicado) |\n" +
                                "| `429 Too Many Requests` | Limite de requisições excedido (veja `Retry-After`) |\n" +
                                "| `500 Internal Server Error` | Erro inesperado no servidor |\n" +
                                "\n" +
                                "Os erros são tratados de forma centralizada pela classe `GlobalExceptionHandler`.\n" +
                                "\n" +
                                "## Testando a API\n" +
                                "\n" +
                                "1. Abra o Swagger UI em `/swagger-docs.html` e clique em **Authorize** para informar a chave.\n" +
                                "2. Ou importe a coleção `API de Gestão de Biblioteca Comunitária.postman_collection` no Postman.\n" +
                                "3. Execute as requisições na ordem: criar, listar, buscar, atualizar e, por último, excluir.\n" +
                                "\n" +
                                "Ordem sugerida entre as pastas: Categorias, Autores, Livros, Usuários e, por fim, Empréstimos, pois livros e empréstimos dependem de registros já criados.")
                        .contact(new Contact()
                                .name("André Luiz Jesus Martins")
                                .email("andreemartinns2@gmail.com")))
                .addServersItem(new Server()
                        .url("https://biblioteca-api-0ikp.onrender.com")
                        .description("Servidor de produção (Render)"))
                .components(new Components().addSecuritySchemes("ApiKeyAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-API-Key")
                                .description("Chave de API. Gere uma em POST /api-keys ou use a chave ADMIN de demonstração.")))
                .addSecurityItem(new SecurityRequirement().addList("ApiKeyAuth"));
    }
}