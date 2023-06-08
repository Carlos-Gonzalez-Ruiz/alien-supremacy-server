package com.carlosgonzalezruiz.aliensupremacyserver.networking;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Alien Supremacy - Proyecto Fin de Ciclo
 * 
 * Clase componente de Spring que se dedica a gestionar las conexión,
 * desconexiones y procesamiento de mensajes entre cliente-servidor.
 * 
 * @author Carlos González Ruiz - 2ºDAM
 */
@Component
public class WebSocketHandler extends TextWebSocketHandler {

	/** Componente para le gestión interna de comunicación cliente-servidor. */
	private ServerClientCommunication serverClientCommunication;

	/** ObjectMapper */
	private ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Método constructor de la clase.
	 * 
	 * @param serverClientCommunication el componente de gestión interna de
	 *                                  comunicación.
	 */
	public WebSocketHandler(ServerClientCommunication serverClientCommunication) {
		super();
		this.serverClientCommunication = serverClientCommunication;
		this.objectMapper = new ObjectMapper();
	}

	/**
	 * Método para procesar los mensajes recibidos por el cliente.
	 * 
	 * @param session la sesión WebSocket del cliente.
	 * @param message el mensaje de texto recibido del cliente.
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message)
			throws InterruptedException, IOException {
		// Obtener datos.
		WebSocketMessage websocketMessage = objectMapper.readValue(message.getPayload(), WebSocketMessage.class);

		// Procesar datos.
		serverClientCommunication.processMessagingFrontEnd(session, websocketMessage);
	}

	/**
	 * Método para procesar tras la conexión del cliente.
	 * 
	 * @param session la sesión WebSocket del cliente.
	 * @throws Exception
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// Añadir cliente.
		serverClientCommunication.addPlayer(session);
	}

	/**
	 * Método para procesar tras la perdida de conexión con el cliente.
	 * 
	 * @param session la sesión WebSocket del cliente.
	 * @throws Exception
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// Desconectar cliente.
		serverClientCommunication.removePlayer(session);
	}

}
