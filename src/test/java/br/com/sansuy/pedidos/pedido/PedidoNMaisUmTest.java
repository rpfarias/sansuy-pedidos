package br.com.sansuy.pedidos.pedido;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import br.com.sansuy.pedidos.cliente.Cliente;
import br.com.sansuy.pedidos.cliente.Segmento;
import br.com.sansuy.pedidos.produto.Produto;

/**
 * Fase 4 — demonstra o N+1 e a correção, CONTANDO as queries com as estatísticas
 * do Hibernate.
 *
 * <p>{@code @DataJpaTest} sobe só a camada JPA e roda em transação com rollback
 * (não polui o banco). {@code Replace.NONE} usa o Postgres real do
 * application.properties (não um H2). Entre o setup e a consulta chamamos
 * {@code em.clear()} para DESANEXAR tudo — assim as leituras vão de fato ao banco.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PedidoNMaisUmTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private PedidoRepository repositorio;

    private Statistics stats;

    @BeforeEach
    void preparar() {
        Cliente cliente = em.persist(
                new Cliente("Cliente " + System.nanoTime(), cnpjAleatorio(), Segmento.INDUSTRIA));
        Produto p1 = em.persist(new Produto("P1-" + System.nanoTime(), "Produto 1", 500,
                new BigDecimal("3.00"), new BigDecimal("10.00")));
        Produto p2 = em.persist(new Produto("P2-" + System.nanoTime(), "Produto 2", 600,
                new BigDecimal("2.00"), new BigDecimal("20.00")));

        for (int i = 0; i < 2; i++) {
            PedidoVenda pedido = new PedidoVenda(cliente);
            pedido.adicionarItem(new ItemPedido(p1, new BigDecimal("5.00"), new BigDecimal("10.00")));
            pedido.adicionarItem(new ItemPedido(p2, new BigDecimal("3.00"), new BigDecimal("20.00")));
            em.persist(pedido);
        }
        em.flush();
        em.clear(); // desanexa: as próximas leituras batem no banco

        stats = em.getEntityManager().getEntityManagerFactory()
                .unwrap(SessionFactory.class).getStatistics();
        stats.setStatisticsEnabled(true);
    }

    private String cnpjAleatorio() {
        return String.format("%014d", System.nanoTime() % 100000000000000L);
    }

    @Test
    @DisplayName("findAll() padrao dispara N+1 ao acessar cliente/itens/produto")
    void demonstraNMaisUm() {
        stats.clear();

        List<PedidoVenda> pedidos = repositorio.findAll();
        for (PedidoVenda p : pedidos) {
            p.getCliente().getRazaoSocial();
            for (ItemPedido item : p.getItens()) {
                item.getProduto().getCodigo();
            }
        }

        long queries = stats.getPrepareStatementCount();
        System.out.println("[N+1]   findAll + acessos => " + queries + " queries");
        assertTrue(queries > 3, "esperava varias queries (N+1), veio " + queries);
    }

    @Test
    @DisplayName("JOIN FETCH resolve em 1 query")
    void joinFetchResolveEmUmaQuery() {
        stats.clear();

        List<PedidoVenda> pedidos = repositorio.buscarTodosComJoinFetch();
        for (PedidoVenda p : pedidos) {
            p.getCliente().getRazaoSocial();
            for (ItemPedido item : p.getItens()) {
                item.getProduto().getCodigo();
            }
        }

        long queries = stats.getPrepareStatementCount();
        System.out.println("[FETCH] join fetch + acessos => " + queries + " queries");
        assertEquals(1, queries, "JOIN FETCH deveria bastar 1 query");
    }

    @Test
    @DisplayName("@EntityGraph tambem resolve em 1 query")
    void entityGraphResolveEmUmaQuery() {
        stats.clear();

        List<PedidoVenda> pedidos = repositorio.buscarTodosComGrafo();
        for (PedidoVenda p : pedidos) {
            p.getCliente().getRazaoSocial();
            for (ItemPedido item : p.getItens()) {
                item.getProduto().getCodigo();
            }
        }

        long queries = stats.getPrepareStatementCount();
        System.out.println("[GRAPH] entityGraph + acessos => " + queries + " queries");
        assertEquals(1, queries, "@EntityGraph deveria bastar 1 query");
    }
}
