package br.com.sansuy.pedidos.cliente;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sansuy.pedidos.cliente.dto.ClienteRequest;
import br.com.sansuy.pedidos.cliente.dto.ClienteResponse;
import br.com.sansuy.pedidos.comum.RecursoNaoEncontradoException;
import br.com.sansuy.pedidos.comum.RegraNegocioException;

/**
 * Regras de negocio de cliente. A camada de servico e dona da transacao
 * ({@code @Transactional}) e nao conhece HTTP.
 */
@Service
@Transactional
public class ClienteService {

    private final ClienteRepository repositorio;

    public ClienteService(ClienteRepository repositorio) {
        this.repositorio = repositorio;
    }

    public ClienteResponse criar(ClienteRequest req) {
        if (repositorio.existsByCnpj(req.getCnpj())) {
            throw new RegraNegocioException("Ja existe cliente com o CNPJ " + req.getCnpj());
        }
        Cliente cliente = new Cliente(req.getRazaoSocial(), req.getCnpj(), req.getSegmento());
        return ClienteResponse.de(repositorio.save(cliente));
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(Long id) {
        return ClienteResponse.de(carregar(id));
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return repositorio.findAll().stream()
                .map(ClienteResponse::de)
                .collect(Collectors.toList());
    }

    /**
     * Carrega a entidade gerenciada ou lanca 404. Reutilizado por outros
     * servicos (ex.: Pedido) que precisam do Cliente.
     */
    @Transactional(readOnly = true)
    public Cliente carregar(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", id));
    }
}
