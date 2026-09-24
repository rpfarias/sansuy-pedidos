package br.com.sansuy.pedidos.pedido.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/** Uma linha na criacao do pedido: qual produto e quanta metragem. */
public class ItemPedidoRequest {

    @NotNull(message = "produtoId e obrigatorio")
    private Long produtoId;

    @NotNull(message = "metragem e obrigatoria")
    @Positive(message = "metragem deve ser positiva")
    private BigDecimal metragem;

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public BigDecimal getMetragem() {
        return metragem;
    }

    public void setMetragem(BigDecimal metragem) {
        this.metragem = metragem;
    }
}
