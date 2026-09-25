package br.com.sansuy.pedidos.pedido.preco;

import java.math.BigDecimal;

import br.com.sansuy.pedidos.cliente.Segmento;

/**
 * STRATEGY — contrato de cálculo de preço por segmento do cliente.
 *
 * <p>Cada segmento tem uma implementação concreta (um {@code @Component}). O
 * {@link SeletorEstrategiaPreco} escolhe a estratégia certa em runtime a partir
 * do {@link Segmento} — sem {@code if/switch} espalhado pelo código.
 *
 * <p>[EU FAÇO]: as implementações concretas ({@code PrecoAgro}, etc.) estão com
 * o método {@link #aplicar(BigDecimal)} por implementar. Veja os testes em
 * {@code src/test/.../preco} para as regras exatas.
 */
public interface EstrategiaPreco {

    /** Segmento que esta estratégia atende. */
    Segmento segmentoAtendido();

    /**
     * Recebe o preço base (R$/m²) do produto e devolve o preço unitário já
     * ajustado pela política do segmento. Sempre com escala 2 (HALF_UP).
     */
    BigDecimal aplicar(BigDecimal precoBase);
}
