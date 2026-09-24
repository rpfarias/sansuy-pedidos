package br.com.sansuy.pedidos.pedido;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de pedidos.
 *
 * <p>Na Fase 4 ganhamos aqui variantes com JOIN FETCH / {@code @EntityGraph}
 * para corrigir o N+1 da listagem de pedidos com itens.
 */
public interface PedidoRepository extends JpaRepository<PedidoVenda, Long> {
}
