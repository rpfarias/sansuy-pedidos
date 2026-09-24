package br.com.sansuy.pedidos;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Integra o Spring Boot ao ciclo de vida de um servlet container EXTERNO
 * (Tomcat 9 standalone).
 *
 * <p>Quando o WAR e publicado em {@code webapps/}, o Tomcat procura
 * implementacoes de {@code javax.servlet.ServletContainerInitializer}; o Spring
 * fornece uma que chama {@link #configure} para inicializar o contexto — no
 * lugar do {@code main()}. Por isso, em deploy WAR, o main() de
 * {@link Application} NAO e usado.
 */
public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }
}
