package br.com.sansuy.pedidos.pedido.estado;

import br.com.sansuy.pedidos.pedido.StatusPedido;

/**
 * [EU FAÇO] Estado FATURADO.
 * Transição permitida: expedir -> EXPEDIDO. (cancelar NÃO é permitido aqui.)
 */
public class EstadoFaturado implements EstadoPedido {

    @Override
    public StatusPedido status() {
        return StatusPedido.FATURADO;
    }

    @Override
    public StatusPedido expedir() {
        return StatusPedido.EXPEDIDO;
    }
}
