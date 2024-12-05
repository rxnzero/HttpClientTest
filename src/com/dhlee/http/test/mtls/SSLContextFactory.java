package com.dhlee.http.test.mtls;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SSLContextFactory {
	public static boolean testMode = System.getProperty("use.test.trust", "n").equals("y");

	public static SSLContext createMTLSContext(String type, String keyStoreInfo, String keyStorePassword,
			String trustStoreInfo, String trustStorePassword, String[] tlsVersions, String[] cipherSuites) {
		try {
			// Ŭ���̾�Ʈ ������ �� Ű����� �ε�
			KeyStore keyStore = KeyStore.getInstance(type);
			byte[] p12Bytes = Base64.getDecoder().decode(keyStoreInfo.getBytes("utf-8"));
			try (ByteArrayInputStream bis = new ByteArrayInputStream(p12Bytes)) {
				keyStore.load(bis, keyStorePassword.toCharArray());
			}
			
			// from file
//			try (FileInputStream keyStoreInput = new FileInputStream(new File(keyStorePath))) {
//				keyStore.load(keyStoreInput, keyStorePassword.toCharArray());
//			}
			
			// Ʈ����Ʈ����� �ε�
			KeyStore trustStore = KeyStore.getInstance(type);

			byte[] p12TrustBytes = Base64.getDecoder().decode(trustStoreInfo);
			try (ByteArrayInputStream bis = new ByteArrayInputStream(p12TrustBytes)) {
				trustStore.load(bis, keyStorePassword.toCharArray());
			}

			KeyManagerFactory keyManagerFactory = KeyManagerFactory
					.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

			boolean withTrust = false;
			SSLContext sslContext = SSLContext.getInstance("TLS");
			if (withTrust) {
				TrustManagerFactory trustManagerFactory = TrustManagerFactory
						.getInstance(TrustManagerFactory.getDefaultAlgorithm());
				trustManagerFactory.init(trustStore);
				// SSLContext ����
				sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
			} else {
				if (testMode) {
					TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
						public java.security.cert.X509Certificate[] getAcceptedIssuers() {
							return null;
						}

						public void checkClientTrusted(X509Certificate[] certs, String authType) {
						}

						public void checkServerTrusted(X509Certificate[] certs, String authType) {
						}
					} };
					sslContext.init(keyManagerFactory.getKeyManagers(), trustAllCerts, null);
				} else {
					// �⺻ TrustManagerFactory�� ����Ͽ� TrustStore �ε�
					TrustManagerFactory trustManagerFactory = TrustManagerFactory
							.getInstance(TrustManagerFactory.getDefaultAlgorithm());
					trustManagerFactory.init((KeyStore) null); // �⺻ TrustStore ���

					// SSLContext �ʱ�ȭ
					sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
				}
			}

			// TLS ���� �� Cipher Suite ����
			SSLParameters sslParameters = sslContext.getDefaultSSLParameters();
			if (tlsVersions != null) {
				sslParameters.setProtocols(tlsVersions);
			}
			if (cipherSuites != null) {
				sslParameters.setCipherSuites(cipherSuites);
			}

			return sslContext;
		} catch (Exception e) {
			System.err.println("Error creating SSLContext: " + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public static SSLContext createMTLSContextForJKS(String keyStorePath, String keyStorePassword,
			String trustStorePath, String trustStorePassword, String[] tlsVersions, String[] cipherSuites) {
		return createMTLSContext("JKS", keyStorePath, keyStorePassword, trustStorePath, trustStorePassword, tlsVersions,
				cipherSuites);
	}

	public static SSLContext createMTLSContextForP12(String keyStorePath, String keyStorePassword,
			String trustStorePath, String trustStorePassword, String[] tlsVersions, String[] cipherSuites) {
		return createMTLSContext("PKCS12", keyStorePath, keyStorePassword, trustStorePath, trustStorePassword,
				tlsVersions, cipherSuites);
	}
}
