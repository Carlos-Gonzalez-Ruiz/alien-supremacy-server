package com.carlosgonzalezruiz.aliensupremacyserver.networking;

/**
 * Alien Supremacy - Proyecto Fin de Ciclo
 * 
 * Objeto que contiene los datos de un mensaje recibido mediante WebSocket.
 * 
 * @author Carlos González Ruiz - 2ºDAM
 */
public class WebSocketMessage {
	
	/** Código del mensaje. */
	private int code;
	
	/** Contenido del mensaje. */
	private String content;
	
	/**
	 * Método constructor de la clase. 
	 */
	public WebSocketMessage() {
		super();
	}
	
	/**
	 * Método constructor de la clase.
	 * 
	 * @param code el código del mensaje.
	 * @param content el contenido del mensaje.
	 */
	public WebSocketMessage(int code, String content) {
		super();
		
		this.code = code;
		this.content = content;
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "WebSocketMessage [code=" + code + ", content=" + content + "]";
	}
	
}
