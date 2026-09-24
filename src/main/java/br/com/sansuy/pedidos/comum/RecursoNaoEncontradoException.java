package br.com.sansuy.pedidos.comum;

/**
 * Lancada quando um recurso solicitado por id nao existe.
 * Mapeada para HTTP 404 pelo {@link ManipuladorGlobalExcecoes}.
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    /** Ex.: {@code new RecursoNaoEncontradoException("Cliente", 10L)}. */
    public RecursoNaoEncontradoException(String recurso, Object id) {
        super(recurso + " nao encontrado(a) para o id " + id);
    }
}
