package br.com.sansuy.pedidos.pedido;

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

import br.com.sansuy.pedidos.pedido.dto.PedidoRequest;
import br.com.sansuy.pedidos.pedido.dto.PedidoResponse;

/**
 * Endpoints REST de pedido.
 *
 * <p>As transicoes de status (aprovar, faturar, expedir, cancelar) e a geracao
 * de documento NAO estao aqui ainda: dependem dos padroes State/Observer/Factory
 * da Fase 3 (partes [EU FACO]).
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService servico;

    public PedidoController(PedidoService servico) {
        this.servico = servico;
    }

    /** 201 Created. 404 se cliente/produto nao existir; 400 se corpo invalido. */
    @PostMapping
    public ResponseEntity<PedidoResponse> criar(@Valid @RequestBody PedidoRequest req,
                                                UriComponentsBuilder uriBuilder) {
        PedidoResponse criado = servico.criar(req);
        URI local = uriBuilder.path("/api/pedidos/{id}")
                .buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(local).body(criado);
    }

    @GetMapping("/{id}")
    public PedidoResponse buscar(@PathVariable Long id) {
        return servico.buscarPorId(id);
    }

    @GetMapping
    public List<PedidoResponse> listar() {
        return servico.listar();
    }
}
