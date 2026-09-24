package br.com.sansuy.pedidos.produto.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/** Dados de entrada para criar um produto. */
public class ProdutoRequest {

    @NotBlank(message = "codigo e obrigatorio")
    private String codigo;

    @NotBlank(message = "descricao e obrigatoria")
    private String descricao;

    @Positive(message = "gramatura deve ser positiva")
    private int gramatura;

    @NotNull(message = "largura e obrigatoria")
    @Positive(message = "largura deve ser positiva")
    private BigDecimal largura;

    @NotNull(message = "precoBaseMetroQuadrado e obrigatorio")
    @Positive(message = "precoBaseMetroQuadrado deve ser positivo")
    private BigDecimal precoBaseMetroQuadrado;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getGramatura() {
        return gramatura;
    }

    public void setGramatura(int gramatura) {
        this.gramatura = gramatura;
    }

    public BigDecimal getLargura() {
        return largura;
    }

    public void setLargura(BigDecimal largura) {
        this.largura = largura;
    }

    public BigDecimal getPrecoBaseMetroQuadrado() {
        return precoBaseMetroQuadrado;
    }

    public void setPrecoBaseMetroQuadrado(BigDecimal precoBaseMetroQuadrado) {
        this.precoBaseMetroQuadrado = precoBaseMetroQuadrado;
    }
}
