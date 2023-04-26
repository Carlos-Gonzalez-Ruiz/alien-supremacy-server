package com.carlosgonzalezruiz.aliensupremacyserver.game.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.carlosgonzalezruiz.aliensupremacyserver.constant.GameConstants;
import com.carlosgonzalezruiz.aliensupremacyserver.game.AbstractThread;
import com.carlosgonzalezruiz.aliensupremacyserver.game.client.ThreadClient;
import com.carlosgonzalezruiz.aliensupremacyserver.networking.bean.RoomData;

/**
 * Alien Supremacy - Proyecto Fin de Ciclo
 * 
 * Hilo servidor que se dedicará a aceptar las peticiones de los clientes,
 * instanciando un hilo por cada cliente.
 * 
 * También matará todos auquellos hilos de clientes ya desconectados o cuyo
 * tiempo de espera se ha excedido.
 * 
 * @author Carlos González Ruiz - 2ºDAM
 */
public class ThreadServer extends AbstractThread {

	/** Logger */
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ThreadServer.class);

	/** Socket del servidor. */
	private ServerSocket server;
	/** Lista de hilos de cliente. */
	private ArrayList<ThreadClient> clients;
	/** Lista de salas actuales. */
	private ArrayList<RoomData> rooms;

	/** Indicar si el servidor está operativo. */
	private boolean running;

	/**
	 * Método constructor de la clase.
	 */
	public ThreadServer() {
		super();

		this.clients = new ArrayList<>();
		this.rooms = new ArrayList<>();
		this.running = true;
	}

	/**
	 * Método run del hilo servidor, en el que aceptará peticiones hasta que running
	 * = false. Creará el servidor bajo el puerto especificado en
	 * GameConstants.SERVER_PORT
	 */
	@Override
	public void run() {
		try {
			// Crear servidor.
			server = new ServerSocket(GameConstants.SERVER_PORT);
			log.info("Server has been succesfully created at port {}.", GameConstants.SERVER_PORT);

			// Aceptar peticiones de clientes hasta que running = false.
			while (running) {
				Socket client = server.accept();
				client.setSoTimeout(5000); // Establecer timeout

				// Limpiar lista de clientes.
				cleanClients();
				
				// Instanciar hilo.
				ThreadClient threadClient = new ThreadClient(this, client);
				threadClient.start();
				clients.add(threadClient);
			}

			server.close();
		} catch (IOException e) {
			log.error("Error creating server: {}", e.getMessage());
		}

		// Esperar a que se cierren todos los hilos cliente.
		try {
			for (ThreadClient c : clients) {
				c.setConnected(false);
				c.join();
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("Failed to join client thread: {}", e.getMessage());
		}
	}

	/**
	 * Método que elimina los clientes de la lista de clientes que está desconectados.
	 */
	public synchronized void cleanClients() {
		ArrayList<ThreadClient> removeClients = new ArrayList<>();
		
		for (ThreadClient c : clients) {
			// Eliminar hilos cliente fantasma (connected = false)
			if (!c.getConnected()) {
				try {
					c.join();
					
					removeClients.add(c);
					
					log.info("Eliminated ghost client thread.");
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					e.printStackTrace();
				}
			}
		}
		
		// Eliminar clientes 1 a 1.
		for (int i = 0; i < removeClients.size(); ++i) {
			clients.remove(removeClients.get(i));
		}
	}
	
	/**
	 * Método que hace que el servidor deje de estar operativo. Una vez llamada, se
	 * requerirá de reinizar la aplicación para volver a funcionar el servidor.
	 */
	public void stopServer() {
		running = false;
	}

	/**
	 * Get clients
	 * 
	 * @return List<ThreadClient>
	 */
	public synchronized List<ThreadClient> getClients() {
		return clients;
	}
	
	/**
	 * Get rooms
	 * 
	 * @return List<RoomData>
	 */
	public synchronized List<RoomData> getRooms() {
		return rooms;
	}
	
	/**
	 * Get running
	 * 
	 * @return boolean
	 */
	public synchronized boolean getRunning() {
		return running;
	}

}
