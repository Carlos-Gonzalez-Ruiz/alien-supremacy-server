package com.carlosgonzalezruiz.aliensupremacyserver.networking.bean;

import java.io.Serializable;

/**
 * Alien Supremacy - Proyecto Fin de Ciclo
 * 
 * Objeto que contiene los datos de un mensaje nuevo usuario.
 * 
 * @author Carlos González Ruiz - 2ºDAM
 */
public class PlayerData implements Serializable {

	/** Serial Version ID */
	private static final long serialVersionUID = 1L;

	/** Código del mensaje. */
	private String userId;
	
	/** Contenido del mensaje. */
	private String username;
	
	/**
	 * Método constructor de la clase. 
	 */
	public PlayerData() {
		super();
	}
	
	/**
	 * Método constructor de la clase.
	 * 
	 * @param code el código del mensaje.
	 * @param content el contenido del mensaje.
	 */
	public PlayerData(String userId, String username) {
		super();
		
		this.userId = userId;
		this.username = username;
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

	@Override
	public String toString() {
		return "PlayerData [userId=" + userId + ", username=" + username + "]";
	}
	
}
