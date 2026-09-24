package br.com.sansuy.pedidos.produto;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.sansuy.pedidos.produto.dto.ProdutoRequest;
import br.com.sansuy.pedidos.produto.dto.ProdutoResponse;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService servico;

    public ProdutoController(ProdutoService servico) {
        this.servico = servico;
    }

    @PostMapping
    public ResponseEntity<ProdutoResponse> criar(@Valid @RequestBody ProdutoRequest req,
                                                 UriComponentsBuilder uriBuilder) {
        ProdutoResponse criado = servico.criar(req);
        URI local = uriBuilder.path("/api/produtos/{id}")
                .buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(local).body(criado);
    }

    @GetMapping
    public List<ProdutoResponse> listar() {
        return servico.listar();
    }

    @GetMapping("/{id}")
    public ProdutoResponse buscar(@PathVariable Long id) {
        return servico.buscarPorId(id);
    }
}
