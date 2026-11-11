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

    private static final String DESCRICAO = """
            # Gestão de Acervo e Empréstimos

            API REST para bibliotecas comunitárias e escolares gerenciarem livros, autores, categorias, \
            usuários e empréstimos. Respostas em JSON (UTF-8) com links de navegação HATEOAS em `_links`. \
            Todas as listagens são paginadas (`?page=0&size=10&sort=nome,asc`).

            ## Primeiros passos

            1. Gere uma chave em `POST /api-keys` (rota pública) ou use a chave ADMIN de demonstração.
            2. Clique em **Authorize**, no topo desta página, e informe a chave uma única vez.
            3. Consultas `GET` funcionam sem chave. Para criar, alterar ou excluir, a chave é obrigatória.

            > **Ambiente de demonstração:** o banco é H2 em memória e os dados, as chaves e os contadores são \
            zerados a cada reinício. O servidor gratuito "dorme" após alguns minutos sem uso, e a primeira \
            requisição pode levar cerca de 1 minuto.

            ## Autenticação (`X-API-Key`)

            | Operação | Chave exigida |
            |---|---|
            | `GET` | Nenhuma (público) |
            | `POST`, `PUT`, `PATCH` | Qualquer chave válida (nível USUARIO ou ADMIN) |
            | `DELETE` | Chave de nível ADMIN |
            | `POST /api-keys` | Nenhuma (público, gera chave USUARIO) |
            | `GET /api-keys` e `PATCH /api-keys/{id}/revogar` | Chave de nível ADMIN |

            - `401 Unauthorized`: chave ausente, inválida ou revogada (a API não sabe quem você é).
            - `403 Forbidden`: chave válida, mas sem permissão para a operação.

            ## Idempotência (`X-Idempotency-Key`)

            Evita que o mesmo `POST` seja processado duas vezes (por exemplo, quando a conexão cai e o cliente reenvia).

            - **Obrigatório** em `POST /emprestimos`. Sem o cabeçalho, a resposta é `400 Bad Request`.
            - Opcional nos demais `POST`.
            - Use um valor único por operação, como um UUID.
            - Ao repetir a mesma chave, a API **não cria um novo registro**: devolve a resposta original, \
            com o cabeçalho `Idempotent-Replayed: true`.
            - As respostas de sucesso (2xx) ficam guardadas por 1 hora.

            ## Limite de requisições (rate limit)

            Cada cliente (identificado pela API Key ou, na falta dela, pelo IP) pode fazer **30 requisições por 60 segundos**.

            | Cabeçalho da resposta | Significado |
            |---|---|
            | `X-RateLimit-Limit` | Limite da janela |
            | `X-RateLimit-Remaining` | Requisições restantes na janela |
            | `Retry-After` | Segundos de espera (só no `429`) |

            Ao exceder o limite, a API responde `429 Too Many Requests`.

            ## CORS

            Navegadores só aceitam chamadas vindas de origens liberadas pela API.

            - **Origens permitidas:** `https://biblioteca-front-two.vercel.app`, `http://localhost:3000`, \
            `http://localhost:5173` e `http://localhost:4200`.
            - **Métodos permitidos:** `GET`, `POST`, `PUT`, `PATCH`, `DELETE` e `OPTIONS`.
            - **Cabeçalhos aceitos:** `Content-Type`, `Accept`, `X-API-Key`, `X-Idempotency-Key` e `X-API-Version`.
            - **Cabeçalhos legíveis pelo front:** `Retry-After`, `X-RateLimit-Limit`, `X-RateLimit-Remaining`, \
            `Idempotent-Replayed` e `Location`.
            - Origem não liberada: `403 Forbidden`. Para liberar outra origem, é preciso incluí-la na configuração do servidor.

            ## Versionamento (`X-API-Version`)

            `GET /livros/{id}` possui duas versões, escolhidas pelo cabeçalho:

            | Cabeçalho | Resposta |
            |---|---|
            | ausente ou `X-API-Version: 1` | Versão 1: livro completo, com links HATEOAS |
            | `X-API-Version: 2` | Versão 2: resumida, com o nome da categoria e a lista de nomes dos autores |

            O Swagger UI mostra apenas uma das versões, porque as duas usam o mesmo caminho. Para testar a versão 2, \
            use o Postman ou o `curl`.

            ## Tratamento de erros

            Todos os erros retornam JSON com `timestamp`, `status` e `erro` (ou `mensagem`). Erros de validação \
            incluem o objeto `campos`, com a mensagem de cada campo inválido.

            | Código | Quando acontece |
            |---|---|
            | `400` | Dados inválidos, JSON malformado ou cabeçalho obrigatório ausente |
            | `401` | API key ausente, inválida ou revogada |
            | `403` | Chave sem nível de acesso suficiente ou origem CORS não permitida |
            | `404` | Recurso ou rota não encontrados |
            | `405` | Método HTTP não suportado na rota |
            | `409` | Conflito: valor duplicado (ISBN, e-mail) ou registro em uso |
            | `429` | Limite de requisições excedido |
            | `500` | Erro inesperado no servidor |

            ## Ordem de criação dos dados

            Livros dependem de **categoria** e **autor**; empréstimos dependem de **usuário** e **livro**. \
            Crie nesta ordem: categorias, autores, livros, usuários e, por fim, empréstimos.
            """;

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gestão de Biblioteca Comunitária")
                        .version("1.0")
                        .description(DESCRICAO)
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