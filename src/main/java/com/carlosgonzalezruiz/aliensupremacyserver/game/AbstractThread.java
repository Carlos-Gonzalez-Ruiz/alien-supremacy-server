package com.carlosgonzalezruiz.aliensupremacyserver.game;

/**
 * Alien Supremacy - Proyecto Fin de Ciclo
 * 
 * Clase abstracta que provee de una base de métodos útiles para hilos.
 * 
 * @author Carlos González Ruiz - 2ºDAM
 */
public abstract class AbstractThread extends Thread {

	/**
	 * Método constructo de la clase.
	 */
	protected AbstractThread() {
		super();
	}

	/**
	 * Método run.
	 */
	@Override
	public void run() {

	}

	/**
	 * Método que abstrae el la espera del hilo. Es necesario realizar esta espera
	 * para evitar que el hilo consuma CPU de forma innceseria.
	 * 
	 * Time es un valor expresado en milisegundos. Para esta aplicación se
	 * recomiendo realizar la espera con la constante GameConstants.SERVER_TICK_RATE
	 * (16)
	 * 
	 * Se recomienda este valor ya que la mayoría de pantallas poseen una frecuencia
	 * de 60 HZ (16.66666ms)
	 * 
	 * @param time
	 */
	public static void sleepThread(long time) {
		try {
			sleep(time);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}
	}

}
