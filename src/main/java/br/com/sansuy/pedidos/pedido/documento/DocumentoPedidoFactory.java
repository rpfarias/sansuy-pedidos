package br.com.sansuy.pedidos.pedido.documento;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import br.com.sansuy.pedidos.comum.RegraNegocioException;
import br.com.sansuy.pedidos.pedido.PedidoVenda;

/**
 * FACTORY — devolve o documento do pedido no formato pedido, escondendo do
 * chamador qual implementação concreta ({@link GeradorCsv}/{@link GeradorPdf})
 * foi usada.
 *
 * <p>O Spring injeta todos os {@link GeradorDocumento}; montamos o mapa
 * {formato -> gerador}. Novo formato = novo {@code @Component}, sem tocar aqui.
 */
@Component
public class DocumentoPedidoFactory {

    private final Map<FormatoDocumento, GeradorDocumento> porFormato =
            new EnumMap<>(FormatoDocumento.class);

    public DocumentoPedidoFactory(List<GeradorDocumento> geradores) {
        for (GeradorDocumento gerador : geradores) {
            porFormato.put(gerador.formato(), gerador);
        }
    }

    public DocumentoPedido gerar(PedidoVenda pedido, FormatoDocumento formato) {
        GeradorDocumento gerador = porFormato.get(formato);
        if (gerador == null) {
            throw new RegraNegocioException("Formato de documento nao suportado: " + formato);
        }
        return gerador.gerar(pedido);
    }
}
