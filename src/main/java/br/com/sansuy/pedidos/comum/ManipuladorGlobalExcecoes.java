package br.com.sansuy.pedidos.comum;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.sansuy.pedidos.pedido.estado.TransicaoInvalidaException;

/**
 * Tratamento centralizado de excecoes da API (padrao "controller advice").
 *
 * <p>Concentra a traducao excecao-de-dominio -> status HTTP num unico lugar,
 * mantendo os controllers limpos. Cada camada lanca a excecao semantica; aqui
 * ela vira um {@link RespostaErro} com o status correto.
 */
@RestControllerAdvice
public class ManipuladorGlobalExcecoes {

    /** Recurso inexistente -> 404 Not Found. */
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<RespostaErro> tratarNaoEncontrado(
            RecursoNaoEncontradoException ex, HttpServletRequest req) {
        return construir(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    /** Violacao de regra de negocio de entrada -> 400 Bad Request. */
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<RespostaErro> tratarRegraNegocio(
            RegraNegocioException ex, HttpServletRequest req) {
        return construir(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    /** Transicao de status invalida (padrao State) -> 409 Conflict. */
    @ExceptionHandler(TransicaoInvalidaException.class)
    public ResponseEntity<RespostaErro> tratarTransicaoInvalida(
            TransicaoInvalidaException ex, HttpServletRequest req) {
        return construir(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    /** Falha de Bean Validation (@Valid) -> 400 com a lista de campos. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespostaErro> tratarValidacao(
            MethodArgumentNotValidException ex, HttpServletRequest req) {
        RespostaErro corpo = new RespostaErro(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Requisicao invalida: verifique os campos.",
                req.getRequestURI());

        List<RespostaErro.CampoInvalido> campos = ex.getBindingResult().getFieldErrors().stream()
                .map(this::mapearCampo)
                .collect(Collectors.toList());
        corpo.setCampos(campos);

        return ResponseEntity.badRequest().body(corpo);
    }

    /** Rede de seguranca: qualquer excecao nao prevista -> 500. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespostaErro> tratarGenerico(Exception ex, HttpServletRequest req) {
        return construir(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno inesperado.", req);
    }

    private RespostaErro.CampoInvalido mapearCampo(FieldError erro) {
        return new RespostaErro.CampoInvalido(erro.getField(), erro.getDefaultMessage());
    }

    private ResponseEntity<RespostaErro> construir(HttpStatus status, String mensagem,
                                                   HttpServletRequest req) {
        RespostaErro corpo = new RespostaErro(
                status.value(), status.getReasonPhrase(), mensagem, req.getRequestURI());
        return ResponseEntity.status(status).body(corpo);
    }
}
