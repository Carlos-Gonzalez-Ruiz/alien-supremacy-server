package com.carlosgonzalezruiz.aliensupremacyserver.networking.bean;

import java.io.Serializable;

/**
 * Alien Supremacy - Proyecto Fin de Ciclo
 * 
 * Objeto que contiene los datos de una sala.
 * 
 * @author Carlos González Ruiz - 2ºDAM
 */
public class RoomData implements Serializable {

	/** Serial Version ID */
	private static final long serialVersionUID = 1L;

	/** Código de sala. */
	private String roomId;
	
	/** Nombre de sala. */
	private String roomName;
	
	/** Número de jugadores. */
	private int players = 0;
	
	/** Número máximo de jugadores. */
	private int playersMax;
	
	/** Modo de juego. */
	private String gamemode;
	
	/** Indicar si la sala es privada. */
	private boolean hide;
	
	/** Contraseña de la sala. */
	private String password;
	
	/**
	 * Método constructor de la clase. 
	 */
	public RoomData() {
		super();
	}
	
	/**
	 * Método constructor de la clase.
	 * 
	 * @param roomId id de la sala.
	 * @param roomName nombre de la sala.
	 * @param playersMax número de jugadores máximo.
	 * @param gamemode modo de juego.
	 * @param hide indicar si la sala es privada.
	 * @param password la contraseña de sala.
	 */
	public RoomData(String roomId, String roomName, int playersMax, String gamemode, boolean hide, String password) {
		super();
		
		this.roomId = roomId;
		this.roomName = roomName;
		this.playersMax = playersMax;
		this.gamemode = gamemode;
		this.hide = hide;
		this.password = password;
	}

	/**
	 * @return the roomId
	 */
	public String getRoomId() {
		return roomId;
	}

	/**
	 * @param roomId the roomId to set
	 */
	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	/**
	 * @return the roomName
	 */
	public String getRoomName() {
		return roomName;
	}

	/**
	 * @param roomName the roomName to set
	 */
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	/**
	 * @return the players
	 */
	public int getPlayers() {
		return players;
	}

	/**
	 * @param players the players to set
	 */
	public void setPlayers(int players) {
		this.players = players;
	}

	/**
	 * @return the playersMax
	 */
	public int getPlayersMax() {
		return playersMax;
	}

	/**
	 * @param playersMax the playersMax to set
	 */
	public void setPlayersMax(int playersMax) {
		this.playersMax = playersMax;
	}

	/**
	 * @return the gamemode
	 */
	public String getGamemode() {
		return gamemode;
	}

	/**
	 * @param gamemode the gamemode to set
	 */
	public void setGamemode(String gamemode) {
		this.gamemode = gamemode;
	}

	/**
	 * @return the hide
	 */
	public boolean isHide() {
		return hide;
	}

	/**
	 * @param hide the hide to set
	 */
	public void setHide(boolean hide) {
		this.hide = hide;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "RoomData [roomId=" + roomId + ", roomName=" + roomName + ", players=" + players + ", playersMax="
				+ playersMax + ", gamemode=" + gamemode + ", hide=" + hide + ", password=" + password + "]";
	}
	
}
