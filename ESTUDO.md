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

- _(nenhum registrado ainda — anote aqui os erros que você provocar/encontrar,
  com a causa e a correção)_

### Decisões em aberto (revisitar nas fases indicadas)

- **Contagem de queries (Fase 4):** Hibernate Statistics (default) vs p6spy.
- **Congelar `precoUnitario` no item:** assumido que sim (subtotal estável).

---

<!-- Próximas fases serão anexadas abaixo conforme avançarmos. -->
