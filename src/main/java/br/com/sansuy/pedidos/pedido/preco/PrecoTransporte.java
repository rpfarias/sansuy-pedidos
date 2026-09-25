package br.com.sansuy.pedidos.pedido.preco;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

import br.com.sansuy.pedidos.cliente.Segmento;

/**
 * [EU FAÇO] Estratégia de preço do segmento TRANSPORTE.
 *
 * <p>Regra (ver teste): acréscimo de 8% sobre o preço base, escala 2 (HALF_UP).
 * Ex.: base 100.00 -> 108.00.
 */
@Component
public class PrecoTransporte implements EstrategiaPreco {

    @Override
    public Segmento segmentoAtendido() {
        return Segmento.TRANSPORTE;
    }

    /** Fator de 8% de acréscimo: preço × 1,08. */
    private static final BigDecimal FATOR = new BigDecimal("1.08");

    @Override
    public BigDecimal aplicar(BigDecimal precoBase) {
        return precoBase.multiply(FATOR).setScale(2, RoundingMode.HALF_UP);
    }
}
