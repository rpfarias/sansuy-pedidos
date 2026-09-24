package br.com.sansuy.pedidos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da aplicacao.
 *
 * <p>Continua tendo um main() para permitir rodar via IDE/embutido durante o
 * desenvolvimento, mas o fluxo oficial (Fase 2) e empacotar como WAR e publicar
 * num Tomcat 9 standalone — veja {@link ServletInitializer}.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
