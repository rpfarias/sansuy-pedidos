package br.com.sansuy.pedidos.pedido.preco;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import br.com.sansuy.pedidos.cliente.Segmento;
import br.com.sansuy.pedidos.comum.RegraNegocioException;

/**
 * Seleciona a {@link EstrategiaPreco} correta para um {@link Segmento}.
 *
 * <p>Demonstra Strategy + injeção do Spring: o container entrega TODAS as
 * implementações de {@link EstrategiaPreco} como uma {@code List}, e aqui
 * montamos um mapa {segmento -> estratégia}. Adicionar um novo segmento é criar
 * um novo {@code @Component} — sem tocar neste seletor (aberto/fechado).
 *
 * <p>Esta classe é a "fiação" (eu implemento). O que falta é o cálculo dentro
 * de cada estratégia — parte [EU FAÇO].
 */
@Component
public class SeletorEstrategiaPreco {

    private final Map<Segmento, EstrategiaPreco> porSegmento = new EnumMap<>(Segmento.class);

    public SeletorEstrategiaPreco(List<EstrategiaPreco> estrategias) {
        for (EstrategiaPreco estrategia : estrategias) {
            porSegmento.put(estrategia.segmentoAtendido(), estrategia);
        }
    }

    public EstrategiaPreco paraSegmento(Segmento segmento) {
        EstrategiaPreco estrategia = porSegmento.get(segmento);
        if (estrategia == null) {
            throw new RegraNegocioException("Sem estrategia de preco para o segmento " + segmento);
        }
        return estrategia;
    }
}
