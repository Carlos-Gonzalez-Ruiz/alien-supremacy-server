package com.carlosgonzalezruiz.aliensupremacyserver.game.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

import com.carlosgonzalezruiz.aliensupremacyserver.constant.GameConstants;
import com.carlosgonzalezruiz.aliensupremacyserver.constant.WebSocketMessageConstants;
import com.carlosgonzalezruiz.aliensupremacyserver.game.AbstractThread;
import com.carlosgonzalezruiz.aliensupremacyserver.game.server.ThreadServer;
import com.carlosgonzalezruiz.aliensupremacyserver.networking.WebSocketMessage;
import com.carlosgonzalezruiz.aliensupremacyserver.networking.WebSocketUtils;
import com.carlosgonzalezruiz.aliensupremacyserver.networking.bean.ChatMessageData;
import com.carlosgonzalezruiz.aliensupremacyserver.networking.bean.PlayerData;
import com.carlosgonzalezruiz.aliensupremacyserver.networking.bean.RoomData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Alien Supremacy - Proyecto Fin de Ciclo
 * 
 * Hilo cliente que se dedicará a trabajar el lado de servidor de cada cliente
 * por cada hilo.
 * 
 * @author Carlos González Ruiz - 2ºDAM
 */
public class ThreadClient extends AbstractThread {

	/** Logger */
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ThreadClient.class);

	/** ObjectMapper */
	private ObjectMapper objectMapper = new ObjectMapper();

	/** Hilo servidor. (Hilo padre) */
	private ThreadServer server;

	/** Socket del cliente. */
	private Socket client;
	/** Objeto de entrada de información. */
	private InputStream in;
	/** Objeto de salida de información. */
	private OutputStream out;

	/** Indicar si está conectado o no. */
	private boolean connected;
	/** Indicar si ya se ha hecho el primer ping. */
	private boolean firstPing;

	/* Datos del cliente. */
	/** Datos de usuario del cliente. */
	private PlayerData playerData;
	
	/** Datos de la request HTTP/ WebSocket */
	private String httpData;

	/**
	 * Método constructor de la clase.
	 * 
	 * @param server
	 * @param client
	 * @throws IOException
	 */
	public ThreadClient(ThreadServer server, Socket client, String httpData) throws IOException {
		super();

		this.server = server;
		this.client = client;
		this.in = client.getInputStream();
		this.out = client.getOutputStream();
		this.httpData = httpData;

		this.connected = true;
		this.firstPing = false;
	}

	/**
	 * Método run del hilo. Realizará el Handshake entre sockets en primer lugar, y
	 * luego procesará los mensajes hasta que connected = false.
	 */
	@Override
	public void run() {
		Scanner s = null;
		try {
			// Realizar handshake.
			s = WebSocketUtils.sendHandshake(httpData, in, out);

			log.info("Handsake sent to new client.");
			log.info("User {} has joined to the server.", client.getInetAddress());

			// Hacer ping inicial para comenzar con el PING/PONG.
			WebSocketUtils.sendPing(out);

			// Ejecutar bucle principal de cliente.
			update();
		} catch (NoSuchAlgorithmException | IOException e) {
			log.error("An error has ocurred while doing the server-client communication: {}", e.getMessage());
			e.printStackTrace();
			
			// Desconectar.
			this.connected = false;
		} finally {
			if (s != null) {
				s.close();
			}
		}

		// Cerrar conexión de los clientes.
		try {
			client.close();
		} catch (IOException e) {
			log.error("An error has ocurred while closing client connection: {}", e.getMessage());
		}

		log.info("A client has just disconnected from the server.");
		
		// Informar a los clientes.
		try {
			WebSocketMessage response = new WebSocketMessage();
			response.setCode(WebSocketMessageConstants.CLIENT_CODE_GLOBAL_PLAYER_LEFT);
			response.setContent(objectMapper.writeValueAsString(playerData));
			String output;
			output = objectMapper.writeValueAsString(response);
			sendBroadcast(output);
		} catch (JsonProcessingException e) {
			log.error("Couldn't send disconnected message.");
		}
	}

	/**
	 * Método que realiza el procesamiento de mensajería, siempre y cuando connected
	 * = true.
	 * 
	 * @throws IOException
	 */
	private void update() throws IOException {
		while (connected) {
			// Recibir código de mensaje.
			try {
				int code = in.read();
				processMessaging((byte) code, in, out);
			} catch (IOException e) {
				e.printStackTrace();
			}

			sleepThread(GameConstants.SERVER_TICK_RATE);
		}
	}

	/**
	 * Método que procesa el código de mensaje.
	 * 
	 * @param code
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public void processMessaging(byte code, InputStream in, OutputStream out) throws IOException {
		switch (code) {

		case GameConstants.CODE_TEXT: {
			// Procesar mensaje.
			String jsonMessage = WebSocketUtils.getMessage(in);

			// Obtener datos.
			WebSocketMessage message = objectMapper.readValue(jsonMessage, WebSocketMessage.class);

			// Procesar y crear respuesta.
			processMessagingFrontEnd(out, message);

			break;
		}

		case GameConstants.CODE_PONG: {// Enviar PING.
			if (WebSocketUtils.getMessage(in).equals(GameConstants.MESSAGE_PING_KEY)) {
				WebSocketUtils.sendPing(out);
				
				if (!firstPing) {
					// Lista de clientes actuales en la sala.
					ArrayList<PlayerData> clients = new ArrayList<>();
					for (ThreadClient c : server.getClients()) {
						if (c.getConnected() && c.getFirstPing()) {
							// Añadir a la lista de clientes.
							clients.add(c.getPlayerData());
						}
					}
					
					// Enviar al cliente lista de clientes actuales.
					WebSocketMessage response = new WebSocketMessage();
					response.setCode(WebSocketMessageConstants.CLIENT_CODE_GLOBAL_PLAYER_LIST);
					response.setContent(objectMapper.writeValueAsString(clients));
					
					String clientsOutput = objectMapper.writeValueAsString(response);
					WebSocketUtils.sendMessage(out, clientsOutput);
					
					// Inidicar que se ha hecho el primer ping.
					firstPing = true;
				}
			}
			break;
		}

		case GameConstants.CODE_CLOSE: { // Terminar WebSocket entre ambos.
			// Cerrar socket.
			WebSocketUtils.sendClose(out);
			connected = false;
			break;
		}

		case -1: // Se ha perdido conexión con el cliente.
			connected = false;
			break;

		default:
			break;

		}
	}

	/**
	 * Método que procesa el código de mensaje de la comunicación de red del
	 * front-end.
	 * 
	 * @param out     el outputStream.
	 * @param message el mensaje.
	 * @return el mensaje Web Socket.
	 * @throws IOException 
	 */
	private void processMessagingFrontEnd(OutputStream out, WebSocketMessage message) throws IOException {
		switch (message.getCode()) {
		case WebSocketMessageConstants.SERVER_CODE_GLOBAL_NEW_PLAYER: { // Enviarle a los nuevos clientes el nuevo
			// Establecer datos de usuario.
			playerData = objectMapper.readValue(message.getContent(), PlayerData.class);
			log.info("New player in join room: {}", playerData);
			
			// Enviar a todos los clientes.
			WebSocketMessage response2 = new WebSocketMessage();
			response2.setCode(WebSocketMessageConstants.CLIENT_CODE_GLOBAL_NEW_PLAYER);
			response2.setContent(message.getContent());
			
			String output = objectMapper.writeValueAsString(response2);
			sendBroadcast(output);
			
			break;
		}
		case WebSocketMessageConstants.SERVER_CODE_GLOBAL_CHAT_MESSAGE: { // Enviar mensaje de chat a los usuarios.
			// Crear mensaje de chat.
			ChatMessageData chatMessage = objectMapper.readValue(message.getContent(), ChatMessageData.class);
			
			// Solo enviar mensaje en caso de que no esté vacio ni sea demasiado largo.
			if (!chatMessage.getContent().isBlank() && chatMessage.getContent().length() < 512) {
				// Enviar mensaje de chat.
				WebSocketMessage response = new WebSocketMessage();
				response.setCode(WebSocketMessageConstants.CLIENT_CODE_GLOBAL_CHAT_MESSAGE);
				response.setContent(objectMapper.writeValueAsString(chatMessage));
	
				String output = objectMapper.writeValueAsString(response);
				sendBroadcast(output);
			}
			
			break;
		}
		case WebSocketMessageConstants.SERVER_CODE_GLOBAL_CREATE_ROOM: { // Crear sala.
				// Añadir sala.
				RoomData room = objectMapper.readValue(message.getContent(), RoomData.class);
				room.setRoomId("a");
				room.setPlayers(0);
				server.getRooms().add(room);
				log.info("Created room {}", room);
				
				// Enviar id de la sala al jugador para entrar.
				WebSocketMessage responseHost = new WebSocketMessage();
				responseHost.setCode(WebSocketMessageConstants.CLIENT_CODE_GLOBAL_ROOM_ID);
				responseHost.setContent(objectMapper.writeValueAsString(room));
				String outputHost = objectMapper.writeValueAsString(responseHost);
				WebSocketUtils.sendMessage(out, outputHost);
				
				// Enviar lista de servidores a todos.
				WebSocketMessage response = new WebSocketMessage();
				response.setCode(WebSocketMessageConstants.CLIENT_CODE_GLOBAL_ROOM_LIST);
				/* ... Crear lista de salas sin mostrar contraseñas ... */
				response.setContent(objectMapper.writeValueAsString(server.getRooms()));
	
				String output = objectMapper.writeValueAsString(response);
				sendBroadcast(output);
			break;
		}
		default:
			// No hacer nada.
			break;
		}
	}
	
	/**
	 * Método que procesa el código de mensaje de la comunicación de red del
	 * front-end.
	 * 
	 * @param message el mensaje.
	 */
	private void sendBroadcast(String output) {
		for (ThreadClient c : server.getClients()) {
			if (c != null && c != this && c.getConnected() && c.getFirstPing()) {
				try {
					WebSocketUtils.sendMessage(c.getOut(), output);
				} catch (IOException e) {
					log.info("Could not send to client({}): {}", c.getPlayerData(), e.getMessage());
				}
			}
		}
	}

	/**
	 * Get client
	 * 
	 * @return Socket
	 */
	public Socket getClient() {
		return client;
	}

	/**
	 * Get out
	 * 
	 * @return OutputStream
	 */
	public OutputStream getOut() {
		return out;
	}

	/**
	 * Get connected
	 * 
	 * @return connected
	 */
	public synchronized boolean getConnected() {
		return connected;
	}
	
	/**
	 * Get firstPing
	 * 
	 * @return firstPing
	 */
	public synchronized boolean getFirstPing() {
		return firstPing;
	}
	
	/**
	 * Get playerData
	 * 
	 * @return playerData
	 */
	public synchronized PlayerData getPlayerData() {
		return playerData;
	}

	/**
	 * Set connected
	 * 
	 * @param connected
	 */
	public synchronized void setConnected(boolean connected) {
		this.connected = connected;
		if (!this.connected) {
			try {
				WebSocketUtils.sendClose(out);
			} catch (IOException e) {
				log.error("Could not close socket after server disconection: {}", e.getMessage());
			}
		}
	}
	
	/**
	 * Get httpData
	 * 
	 * @param httpData
	 */
	public synchronized void setHttpData(String httpData) {
		this.httpData = httpData;
	}

}
