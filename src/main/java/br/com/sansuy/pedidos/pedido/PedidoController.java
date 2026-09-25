package br.com.sansuy.pedidos.pedido;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.sansuy.pedidos.pedido.documento.DocumentoPedido;
import br.com.sansuy.pedidos.pedido.documento.FormatoDocumento;
import br.com.sansuy.pedidos.pedido.dto.PedidoRequest;
import br.com.sansuy.pedidos.pedido.dto.PedidoResponse;

/**
 * Endpoints REST de pedido.
 *
 * <p>Transições de status retornam 200 (ok) ou 409 (TransicaoInvalidaException,
 * via @ControllerAdvice). O documento retorna os bytes com o content-type certo.
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService servico;

    public PedidoController(PedidoService servico) {
        this.servico = servico;
    }

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

    // ---- Transições de status ----

    @PostMapping("/{id}/aprovar")
    public PedidoResponse aprovar(@PathVariable Long id) {
        return servico.aprovar(id);
    }

    @PostMapping("/{id}/iniciar-producao")
    public PedidoResponse iniciarProducao(@PathVariable Long id) {
        return servico.iniciarProducao(id);
    }

    @PostMapping("/{id}/faturar")
    public PedidoResponse faturar(@PathVariable Long id) {
        return servico.faturar(id);
    }

    @PostMapping("/{id}/expedir")
    public PedidoResponse expedir(@PathVariable Long id) {
        return servico.expedir(id);
    }

    @PostMapping("/{id}/cancelar")
    public PedidoResponse cancelar(@PathVariable Long id) {
        return servico.cancelar(id);
    }

    // ---- Documento (Factory) ----

    @GetMapping("/{id}/documento")
    public ResponseEntity<byte[]> documento(@PathVariable Long id,
                                            @RequestParam(defaultValue = "CSV") FormatoDocumento formato) {
        DocumentoPedido doc = servico.gerarDocumento(id, formato);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + doc.getNomeArquivo() + "\"")
                .contentType(MediaType.parseMediaType(doc.getContentType()))
                .body(doc.getConteudo());
    }
}
