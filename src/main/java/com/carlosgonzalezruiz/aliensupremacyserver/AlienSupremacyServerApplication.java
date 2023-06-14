package com.carlosgonzalezruiz.aliensupremacyserver;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Alien Supremacy - Proyecto Fin de Ciclo
 * 
 * Clase principal de la aplicación de servidor.
 * 
 * @author Carlos González Ruiz - 2ºDAM
 */
@SpringBootApplication
public class AlienSupremacyServerApplication implements CommandLineRunner {

	/** Logger */
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(AlienSupremacyServerApplication.class);

	/**
	 * Método principal de la clase.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(AlienSupremacyServerApplication.class, args);
	}

	/**
	 * Método que se ejecuta tras finalizar la inicialización de Spring.
	 * 
	 * @param args
	 */
	@Override
	public void run(String... args) {
		log.info("Application started...");
	}

}