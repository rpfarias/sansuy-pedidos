package br.com.sansuy.pedidos.pedido;

/**
 * Situacao do pedido de venda.
 *
 * <p>As TRANSICOES validas entre estes estados (e o bloqueio das invalidas)
 * serao implementadas na Fase 3 com o padrao State — marcado como [EU FACO].
 * Nesta fase, o enum guarda apenas os valores; todo pedido novo nasce ABERTO.
 */
public enum StatusPedido {
    ABERTO,
    APROVADO,
    EM_PRODUCAO,
    FATURADO,
    EXPEDIDO,
    CANCELADO
}
