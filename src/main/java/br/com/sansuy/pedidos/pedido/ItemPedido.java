package br.com.sansuy.pedidos.pedido;

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

import br.com.sansuy.pedidos.produto.Produto;

/**
 * Linha do pedido: um produto, a metragem (m2) e o preco unitario aplicado.
 *
 * <p>Nesta fase, o {@code precoUnitario} recebe o preco base do produto.
 * Na Fase 3 ele passa a ser calculado pela Strategy do segmento do cliente.
 */
@Entity
@Table(name = "item_pedido")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoVenda pedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    /** Metragem em m2. */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal metragem;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precoUnitario;

    /** metragem * precoUnitario. */
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal;

    protected ItemPedido() {
        // Exigido pelo JPA.
    }

    public ItemPedido(Produto produto, BigDecimal metragem, BigDecimal precoUnitario) {
        this.produto = produto;
        this.metragem = metragem;
        this.precoUnitario = precoUnitario;
        this.subtotal = metragem.multiply(precoUnitario);
    }

    public Long getId() {
        return id;
    }

    public PedidoVenda getPedido() {
        return pedido;
    }

    public void setPedido(PedidoVenda pedido) {
        this.pedido = pedido;
    }

    public Produto getProduto() {
        return produto;
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
