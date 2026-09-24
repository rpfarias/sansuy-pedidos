# PLANO — Fases de desenvolvimento

Metodologia (combinada):
- **Uma fase por vez.** Ao fim de cada fase eu explico o que foi feito e por quê,
  listo **3 perguntas de entrevista com respostas curtas** e **PARO**, aguardando você
  dizer **"próxima fase"**.
- Partes **[EU FAÇO]**: eu crio a estrutura, escrevo os testes que devem passar e
  explico o objetivo — **não implemento**. Você implementa e depois eu **reviso como
  um entrevistador**.
- `ESTUDO.md` é atualizado a cada fase (conceitos + perguntas/respostas + erros encontrados).

Legenda: 🟩 eu faço · 🟦 [EU FAÇO] (você implementa) · 🟨 misto

---

## Fase 1 — Base do projeto 🟩
**Objetivo:** projeto Java legado empacotável como WAR, com camadas completas.
- `pom.xml`: Spring Boot 2.7.x, Java 11, `packaging=war`, starter-web, data-jpa,
  validation, postgresql, `starter-tomcat` **provided**, JUnit5/Mockito.
- Classe `Application` + `ServletInitializer extends SpringBootServletInitializer`.
- Entidades JPA (§3 do SPEC) com relacionamentos **LAZY** e enums de status.
- Repositories (Spring Data), Services (regras), DTOs (request/response), Mappers.
- Controllers REST com **status HTTP corretos** e `@ControllerAdvice` global.
- `application.properties` (perfil `dev` com Postgres; SQL log preparado p/ Fase 4).
**Entregável:** compila; endpoints básicos respondendo; sem lógica de padrões ainda.

## Fase 2 — Deploy legado (Tomcat 9 + Docker) 🟩🟨
**Objetivo:** entender a camada de servlet abaixo do Spring e o deploy de WAR.
- `docker-compose.yml`: `postgres` + `tomcat:9-jdk11`; WAR montado em `webapps`.
- Build do WAR (`mvn package`) e publicação; **sem** Tomcat embutido.
- Guia prático: ler `catalina.out`, onde fica `server.xml`, **context path**, **redeploy**.
- **Servlet puro** (`javax.servlet.http.HttpServlet`) + **Filter** (`javax.servlet.Filter`)
  para enxergar a camada abaixo do Spring.
**Entregável:** app acessível via Tomcat container; roteiro de troubleshooting.

## Fase 3 — Padrões de projeto no domínio 🟨
- **Strategy** — cálculo de preço por segmento 🟦 **[EU FAÇO]** (estrutura + testes).
- **State** — transições de status do pedido, bloqueando inválidas 🟦 **[EU FAÇO]**.
- **Factory** — geração de documento do pedido (CSV/PDF) 🟩.
- **Builder** — montagem do pedido com itens 🟩.
- **Observer (ApplicationEvent)** — aprovar pedido → gerar `OrdemProducao` 🟩.
- Explicação: padrões que o **próprio Spring** usa (IoC/DI, Singleton, Proxy,
  Template Method) 🟩.
**Entregável:** padrões plugados no fluxo; testes das partes [EU FAÇO] prontos e falhando.

## Fase 4 — Laboratório JPA/Hibernate 🟨
Com **SQL logado** e **contagem de queries** (Hibernate statistics / `p6spy`).
- **N+1** ao listar pedidos com itens → correção com `JOIN FETCH` e `@EntityGraph`
  🟦 **[EU FAÇO a correção]**.
- Reproduzir **LazyInitializationException** e mostrar as formas corretas de resolver 🟩.
- **Reserva de metragem** de bobina com `@Transactional`: faltou estoque → rollback total 🟩.
- **Lock otimista** com `@Version`: duas reservas simultâneas → `OptimisticLockException`
  🟦 **[EU FAÇO o tratamento]**.
- `@Transactional` em **auto-invocação** (mesma classe) e por que é o **proxy** 🟩.
- Estados da entidade (**transient/managed/detached**), **cache de 1º nível**,
  `equals`/`hashCode` em entidades 🟩.
**Entregável:** provas em teste/log de cada fenômeno + correções.

## Fase 5 — Testes 🟩
- Unitários **JUnit 5 + Mockito** nos services.
- `@DataJpaTest` nos repositories.
- Um **teste de integração** de endpoint (MockMvc / `@SpringBootTest`).
**Entregável:** suíte verde cobrindo regras e camadas.

## Fase 6 — Java core (exercícios) 🟦 **[EU FAÇO todos]**
Pasta `exercicios/` com **testes já escritos e falhando**:
- Streams com `groupingBy` (ex.: total vendido por segmento), `reduce`.
- Contrato `equals`/`hashCode` em `HashMap`.
- Exceções **checked × unchecked**.
- `Optional` e manipulação de `String`.
**Entregável:** testes falhando + objetivo de cada um; você implementa, eu reviso.

## Fase 7 — SVN 🟩
- Criar repositório **SVN local**, checkout, commit, update.
- Provocar e **resolver um conflito**.
- Comparativo **SVN × Git** no `ESTUDO.md`.
**Entregável:** roteiro reproduzível + tabela comparativa.

---

## Artefatos vivos
- `SPEC.md` — escopo e regras (este pacote).
- `PLANO.md` — este arquivo.
- `ESTUDO.md` — conceitos, perguntas/respostas de entrevista, erros encontrados
  (criado no início da Fase 1 e atualizado sempre).
- `README.md` — como rodar; **por que Spring Boot 2.7 / javax.\*** (criado na Fase 1/2).

## Próximo passo
Revise `SPEC.md` e este `PLANO.md`. Ao aprovar (ou pedir ajustes), começo a **Fase 1**.
