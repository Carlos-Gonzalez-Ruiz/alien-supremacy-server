package com.carlosgonzalezruiz.aliensupremacyserver.game.server;

import java.util.ConcurrentModificationException;

import com.carlosgonzalezruiz.aliensupremacyserver.constant.GameConstants;
import com.carlosgonzalezruiz.aliensupremacyserver.game.AbstractThread;
import com.carlosgonzalezruiz.aliensupremacyserver.game.client.ThreadClient;

/**
 * Alien Supremacy - Proyecto Fin de Ciclo
 * 
 * Hilo adicional al del servidor que comprueba la conectividad de los clientes,
 * desconectando, liberando y/o terminado los hilos de aquellos clientes que
 * sobrepasado el tiempo de espera.
 * 
 * @author Carlos González Ruiz - 2ºDAM
 */
public class ThreadServerListener extends AbstractThread {

	/** Logger */
	public static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ThreadServerListener.class);

	/** Hilo del servidor. */
	ThreadServer server;

	/**
	 * Método constructor de la clase.
	 * 
	 * @param server
	 */
	public ThreadServerListener(ThreadServer server) {
		super();

		this.server = server;
	}

	/**
	 * Método run del hilo, en dónde se comprobará si el cliente se ha desconectado.
	 */
	@Override
	public void run() {
		while (server.getRunning()) {
			try {
				for (ThreadClient c : server.getClients()) {
					// Eliminar hilos cliente fantasma (connected = false)
					if (!c.getConnected()) {
						try {
							c.join();
							server.getClients().remove(c);
							
							log.info("Eliminated ghost client thread.");
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
							e.printStackTrace();
						}
					}
				}
				
				sleepThread(GameConstants.SERVER_TICK_RATE);
			} catch (ConcurrentModificationException e) {
				log.info("Got concurrent modification exception.");
			}
		}
	}

}
