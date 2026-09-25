package br.com.sansuy.pedidos.pedido.estado;

import br.com.sansuy.pedidos.pedido.StatusPedido;

/**
 * [EU FAÇO] Estado EM_PRODUCAO.
 * Transições permitidas: faturar -> FATURADO, cancelar -> CANCELADO.
 */
public class EstadoEmProducao implements EstadoPedido {

    @Override
    public StatusPedido status() {
        return StatusPedido.EM_PRODUCAO;
    }

    @Override
    public StatusPedido faturar() {
        return StatusPedido.FATURADO;
    }

    @Override
    public StatusPedido cancelar() {
        return StatusPedido.CANCELADO;
    }
}
