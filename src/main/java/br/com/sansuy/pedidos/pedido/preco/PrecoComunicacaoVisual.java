package br.com.sansuy.pedidos.pedido.preco;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

import br.com.sansuy.pedidos.cliente.Segmento;

/**
 * [EU FAÇO] Estratégia de preço do segmento COMUNICACAO_VISUAL.
 *
 * <p>Regra (ver teste): preço CHEIO (sem ajuste), escala 2 (HALF_UP).
 * Ex.: base 100.00 -> 100.00.
 */
@Component
public class PrecoComunicacaoVisual implements EstrategiaPreco {

    @Override
    public Segmento segmentoAtendido() {
        return Segmento.COMUNICACAO_VISUAL;
    }

    @Override
    public BigDecimal aplicar(BigDecimal precoBase) {
        // Preço cheio: sem ajuste, apenas normalizando a escala para 2 casas.
        return precoBase.setScale(2, RoundingMode.HALF_UP);
    }
}
