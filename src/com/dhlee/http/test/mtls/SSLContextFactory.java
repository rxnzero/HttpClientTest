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
	public static String loadStoreFileToString(String filePath) throws Exception {
		File storeFile = new File(filePath);
		byte[] contentBytes = new byte[(int) storeFile.length()];
		try (InputStream inputStream = new FileInputStream(storeFile)) {
			inputStream.read(contentBytes);
		}
		return Base64.getEncoder().encodeToString(contentBytes);
	}

	public static SSLContext createMTLSContext(String type, String keyStorePath, String keyStorePassword,
			String trustStorePath, String trustStorePassword,
			String[] tlsVersions, String[] cipherSuites) {
		try {
			// 클라이언트 인증서 및 키스토어 로드
			KeyStore keyStore = KeyStore.getInstance(type);
//            keyStore.load(new FileInputStream(keyStorePath), keyStorePassword.toCharArray());
			String leyString = loadStoreFileToString(keyStorePath);
			System.out.println("p12Key : " + leyString);
			byte[] p12Bytes = Base64.getDecoder().decode(leyString);
			try (ByteArrayInputStream bis = new ByteArrayInputStream(p12Bytes)) {
				keyStore.load(bis, keyStorePassword.toCharArray());
			}

			// 트러스트스토어 로드
			KeyStore trustStore = KeyStore.getInstance(type);
//            trustStore.load(new FileInputStream(trustStorePath), trustStorePassword.toCharArray());
			String trustString = loadStoreFileToString(trustStorePath);
			System.out.println("p12Trust : " + trustString);
			byte[] p12TrustBytes = Base64.getDecoder().decode(trustString);
			try (ByteArrayInputStream bis = new ByteArrayInputStream(p12TrustBytes)) {
				trustStore.load(bis, keyStorePassword.toCharArray());
			}

			// KeyManager 및 TrustManager 설정
//			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
//			keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());
//
//			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
//			trustManagerFactory.init(trustStore);

			KeyManagerFactory keyManagerFactory = KeyManagerFactory
					.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

			boolean withTrust = false;
			SSLContext sslContext = SSLContext.getInstance("TLS");
			if(withTrust) {
				TrustManagerFactory trustManagerFactory = TrustManagerFactory
						.getInstance(TrustManagerFactory.getDefaultAlgorithm());
				trustManagerFactory.init(trustStore);
				// SSLContext 설정
				sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
			}
			else {
			TrustManager[] trustAllCerts = new TrustManager[] {
				    new X509TrustManager() {
				        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				            return null;
				        }
				        public void checkClientTrusted(X509Certificate[] certs, String authType) { }
				        public void checkServerTrusted(X509Certificate[] certs, String authType) { }
				    }
				};
				sslContext.init(keyManagerFactory.getKeyManagers(), trustAllCerts, null);				
			}

			
            // TLS 버전 및 Cipher Suite 설정
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
			String trustStorePath, String trustStorePassword,
			String[] tlsVersions, String[] cipherSuites) {
        return createMTLSContext("JKS", keyStorePath, keyStorePassword, trustStorePath, trustStorePassword
        		,tlsVersions, cipherSuites);
    }
	
	public static SSLContext createMTLSContextForP12(String keyStorePath, String keyStorePassword, 
			String trustStorePath, String trustStorePassword,
			String[] tlsVersions, String[] cipherSuites) {
		return createMTLSContext("PKCS12", keyStorePath, keyStorePassword, trustStorePath, trustStorePassword
				,tlsVersions, cipherSuites);
	}
}
