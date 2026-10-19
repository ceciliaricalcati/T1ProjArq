# Sistema de Gestão de Planos de Voo

Backend REST em Java 21 + Spring Boot 3.4, Spring Data JPA, H2 e OpenAPI. O domínio puro e os testes existentes foram preservados; a camada HTTP e a persistência foram adicionadas para tornar o projeto executável.

## Requisitos

- JDK 21
- Maven 3.9+
- Acesso à internet na primeira compilação para baixar dependências

## Executar

Na raiz do repositório:

```bash
mvn clean test
mvn spring-boot:run
```

A aplicação sobe em `http://localhost:8080`. A documentação interativa fica em `http://localhost:8080/swagger-ui.html`; OpenAPI JSON em `/v3/api-docs`. Console H2 em `/h2-console` (JDBC URL `jdbc:h2:file:./data/voodb`, usuário `sa`, senha vazia).

O banco é persistido em arquivo local `./data/voodb`, portanto os cadastros e planos sobrevivem ao reinício. O Hibernate cria/atualiza as tabelas na inicialização.

## Data de referência e dados de demonstração

**Data de referência: 20/10/2026.** Na primeira inicialização são cadastrados os quatro aeroportos e oito aerovias do enunciado, três pilotos, aeronaves das três categorias e cinco planos de demonstração marcados como `APROVADO`. As reservas desses planos ocupam as aerovias R1, R2, R3, R4 e R5 na data de referência, permitindo demonstrar disponibilidade e conflito. A carga só cria cada cadastro se ele ainda não existir e só semeia os planos quando a tabela de planos está vazia.

## API principal

Todos os corpos e respostas usam JSON. Identificadores de piloto são strings neste protótipo.

| Método | Endpoint | Função |
|---|---|---|
| GET | `/api/aeroportos` / `/api/aeroportos/{codigo}` | Consultar aeroportos |
| POST | `/api/aeroportos` | Cadastrar aeroporto: `codigo`, `nome`, `quantidadeVagas` |
| GET | `/api/aerovias` / `/api/aerovias/{id}` | Consultar aerovias |
| POST | `/api/aerovias` | Cadastrar aerovia: `id`, `origem`, `destino`, `distanciaKm` |
| GET / POST | `/api/pilotos` | Consultar/cadastrar pilotos (`id`, `nome`) |
| GET / POST | `/api/aeronaves` | Consultar/cadastrar aeronaves |
| GET / POST | `/api/planos` | Consultar/criar plano |
| GET | `/api/planos/{id}` | Consultar plano por ID |
| PUT | `/api/planos/{id}` | Atualizar dados de plano em edição |
| PUT | `/api/planos/{id}/percurso` | Substituir sequência de trechos |
| POST | `/api/planos/{id}/validar` | Validar sem reservar |
| POST | `/api/planos/{id}/aprovar` | Validar novamente e reservar atomicamente |
| POST | `/api/planos/{id}/abandonar` | Abandonar plano em edição |
| POST | `/api/planos/{id}/cancelar` | Cancelar plano aprovado futuro e liberar reservas |
| GET | `/api/planos?status=APROVADO` | Filtrar por situação |
| GET | `/api/disponibilidade/aerovias/{id}?altitude=28000&inicio=2026-10-20T08:00:00&fim=2026-10-20T12:00:00` | Consultar slots de altitude |
| GET | `/api/disponibilidade/aeroportos/{codigo}?vaga=1&inicio=2026-10-20T08:00:00&fim=2026-10-20T12:00:00` | Consultar slots de vaga |

### Exemplo: criar um plano

```http
POST /api/planos
Content-Type: application/json

{
  "pilotoId": "P1",
  "aeronavePrefixo": "PR-PAX",
  "origem": "POA",
  "destino": "FLN",
  "partida": "2026-10-20T08:00:00"
}
```

O plano nasce em edição. Para definir o percurso:

```http
PUT /api/planos/6/percurso
Content-Type: application/json

[
  {
    "aeroviaId": "R1",
    "altitudePes": 28000,
    "entrada": "2026-10-20T08:00:00",
    "acao": "POUSO",
    "vaga": 2,
    "permanenciaMin": 30
  }
]
```

`acao` aceita `POUSO` ou `SOBREVOO`. Para pouso, informe `vaga` e `permanenciaMin`. A entrada do primeiro trecho deve ser igual à partida; entradas seguintes devem respeitar chegada calculada e permanência. Os horários são ISO-8601 local, sem offset.

Fluxo de demonstração: criar plano → definir percurso → validar → aprovar. Para verificar conflito, tente aprovar um plano que use R1/28.000 no slot de 08h de 20/10/2026. A aprovação malsucedida retorna erro e não grava reservas parciais. Planos aprovados, cancelados e abandonados continuam consultáveis.

## Modelo e arquitetura

- `domain`: entidades/objetos de valor e validadores Java puro já existentes.
- `api`: endpoints REST e tradução básica de erros HTTP.
- `application`: serviço de registros e carga inicial.
- `infrastructure`: persistência JPA genérica e reservas.
- `docs/dominio`: UML do domínio.
- `docs/arquitetura`: diagramas C4 e decisões arquiteturais.

## Limitações a resolver antes da entrega final

A camada REST/persistência adicionada é uma primeira integração funcional, não uma revisão completa de produção. O armazenamento dos cadastros e planos utiliza payload JSON genérico; recomenda-se evoluir para entidades JPA relacionais e repositórios tipados. A validação HTTP cobre percurso, horários, categoria, autonomia e ocupação, mas deve ser consolidada para reutilizar diretamente `ValidadorDePlano` do domínio, evitando duplicação de regras. Também é recomendável adicionar testes de integração HTTP, concorrência de aprovação e migrações versionadas (Flyway), além de completar validações de entrada e status HTTP específicos. Os diagramas C4 devem ser regenerados após refletir essa integração.
