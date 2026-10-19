# ADR-001 — Arquitetura e persistência inicial

- **Status:** adotada como etapa inicial; recomenda-se evolução antes da entrega final.
- **Contexto:** o sistema precisa expor API REST, persistir dados entre reinicializações e realizar a aprovação/cancelamento com alterações coordenadas em planos e reservas.
- **Decisão:** Java 21 + Spring Boot; API com Spring MVC; persistência via Spring Data JPA e banco H2 em arquivo; reservas em tabela própria; cadastros e planos inicialmente armazenados como payload JSON indexado por tipo e chave.
- **Alternativas consideradas:** (1) armazenamento exclusivamente em memória/CSV — rejeitado por não cumprir persistência; (2) entidades JPA relacionais tipadas para cada conceito — opção preferível para versão final, mas exige mapeamentos adicionais; (3) H2 em memória — rejeitado porque perde estado ao reiniciar.
- **Justificativa:** o banco em arquivo é simples de executar na apresentação, não exige servidor externo e mantém dados entre reinicializações. A tabela de reservas separada permite consultar slots e excluir as reservas de um plano cancelado dentro de uma transação.
- **Consequências e riscos:** payload JSON genérico reduz a segurança de tipos e a capacidade de consulta relacional; regras de validação duplicadas na API e no domínio podem divergir. A próxima evolução deve normalizar entidades, centralizar regras em `ValidadorDePlano`, implementar migrações versionadas e acrescentar testes de integração/concorrência.
