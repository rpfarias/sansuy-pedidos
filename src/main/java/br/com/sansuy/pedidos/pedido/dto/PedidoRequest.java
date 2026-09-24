package br.com.sansuy.pedidos.pedido.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/** Dados de entrada para abrir um pedido: o cliente e ao menos um item. */
public class PedidoRequest {

    @NotNull(message = "clienteId e obrigatorio")
    private Long clienteId;

    /** @Valid propaga a validacao para cada item da lista. */
    @NotEmpty(message = "o pedido deve ter ao menos um item")
    @Valid
    private List<ItemPedidoRequest> itens;

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public List<ItemPedidoRequest> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoRequest> itens) {
        this.itens = itens;
    }
}
