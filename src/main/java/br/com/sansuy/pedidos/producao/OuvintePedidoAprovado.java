package br.com.sansuy.pedidos.producao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import br.com.sansuy.pedidos.pedido.PedidoVenda;
import br.com.sansuy.pedidos.pedido.evento.PedidoAprovadoEvent;

/**
 * OBSERVER (lado ouvinte) — ao aprovar um pedido, gera a {@link OrdemProducao}.
 *
 * <p>{@code @EventListener} síncrono: roda na MESMA thread e na MESMA transação
 * de quem publicou (o {@code PedidoService.aprovar}). Assim, a aprovação e a
 * criação da ordem commitam (ou sofrem rollback) juntas. Para rodar só após o
 * commit, usaríamos {@code @TransactionalEventListener(phase = AFTER_COMMIT)}.
 */
@Component
public class OuvintePedidoAprovado {

    private static final Logger log = LoggerFactory.getLogger(OuvintePedidoAprovado.class);

    private final OrdemProducaoRepository ordemRepositorio;

    public OuvintePedidoAprovado(OrdemProducaoRepository ordemRepositorio) {
        this.ordemRepositorio = ordemRepositorio;
    }

    @EventListener
    public void aoAprovarPedido(PedidoAprovadoEvent evento) {
        PedidoVenda pedido = evento.getPedido();
        OrdemProducao ordem = ordemRepositorio.save(new OrdemProducao(pedido));
        log.info("[observer] Pedido {} aprovado -> OrdemProducao {} criada (status {})",
                pedido.getId(), ordem.getId(), ordem.getStatus());
    }
}
