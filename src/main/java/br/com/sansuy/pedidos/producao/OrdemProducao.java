package br.com.sansuy.pedidos.producao;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.sansuy.pedidos.pedido.PedidoVenda;

/**
 * Ordem de producao gerada a partir de um pedido aprovado.
 *
 * <p>A GERACAO automatica (ao aprovar o pedido) e feita na Fase 3 via Observer /
 * ApplicationEvent. Nesta fase a entidade existe apenas para o schema.
 */
@Entity
@Table(name = "ordem_producao")
public class OrdemProducao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoVenda pedido;

    @Column(nullable = false)
    private LocalDateTime dataGeracao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusOrdemProducao status;

    protected OrdemProducao() {
        // Exigido pelo JPA.
    }

    public OrdemProducao(PedidoVenda pedido) {
        this.pedido = pedido;
        this.dataGeracao = LocalDateTime.now();
        this.status = StatusOrdemProducao.PENDENTE;
    }

    public Long getId() {
        return id;
    }

    public PedidoVenda getPedido() {
        return pedido;
    }

    public LocalDateTime getDataGeracao() {
        return dataGeracao;
    }

    public StatusOrdemProducao getStatus() {
        return status;
    }

    public void setStatus(StatusOrdemProducao status) {
        this.status = status;
    }
}
