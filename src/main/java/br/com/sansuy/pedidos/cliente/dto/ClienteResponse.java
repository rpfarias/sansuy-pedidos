package br.com.sansuy.pedidos.cliente.dto;

import br.com.sansuy.pedidos.cliente.Cliente;
import br.com.sansuy.pedidos.cliente.Segmento;

/**
 * Representacao de saida de um cliente (fronteira de DTO — a entidade nunca
 * vaza direto na API).
 */
public class ClienteResponse {

    private Long id;
    private String razaoSocial;
    private String cnpj;
    private Segmento segmento;

    public ClienteResponse(Long id, String razaoSocial, String cnpj, Segmento segmento) {
        this.id = id;
        this.razaoSocial = razaoSocial;
        this.cnpj = cnpj;
        this.segmento = segmento;
    }

    /** Mapper entidade -> DTO (estatico e simples nesta fase). */
    public static ClienteResponse de(Cliente c) {
        return new ClienteResponse(c.getId(), c.getRazaoSocial(), c.getCnpj(), c.getSegmento());
    }

    public Long getId() {
        return id;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public String getCnpj() {
        return cnpj;
    }

    public Segmento getSegmento() {
        return segmento;
    }
}
