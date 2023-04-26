package com.carlosgonzalezruiz.aliensupremacyserver.game.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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

				try {
					// Comprobar tipo de cliente.
					if (isWsClient(client)) {
						// Limpiar lista de clientes.
						cleanClients();
						
						// Instanciar hilo.
						ThreadClient threadClient = new ThreadClient(this, client);
						threadClient.start();
						clients.add(threadClient);
					} else {
						client.close();
					}
				} catch (IOException e) {
					log.error("Client connection error: {}", e.getMessage());
				}
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
	 * Método que comprueba mediante el socket del cliente si cierto cliente ha intentado conectarse a este servidor mediante Websocket o no.
	 * 
	 * @param client el socket del cliente.
	 * @return si la conexión se ha hecho mediante WebSocket,
	 * @throws IOException 
	 */
	public boolean isWsClient(Socket client) throws IOException {
		boolean isWebsocket = false;
		
		BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        
        // Comprobar cliente.
        String s;
        String request = "";
        while ((s = in.readLine()) != null) {
        	if (s.toLowerCase().contains("sec-websocket")) {
        		isWebsocket = true;
        		log.info("---Conexion is WebSocket----");
        	}
        	
        	if (request.equals("") && !s.equals("")) {
        		log.info("Checking request...");
        	}
        	
            System.out.println(s);
            
            if (s.isEmpty()) {
                break;
            } else {
            	request += s;
            }
        }
        
        if (!request.equals("")) {
	        if (isWebsocket) {
	        	log.info("Conexion is WebSocket, not sending dummy HTTP response.");
	        } else {
		        // Enviar mensaje tonto al cliente HTTP.
		        log.info("Detected HTTP client, sending dummy HTTP response.");
	
		        String message = """
		        		<html>
		        			<head>
		        				<title>Dummy title</title>
		        			</head>
		        			<body>
		        				<p>Dummy content</p>
		        			</body>
		        		</html>
		        """;
		        out.write("HTTP/1.0 200 OK\r\n");
		        out.write("Date: Fri, 31 Dec 1999 23:59:59 GMT\r\n");
		        out.write("Server: Apache/0.8.4\r\n");
		        out.write("Content-Type: text/html\r\n");
		        out.write("Content-Length: " + message.length() + "\r\n");
		        out.write("Expires: Sat, 01 Jan 2000 00:59:59 GMT\r\n");
		        out.write("Last-modified: Fri, 09 Aug 1996 14:21:40 GMT\r\n");
		        out.write("\r\n");
		        out.write(message);
		        
		        log.info("Sent response");
	        }
        }
		
		return isWebsocket;
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
