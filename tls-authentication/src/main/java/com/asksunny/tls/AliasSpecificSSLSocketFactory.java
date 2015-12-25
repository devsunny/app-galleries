package com.asksunny.tls;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;


/**
 * Create self-signed X.509 Certificated for TLS
 * <pre>
 * keytool -genKey -keyalg RSA -alias socketserver -keystore server_keystore.jks -storepass changeit -validity 3650 -keysize 2048
 * </pre>
 * 
 * <pre>
 * keytool -genKey -keyalg RSA -alias socketclient -keystore client_keystore.jks -storepass changeit -validity 3650 -keysize 2048
 * </pre>
 * 
 * @author SunnyLiu
 *
 */
public final class AliasSpecificSSLSocketFactory {

	public static final String DEFAULT_SSL_PROTOCOL = "TLSv1.2";
	public static final String SECURE_RANDOM_ALGO = "SHA1PRNG";

	public static SSLSocketFactory getSSLSocketFactory(InputStream jksKeyStoreIs, String pKeyPassword,
			String keyCcertAlias, InputStream jksTrustStoreIs, String pTrustPassword) throws Exception {
		SSLContext context = getSSLContext(jksKeyStoreIs, pKeyPassword, keyCcertAlias, jksTrustStoreIs, pTrustPassword);
		return context.getSocketFactory();
	}

	public static SSLServerSocketFactory getSSLServerSocketFactory(InputStream jksKeyStoreIs, String pKeyPassword,
			String keyCcertAlias, InputStream jksTrustStoreIs, String pTrustPassword) throws Exception {
		SSLContext context = getSSLContext(jksKeyStoreIs, pKeyPassword, keyCcertAlias, jksTrustStoreIs, pTrustPassword);
		return context.getServerSocketFactory();
	}

	public static SSLContext getSSLContext(InputStream jksKeyStoreIs, String pKeyPassword, String keyCcertAlias,
			InputStream jksTrustStoreIs, String pTrustPassword) throws Exception {
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		keyStore.load(jksKeyStoreIs, pKeyPassword.toCharArray());
		keyManagerFactory.init(keyStore, pKeyPassword.toCharArray());
		
		KeyManager[] kms = keyManagerFactory.getKeyManagers();
		if (keyCcertAlias != null) {
			for (int i = 0; i < kms.length; i++) {
				if (kms[i] instanceof X509KeyManager) {
					kms[i] = new AliasSpecificKeyManager((X509KeyManager) kms[i], keyCcertAlias);
				}
			}
		}
		KeyStore serverPub = KeyStore.getInstance(KeyStore.getDefaultType());
		serverPub.load(jksTrustStoreIs, pTrustPassword.toCharArray());
		TrustManagerFactory trustManager = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManager.init(serverPub);
		TrustManager[] tms = trustManager.getTrustManagers();
		SSLContext context = SSLContext.getInstance(DEFAULT_SSL_PROTOCOL);
		context.init(kms, tms, SecureRandom.getInstance(SECURE_RANDOM_ALGO));
		return context;
	}

	private AliasSpecificSSLSocketFactory() {
	}

}
