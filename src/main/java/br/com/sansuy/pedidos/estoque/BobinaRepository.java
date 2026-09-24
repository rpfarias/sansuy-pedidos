package br.com.sansuy.pedidos.estoque;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BobinaRepository extends JpaRepository<Bobina, Long> {

    /** Bobinas de um produto (base para a reserva de metragem da Fase 4). */
    List<Bobina> findByProdutoIdOrderByIdAsc(Long produtoId);
}
