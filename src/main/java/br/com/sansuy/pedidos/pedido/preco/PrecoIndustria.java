package br.com.sansuy.pedidos.pedido.preco;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

import br.com.sansuy.pedidos.cliente.Segmento;

/**
 * [EU FAÇO] Estratégia de preço do segmento INDUSTRIA.
 *
 * <p>Regra (ver teste): desconto de 3% sobre o preço base, escala 2 (HALF_UP).
 * Ex.: base 100.00 -> 97.00.
 */
@Component
public class PrecoIndustria implements EstrategiaPreco {

    @Override
    public Segmento segmentoAtendido() {
        return Segmento.INDUSTRIA;
    }

    /** Fator de 3% de desconto: preço × 0,97. */
    private static final BigDecimal FATOR = new BigDecimal("0.97");

    @Override
    public BigDecimal aplicar(BigDecimal precoBase) {
        return precoBase.multiply(FATOR).setScale(2, RoundingMode.HALF_UP);
    }
}
