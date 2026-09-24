package br.com.sansuy.pedidos.pedido.dto;

import java.math.BigDecimal;

import br.com.sansuy.pedidos.pedido.ItemPedido;

/** Representacao de saida de um item do pedido. */
public class ItemPedidoResponse {

    private Long produtoId;
    private String produtoCodigo;
    private String produtoDescricao;
    private BigDecimal metragem;
    private BigDecimal precoUnitario;
    private BigDecimal subtotal;

    public ItemPedidoResponse(Long produtoId, String produtoCodigo, String produtoDescricao,
                              BigDecimal metragem, BigDecimal precoUnitario, BigDecimal subtotal) {
        this.produtoId = produtoId;
        this.produtoCodigo = produtoCodigo;
        this.produtoDescricao = produtoDescricao;
        this.metragem = metragem;
        this.precoUnitario = precoUnitario;
        this.subtotal = subtotal;
    }

    /**
     * Mapper item -> DTO. Acessa produto (LAZY): funciona porque a conversao
     * ocorre dentro da transacao do service. Fora dela seria
     * LazyInitializationException — tema da Fase 4.
     */
    public static ItemPedidoResponse de(ItemPedido item) {
        return new ItemPedidoResponse(
                item.getProduto().getId(),
                item.getProduto().getCodigo(),
                item.getProduto().getDescricao(),
                item.getMetragem(),
                item.getPrecoUnitario(),
                item.getSubtotal());
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public String getProdutoCodigo() {
        return produtoCodigo;
    }

    public String getProdutoDescricao() {
        return produtoDescricao;
    }

    public BigDecimal getMetragem() {
        return metragem;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}
