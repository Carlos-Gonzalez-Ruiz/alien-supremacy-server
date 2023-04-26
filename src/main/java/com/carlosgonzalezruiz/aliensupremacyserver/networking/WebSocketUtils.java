package com.carlosgonzalezruiz.aliensupremacyserver.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.carlosgonzalezruiz.aliensupremacyserver.constant.GameConstants;
import com.carlosgonzalezruiz.aliensupremacyserver.game.client.ThreadClient;

/**
 * Alien Supremacy - Proyecto Fin de Ciclo
 * 
 * Clase estática que provee de una serie de utilidades para abstraer el proceso
 * de conexión, envío, recibo de paquetes en una comunicación Java ServerSocket
 * + WebSocket.
 * 
 * Para cuando el servidor reciba un mensaje del cliente, el servidor deberá
 * decodificarlo en función de la clave que se le pasa en el paquete. (Se
 * recomiendo ver implementación en los métodos de esta clase)
 * 
 * Por el contrario, cuando el servidor envía un mensaje al cliente, el servidor
 * solo deberá pasar el código de mensaje, la longitud, y el mensaje.
 * 
 * @author Carlos González Ruiz - 2ºDAM
 */
public class WebSocketUtils {
	
	/** Logger */
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WebSocketUtils.class);
	
	/**
	 * Método constructor de la clase.
	 */
	private WebSocketUtils() {
		super();
	}

	/**
	 * Método que realiza el saludo inicial entre Java ServerSocket y el WebSocket.
	 * 
	 * A la hora de realizar el saludo, el servidor recibirá primero una respuesta,
	 * de la cual contendrá una clave.
	 * 
	 * Esta clave tendrá que ser enviada al cliente, pero transformada a un hash
	 * SHA-1, obtenerlo en base64, y devolverle esto al cliente de nuevo para
	 * establecer finalmente la conexión entre ambos sockets.
	 * 
	 * Adicionalmente, este método devolverá un objeto Scanner abierto, del cual
	 * programador deberá cerrar tras finalizar la conexión con el cliente.
	 * 
	 * @param in
	 * @param out
	 * @return Scanner
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public static synchronized Scanner sendHandshake(InputStream in, OutputStream out)
			throws IOException, NoSuchAlgorithmException {
		Scanner s = new Scanner(in, StandardCharsets.UTF_8);

		// Obtener texto de la petición. (hasta que se encuentre 2 retornos de carro)
		String data = s.useDelimiter("\r\n\r\n").next();

		// Comprobar si encuentra la propiedad GET en la petición.
		Matcher get = Pattern.compile("^GET").matcher(data);
		if (get.find()) {
			// Obtener el valor de clave mediante regex.
			Matcher match = Pattern.compile("Sec-Websocket-Key: (.*)").matcher(data);
			match.find();

			// Crear petición.
			byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n" + "Connection: Upgrade\r\n"
					+ "Upgrade: websocket\r\n" + "Sec-Websocket-Accept: "
					+ Base64.getEncoder()
							.encodeToString(MessageDigest.getInstance("SHA-1")
									.digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
											.getBytes(StandardCharsets.UTF_8)))
					+ "\r\n\r\n").getBytes(StandardCharsets.UTF_8);

			// Enviar petición.
			out.write(response, 0, response.length);
			out.flush();
		}

		return s;
	}

	/* Ascii / UTF-8 */
	/**
	 * Envía un mensaje message, como String, usando de código de mensaje el de
	 * texto. (code → 128 | 0x1 → 129)
	 * 
	 * @param out
	 * @param message
	 * @throws IOException
	 */
	public static synchronized void sendMessage(OutputStream out, String message) throws IOException {
		sendMessage(GameConstants.CODE_TEXT, out, message);
	}

	/**
	 * Envía un mensaje message, como String, usando de código de mensaje el que el
	 * programador desee.
	 * 
	 * @param code
	 * @param out
	 * @param message
	 * @throws IOException
	 */
	public static synchronized void sendMessage(byte code, OutputStream out, String message) throws IOException {
		// Crear response.
		ArrayList<Byte> listResponse = new ArrayList<>();

		// Establecer código del mensaje.
		listResponse.add(code);

		// Indicar longitud del mensaje (esta aplicacion solo tiene soporte para 256
		// bytes y 2^16 bytes)
		
		if (message.length() > 127) {
			listResponse.add((byte) 126);
			listResponse.add((byte) (message.length() >> 8));
			listResponse.add((byte) (message.length() & 255));
		} else {
			listResponse.add((byte) message.length());
		}

		// Codificar mensaje mediante las claves.
		for (int i = 0; i < message.length(); ++i) {
			listResponse.add(message.getBytes()[i]);
		}

		// Convertir response.
		byte[] response = new byte[listResponse.size()];
		for (int i = 0; i < response.length; ++i) {
			response[i] = listResponse.get(i);
		}

		// Enviar response al cliente.
		out.write(response, 0, response.length);
		out.flush();
	}

	/**
	 * Obtiene un mensaje, como String. Es importante tener en cuenta que este
	 * método solo se ha de usar tras leer el primer byte, que define el código de
	 * mensaje e indica el uso específico de este. No haberlo hecho podría causar
	 * comportamiento indefinido.
	 * 
	 * @param in
	 * @return String
	 * @throws IOException
	 */
	public static synchronized String getMessage(InputStream in) throws IOException {
		StringBuilder out = new StringBuilder();

		byte initialLength = (byte) (in.read() - 128); // Para obtener la logitud del mensaje, restar - 128 (establecer
														// a 0 el bit mayor).
		int finalLength;
		
		switch (initialLength) {
		case 126: // Short.
			finalLength = ((in.read() << 8) + in.read());
			break;
		case 127: // Long (Sin soporte para esta aplicación Java)
			finalLength = 0;
			break;
		default: // Por defecto, tomar valor inicial.
			finalLength = initialLength;
			break;
		}

		// Clave para decodificar el mensaje.
		byte[] key = new byte[] { (byte) (in.read()), (byte) (in.read()), (byte) (in.read()), (byte) (in.read()) };
		
		if (finalLength > 0) {
			// Mensaje codificado.
			byte[] binMessage = in.readNBytes(finalLength);

			// Proceso de decodificación del mensaje.
			for (int i = 0; i < binMessage.length; ++i) {
				out.append((char) (binMessage[i] ^ key[i & 0x3]));
			}
		}
		
		return out.toString();
	}

	/* Binario */
	/**
	 * Envía un mensaje message, como bytes, usando de código de mensaje el de
	 * binario. (code → 128 | 0x2 → 130)
	 * 
	 * @param out
	 * @param message
	 * @throws IOException
	 */
	public static synchronized void sendMessageBin(OutputStream out, byte[] message) throws IOException {
		sendMessageBin(GameConstants.CODE_BINARY, out, message);
	}

	/**
	 * Envía un mensaje message, como bytes, usando de código de mensaje el que el
	 * programador desee.
	 * 
	 * @param code
	 * @param out
	 * @param message
	 * @throws IOException
	 */
	public static synchronized void sendMessageBin(byte code, OutputStream out, byte[] message) throws IOException {
		// Crear response.
		ArrayList<Byte> listResponse = new ArrayList<>();

		// Establecer código del mensaje.
		listResponse.add(code);

		// Indicar longitud del mensaje (esta aplicacion solo tiene soporte para 256
		// bytes)
		listResponse.add((byte) message.length);

		// Codificar mensaje mediante las claves.
		for (int i = 0; i < message.length; ++i) {
			listResponse.add(message[i]);
		}

		// Convertir response.
		byte[] response = new byte[listResponse.size()];
		for (int i = 0; i < response.length; ++i) {
			response[i] = listResponse.get(i);
		}

		// Enviar response al cliente.
		out.write(response, 0, response.length);
		out.flush();
	}

	/**
	 * Obtiene un mensaje, como bytes. Es importante tener en cuenta que este método
	 * solo se ha de usar tras leer el primer byte, que define el código de mensaje
	 * e indica el uso específico de este. No haberlo hecho podría causar
	 * comportamiento indefinido.
	 * 
	 * @param in
	 * @return String
	 * @throws IOException
	 */
	public static synchronized byte[] getMessageBin(InputStream in) throws IOException {
		ArrayList<Byte> message = new ArrayList<>();

		byte initialLength = (byte) (in.read() - 128); // Para obtener la logitud del mensaje, restar (establecer a 0)
														// el bit mayor.
		int finalLength;

		switch (initialLength) {
		case 126: // Short.
			finalLength = (in.read() << 8) + in.read();
			break;
		case 127: // Long (Sin soporte para esta aplicación Java)
			finalLength = 0;
			break;
		default: // Por defecto, tomar valor inicial.
			finalLength = initialLength;
			break;
		}

		// Clave para decodificar el mensaje.
		byte[] key = new byte[] { (byte) (in.read()), (byte) (in.read()), (byte) (in.read()), (byte) (in.read()) };

		if (finalLength > 0) {
			// Mensaje codificado.
			byte[] binMessage = in.readNBytes(finalLength);

			// Proceso de decodificación del mensaje.
			for (int i = 0; i < binMessage.length; ++i) {
				message.add((byte) (binMessage[i] ^ key[i & 0x3]));
			}
		}

		// Construir array.
		byte[] out = new byte[message.size()];
		for (int i = 0; i < out.length; ++i) {
			out[i] = message.get(i);
		}

		return out;
	}

	/* Comunicación */
	/**
	 * Envía un mensaje PING a un cliente, usando de clave la constante
	 * "GameConstants.MESSAGE_PING_KEY".
	 * 
	 * @param out
	 * @throws IOException
	 */
	public static synchronized void sendPing(OutputStream out) throws IOException {
		sendMessage(GameConstants.CODE_PING, out, GameConstants.MESSAGE_PING_KEY);
	}

	/**
	 * Envía al cliente que se termina su conexión en el servidor.
	 * 
	 * @param out
	 * @throws IOException
	 */
	public static synchronized void sendClose(OutputStream out) throws IOException {
		byte[] message = new byte[2];
		message[0] = GameConstants.CODE_CLOSE;
		message[1] = (byte) 0; // Length a 0
		out.write(message);
		out.flush();
	}

}
