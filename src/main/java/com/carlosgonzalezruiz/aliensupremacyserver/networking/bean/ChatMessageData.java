/**
 * 
 */
package com.carlosgonzalezruiz.aliensupremacyserver.networking.bean;

import java.io.Serializable;

/**
 * Alien Supremacy - Proyecto Fin de Ciclo
 * 
 * Objeto que contiene los datos de un mensaje de mensaje de chat.
 * 
 * @author Carlos González Ruiz - 2ºDAM
 */
public class ChatMessageData implements Serializable {

	/** Serial Version ID */
	private static final long serialVersionUID = 1L;

	/** Código del usuario. */
	private String userId;
	
	/** Nombre del usuario. */
	private String username;
	
	/** Contenido del mensaje. */
	private String content;
	
	/**
	 * Método constructor de la clase. 
	 */
	public ChatMessageData() {
		super();
	}
	
	/**
	 * Método constructor de la clase.
	 * 
	 * @param code el código del mensaje.
	 * @param content el contenido del mensaje.
	 */
	public ChatMessageData(String userId, String username, String content) {
		super();
		
		this.userId = userId;
		this.username = username;
		this.content = content;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
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
		return "ChatMessageData [userId=" + userId + ", username=" + username + ", content=" + content + "]";
	}
	
}
