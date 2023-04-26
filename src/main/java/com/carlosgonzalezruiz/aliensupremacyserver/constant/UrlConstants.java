package com.carlosgonzalezruiz.aliensupremacyserver.constant;

/**
 * Alien Supremacy - Proyecto Fin de Ciclo
 * 
 * Clase estática que define constantes globales para lo a URLs.
 * 
 * @author Carlos González Ruiz - 2ºDAM
 */
public final class UrlConstants {

	/** Ruta HOME base. */
	public static final String HOME_CONTROLLER_PATH = "/*";
	/** Ruta HOME juego. (Juego) */
	public static final String HOME_CONTROLLER_PATH_GAME = "/";

	/** Ruta para peticiones de error. */
	public static final String ERROR_PATH = "error";

	/** Ruta para el fichero index.html */
	public static final String INDEX_HTML = "index.html";
	/** Ruta para el fichero genericError.html (error genérico) */
	public static final String GENERIC_ERROR_HTML = "error/generic-error.html";

	/**
	 * Método constructor de la clase.
	 */
	private UrlConstants() {
		super();
	}

}
