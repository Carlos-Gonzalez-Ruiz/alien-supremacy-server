package com.carlosgonzalezruiz.aliensupremacyserver.networking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.carlosgonzalezruiz.aliensupremacyserver.constant.WebSocketConstants;

/**
 * Alien Supremacy - Proyecto Fin de Ciclo
 * 
 * Clase de configuración para establecer la ruta de
 * 
 * @author Carlos González Ruiz - 2ºDAM
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	/** Componente para le gestión interna de comunicación cliente-servidor. */
	@Autowired
	private ServerClientCommunication serverClientCommunication;

	/**
	 * Método constructor de la clase.
	 * 
	 * @param serverClientCommunication el componente de gestión interna de
	 *                                  comunicación.
	 */
	public WebSocketConfig(ServerClientCommunication serverClientCommunication) {
		super();
		this.serverClientCommunication = serverClientCommunication;
	}

	/**
	 * Método para asi. En el caso de esta aplicación, solo se añadirá la ruta
	 * principal, ya que solo interesa tener un servidor principal. No obstante, es
	 * posible en futuras versiones esto cambie.
	 * 
	 * @param registry el WebSocketHandlerRegistry.
	 */
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(new WebSocketHandler(serverClientCommunication), WebSocketConstants.SERVER_PATH)
				.setAllowedOrigins("*");
	}

}
