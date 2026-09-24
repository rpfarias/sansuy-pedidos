# Sansuy — Sistema de Pedidos e Produção (projeto de estudo)

Projeto de **estudo** para preparação de entrevista técnica de
**Analista Desenvolvedor Pleno Java**. Modela, de forma simplificada, o fluxo de
**venda → aprovação → produção → faturamento → expedição** de bobinas de
laminado de PVC.

> Objetivo é **aprender e praticar** um ambiente Java corporativo/legado, não só
> ter o código. Veja `SPEC.md`, `PLANO.md`, `ARQUITETURA.md` e `ESTUDO.md`.

## Stack

- **Java 8** · **Spring Boot 2.7.x** · **Maven**
- **JPA / Hibernate** · **PostgreSQL**
- Empacotamento **WAR** para **Tomcat 9 standalone** (Fase 2)
- **JUnit 5 + Mockito** (Fase 5)

## Por que Spring Boot 2.7 e Java 8 (e não Boot 3)?

Esta é uma **decisão deliberada** de estudo, e um ótimo ponto de conversa na
entrevista:

- **Spring Boot 3** exige **Jakarta EE 9+**: o namespace de pacotes mudou de
  `javax.*` para `jakarta.*` (ex.: `javax.persistence.Entity` →
  `jakarta.persistence.Entity`). Isso obriga **Tomcat 10+** e **Java 17+**.
- O anúncio da vaga pede **Java 8+** e **Tomcat 8+/9**, típico de sistemas
  legados que ainda vivem no **`javax.*`**. Para praticar exatamente esse mundo,
  fixamos **Spring Boot 2.7.x** (a última linha `javax.*`) e **Java 8**.
- Todo o código de persistência/validação aqui usa `javax.persistence.*` e
  `javax.validation.*` de propósito.

> Compilando num JDK mais novo (ex.: 21), o `javac` pode emitir um aviso de que
> *source 8 é obsoleto* — é cosmético e não impede o build. O container da
> Fase 2 usa `tomcat:9-jdk8`.

## Empacotamento WAR

- `pom.xml`: `<packaging>war</packaging>`.
- `ServletInitializer extends SpringBootServletInitializer` — integra o Boot ao
  ciclo de vida de um Tomcat externo.
- `spring-boot-starter-tomcat` com `<scope>provided</scope>` — o servlet
  container é fornecido pelo Tomcat do deploy, **não** vai embutido no WAR.

## Como compilar

```bash
mvn -DskipTests package
```

Gera `target/sansuy-pedidos.war`.

## Como rodar (Fase 1, antes do Docker da Fase 2)

O app precisa de um PostgreSQL. Para um banco descartável rápido:

```bash
docker run --name sansuy-pg -e POSTGRES_DB=sansuy -e POSTGRES_USER=sansuy -e POSTGRES_PASSWORD=sansuy -p 5432:5432 -d postgres:15
```

Depois, para desenvolvimento, pode subir com o Tomcat embutido do Boot:

```bash
mvn spring-boot:run
```

> O fluxo **oficial** (WAR em Tomcat 9 standalone, sem embutido) é montado na
> **Fase 2** com `docker-compose`.

## Endpoints (Fase 1)

Base: `/api`

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/clientes` | Cria cliente (201) |
| GET | `/clientes` | Lista clientes |
| GET | `/clientes/{id}` | Busca cliente (200/404) |
| POST | `/produtos` | Cria produto (201) |
| GET | `/produtos` | Lista produtos |
| GET | `/produtos/{id}` | Busca produto (200/404) |
| POST | `/pedidos` | Cria pedido com itens (201) |
| GET | `/pedidos` | Lista pedidos |
| GET | `/pedidos/{id}` | Detalha pedido (200/404) |

As transições de status (`aprovar`, `faturar`, ...) e a geração de documento
chegam na **Fase 3** (padrões State/Observer/Factory).

### Exemplo rápido

```bash
# 1) cliente
curl -X POST http://localhost:8080/api/clientes -H "Content-Type: application/json" \
  -d '{"razaoSocial":"AgroPlast Ltda","cnpj":"12345678000199","segmento":"AGRO"}'

# 2) produto
curl -X POST http://localhost:8080/api/produtos -H "Content-Type: application/json" \
  -d '{"codigo":"LONA-500","descricao":"Lona PVC 500g","gramatura":500,"largura":2.50,"precoBaseMetroQuadrado":18.90}'

# 3) pedido (ajuste os ids)
curl -X POST http://localhost:8080/api/pedidos -H "Content-Type: application/json" \
  -d '{"clienteId":1,"itens":[{"produtoId":1,"metragem":120.00}]}'
```

## Documentos do projeto

- `SPEC.md` — escopo e regras de negócio.
- `PLANO.md` — fases de desenvolvimento.
- `ARQUITETURA.md` — arquitetura e o porquê das decisões.
- `ESTUDO.md` — conceitos, perguntas de entrevista e erros encontrados.
