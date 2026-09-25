package br.com.sansuy.pedidos.pedido.evento;

import br.com.sansuy.pedidos.pedido.PedidoVenda;

/**
 * OBSERVER — evento de domínio publicado quando um pedido é aprovado.
 *
 * <p>Desde o Spring 4.2 um evento pode ser um POJO (não precisa estender
 * {@code ApplicationEvent}). Publicado via {@code ApplicationEventPublisher}, é
 * consumido por listeners {@code @EventListener} — desacoplando "aprovar
 * pedido" de "gerar ordem de produção".
 */
public class PedidoAprovadoEvent {

    private final PedidoVenda pedido;

    public PedidoAprovadoEvent(PedidoVenda pedido) {
        this.pedido = pedido;
    }

    public PedidoVenda getPedido() {
        return pedido;
    }
}
