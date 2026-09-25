package br.com.sansuy.pedidos.pedido.estado;

import br.com.sansuy.pedidos.pedido.StatusPedido;

/**
 * STATE — comportamento de transição de um pedido conforme o seu status atual.
 *
 * <p>Design: por PADRÃO toda transição é PROIBIDA (os métodos default lançam
 * {@link TransicaoInvalidaException}). Cada estado concreto SOBRESCREVE apenas
 * as transições que ele permite, devolvendo o próximo {@link StatusPedido}.
 * Assim as regras ficam localizadas em cada estado, sem um switch central.
 *
 * <p>[EU FAÇO]: implementar, em cada classe de estado
 * ({@code EstadoAberto}, {@code EstadoAprovado}, ...), os overrides das
 * transições válidas. As regras exatas estão nos testes
 * ({@code MaquinaEstadoPedidoTest}) e no SPEC:
 * <pre>
 * ABERTO      --aprovar-->        APROVADO
 * ABERTO      --cancelar-->       CANCELADO
 * APROVADO    --iniciarProducao-> EM_PRODUCAO
 * APROVADO    --cancelar-->       CANCELADO
 * EM_PRODUCAO --faturar-->        FATURADO
 * EM_PRODUCAO --cancelar-->       CANCELADO
 * FATURADO    --expedir-->        EXPEDIDO
 * (FATURADO, EXPEDIDO e CANCELADO nao permitem mais transicoes)
 * </pre>
 */
public interface EstadoPedido {

    /** Status que este estado representa. */
    StatusPedido status();

    default StatusPedido aprovar() {
        throw proibido("aprovar");
    }

    default StatusPedido iniciarProducao() {
        throw proibido("iniciar producao");
    }

    default StatusPedido faturar() {
        throw proibido("faturar");
    }

    default StatusPedido expedir() {
        throw proibido("expedir");
    }

    default StatusPedido cancelar() {
        throw proibido("cancelar");
    }

    /** Monta a exceção padrão de transição proibida a partir do estado atual. */
    default TransicaoInvalidaException proibido(String acao) {
        return new TransicaoInvalidaException(
                "Nao e possivel '" + acao + "' um pedido no status " + status());
    }
}
