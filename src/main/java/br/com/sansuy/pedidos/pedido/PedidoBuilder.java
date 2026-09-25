package br.com.sansuy.pedidos.pedido;

import java.math.BigDecimal;

import br.com.sansuy.pedidos.cliente.Cliente;
import br.com.sansuy.pedidos.comum.RegraNegocioException;
import br.com.sansuy.pedidos.pedido.preco.SeletorEstrategiaPreco;
import br.com.sansuy.pedidos.produto.Produto;

/**
 * BUILDER — monta um {@link PedidoVenda} com seus itens de forma fluente.
 *
 * <p>Encapsula a construção do agregado: para cada item, resolve o preço
 * unitário pela {@link SeletorEstrategiaPreco} (Strategy) conforme o segmento do
 * cliente, cria o {@link ItemPedido} e mantém o total consistente. O chamador
 * (service) não precisa saber como o preço é calculado nem como o total é somado.
 */
public class PedidoBuilder {

    private final Cliente cliente;
    private final SeletorEstrategiaPreco seletor;
    private final PedidoVenda pedido;

    private PedidoBuilder(Cliente cliente, SeletorEstrategiaPreco seletor) {
        this.cliente = cliente;
        this.seletor = seletor;
        this.pedido = new PedidoVenda(cliente);
    }

    public static PedidoBuilder para(Cliente cliente, SeletorEstrategiaPreco seletor) {
        return new PedidoBuilder(cliente, seletor);
    }

    /** Adiciona um item, calculando o preço unitário pela Strategy do segmento. */
    public PedidoBuilder adicionarItem(Produto produto, BigDecimal metragem) {
        BigDecimal precoUnitario = seletor
                .paraSegmento(cliente.getSegmento())
                .aplicar(produto.getPrecoBaseMetroQuadrado());
        pedido.adicionarItem(new ItemPedido(produto, metragem, precoUnitario));
        return this;
    }

    public PedidoVenda construir() {
        if (pedido.getItens().isEmpty()) {
            throw new RegraNegocioException("O pedido deve ter ao menos um item");
        }
        return pedido;
    }
}
