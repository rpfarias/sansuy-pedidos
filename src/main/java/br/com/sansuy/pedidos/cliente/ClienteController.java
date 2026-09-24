package br.com.sansuy.pedidos.cliente;

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

import br.com.sansuy.pedidos.cliente.dto.ClienteRequest;
import br.com.sansuy.pedidos.cliente.dto.ClienteResponse;

/**
 * Endpoints REST de cliente. So traduz HTTP<->objeto; regra fica no service.
 */
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService servico;

    public ClienteController(ClienteService servico) {
        this.servico = servico;
    }

    /** 201 Created + Location do recurso criado. */
    @PostMapping
    public ResponseEntity<ClienteResponse> criar(@Valid @RequestBody ClienteRequest req,
                                                 UriComponentsBuilder uriBuilder) {
        ClienteResponse criado = servico.criar(req);
        URI local = uriBuilder.path("/api/clientes/{id}")
                .buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(local).body(criado);
    }

    /** 200 OK, ou 404 (via @ControllerAdvice) se nao existir. */
    @GetMapping("/{id}")
    public ClienteResponse buscar(@PathVariable Long id) {
        return servico.buscarPorId(id);
    }

    /** 200 OK com a lista. */
    @GetMapping
    public List<ClienteResponse> listar() {
        return servico.listar();
    }
}
