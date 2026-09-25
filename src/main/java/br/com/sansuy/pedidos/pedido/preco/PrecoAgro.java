package br.com.sansuy.pedidos.pedido.preco;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

import br.com.sansuy.pedidos.cliente.Segmento;

/**
 * [EU FAÇO] Estratégia de preço do segmento AGRO.
 *
 * <p>Regra (ver teste): desconto de 5% sobre o preço base, escala 2 (HALF_UP).
 * Ex.: base 100.00 -> 95.00.
 *
 * <p>Implemente o {@link #aplicar(BigDecimal)}. Dicas: use
 * {@code BigDecimal.multiply} com um fator (ex.: 0.95) e
 * {@code setScale(2, RoundingMode.HALF_UP)}.
 */
@Component
public class PrecoAgro implements EstrategiaPreco {

    @Override
    public Segmento segmentoAtendido() {
        return Segmento.AGRO;
    }

    /** Fator de 5% de desconto: preço × 0,95. */
    private static final BigDecimal FATOR = new BigDecimal("0.95");

    @Override
    public BigDecimal aplicar(BigDecimal precoBase) {
        return precoBase.multiply(FATOR).setScale(2, RoundingMode.HALF_UP);
    }
}
