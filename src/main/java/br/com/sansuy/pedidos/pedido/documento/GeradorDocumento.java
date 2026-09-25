package br.com.sansuy.pedidos.pedido.documento;

import br.com.sansuy.pedidos.pedido.PedidoVenda;

/**
 * Contrato de um gerador de documento de pedido. Cada formato (CSV, PDF) tem uma
 * implementação; a {@link DocumentoPedidoFactory} escolhe a certa.
 */
public interface GeradorDocumento {

    /** Formato que este gerador produz. */
    FormatoDocumento formato();

    /** Gera o documento a partir do pedido (itens já inicializados). */
    DocumentoPedido gerar(PedidoVenda pedido);
}
