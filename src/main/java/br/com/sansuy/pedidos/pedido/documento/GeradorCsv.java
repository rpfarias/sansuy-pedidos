package br.com.sansuy.pedidos.pedido.documento;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

import br.com.sansuy.pedidos.pedido.ItemPedido;
import br.com.sansuy.pedidos.pedido.PedidoVenda;

/**
 * Gerador de documento em CSV (separador ';', amigável ao Excel pt-BR).
 */
@Component
public class GeradorCsv implements GeradorDocumento {

    @Override
    public FormatoDocumento formato() {
        return FormatoDocumento.CSV;
    }

    @Override
    public DocumentoPedido gerar(PedidoVenda pedido) {
        StringBuilder sb = new StringBuilder();
        sb.append("Pedido;").append(pedido.getId()).append('\n');
        sb.append("Cliente;").append(pedido.getCliente().getRazaoSocial()).append('\n');
        sb.append("Status;").append(pedido.getStatus()).append('\n');
        sb.append("Data;").append(pedido.getDataCriacao()).append('\n');
        sb.append("Valor Total;").append(pedido.getValorTotal()).append('\n');
        sb.append('\n');
        sb.append("Produto;Descricao;Metragem;Preco Unitario;Subtotal\n");
        for (ItemPedido item : pedido.getItens()) {
            sb.append(item.getProduto().getCodigo()).append(';')
                    .append(item.getProduto().getDescricao()).append(';')
                    .append(item.getMetragem()).append(';')
                    .append(item.getPrecoUnitario()).append(';')
                    .append(item.getSubtotal()).append('\n');
        }

        byte[] conteudo = sb.toString().getBytes(StandardCharsets.UTF_8);
        return new DocumentoPedido(
                "pedido-" + pedido.getId() + ".csv", "text/csv; charset=UTF-8", conteudo);
    }
}
