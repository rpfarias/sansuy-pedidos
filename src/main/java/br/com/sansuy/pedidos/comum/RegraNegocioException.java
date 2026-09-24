package br.com.sansuy.pedidos.comum;

/**
 * Violacao de uma regra de negocio de entrada (dados invalidos do ponto de
 * vista do dominio). Mapeada para HTTP 400 pelo
 * {@link ManipuladorGlobalExcecoes}.
 *
 * <p>Subtipos mais especificos surgem nas fases seguintes, por exemplo:
 * TransicaoInvalidaException (409) e EstoqueInsuficienteException (422).
 */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
