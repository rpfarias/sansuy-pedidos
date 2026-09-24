package br.com.sansuy.pedidos.pedido.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import br.com.sansuy.pedidos.pedido.PedidoVenda;
import br.com.sansuy.pedidos.pedido.StatusPedido;

/** Representacao de saida de um pedido completo. */
public class PedidoResponse {

    private Long id;
    private Long clienteId;
    private String clienteRazaoSocial;
    private LocalDateTime dataCriacao;
    private StatusPedido status;
    private BigDecimal valorTotal;
    private List<ItemPedidoResponse> itens;

    public PedidoResponse(Long id, Long clienteId, String clienteRazaoSocial,
                          LocalDateTime dataCriacao, StatusPedido status,
                          BigDecimal valorTotal, List<ItemPedidoResponse> itens) {
        this.id = id;
        this.clienteId = clienteId;
        this.clienteRazaoSocial = clienteRazaoSocial;
        this.dataCriacao = dataCriacao;
        this.status = status;
        this.valorTotal = valorTotal;
        this.itens = itens;
    }

    /** Mapper pedido -> DTO. Acessa cliente e itens (LAZY) dentro da transacao. */
    public static PedidoResponse de(PedidoVenda pedido) {
        List<ItemPedidoResponse> itens = pedido.getItens().stream()
                .map(ItemPedidoResponse::de)
                .collect(Collectors.toList());
        return new PedidoResponse(
                pedido.getId(),
                pedido.getCliente().getId(),
                pedido.getCliente().getRazaoSocial(),
                pedido.getDataCriacao(),
                pedido.getStatus(),
                pedido.getValorTotal(),
                itens);
    }

    public Long getId() {
        return id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public String getClienteRazaoSocial() {
        return clienteRazaoSocial;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public List<ItemPedidoResponse> getItens() {
        return itens;
    }
}
