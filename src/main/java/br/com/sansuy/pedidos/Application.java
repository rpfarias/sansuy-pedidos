package br.com.sansuy.pedidos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * Ponto de entrada da aplicacao.
 *
 * <p>Continua tendo um main() para permitir rodar via IDE/embutido durante o
 * desenvolvimento, mas o fluxo oficial (Fase 2) e empacotar como WAR e publicar
 * num Tomcat 9 standalone — veja {@link ServletInitializer}.
 *
 * <p>{@code @ServletComponentScan} faz o Spring Boot registrar as anotacoes
 * {@code @WebServlet}/{@code @WebFilter} (Servlet e Filter puros) quando roda
 * EMBUTIDO (IDE). No deploy WAR em Tomcat standalone, o proprio container ja
 * escaneia essas anotacoes — a anotacao aqui nao causa registro duplicado.
 */
@ServletComponentScan
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
