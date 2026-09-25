package br.com.sansuy.pedidos.pedido.preco;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * [EU FAÇO] Testes das estratégias de preço (Strategy).
 *
 * <p>Estes testes definem as regras que cada {@code aplicar(...)} deve cumprir.
 * Hoje falham (as estratégias lançam UnsupportedOperationException). Implemente
 * o cálculo em cada classe para deixá-los verdes. Repare que a comparação usa
 * {@code assertEquals} de BigDecimal — ou seja, a ESCALA importa (deve ser 2).
 */
class EstrategiaPrecoTest {

    private final BigDecimal base = new BigDecimal("100.00");

    @Test
    @DisplayName("AGRO aplica 5% de desconto -> 95.00")
    void agro() {
        assertEquals(new BigDecimal("95.00"), new PrecoAgro().aplicar(base));
    }

    @Test
    @DisplayName("COMUNICACAO_VISUAL mantem o preco cheio -> 100.00")
    void comunicacaoVisual() {
        assertEquals(new BigDecimal("100.00"), new PrecoComunicacaoVisual().aplicar(base));
    }

    @Test
    @DisplayName("TRANSPORTE aplica 8% de acrescimo -> 108.00")
    void transporte() {
        assertEquals(new BigDecimal("108.00"), new PrecoTransporte().aplicar(base));
    }

    @Test
    @DisplayName("INDUSTRIA aplica 3% de desconto -> 97.00")
    void industria() {
        assertEquals(new BigDecimal("97.00"), new PrecoIndustria().aplicar(base));
    }

    @Test
    @DisplayName("A escala do resultado deve ser 2 (HALF_UP)")
    void escala() {
        // 18.90 * 0.95 = 17.955 -> arredonda para 17.96
        assertEquals(new BigDecimal("17.96"), new PrecoAgro().aplicar(new BigDecimal("18.90")));
    }
}
