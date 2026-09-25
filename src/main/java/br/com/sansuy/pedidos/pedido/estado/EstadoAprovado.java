package br.com.sansuy.pedidos.pedido.estado;

import br.com.sansuy.pedidos.pedido.StatusPedido;

/**
 * [EU FAÇO] Estado APROVADO.
 * Transições permitidas: iniciarProducao -> EM_PRODUCAO, cancelar -> CANCELADO.
 */
public class EstadoAprovado implements EstadoPedido {

    @Override
    public StatusPedido status() {
        return StatusPedido.APROVADO;
    }

    @Override
    public StatusPedido iniciarProducao() {
        return StatusPedido.EM_PRODUCAO;
    }

    @Override
    public StatusPedido cancelar() {
        return StatusPedido.CANCELADO;
    }
}
