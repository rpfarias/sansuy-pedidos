# ESTUDO — caderno de conceitos e perguntas de entrevista

Arquivo vivo. A cada fase acumulamos: **conceitos**, **perguntas de entrevista
com respostas curtas** e os **erros encontrados** (com a correção).

---

## Fase 1 — Base do projeto

### Conceitos praticados

- **Arquitetura em camadas** (Web → Serviço → Persistência) com regra de
  dependência só para baixo. Detalhes em `ARQUITETURA.md`.
- **Fronteira de DTO**: o controller nunca devolve `@Entity`; converte para DTO
  dentro da transação. Evita `LazyInitializationException` e não vaza o modelo.
- **Spring Data JPA**: repositórios como interfaces; o Spring gera a
  implementação em runtime (Proxy).
- **`@Transactional` no serviço**: a camada de serviço é a dona da transação;
  `readOnly = true` nas leituras.
- **Bean Validation** (`javax.validation`) com `@Valid` + tratamento central de
  erros no `@RestControllerAdvice`.
- **Status HTTP corretos**: 201 + `Location` na criação, 200 na leitura, 404
  para recurso inexistente, 400 para validação/regra.
- **Empacotamento WAR** com `SpringBootServletInitializer` e
  `starter-tomcat` **provided**.
- **Namespace `javax.*`** (Spring Boot 2.7 / Java 8) — legado proposital.

### Perguntas de entrevista (Fase 1)

**1) Por que devolver DTO em vez da entidade JPA no controller?**
Porque a entidade tem associações LAZY que, ao serializar fora da transação,
disparam `LazyInitializationException`; além disso, expor a entidade vaza o
modelo interno, acopla a API ao banco e pode causar serialização recursiva
(`Pedido ↔ Item`). O DTO é um contrato estável e sob controle.

**2) Qual a diferença entre `@Controller` e `@RestController`?**
`@RestController` = `@Controller` + `@ResponseBody` em todos os métodos: o retorno
é serializado direto no corpo da resposta (JSON), sem passar por resolução de
view. `@Controller` sozinho é usado quando se retorna o nome de uma view (MVC
tradicional com template).

**3) Qual o papel do `SpringBootServletInitializer` e por que o Tomcat é
`provided`?**
Ele registra a aplicação Spring Boot no ciclo de vida de um **servlet container
externo** (via `ServletContainerInitializer`), substituindo o `main()` quando o
WAR roda dentro de um Tomcat standalone. O `spring-boot-starter-tomcat` fica
`provided` para **não** empacotar um Tomcat embutido no WAR — o container é o do
deploy. Se fosse `compile`, haveria conflito (dois containers).

### Erros encontrados (Fase 1)

- **Autenticação Postgres falhou (`FATAL: senha falhou para o usuário "sansuy"`).**
  Causa: existia o *database* `sansuy`, mas não o *role* de login `sansuy`.
  Database e role são coisas distintas no Postgres. Correção:
  `CREATE ROLE sansuy WITH LOGIN PASSWORD 'sansuy';` +
  `ALTER DATABASE sansuy OWNER TO sansuy;` (o `OWNER` é necessário no PG 15+
  para o role poder criar tabelas no schema `public`, dono do banco).

- **Escala de `BigDecimal` inconsistente no `subtotal`.** No POST o valor saía
  com 4 casas (`2268.0000`) e no GET com 2 (`2268.00`). Causa:
  `metragem(scale 2).multiply(preco(scale 2))` produz escala 4 em memória, mas a
  coluna é `scale=2`; ao reler do banco a escala cai para 2. Correção:
  `.setScale(2, RoundingMode.HALF_UP)` no cálculo do subtotal (alinha com o SPEC).
  Lição: `multiply` **soma** as escalas dos operandos; sempre normalize a escala
  de valores monetários explicitamente.

- **`Failed to introspect Class [ManipuladorGlobalExcecoes] from ClassLoader`
  ao rodar pelo IntelliJ.** Causa real (no último `Caused by:`):
  `NoClassDefFoundError: javax/servlet/http/HttpServletRequest`. A classe usa
  `HttpServletRequest`, que vem do servlet-api fornecido pelo
  `spring-boot-starter-tomcat` com escopo **`provided`**. O IntelliJ, por padrão,
  **não** inclui dependências `provided` no classpath de execução (o
  `mvn spring-boot:run` inclui). Correção: na run configuration, `Modify options`
  → marcar **"Include dependencies with 'Provided' scope"**.
  Lições de entrevista: (1) `Failed to introspect Class ... from ClassLoader`
  significa "classe referenciada ausente no classpath" — leia sempre o último
  `Caused by`; (2) `provided` = compila mas não empacota/roda (o container
  fornece), por isso o WAR não leva Tomcat embutido; (3) IDE e Maven montam o
  classpath de forma diferente.

### Decisões em aberto (revisitar nas fases indicadas)

- **Contagem de queries (Fase 4):** Hibernate Statistics (default) vs p6spy.
- **Congelar `precoUnitario` no item:** assumido que sim (subtotal estável).

---

## Fase 2 — Deploy legado (Tomcat 9 standalone + Docker)

### Conceitos praticados

- **WAR em Tomcat standalone** vs Tomcat **embutido**: no deploy legado o
  container é externo; o WAR não leva servlet container dentro (por isso o
  `starter-tomcat` é `provided`).
- **`docker-compose`** com Postgres + `tomcat:9-jdk8`; WAR publicado via
  **bind-mount** em `/usr/local/tomcat/webapps/`.
- **Context path** derivado do nome do WAR (`sansuy-pedidos.war` →
  `/sansuy-pedidos`); `ROOT.war` serve na raiz.
- **`catalina.out`** (log central do Tomcat) e **`server.xml`**
  (`conf/server.xml`): `Connector` (porta), `Host` (`appBase=webapps`,
  `autoDeploy`, `unpackWARs`), hierarquia Server→Service→Engine→Host→Context.
- **Redeploy** com `autoDeploy=true`: regerar o WAR e o Tomcat republica.
- **Config externa vence o properties**: env vars `SPRING_DATASOURCE_*`
  (relaxed binding) sobrepõem `application.properties` — hostname `db` na rede
  do compose no lugar de `localhost`.
- **Servlet puro** (`@WebServlet`/`HttpServlet`) e **Filter** (`@WebFilter`/
  `javax.servlet.Filter`): a camada abaixo do Spring. O filtro `/*` envolve
  inclusive o DispatcherServlet; o servlet é roteado direto pelo container.
- **`@ServletComponentScan`**: registra `@WebServlet/@WebFilter` no modo
  **embutido**; no WAR standalone o container é quem escaneia.

### Perguntas de entrevista (Fase 2)

**1) Diferença entre Tomcat embutido e standalone; por que `provided`?**
Embutido: o Tomcat vem como dependência dentro do JAR/execução e a app tem
`main()`. Standalone: o Tomcat é um servidor externo onde se publica o WAR; o
container fornece a API de servlet, por isso `spring-boot-starter-tomcat` fica
`provided` — compila mas não é empacotado, evitando dois containers em conflito.

**2) O que é context path e de onde ele vem?**
É o prefixo de URL que identifica a aplicação no servidor. Por padrão vem do nome
do WAR (`sansuy-pedidos.war` → `/sansuy-pedidos`); `ROOT.war` mapeia para a raiz
(`/`). Pode também ser definido por um elemento `<Context>`.

**3) Qual a ordem entre Filter, Servlet e o DispatcherServlet do Spring?**
A requisição passa primeiro pela **cadeia de Filters** (`doFilter` antes),
depois chega ao **Servlet** mapeado — que, para `/`, é o **DispatcherServlet** do
Spring (o qual roteia para o `@Controller`) ou, para `/servlet/ping`, o nosso
`HttpServlet` puro. No retorno, o controle volta pela cadeia de Filters (código
após o `chain.doFilter`). Ou seja, o Filter roda "por fora" de tudo.

### Erros encontrados (Fase 2)

- **`ERROR: constraint "uk_a32tv48ww0lvru07p7se4c15o" of relation "produto"
  does not exist`** (Hibernate `ddl-auto=update`). Causa: `@Column(unique=true)`
  faz o Hibernate gerar a unique constraint com **nome aleatório** (hash); esse
  nome pode divergir do que está no banco, e num boot seguinte o `update` tenta
  **dropar** a constraint por um nome inexistente. Correção: declarar a constraint
  com **nome explícito e estável** —
  `@Table(uniqueConstraints = @UniqueConstraint(name = "uk_produto_codigo",
  columnNames = "codigo"))` — e remover `unique=true` do `@Column`. Feito também
  em `Cliente.cnpj`. Para limpar o estado sujo do banco: `docker compose down -v`
  (zera o volume) e suba de novo. Lição de entrevista: com `ddl-auto=update`
  nunca dependa de nomes autogerados; em produção, use migrações
  (Flyway/Liquibase) e `ddl-auto=validate`.

- _(anote outros que aparecerem — WAR virar pasta, falha de conexão ao `db`,
  404 por esquecer o context path)_

---

<!-- Próximas fases serão anexadas abaixo conforme avançarmos. -->
