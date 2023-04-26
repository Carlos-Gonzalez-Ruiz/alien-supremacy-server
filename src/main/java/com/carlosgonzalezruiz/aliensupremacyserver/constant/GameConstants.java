package com.carlosgonzalezruiz.aliensupremacyserver.constant;

/**
 * Alien Supremacy - Proyecto Fin de Ciclo
 * 
 * Clase estática que define constantes globales para lo relativo a la lógica
 * del juego.
 * 
 * @author Carlos González Ruiz - 2ºDAM
 */
public final class GameConstants {

	/** Puerto del que se instanciará el servidor de juego. */
	public static final int SERVER_PORT = 8080/*42000*/;
	/** Tiempo en milisegundos que esperan los hilos cliente. */
	public static final int SERVER_TICK_RATE = 16*0+100;

	/** Código de mensaje para comunicación por texto. (text frame) */
	public static final byte CODE_TEXT = (byte) 128 | 0x1;
	/** Código de mensaje para comunicación por binario. (text frame) */
	public static final byte CODE_BINARY = (byte) 128 | 0x2;
	/** Código de mensaje para comunicación de PING. */
	public static final byte CODE_PING = (byte) 128 | 0x9;
	/** Código de mensaje para comunicación de PONG. */
	public static final byte CODE_PONG = (byte) 128 | 0xA;
	/** Código de mensaje para terminar comunicación. */
	public static final byte CODE_CLOSE = (byte) 128 | 0x8;

	/*
	 * Para más información sobre códigos, revisar:
	 * https://datatracker.ietf.org/doc/html/rfc6455#section-5.2
	 */

	/**
	 * Clave definida por esta aplicación para comprobar que el PING/PONG es
	 * correcto.
	 */
	public static final String MESSAGE_PING_KEY = "XXX9871237638287";

	/**
	 * Método constructor de la clase.
	 */
	private GameConstants() {
		super();
	}

}
