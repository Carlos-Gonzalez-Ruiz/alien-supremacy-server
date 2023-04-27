package com.carlosgonzalezruiz.aliensupremacyserver;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import com.carlosgonzalezruiz.aliensupremacyserver.game.server.ThreadServer;

/**
 * Alien Supremacy - Proyecto Fin de Ciclo
 * 
 * Clase principal de la aplicación de servidor.
 * 
 * @author Carlos González Ruiz - 2ºDAM
 */
public class AlienSupremacyServerApplication {

	/** Logger */
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AlienSupremacyServerApplication.class);
	
	/**
	 * Método principal de la clase.
	 * 
	 * @param args
	 * @throws IOException 
	 * @throws KeyStoreException 
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 * @throws UnrecoverableKeyException 
	 * @throws KeyManagementException 
	 */
	public static void main(String[] args) throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException {
		log.info("Application started...");
		
		// Iniciar servidor.
		ThreadServer threadServer = new ThreadServer();
		threadServer.start();

		// Esperar a que termine el servidor.
		try {
			threadServer.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("Failed to join the thread: {}", e.getMessage());
		}
	}
	
	/*public static void main(String[] args) throws Exception {
	    ChatServer chatserver = new ChatServer(8887);

	    SSLContext context = getContext();
	    if (context != null) {
	      chatserver.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(getContext()));
	    }
	    chatserver.setConnectionLostTimeout(30);
	    chatserver.start();

	  }

	  private static SSLContext getContext() {
	    SSLContext context;
	    String password = "";
	    String pathname = "classpath:/";
	    try {
	      context = SSLContext.getInstance("TLS");

	      byte[] certBytes = parseDERFromPEM(getBytes(new File(pathname + File.separator + "cert.pem")),
	          "-----BEGIN CERTIFICATE-----", "-----END CERTIFICATE-----");
	      byte[] keyBytes = parseDERFromPEM(
	          getBytes(new File(pathname + File.separator + "privkey.pem")),
	          "-----BEGIN PRIVATE KEY-----", "-----END PRIVATE KEY-----");

	      X509Certificate cert = generateCertificateFromDER(certBytes);
	      RSAPrivateKey key = generatePrivateKeyFromDER(keyBytes);

	      KeyStore keystore = KeyStore.getInstance("JKS");
	      keystore.load(null);
	      keystore.setCertificateEntry("cert-alias", cert);
	      keystore.setKeyEntry("key-alias", key, password.toCharArray(), new Certificate[]{cert});

	      KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
	      kmf.init(keystore, password.toCharArray());

	      KeyManager[] km = kmf.getKeyManagers();

	      context.init(km, null, null);
	    } catch (Exception e) {
	      context = null;
	    }
	    return context;
	  }

	  private static byte[] parseDERFromPEM(byte[] pem, String beginDelimiter, String endDelimiter) {
	    String data = new String(pem);
	    String[] tokens = data.split(beginDelimiter);
	    tokens = tokens[1].split(endDelimiter);
	    return DatatypeConverter.parseBase64Binary(tokens[0]);
	  }

	  private static RSAPrivateKey generatePrivateKeyFromDER(byte[] keyBytes)
	      throws InvalidKeySpecException, NoSuchAlgorithmException {
	    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);

	    KeyFactory factory = KeyFactory.getInstance("RSA");

	    return (RSAPrivateKey) factory.generatePrivate(spec);
	  }

	  private static X509Certificate generateCertificateFromDER(byte[] certBytes)
	      throws CertificateException {
	    CertificateFactory factory = CertificateFactory.getInstance("X.509");

	    return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(certBytes));
	  }

	  private static byte[] getBytes(File file) {
	    byte[] bytesArray = new byte[(int) file.length()];

	    FileInputStream fis = null;
	    try {
	      fis = new FileInputStream(file);
	      fis.read(bytesArray); //read file into bytes[]
	      fis.close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    return bytesArray;
	  }*/

}
