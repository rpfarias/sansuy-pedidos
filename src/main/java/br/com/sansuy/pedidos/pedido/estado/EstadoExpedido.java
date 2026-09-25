package br.com.sansuy.pedidos.pedido.estado;

import br.com.sansuy.pedidos.pedido.StatusPedido;

/**
 * Estado EXPEDIDO — terminal. Nenhuma transição é permitida (todos os métodos
 * default lançam TransicaoInvalidaException). Nada a fazer aqui.
 */
public class EstadoExpedido implements EstadoPedido {

    @Override
    public StatusPedido status() {
        return StatusPedido.EXPEDIDO;
    }
}
