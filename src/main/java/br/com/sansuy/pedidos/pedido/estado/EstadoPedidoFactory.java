package br.com.sansuy.pedidos.pedido.estado;

import br.com.sansuy.pedidos.pedido.StatusPedido;

/**
 * Devolve o objeto {@link EstadoPedido} correspondente a um {@link StatusPedido}.
 *
 * <p>É a "fiação" do padrão State (eu implemento). Os estados são imutáveis e
 * sem dependências, então instanciamos direto — não precisam ser beans Spring.
 */
public final class EstadoPedidoFactory {

    private EstadoPedidoFactory() {
    }

    public static EstadoPedido de(StatusPedido status) {
        switch (status) {
            case ABERTO:
                return new EstadoAberto();
            case APROVADO:
                return new EstadoAprovado();
            case EM_PRODUCAO:
                return new EstadoEmProducao();
            case FATURADO:
                return new EstadoFaturado();
            case EXPEDIDO:
                return new EstadoExpedido();
            case CANCELADO:
                return new EstadoCancelado();
            default:
                throw new IllegalArgumentException("Status sem estado mapeado: " + status);
        }
    }
}
