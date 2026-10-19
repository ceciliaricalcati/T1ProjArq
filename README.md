# Documentação de arquitetura e domínio

Os diagramas são código-fonte (PlantUML) versionado no Git; os PNGs em `img/` são gerados a partir deles.

| Diagrama | Fonte | Imagem |
|---|---|---|
| C4 Nível 1: Contexto | `arquitetura/c4-1-contexto.puml` | `img/c4-1-contexto.png` |
| C4 Nível 2: Contêineres | `arquitetura/c4-2-conteineres.puml` | `img/c4-2-conteineres.png` |
| C4 Nível 3: Componentes da API | `arquitetura/c4-3-componentes.puml` | `img/c4-3-componentes.png` |
| UML do domínio | `dominio/uml-dominio.puml` | `img/uml-dominio.png` |
| UML do domínio (Mermaid, renderizado pelo GitHub) | `dominio/uml-dominio.mmd` | |

## Como regenerar as imagens

    plantuml -tpng -o ../img arquitetura/*.puml dominio/*.puml

(ou use a extensão PlantUML do VS Code / IntelliJ). Os arquivos usam a biblioteca C4 embutida no PlantUML
(`!include <C4/...>`), então não dependem de acesso à internet.

## Estado de implementação (manter atualizado)

O modelo UML e os componentes da camada **domain** correspondem ao código em `src/main/java/br/edu/voo/domain`.
Os componentes das camadas **api**, **application** e **infrastructure** do C4 Nível 3 são a arquitetura
definida e ainda **serão implementados** nas próximas etapas; atualize esta seção a cada etapa entregue,
para que os diagramas e a implementação continuem consistentes (critério de avaliação do enunciado).
\n> Observação: após atualizar os arquivos `.puml`, regenere as imagens PNG antes da entrega final; as imagens versionadas podem ainda refletir uma versão anterior dos diagramas.\n