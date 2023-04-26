package com.carlosgonzalezruiz.aliensupremacyserver;

import com.carlosgonzalezruiz.aliensupremacyserver.game.server.ThreadServer;

/**
 * Alien Supremacy - Proyecto Fin de Ciclo
 * 
 * Clase principal de la aplicación de servidor.
 * 
 * @author Carlos González Ruiz - 2ºDAM
 */
public class AlienSupremacyServerApplication {

	/** Logger */
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AlienSupremacyServerApplication.class);
	
	/**
	 * Método principal de la clase.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		log.info("Application started...");
		
		// Iniciar servidor.
		ThreadServer threadServer = new ThreadServer();
		threadServer.start();

		// Esperar a que termine el servidor.
		try {
			threadServer.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("Failed to join the thread: {}", e.getMessage());
		}
	}

}
