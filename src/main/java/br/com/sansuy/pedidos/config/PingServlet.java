package br.com.sansuy.pedidos.config;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet PURO (sem Spring MVC), para enxergar a camada abaixo do framework.
 *
 * <p>O container (Tomcat) roteia diretamente {@code /servlet/ping} para este
 * servlet — sem passar pelo DispatcherServlet do Spring. Compare com um
 * {@code @RestController}: aqui nao ha injecao de dependencia, conversao de
 * JSON nem @ControllerAdvice; e a API crua de {@code javax.servlet}.
 *
 * <p>Mapeamento por {@code @WebServlet}: no WAR em Tomcat standalone o proprio
 * container le esta anotacao (Servlet 3.0+); embutido, quem le e o
 * {@code @ServletComponentScan} do Spring Boot.
 */
@WebServlet(name = "pingServlet", urlPatterns = "/servlet/ping")
public class PingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().printf(
                "pong (servlet puro)%n"
                        + "horario: %s%n"
                        + "thread: %s%n"
                        + "servletContext: %s%n"
                        + "requestURI: %s%n",
                LocalDateTime.now(),
                Thread.currentThread().getName(),
                getServletContext().getServerInfo(),
                req.getRequestURI());
    }
}
