package com.carlosgonzalezruiz.aliensupremacyserver.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Alien Supremacy - Proyecto Fin de Ciclo
 * 
 * Controlador de peticiones de la ruta Home (/)
 * 
 * @author Carlos González Ruiz - 2ºDAM
 */
@RestController
@RequestMapping("/")
public class HomeController {

	/**
	 * Redirecciona cualquier peticición index.html a index.html
	 * 
	 * @return String
	 */
	@RequestMapping("/")
	public String indexHTML() {
		return "test";
	}
	
}