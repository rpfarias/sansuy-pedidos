# SPEC — Sistema de Pedidos e Produção (Laminados de PVC)

> Projeto de **estudo** para preparação de entrevista técnica presencial
> **Analista Desenvolvedor Pleno Java — Sansuy**.
> Objetivo é **aprender e praticar** um ambiente Java legado (javax.*, WAR em Tomcat),
> não apenas ter código pronto.

---

## 1. Contexto de negócio

A Sansuy é uma indústria de laminados e manufaturados de PVC (lonas, geomembranas,
laminados automotivos). Este sistema modela, de forma simplificada, o fluxo de
**venda → aprovação → produção → faturamento → expedição** de bobinas de laminado,
com controle de estoque por lote.

O domínio foi escolhido para exercitar os pontos técnicos da vaga: OO, padrões de
projeto, JPA/Hibernate (incluindo N+1, lock otimista, transações), REST e deploy em
Tomcat standalone.

**Todo o código de domínio (classes, atributos, comentários) fica em português.**

---

## 2. Glossário do domínio

| Termo | Significado |
|-------|-------------|
| **Cliente** | Empresa compradora, identificada por CNPJ, classificada por segmento. |
| **Segmento** | Ramo do cliente: `AGRO`, `COMUNICACAO_VISUAL`, `TRANSPORTE`, `INDUSTRIA`. Afeta o preço. |
| **Produto** | Item de catálogo (código, descrição, gramatura, largura, preço base por m²). |
| **PedidoVenda** | Cabeçalho de venda de um cliente, com 1..N itens e um status. |
| **ItemPedido** | Linha do pedido: produto, metragem (m²) e preço unitário aplicado. |
| **OrdemProducao** | Ordem gerada ao aprovar um pedido; consome bobinas do estoque. |
| **Bobina** | Lote físico de um produto em estoque, com metragem disponível e versão (lock otimista). |

---

## 3. Entidades e atributos

### 3.1 Cliente
- `id` (PK)
- `razaoSocial` (String, obrigatório)
- `cnpj` (String, obrigatório, único, 14 dígitos — validação simples)
- `segmento` (enum `Segmento`)

### 3.2 Produto
- `id` (PK)
- `codigo` (String, único)
- `descricao` (String)
- `gramatura` (int, g/m²)
- `largura` (BigDecimal, metros)
- `precoBaseMetroQuadrado` (BigDecimal, R$/m²)

### 3.3 PedidoVenda
- `id` (PK)
- `cliente` (ManyToOne, **LAZY**)
- `dataCriacao` (LocalDateTime)
- `status` (enum `StatusPedido`)
- `itens` (OneToMany, **LAZY**, cascade ALL, orphanRemoval)
- `valorTotal` (BigDecimal, derivado da soma dos itens)

### 3.4 ItemPedido
- `id` (PK)
- `pedido` (ManyToOne, **LAZY**)
- `produto` (ManyToOne, **LAZY**)
- `metragem` (BigDecimal, m²)
- `precoUnitario` (BigDecimal — preço já calculado pela Strategy do segmento)
- `subtotal` (BigDecimal, derivado = metragem × precoUnitario)

### 3.5 OrdemProducao
- `id` (PK)
- `pedido` (OneToOne/ManyToOne, **LAZY**)
- `dataGeracao` (LocalDateTime)
- `status` (enum `StatusOrdemProducao`: `PENDENTE`, `EM_ANDAMENTO`, `CONCLUIDA`)

### 3.6 Bobina
- `id` (PK)
- `lote` (String)
- `produto` (ManyToOne, **LAZY**)
- `metragemDisponivel` (BigDecimal, m²)
- `versao` (int, `@Version` — lock otimista)

---

## 4. Máquina de estados do pedido

Enum `StatusPedido` com transições **explicitamente validadas** (padrão State, Fase 3):

```
ABERTO ──aprovar──▶ APROVADO ──iniciarProducao──▶ EM_PRODUCAO ──faturar──▶ FATURADO ──expedir──▶ EXPEDIDO
   │                    │                                                        
   └──cancelar──▶ CANCELADO  ◀──cancelar── (permitido até EM_PRODUCAO)
```

Regras:
- Só é possível **aprovar** um pedido `ABERTO` que tenha ao menos 1 item.
- **Aprovar** dispara (Observer / ApplicationEvent) a geração da `OrdemProducao`.
- `CANCELADO`, `FATURADO` e `EXPEDIDO` são estados sem saída (exceto expedir a partir de faturado).
- Qualquer transição inválida lança `TransicaoInvalidaException` → HTTP 409 (Conflict).

Enum `StatusOrdemProducao`: `PENDENTE → EM_ANDAMENTO → CONCLUIDA`.

---

## 5. Regras de negócio

1. **Cálculo de preço (Strategy, Fase 3 — [EU FAÇO])**
   Preço unitário do item = `precoBaseMetroQuadrado` ajustado por uma estratégia
   dependente do `segmento` do cliente. Exemplos de política (ajustável):
   - `AGRO`: desconto de 5% (volume alto, margem menor).
   - `COMUNICACAO_VISUAL`: preço cheio (produto de acabamento).
   - `TRANSPORTE`: acréscimo de 8% (especificação técnica).
   - `INDUSTRIA`: desconto de 3%.

2. **Aprovação e produção (Observer, Fase 3)**
   Ao aprovar o pedido, um `ApplicationEvent` (`PedidoAprovadoEvent`) é publicado;
   um listener cria a `OrdemProducao` correspondente com status `PENDENTE`.

3. **Reserva de estoque (Transação, Fase 4)**
   Ao iniciar produção, o serviço reserva a metragem necessária de `Bobina`(s) do
   produto. Se **qualquer** item não puder ser atendido por falta de metragem,
   a transação inteira sofre **rollback** (nada é reservado). Falta de estoque →
   `EstoqueInsuficienteException` → HTTP 422.

4. **Concorrência (Lock otimista, Fase 4 — tratamento [EU FAÇO])**
   Duas reservas simultâneas na mesma bobina devem ser detectadas via `@Version`;
   a segunda recebe `OptimisticLockException`, tratada com retry/erro amigável.

5. **Valor total** do pedido é sempre recalculado a partir dos subtotais dos itens.

---

## 6. API REST (Fase 1)

Base: `/api`

| Método | Rota | Descrição | Status esperados |
|--------|------|-----------|------------------|
| POST | `/clientes` | Cria cliente | 201, 400 |
| GET | `/clientes/{id}` | Busca cliente | 200, 404 |
| POST | `/produtos` | Cria produto | 201, 400 |
| GET | `/produtos` | Lista produtos | 200 |
| POST | `/pedidos` | Cria pedido (Builder) com itens | 201, 400, 404 |
| GET | `/pedidos/{id}` | Detalha pedido | 200, 404 |
| GET | `/pedidos` | Lista pedidos (lab de N+1 na Fase 4) | 200 |
| POST | `/pedidos/{id}/aprovar` | Transição State → gera OrdemProducao | 200, 409 |
| POST | `/pedidos/{id}/iniciar-producao` | Reserva estoque (transacional) | 200, 409, 422 |
| POST | `/pedidos/{id}/faturar` | Transição State | 200, 409 |
| POST | `/pedidos/{id}/expedir` | Transição State | 200, 409 |
| POST | `/pedidos/{id}/cancelar` | Transição State | 200, 409 |
| GET | `/pedidos/{id}/documento?formato=CSV\|PDF` | Factory de documentos (Fase 3) | 200, 404 |

`@ControllerAdvice` global mapeia exceções de domínio para os status HTTP acima,
com corpo JSON padronizado `{ timestamp, status, erro, mensagem, caminho }`.

---

## 7. Restrições técnicas (obrigatórias)

- **Spring Boot 2.7.x**, **Java 11**, **Tomcat 9 standalone**.
  - **NÃO** usar Spring Boot 3 (exigiria Jakarta / Tomcat 10+). O objetivo é praticar
    o namespace **`javax.*`** de sistemas legados. O README explica o porquê.
- Empacotamento **WAR**:
  - `<packaging>war</packaging>`
  - Classe principal estende `SpringBootServletInitializer`.
  - `spring-boot-starter-tomcat` com `<scope>provided</scope>`.
- **Maven**, **PostgreSQL**, **JPA/Hibernate**, **JUnit 5 + Mockito**.
- **Docker Compose**: PostgreSQL + `tomcat:9-jdk11` oficial, com o WAR publicado em
  `webapps`. Nesse fluxo a aplicação **não** roda com Tomcat embutido.
- Relacionamentos JPA **LAZY** por padrão (para exercitar N+1 e LazyInitialization).

---

## 8. Fora de escopo

- Autenticação/autorização, multi-tenancy, front-end.
- Cálculo fiscal real, integração com ERP, emissão de NF-e real.
- Precisão contábil de arredondamento além de `BigDecimal` com `RoundingMode.HALF_UP`.

---

## 9. Critérios de "pronto" por conceito estudado

Cada fase só é considerada concluída quando:
- O código compila e (quando aplicável) os testes passam.
- Há uma explicação do **porquê** das decisões.
- Existem **3 perguntas de entrevista** sobre o tema, com respostas curtas.
- Os conceitos e erros foram acumulados em `ESTUDO.md`.

As partes marcadas **[EU FAÇO]** têm apenas estrutura + testes falhando + objetivo;
a implementação é do estudante e depois revisada como faria um entrevistador.
