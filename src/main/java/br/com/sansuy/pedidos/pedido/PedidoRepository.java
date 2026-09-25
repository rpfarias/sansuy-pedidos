package br.com.sansuy.pedidos.pedido;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repositorio de pedidos.
 *
 * <p>Fase 4: temos 3 formas de listar, para comparar o N+1:
 * <ul>
 *   <li>{@code findAll()} (herdado): NÃO faz fetch — acessar itens/cliente/produto
 *       depois dispara o N+1.</li>
 *   <li>{@link #buscarTodosComJoinFetch()}: 1 query com JOIN FETCH.</li>
 *   <li>{@link #buscarTodosComGrafo()}: 1 query via @EntityGraph.</li>
 * </ul>
 */
public interface PedidoRepository extends JpaRepository<PedidoVenda, Long> {

    /**
     * Correção do N+1 via JPQL com JOIN FETCH: traz pedido + cliente + itens +
     * produto de cada item numa única consulta.
     *
     * <p>{@code distinct} remove as linhas duplicadas do produto cartesiano
     * gerado pelo join da coleção {@code itens}.
     */
    @Query("select distinct p from PedidoVenda p "
            + "join fetch p.cliente "
            + "left join fetch p.itens i "
            + "left join fetch i.produto")
    List<PedidoVenda> buscarTodosComJoinFetch();

    /**
     * Mesma correção via {@code @EntityGraph}: declara os atributos a carregar
     * junto (EAGER pontual), sem escrever o JOIN na mão.
     */
    @EntityGraph(attributePaths = {"cliente", "itens", "itens.produto"})
    @Query("select p from PedidoVenda p")
    List<PedidoVenda> buscarTodosComGrafo();
}
