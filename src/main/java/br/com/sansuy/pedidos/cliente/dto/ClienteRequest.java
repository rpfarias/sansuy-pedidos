package br.com.sansuy.pedidos.cliente.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import br.com.sansuy.pedidos.cliente.Segmento;

/**
 * Dados de entrada para criar/atualizar um cliente.
 * Validado com Bean Validation (javax.validation) via @Valid no controller.
 */
public class ClienteRequest {

    @NotBlank(message = "razaoSocial e obrigatoria")
    private String razaoSocial;

    @NotBlank(message = "cnpj e obrigatorio")
    @Pattern(regexp = "\\d{14}", message = "cnpj deve ter 14 digitos numericos")
    private String cnpj;

    @NotNull(message = "segmento e obrigatorio")
    private Segmento segmento;

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public Segmento getSegmento() {
        return segmento;
    }

    public void setSegmento(Segmento segmento) {
        this.segmento = segmento;
    }
}
