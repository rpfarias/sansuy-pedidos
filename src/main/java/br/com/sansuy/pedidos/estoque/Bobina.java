package br.com.sansuy.pedidos.estoque;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import br.com.sansuy.pedidos.produto.Produto;

/**
 * Lote fisico de um produto em estoque, com metragem disponivel.
 *
 * <p>O campo {@link #versao} ({@code @Version}) habilita LOCK OTIMISTA: duas
 * reservas simultaneas na mesma bobina serao detectadas no commit e a segunda
 * recebera OptimisticLockException (tratamento e o [EU FACO] da Fase 4).
 */
@Entity
@Table(name = "bobina")
public class Bobina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String lote;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    /** Metragem disponivel em m2. */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal metragemDisponivel;

    /** Versao para controle de concorrencia otimista. */
    @Version
    @Column(nullable = false)
    private int versao;

    protected Bobina() {
        // Exigido pelo JPA.
    }

    public Bobina(String lote, Produto produto, BigDecimal metragemDisponivel) {
        this.lote = lote;
        this.produto = produto;
        this.metragemDisponivel = metragemDisponivel;
    }

    public Long getId() {
        return id;
    }

    public String getLote() {
        return lote;
    }

    public Produto getProduto() {
        return produto;
    }

    public BigDecimal getMetragemDisponivel() {
        return metragemDisponivel;
    }

    public void setMetragemDisponivel(BigDecimal metragemDisponivel) {
        this.metragemDisponivel = metragemDisponivel;
    }

    public int getVersao() {
        return versao;
    }
}
