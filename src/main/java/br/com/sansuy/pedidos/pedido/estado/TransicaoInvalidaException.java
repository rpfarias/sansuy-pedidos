package br.com.sansuy.pedidos.pedido.estado;

/**
 * Tentativa de transição de status não permitida pela máquina de estados.
 * Mapeada para HTTP 409 (Conflict) pelo ManipuladorGlobalExcecoes.
 */
public class TransicaoInvalidaException extends RuntimeException {

    public TransicaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
