# ARQUITETURA

> Como o sistema está organizado e **por quê**. Cada decisão aqui é também um
> ponto de conversa esperado na entrevista.

## 1. Estilo: monólito em camadas (layered / N-tier)

A stack pedida (Spring MVC + JPA + WAR em Tomcat) leva naturalmente a um
**monólito em três camadas**, empacotado como **WAR** e servido por **Tomcat 9
standalone**. Nada de microsserviços ou hexagonal “pesado” — seria
over-engineering para o escopo e desvia do objetivo, que é dominar o
feijão-com-arroz corporativo Java.

```
┌─────────────────────────────────────────────────────────┐
│  Tomcat 9 (standalone)  ──publica──▶  sansuy-pedidos.war │
└─────────────────────────────────────────────────────────┘
                              │
        HTTP (JSON)           ▼
┌─────────────────────────────────────────────────────────┐
│  Camada WEB / Apresentação                               │
│  @RestController · DTOs · @ControllerAdvice (erros→HTTP) │
│  Servlet puro + Filter (Fase 2, p/ ver "abaixo do Spring")│
└─────────────────────────────────────────────────────────┘
                              │ DTO
                              ▼
┌─────────────────────────────────────────────────────────┐
│  Camada de SERVIÇO / Negócio                             │
│  @Service · @Transactional · regras · orquestra padrões  │
│  (Strategy, State, Builder, Factory, Observer)           │
└─────────────────────────────────────────────────────────┘
                              │ Entidade
                              ▼
┌─────────────────────────────────────────────────────────┐
│  Camada de PERSISTÊNCIA / Repositório                    │
│  @Repository (Spring Data JPA) · @EntityGraph · JOIN FETCH│
└─────────────────────────────────────────────────────────┘
                              │ SQL / JDBC
                              ▼
┌─────────────────────────────────────────────────────────┐
│  PostgreSQL (container)                                  │
└─────────────────────────────────────────────────────────┘
```

**Regra de dependência:** as setas só apontam para baixo. Web conhece Service,
Service conhece Repository — nunca o contrário. É isso que torna a arquitetura
testável (mocka-se a camada de baixo) e é a primeira coisa que um entrevistador
valida.

## 2. As três camadas

| Camada | Componentes | Responsabilidade | O que **não** faz |
|--------|-------------|------------------|-------------------|
| **Web** | `@RestController`, `DTO`, `Mapper`, `@ControllerAdvice` | Traduzir HTTP↔objeto, validar entrada (`@Valid`), mapear exceção→status | Regra de negócio, acesso a banco |
| **Serviço** | `@Service`, `@Transactional`, padrões de projeto | Regras, transações, orquestração, publicar eventos | Conhecer HTTP, montar JSON |
| **Persistência** | `@Repository`, entidades `@Entity` | Consultas, fetch strategy, lock otimista | Regra de negócio |

## 3. Decisão-chave: fronteira de DTO

O controller **nunca** devolve `@Entity` direto — sempre um DTO. Motivos:

- Evita **`LazyInitializationException`**: a entidade sairia do escopo
  transacional antes da serialização JSON (assunto central da Fase 4).
- Não vaza o modelo interno na API (segurança e versionamento).
- Corta serialização recursiva (`Pedido ↔ Item`).

A conversão Entidade↔DTO fica em **mappers** por feature (métodos estáticos
simples nesta fase; nada de MapStruct para manter o foco).

## 4. Organização de pacotes: por funcionalidade (package-by-feature)

Agrupamos por domínio, não por tipo técnico:

```
br.com.sansuy.pedidos
├── config/                 (beans, servlet, filter — Fase 2; p6spy — Fase 4)
├── comum/                  (exceções base, @ControllerAdvice, corpo de erro)
├── cliente/                (Cliente, Repository, Service, Controller, DTOs)
├── produto/                (Produto, ...)
├── pedido/                 (PedidoVenda, ItemPedido, ...)
│   ├── documento/          (Factory: CSV/PDF — Fase 3)
│   └── preco/              (Strategy por segmento — Fase 3)
├── producao/               (OrdemProducao, listener do Observer — Fase 3)
└── estoque/                (Bobina, reserva transacional, lock @Version — Fase 4)
```

**Por que package-by-feature?** Escala melhor (tudo de “pedido” junto), reflete o
domínio e permite usar visibilidade `package-private` para esconder implementação.
Trade-off: muito sistema legado usa **package-by-layer**
(`controllers/ services/ repositories/`); é preciso saber defender os dois na
entrevista.

## 5. Onde os padrões de projeto encaixam

Os padrões **não** são uma camada nova — vivem **dentro do domínio/serviço**:

| Padrão | Onde | Papel |
|--------|------|-------|
| **Strategy** | `pedido/preco` | Preço por segmento do cliente |
| **State** | `pedido` | Transições de status, bloqueando inválidas |
| **Builder** | `pedido` | Montagem do agregado Pedido + itens |
| **Factory** | `pedido/documento` | Gera documento (CSV/PDF) sem o chamador saber a implementação |
| **Observer** | `pedido` → `producao` | `PedidoAprovadoEvent` desacopla aprovação de geração da OrdemProducao |

E o gancho que impressiona: o **próprio Spring** é feito de padrões —
**IoC/DI** (contêiner), **Singleton** (escopo padrão dos beans), **Proxy**
(`@Transactional`, `@Async`, AOP) e **Template Method** (classes `*Template`).
Aterrissamos isso nas Fases 3 e 4.

## 6. Transação e consistência

- A **transação é dona da camada de serviço** (`@Transactional` no `@Service`).
- A reserva de estoque é atômica: falta de metragem → **rollback total**
  (Fase 4).
- Concorrência tratada com **lock otimista** (`@Version` na `Bobina`) — sem
  travar linha no banco, detecta conflito no commit (Fase 4).

## 7. Empacotamento e execução

- **WAR** (`packaging=war`), classe principal estende
  `SpringBootServletInitializer`, `spring-boot-starter-tomcat` com scope
  **provided** (o Tomcat do container fornece o servlet container; não vai
  embutido no artefato).
- **Java 11 / Spring Boot 2.7.x** deliberadamente — mantém o namespace
  **`javax.*`** (Spring Boot 3 exigiria Jakarta / Tomcat 10+). O README detalha.

## 8. Fora de escopo (arquitetura)

Segurança/autenticação, cache distribuído, mensageria, observabilidade e
front-end ficam de fora — o foco é o núcleo Java/JPA/REST cobrado pela vaga.
