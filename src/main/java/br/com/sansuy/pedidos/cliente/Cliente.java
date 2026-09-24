package br.com.sansuy.pedidos.cliente;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Empresa compradora de laminados.
 *
 * <p>Observacao de estudo: NAO sobrescrevemos equals/hashCode nesta fase de
 * proposito — o comportamento padrao (identidade) e um dos temas da Fase 4
 * (contrato de equals/hashCode em entidades JPA).
 */
@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String razaoSocial;

    /** CNPJ com 14 digitos (validacao simples). Unico. */
    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Segmento segmento;

    protected Cliente() {
        // Exigido pelo JPA.
    }

    public Cliente(String razaoSocial, String cnpj, Segmento segmento) {
        this.razaoSocial = razaoSocial;
        this.cnpj = cnpj;
        this.segmento = segmento;
    }

    public Long getId() {
        return id;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public Segmento getSegmento() {
        return segmento;
    }

    public void setSegmento(Segmento segmento) {
        this.segmento = segmento;
    }
}
