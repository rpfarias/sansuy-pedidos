package br.com.sansuy.pedidos.pedido.estado;

import br.com.sansuy.pedidos.pedido.StatusPedido;

/**
 * [EU FAÇO] Estado ABERTO.
 * Transições permitidas: aprovar -> APROVADO, cancelar -> CANCELADO.
 * Sobrescreva {@code aprovar()} e {@code cancelar()}.
 */
public class EstadoAberto implements EstadoPedido {

    @Override
    public StatusPedido status() {
        return StatusPedido.ABERTO;
    }

    @Override
    public StatusPedido aprovar() {
        return StatusPedido.APROVADO;
    }

    @Override
    public StatusPedido cancelar() {
        return StatusPedido.CANCELADO;
    }
}
