package br.com.sansuy.pedidos.produto;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Item de catalogo (um laminado de PVC). O preco base e por metro quadrado; a
 * estrategia de preco por segmento (Fase 3) parte deste valor.
 */
@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(nullable = false)
    private String descricao;

    /** Gramatura em g/m2. */
    @Column(nullable = false)
    private int gramatura;

    /** Largura em metros. */
    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal largura;

    /** Preco base em R$/m2. */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precoBaseMetroQuadrado;

    protected Produto() {
        // Exigido pelo JPA.
    }

    public Produto(String codigo, String descricao, int gramatura,
                   BigDecimal largura, BigDecimal precoBaseMetroQuadrado) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.gramatura = gramatura;
        this.largura = largura;
        this.precoBaseMetroQuadrado = precoBaseMetroQuadrado;
    }

    public Long getId() {
        return id;
    }

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
