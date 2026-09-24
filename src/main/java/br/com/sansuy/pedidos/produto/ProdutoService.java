package br.com.sansuy.pedidos.produto;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sansuy.pedidos.comum.RecursoNaoEncontradoException;
import br.com.sansuy.pedidos.comum.RegraNegocioException;
import br.com.sansuy.pedidos.produto.dto.ProdutoRequest;
import br.com.sansuy.pedidos.produto.dto.ProdutoResponse;

@Service
@Transactional
public class ProdutoService {

    private final ProdutoRepository repositorio;

    public ProdutoService(ProdutoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public ProdutoResponse criar(ProdutoRequest req) {
        if (repositorio.existsByCodigo(req.getCodigo())) {
            throw new RegraNegocioException("Ja existe produto com o codigo " + req.getCodigo());
        }
        Produto produto = new Produto(req.getCodigo(), req.getDescricao(), req.getGramatura(),
                req.getLargura(), req.getPrecoBaseMetroQuadrado());
        return ProdutoResponse.de(repositorio.save(produto));
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listar() {
        return repositorio.findAll().stream()
                .map(ProdutoResponse::de)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId(Long id) {
        return ProdutoResponse.de(carregar(id));
    }

    /** Carrega a entidade gerenciada ou lanca 404. Usado pelo PedidoService. */
    @Transactional(readOnly = true)
    public Produto carregar(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", id));
    }
}
