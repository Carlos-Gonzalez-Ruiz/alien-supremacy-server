package com.carlosgonzalezruiz.aliensupremacyserver.constant;

/**
 * Alien Supremacy - Proyecto Fin de Ciclo
 * 
 * Clase estática que define constantes globales para lo relativo a la lógica
 * de las comunicaciones de red mediante WebSocket.
 * 
 * @author Carlos González Ruiz - 2ºDAM
 */
public final class WebSocketMessageConstants {
	
	/* Cliente → Servidor */
	
	/** Código de mensaje de nuevo jugador en el listado de salas. (Cliente → Servidor)
	 * 
	 * Contenido:
	 * 	{
	 * 		"userId": "...",
	 * 		"username": "..." 
	 * 	}
	 */
	public static final int SERVER_CODE_GLOBAL_NEW_PLAYER = 1;
	
	/** Código de mensaje de mensaje de chat en el listado de salas. (Cliente → Servidor)
	 * 
	 * Contenido:
	 * 	{
	 * 		"userId": "...",
	 * 		"username": "...", 
	 * 		"message": "..."
	 * 	}
	 */
	public static final int SERVER_CODE_GLOBAL_CHAT_MESSAGE = 2;
	
	/** Código de mensaje de jugador sale del listado de salas. (Cliente → Servidor)
	 * 
	 * Contenido:
	 * 	{
	 * 		"userId": "...",
	 * 		"username": "..." 
	 * 	}
	 */
	public static final int SERVER_CODE_GLOBAL_PLAYER_LEFT = 3;
	
	/** Código de mensaje para crear sala. (Cliente → Servidor)
	 * 
	 * Contenido:
	 * 	{
	 * 		"roomId": "...", // Vacio
	 * 		"roomName": "...",
	 * 		"players": "...", // Vacio
	 * 		"playerMax": "...",
	 * 		"gamemode": "...",
	 * 		"hide": "...",
	 * 		"password": "..."
	 * 	}
	 */
	public static final int SERVER_CODE_GLOBAL_CREATE_ROOM = 4;
	
	/* Servidor → Cliente */
	
	/** Código de mensaje de nuevo jugador en el listado de salas. (Servidor → Cliente)
	 * 
	 * Contenido:
	 * 	{
	 * 		"userId": "..."
	 * 		"username": "..." 
	 * 	}
	 */
	public static final int CLIENT_CODE_GLOBAL_NEW_PLAYER = 1;
	
	/** Código de mensaje de mensaje de chat en el listado de salas. (Servidor → Cliente)
	 * 
	 * Contenido:
	 * 	{
	 * 		"userId": "...",
	 * 		"username": "...",
	 * 		"message": "..."
	 * 	}
	 */
	public static final int CLIENT_CODE_GLOBAL_CHAT_MESSAGE = 2;
	
	/** Código de mensaje de jugador sale del listado de salas. (Servidor → Cliente)
	 * 
	 * Contenido:
	 * 	{
	 * 		"userId": "...",
	 * 		"username": "..." 
	 * 	}
	 */
	public static final int CLIENT_CODE_GLOBAL_PLAYER_LEFT = 3;
	
	/** Código de mensaje para enviar todos los jugadores que hay globalmente. (Servidor → Cliente)
	 * 
	 * Contenido:
	 * 	[
	 * 		{
	 * 			"userId": "...",
	 * 			"username": "..."
	 * 		}, 
	 *  ]
	 */
	public static final int CLIENT_CODE_GLOBAL_PLAYER_LIST = 4;
	
	/** Código de mensaje de jugador sale del listado de salas. (Servidor → Cliente)
	 * 
	 * Contenido:
	 * 	[
	 * 		{
	 * 			"roomId": "...",
	 * 			"roomName": "...",
	 * 			"players": "...",
	 * 			"playerMax": "...",
	 * 			"gamemode": "...",
	 * 			"hide": "...",
	 * 			"password": "..." // Vacio
	 * 		}, 
	 *  ]
	 */
	public static final int CLIENT_CODE_GLOBAL_ROOM_LIST = 5;
	
	/** Código de mensaje para obtener los datos de la sala creada por el cliente. (Servidor → Cliente)
	 * 
	 * Contenido:
	 * 	{
	 * 		"roomId": "...",
	 * 		"roomName": "...",
	 * 		"players": "...",
	 * 		"playerMax": "...",
	 * 		"gamemode": "...",
	 * 		"hide": "...",
	 * 		"password": "..." // Vacio
	 * 	}
	 */
	public static final int CLIENT_CODE_GLOBAL_ROOM_ID = 6;
	
	/**
	 * Método constructor de la clase.
	 */
	private WebSocketMessageConstants() {
		super();
	}
	
}
