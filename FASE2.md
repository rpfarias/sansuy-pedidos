# FASE 2 — Deploy legado (Tomcat 9 standalone + Docker)

Objetivo: sair do Tomcat **embutido** e publicar o **WAR** num **Tomcat 9
standalone** (container oficial), com PostgreSQL ao lado, entendendo a camada de
servlet abaixo do Spring.

---

## 1. Como subir

Pré-requisito: **Docker Desktop rodando**.

```bash
# 1) gerar o WAR (obrigatorio ANTES do up: o bind-mount de arquivo precisa que
#    o arquivo exista, senao o Docker cria um diretorio vazio no lugar)
mvn -DskipTests package

# 2) subir Postgres + Tomcat
docker compose up            # Ctrl+C para parar; use -d para rodar ao fundo
```

Aguarde no log a linha `Started Application in ... seconds`.

## 2. URLs — atenção ao CONTEXT PATH

O WAR se chama `sansuy-pedidos.war`, então o Tomcat publica no context path
**`/sansuy-pedidos`** (o nome do arquivo vira o prefixo da URL):

| O quê | URL |
|-------|-----|
| API REST (Spring MVC) | http://localhost:8080/sansuy-pedidos/api/produtos |
| Servlet PURO (sem Spring) | http://localhost:8080/sansuy-pedidos/servlet/ping |

> Para servir na **raiz** (`http://localhost:8080/api/...`), publique o WAR como
> **`ROOT.war`** (nome reservado do Tomcat para context path vazio). Bastaria
> mudar o destino do volume para `/usr/local/tomcat/webapps/ROOT.war`.

## 3. Banco: env vars sobrepõem o application.properties

O `application.properties` aponta para `localhost:5432` — que, **dentro** do
container do Tomcat, seria o próprio container. Por isso o `docker-compose.yml`
injeta variáveis de ambiente que o Spring Boot lê (relaxed binding) e que
**vencem** o arquivo:

```
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/sansuy
```

`db` é o hostname do serviço Postgres na rede interna do compose. No host, esse
mesmo Postgres está exposto em **5433** (para não brigar com o seu Postgres
nativo na 5432) — dá para inspecionar no pgAdmin conectando em
`localhost:5433`, usuário/senha `sansuy`.

## 4. `catalina.out` — o log do Tomcat

`catalina.out` é o arquivo onde o Tomcat concentra o stdout/stderr (boot do
servidor, deploy de apps, stack traces). Formas de ler:

```bash
# jeito Docker (recomendado): segue o stdout do container, que inclui o catalina
docker compose logs -f tomcat

# entrar no container e ver o arquivo fisico
docker compose exec tomcat bash
tail -f /usr/local/tomcat/logs/catalina.out
```

O que procurar: `Deploying web application archive ... sansuy-pedidos.war`,
`Deployment ... has finished`, e o `Started Application` do Spring Boot.

## 5. `server.xml` — a configuração do Tomcat

Fica em **`/usr/local/tomcat/conf/server.xml`** dentro do container. Pontos que
valem conhecer:

```bash
docker compose exec tomcat cat /usr/local/tomcat/conf/server.xml
```

- **`<Connector port="8080" protocol="HTTP/1.1" ...>`** — a porta HTTP.
- **`<Host name="localhost" appBase="webapps" unpackWARs="true"
  autoDeploy="true">`** — `appBase` é a pasta escaneada (`webapps`);
  `autoDeploy=true` faz o Tomcat detectar e (re)publicar WARs em runtime;
  `unpackWARs=true` explode o WAR numa pasta.
- **`<Engine>`, `<Service>`** — a hierarquia Server → Service → Engine → Host →
  Context.

## 6. Context path e redeploy

- **Context path** = prefixo da URL de uma app. Vem do nome do WAR
  (`sansuy-pedidos.war` → `/sansuy-pedidos`) ou de um `<Context>` explícito.
- **Redeploy** (com `autoDeploy=true`): basta **regerar o WAR** que o Tomcat
  detecta a alteração e republica:

  ```bash
  mvn -DskipTests package     # sobrescreve target/sansuy-pedidos.war
  # o Tomcat percebe o novo timestamp e redeploya sozinho
  docker compose logs -f tomcat   # acompanhe "Redeploying" / "Undeploying"
  ```

  Se o autoDeploy não pegar a troca do arquivo (acontece em bind-mount de
  arquivo único em alguns ambientes), reinicie só o Tomcat:

  ```bash
  docker compose restart tomcat
  ```

## 7. Servlet e Filter puros (a camada abaixo do Spring)

- **`PingServlet`** (`/servlet/ping`) — um `HttpServlet` cru, roteado pelo
  container, **sem** passar pelo DispatcherServlet do Spring. Retorna texto com
  horário, thread e `serverInfo` (deve aparecer algo como *Apache Tomcat/9.x*
  quando rodando no container).
- **`TempoRequisicaoFilter`** (`/*`) — um `Filter` que envolve **todas** as
  requisições (inclusive `/api/...` e `/servlet/ping`), mede o tempo, loga
  `[filtro] GET /... levou N ms` e tenta setar o header `X-Tempo-Ms`.

  > Lição: o header só entra se a resposta ainda **não** foi enviada
  > (`committed`). Em respostas já escritas, o `setHeader` é ignorado — não dá
  > para alterar cabeçalho depois que o corpo começou a ir para o cliente.

Teste rápido dos dois:

```bash
curl -i http://localhost:8080/sansuy-pedidos/servlet/ping   # veja o corpo e o X-Tempo-Ms
curl -i http://localhost:8080/sansuy-pedidos/api/produtos   # e o log [filtro] no catalina
```

## 8. Parar / limpar

```bash
docker compose down          # para e remove os containers (mantem o volume do banco)
docker compose down -v       # tambem apaga o volume pgdata (zera o banco)
```

## 9. Problemas comuns

- **`webapps/sansuy-pedidos.war` virou uma pasta vazia** → você rodou
  `docker compose up` antes do `mvn package`. Rode o package e suba de novo.
- **App sobe mas não conecta no banco** → confira as env `SPRING_DATASOURCE_*`
  e se o serviço `db` está `healthy` (`docker compose ps`).
- **404 em `/api/...`** → lembre do context path: a URL é
  `/sansuy-pedidos/api/...`, não `/api/...`.
