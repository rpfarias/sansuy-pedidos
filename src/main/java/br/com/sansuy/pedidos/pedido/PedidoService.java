package br.com.sansuy.pedidos.pedido;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sansuy.pedidos.cliente.Cliente;
import br.com.sansuy.pedidos.cliente.ClienteService;
import br.com.sansuy.pedidos.comum.RecursoNaoEncontradoException;
import br.com.sansuy.pedidos.pedido.documento.DocumentoPedido;
import br.com.sansuy.pedidos.pedido.documento.DocumentoPedidoFactory;
import br.com.sansuy.pedidos.pedido.documento.FormatoDocumento;
import br.com.sansuy.pedidos.pedido.dto.ItemPedidoRequest;
import br.com.sansuy.pedidos.pedido.dto.PedidoRequest;
import br.com.sansuy.pedidos.pedido.dto.PedidoResponse;
import br.com.sansuy.pedidos.pedido.estado.EstadoPedidoFactory;
import br.com.sansuy.pedidos.pedido.evento.PedidoAprovadoEvent;
import br.com.sansuy.pedidos.pedido.preco.SeletorEstrategiaPreco;
import br.com.sansuy.pedidos.produto.Produto;
import br.com.sansuy.pedidos.produto.ProdutoService;

/**
 * Orquestra criação, transições de status e documentos do pedido.
 *
 * <p>Padrões plugados (Fase 3): <b>Builder</b> monta o agregado usando a
 * <b>Strategy</b> de preço; as transições usam o <b>State</b>
 * ({@link EstadoPedidoFactory}); a aprovação publica um evento
 * (<b>Observer</b>) que gera a OrdemProducao; documentos vêm da <b>Factory</b>.
 */
@Service
@Transactional
public class PedidoService {

    private final PedidoRepository repositorio;
    private final ClienteService clienteService;
    private final ProdutoService produtoService;
    private final SeletorEstrategiaPreco seletorPreco;
    private final ApplicationEventPublisher publicador;
    private final DocumentoPedidoFactory documentoFactory;

    public PedidoService(PedidoRepository repositorio,
                         ClienteService clienteService,
                         ProdutoService produtoService,
                         SeletorEstrategiaPreco seletorPreco,
                         ApplicationEventPublisher publicador,
                         DocumentoPedidoFactory documentoFactory) {
        this.repositorio = repositorio;
        this.clienteService = clienteService;
        this.produtoService = produtoService;
        this.seletorPreco = seletorPreco;
        this.publicador = publicador;
        this.documentoFactory = documentoFactory;
    }

    public PedidoResponse criar(PedidoRequest req) {
        Cliente cliente = clienteService.carregar(req.getClienteId());
        PedidoBuilder builder = PedidoBuilder.para(cliente, seletorPreco);
        for (ItemPedidoRequest itemReq : req.getItens()) {
            Produto produto = produtoService.carregar(itemReq.getProdutoId());
            builder.adicionarItem(produto, itemReq.getMetragem());
        }
        PedidoVenda pedido = builder.construir();
        return PedidoResponse.de(repositorio.save(pedido));
    }

    // ---- Transições de status (padrão State) ----

    /** Aprova o pedido e publica o evento que gera a OrdemProducao (Observer). */
    public PedidoResponse aprovar(Long id) {
        PedidoVenda pedido = carregar(id);
        pedido.setStatus(EstadoPedidoFactory.de(pedido.getStatus()).aprovar());
        publicador.publishEvent(new PedidoAprovadoEvent(pedido));
        return PedidoResponse.de(pedido);
    }

    /** Inicia a produção. Na Fase 4 ganha a reserva de estoque transacional. */
    public PedidoResponse iniciarProducao(Long id) {
        PedidoVenda pedido = carregar(id);
        pedido.setStatus(EstadoPedidoFactory.de(pedido.getStatus()).iniciarProducao());
        return PedidoResponse.de(pedido);
    }

    public PedidoResponse faturar(Long id) {
        PedidoVenda pedido = carregar(id);
        pedido.setStatus(EstadoPedidoFactory.de(pedido.getStatus()).faturar());
        return PedidoResponse.de(pedido);
    }

    public PedidoResponse expedir(Long id) {
        PedidoVenda pedido = carregar(id);
        pedido.setStatus(EstadoPedidoFactory.de(pedido.getStatus()).expedir());
        return PedidoResponse.de(pedido);
    }

    public PedidoResponse cancelar(Long id) {
        PedidoVenda pedido = carregar(id);
        pedido.setStatus(EstadoPedidoFactory.de(pedido.getStatus()).cancelar());
        return PedidoResponse.de(pedido);
    }

    // ---- Documento (padrão Factory) ----

    @Transactional(readOnly = true)
    public DocumentoPedido gerarDocumento(Long id, FormatoDocumento formato) {
        PedidoVenda pedido = carregar(id);
        pedido.getItens().size(); // inicializa a coleção LAZY dentro da transação
        return documentoFactory.gerar(pedido, formato);
    }

    // ---- Leitura ----

    @Transactional(readOnly = true)
    public PedidoResponse buscarPorId(Long id) {
        return PedidoResponse.de(carregar(id));
    }

    /**
     * Lista todos os pedidos.
     *
     * <p>Fase 4: usamos {@code buscarTodosComJoinFetch()} para carregar
     * cliente + itens + produto numa ÚNICA query, evitando o N+1 que
     * ocorreria com o {@code findAll()} padrão ao mapear cada pedido.
     */
    @Transactional(readOnly = true)
    public List<PedidoResponse> listar() {
        return repositorio.buscarTodosComJoinFetch().stream()
                .map(PedidoResponse::de)
                .collect(Collectors.toList());
    }

    private PedidoVenda carregar(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido", id));
    }
}
