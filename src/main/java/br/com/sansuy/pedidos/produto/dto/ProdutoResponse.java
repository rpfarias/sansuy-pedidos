package br.com.sansuy.pedidos.produto.dto;

import java.math.BigDecimal;

import br.com.sansuy.pedidos.produto.Produto;

/** Representacao de saida de um produto. */
public class ProdutoResponse {

    private Long id;
    private String codigo;
    private String descricao;
    private int gramatura;
    private BigDecimal largura;
    private BigDecimal precoBaseMetroQuadrado;

    public ProdutoResponse(Long id, String codigo, String descricao, int gramatura,
                           BigDecimal largura, BigDecimal precoBaseMetroQuadrado) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.gramatura = gramatura;
        this.largura = largura;
        this.precoBaseMetroQuadrado = precoBaseMetroQuadrado;
    }

    public static ProdutoResponse de(Produto p) {
        return new ProdutoResponse(p.getId(), p.getCodigo(), p.getDescricao(),
                p.getGramatura(), p.getLargura(), p.getPrecoBaseMetroQuadrado());
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getGramatura() {
        return gramatura;
    }

    public BigDecimal getLargura() {
        return largura;
    }

    public BigDecimal getPrecoBaseMetroQuadrado() {
        return precoBaseMetroQuadrado;
    }
}
