package br.com.sansuy.pedidos.pedido.estado;

import br.com.sansuy.pedidos.pedido.StatusPedido;

/**
 * Estado CANCELADO — terminal. Nenhuma transição é permitida. Nada a fazer aqui.
 */
public class EstadoCancelado implements EstadoPedido {

    @Override
    public StatusPedido status() {
        return StatusPedido.CANCELADO;
    }
}
