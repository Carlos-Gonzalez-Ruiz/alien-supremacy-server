package com.carlosgonzalezruiz.aliensupremacyserver.networking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.carlosgonzalezruiz.aliensupremacyserver.constant.WebSocketMessageConstants;
import com.carlosgonzalezruiz.aliensupremacyserver.networking.bean.ChatMessageData;
import com.carlosgonzalezruiz.aliensupremacyserver.networking.bean.PlayerData;
import com.carlosgonzalezruiz.aliensupremacyserver.networking.bean.RoomData;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Alien Supremacy - Proyecto Fin de Ciclo
 * 
 * Clase componente de Spring que se dedica a trabajar internamente la
 * comunicación cliente-servidor.
 * 
 * @author Carlos González Ruiz - 2ºDAM
 */
@Component
public class ServerClientCommunication {

	/** Logger */
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ServerClientCommunication.class);

	/** ObjectMapper */
	private ObjectMapper objectMapper = new ObjectMapper();

	/** Lista de sesiones WebSocket de clientes. */
	private List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

	/** Lista de hilos de cliente. */
	private ArrayList<PlayerData> players;
	/** Lista de salas actuales. */
	private ArrayList<RoomData> rooms;
	
	/** Mapa de sesiones / jugadores. */
	private HashMap<WebSocketSession, PlayerData> sessionPlayers;

	/**
	 * Método constructor de la clase.
	 */
	public ServerClientCommunication() {
		super();

		this.players = new ArrayList<>();
		this.rooms = new ArrayList<>();
		this.sessionPlayers = new HashMap<>();
	}

	/**
	 * Método para enviar la lista de jugadores globales actuales.
	 * 
	 * @param session la sesión WebSocket del cliente.
	 * @throws IOException 
	 */
	public void sendPlayersList(WebSocketSession session) throws IOException {
		// Enviar al cliente lista de clientes actuales.
		WebSocketMessage response = new WebSocketMessage();
		response.setCode(WebSocketMessageConstants.CLIENT_CODE_GLOBAL_PLAYER_LIST);
		response.setContent(objectMapper.writeValueAsString(players));

		String clientsOutput = objectMapper.writeValueAsString(response);
		sendMessage(session, clientsOutput);
	}

	/**
	 * Método que procesa el código de mensaje de la comunicación de red del
	 * front-end.
	 * 
	 * @param session la sesión WebSocket del cliente.
	 * @param message el mensaje.
	 * @return el mensaje Web Socket.
	 * @throws IOException
	 */
	public void processMessagingFrontEnd(WebSocketSession session, WebSocketMessage message) throws IOException {
		switch (message.getCode()) {
		case WebSocketMessageConstants.SERVER_CODE_GLOBAL_NEW_PLAYER: { // Enviarle a los nuevos clientes el nuevo
			// Establecer datos de usuario.
			PlayerData playerData = objectMapper.readValue(message.getContent(), PlayerData.class);
			log.info("New player in join room: {}", playerData);
			players.add(playerData);
			sessionPlayers.put(session, playerData);

			// Enviar a todos los clientes.
			WebSocketMessage response = new WebSocketMessage();
			response.setCode(WebSocketMessageConstants.CLIENT_CODE_GLOBAL_NEW_PLAYER);
			response.setContent(message.getContent());

			String output = objectMapper.writeValueAsString(response);
			sendBroadcast(session, output);

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
				sendBroadcast(session, output);
			}

			break;
		}
		case WebSocketMessageConstants.SERVER_CODE_GLOBAL_CREATE_ROOM: { // Crear sala.
			// Añadir sala.
			RoomData room = objectMapper.readValue(message.getContent(), RoomData.class);
			room.setRoomId("a");
			room.setPlayers(0);
			rooms.add(room);
			log.info("Created room {}", room);

			// Enviar id de la sala al jugador para entrar.
			WebSocketMessage responseHost = new WebSocketMessage();
			responseHost.setCode(WebSocketMessageConstants.CLIENT_CODE_GLOBAL_ROOM_ID);
			responseHost.setContent(objectMapper.writeValueAsString(room));
			String outputHost = objectMapper.writeValueAsString(responseHost);
			sendMessage(session, outputHost);

			// Enviar lista de servidores a todos.
			WebSocketMessage response = new WebSocketMessage();
			response.setCode(WebSocketMessageConstants.CLIENT_CODE_GLOBAL_ROOM_LIST);
			/* ... Crear lista de salas sin mostrar contraseñas ... */
			response.setContent(objectMapper.writeValueAsString(rooms));

			String output = objectMapper.writeValueAsString(response);
			sendBroadcast(session, output);
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
	 * @param session la sesión de cliente que envia el mensaje de broadcast.
	 * @param output el mensaje.
	 * @throws IOException 
	 */
	public void sendBroadcast(WebSocketSession session, String output) throws IOException {
		for (WebSocketSession s : sessions) {
			if (!s.equals(session)) {
				sendMessage(s, output);
			}
		}
	}

	/**
	 * Método para añadir una sesión de cliente a la lista de sesiones de cliente,
	 * cuando ocurre el proceso de conexión con el cliente.
	 * @throws IOException 
	 * 
	 * @parma session la sesión del cliente.
	 */
	public void addPlayer(WebSocketSession session) throws IOException {
		sessions.add(session);
		
		// Enviar lista de jugadores a este cliente.
		sendPlayersList(session);
	}

	/**
	 * Método para eliminar una sesión de cliente a la lista de sesiones de cliente,
	 * cuando ocurre el proceso de desconexión con el cliente.
	 * @throws IOException 
	 * @parma session la sesión del cliente.
	 */
	public void removePlayer(WebSocketSession session) throws IOException {
		sessions.remove(session);
		
		// Obtener jugador.
		PlayerData player = sessionPlayers.get(session);
		
		// Informar de desconexión.
		WebSocketMessage response = new WebSocketMessage();
		response.setCode(WebSocketMessageConstants.CLIENT_CODE_GLOBAL_PLAYER_LEFT);
		response.setContent(objectMapper.writeValueAsString(player));
		String output = objectMapper.writeValueAsString(response);
		sendBroadcast(session, output);
		
		// Eliminar del mapa de sesiones / jugadores y jugador.
		sessionPlayers.remove(session);
		players.remove(player);
	}

	/**
	 * Método de utilidad para abstraer el proceso de envío de mensaje de cadenas de
	 * texto.
	 * 
	 * @param session la sesión WebSocket del cliente.
	 * @param message el mensaje de texto a enviar.
	 * @param text    el texto del mensaje.
	 * @throws IOException
	 */
	private void sendMessage(WebSocketSession session, String message) throws IOException {
		session.sendMessage(new TextMessage(message));
	}

}
