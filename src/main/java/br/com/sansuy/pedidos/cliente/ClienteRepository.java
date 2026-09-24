package br.com.sansuy.pedidos.cliente;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de {@link Cliente}. Spring Data gera a implementacao em runtime
 * (padrao Proxy + Repository).
 */
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCnpj(String cnpj);

    boolean existsByCnpj(String cnpj);
}
