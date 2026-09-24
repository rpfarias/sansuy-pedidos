package br.com.sansuy.pedidos.pedido;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sansuy.pedidos.cliente.Cliente;
import br.com.sansuy.pedidos.cliente.ClienteService;
import br.com.sansuy.pedidos.comum.RecursoNaoEncontradoException;
import br.com.sansuy.pedidos.pedido.dto.ItemPedidoRequest;
import br.com.sansuy.pedidos.pedido.dto.PedidoRequest;
import br.com.sansuy.pedidos.pedido.dto.PedidoResponse;
import br.com.sansuy.pedidos.produto.Produto;
import br.com.sansuy.pedidos.produto.ProdutoService;

/**
 * Orquestra a criacao e a leitura de pedidos.
 *
 * <p>Depende de {@link ClienteService} e {@link ProdutoService} para reaproveitar
 * o carregamento (e o 404) de cada agregado.
 *
 * <p>Fase 1: o preco unitario do item recebe o preco base do produto. Na Fase 3,
 * a montagem passa a usar Builder e o preco vem de uma Strategy por segmento.
 */
@Service
@Transactional
public class PedidoService {

    private final PedidoRepository repositorio;
    private final ClienteService clienteService;
    private final ProdutoService produtoService;

    public PedidoService(PedidoRepository repositorio,
                         ClienteService clienteService,
                         ProdutoService produtoService) {
        this.repositorio = repositorio;
        this.clienteService = clienteService;
        this.produtoService = produtoService;
    }

    public PedidoResponse criar(PedidoRequest req) {
        Cliente cliente = clienteService.carregar(req.getClienteId());
        PedidoVenda pedido = new PedidoVenda(cliente);

        for (ItemPedidoRequest itemReq : req.getItens()) {
            Produto produto = produtoService.carregar(itemReq.getProdutoId());
            // Placeholder de preco (Fase 1). Fase 3 substitui pela Strategy.
            BigDecimal precoUnitario = produto.getPrecoBaseMetroQuadrado();
            ItemPedido item = new ItemPedido(produto, itemReq.getMetragem(), precoUnitario);
            pedido.adicionarItem(item);
        }

        // orphanRemoval + cascade ALL persistem os itens junto (agregado).
        return PedidoResponse.de(repositorio.save(pedido));
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscarPorId(Long id) {
        PedidoVenda pedido = repositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido", id));
        return PedidoResponse.de(pedido);
    }

    /**
     * Lista todos os pedidos.
     *
     * <p>ATENCAO (proposital): mapear cada pedido acessa itens/cliente LAZY,
     * gerando o problema de N+1. A correcao com JOIN FETCH / @EntityGraph e o
     * exercicio [EU FACO] da Fase 4.
     */
    @Transactional(readOnly = true)
    public List<PedidoResponse> listar() {
        return repositorio.findAll().stream()
                .map(PedidoResponse::de)
                .collect(Collectors.toList());
    }
}
