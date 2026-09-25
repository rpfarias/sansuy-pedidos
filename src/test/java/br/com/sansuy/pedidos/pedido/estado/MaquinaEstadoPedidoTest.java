package br.com.sansuy.pedidos.pedido.estado;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.sansuy.pedidos.pedido.StatusPedido;

/**
 * [EU FAÇO] Testes das transições de status (State).
 *
 * <p>Definem quais transições são válidas e quais devem ser bloqueadas com
 * {@link TransicaoInvalidaException}. Hoje as transições VÁLIDAS falham (os
 * estados concretos ainda não sobrescrevem os métodos permitidos). Implemente os
 * overrides em cada classe {@code EstadoXxx} para deixá-los verdes.
 */
class MaquinaEstadoPedidoTest {

    private StatusPedido transicao(StatusPedido atual, Acao acao) {
        EstadoPedido estado = EstadoPedidoFactory.de(atual);
        switch (acao) {
            case APROVAR: return estado.aprovar();
            case INICIAR: return estado.iniciarProducao();
            case FATURAR: return estado.faturar();
            case EXPEDIR: return estado.expedir();
            case CANCELAR: return estado.cancelar();
            default: throw new IllegalStateException();
        }
    }

    private enum Acao { APROVAR, INICIAR, FATURAR, EXPEDIR, CANCELAR }

    // ---- Transições VÁLIDAS (hoje falham; ficam verdes após implementar) ----

    @Test
    @DisplayName("ABERTO -> aprovar -> APROVADO")
    void aprovar() {
        assertEquals(StatusPedido.APROVADO, transicao(StatusPedido.ABERTO, Acao.APROVAR));
    }

    @Test
    @DisplayName("ABERTO -> cancelar -> CANCELADO")
    void cancelarAberto() {
        assertEquals(StatusPedido.CANCELADO, transicao(StatusPedido.ABERTO, Acao.CANCELAR));
    }

    @Test
    @DisplayName("APROVADO -> iniciarProducao -> EM_PRODUCAO")
    void iniciar() {
        assertEquals(StatusPedido.EM_PRODUCAO, transicao(StatusPedido.APROVADO, Acao.INICIAR));
    }

    @Test
    @DisplayName("APROVADO -> cancelar -> CANCELADO")
    void cancelarAprovado() {
        assertEquals(StatusPedido.CANCELADO, transicao(StatusPedido.APROVADO, Acao.CANCELAR));
    }

    @Test
    @DisplayName("EM_PRODUCAO -> faturar -> FATURADO")
    void faturar() {
        assertEquals(StatusPedido.FATURADO, transicao(StatusPedido.EM_PRODUCAO, Acao.FATURAR));
    }

    @Test
    @DisplayName("EM_PRODUCAO -> cancelar -> CANCELADO")
    void cancelarEmProducao() {
        assertEquals(StatusPedido.CANCELADO, transicao(StatusPedido.EM_PRODUCAO, Acao.CANCELAR));
    }

    @Test
    @DisplayName("FATURADO -> expedir -> EXPEDIDO")
    void expedir() {
        assertEquals(StatusPedido.EXPEDIDO, transicao(StatusPedido.FATURADO, Acao.EXPEDIR));
    }

    // ---- Transições INVÁLIDAS (já passam: por padrão tudo é bloqueado) ----

    @Test
    @DisplayName("ABERTO -> faturar é bloqueado (409)")
    void abertoNaoFatura() {
        assertThrows(TransicaoInvalidaException.class,
                () -> transicao(StatusPedido.ABERTO, Acao.FATURAR));
    }

    @Test
    @DisplayName("APROVADO -> aprovar de novo é bloqueado")
    void naoAprovaDuasVezes() {
        assertThrows(TransicaoInvalidaException.class,
                () -> transicao(StatusPedido.APROVADO, Acao.APROVAR));
    }

    @Test
    @DisplayName("FATURADO -> cancelar é bloqueado")
    void faturadoNaoCancela() {
        assertThrows(TransicaoInvalidaException.class,
                () -> transicao(StatusPedido.FATURADO, Acao.CANCELAR));
    }

    @Test
    @DisplayName("EXPEDIDO e CANCELADO são terminais")
    void terminais() {
        assertThrows(TransicaoInvalidaException.class,
                () -> transicao(StatusPedido.EXPEDIDO, Acao.CANCELAR));
        assertThrows(TransicaoInvalidaException.class,
                () -> transicao(StatusPedido.CANCELADO, Acao.APROVAR));
    }
}
