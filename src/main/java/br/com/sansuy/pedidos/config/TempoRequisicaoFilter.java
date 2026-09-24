package br.com.sansuy.pedidos.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filter PURO ({@code javax.servlet.Filter}) aplicado a TODAS as requisicoes.
 *
 * <p>Demonstra que a cadeia de filtros roda ABAIXO do Spring: este filtro
 * envolve inclusive as chamadas ao DispatcherServlet ({@code /api/...}) e ao
 * {@link PingServlet} ({@code /servlet/ping}). Mede o tempo da requisicao,
 * escreve no log e tenta devolver o valor no header {@code X-Tempo-Ms}.
 *
 * <p>Ordem: filtro (antes) -> {@code chain.doFilter} -> servlet/controller ->
 * filtro (depois). O {@code doFilter} e o ponto onde o resto da cadeia executa.
 */
@WebFilter(filterName = "tempoRequisicaoFilter", urlPatterns = "/*")
public class TempoRequisicaoFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(TempoRequisicaoFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest http = (HttpServletRequest) request;
        long inicio = System.currentTimeMillis();

        try {
            // Passa o controle adiante na cadeia (proximo filtro -> servlet).
            chain.doFilter(request, response);
        } finally {
            long duracao = System.currentTimeMillis() - inicio;
            // Header so pode ser adicionado se a resposta ainda nao foi enviada
            // (committed). Licao de servlet: nao da para mexer em headers apos o
            // corpo comecar a ser escrito.
            if (response instanceof HttpServletResponse && !response.isCommitted()) {
                ((HttpServletResponse) response).setHeader("X-Tempo-Ms", String.valueOf(duracao));
            }
            log.info("[filtro] {} {} levou {} ms",
                    http.getMethod(), http.getRequestURI(), duracao);
        }
    }
}
