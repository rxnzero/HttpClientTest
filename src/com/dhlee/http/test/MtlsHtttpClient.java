package com.dhlee.http.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

import javax.net.ssl.SSLContext;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import com.dhlee.http.test.mtls.CustomSSLProtocolSocketFactory;
import com.dhlee.http.test.mtls.SSLContextFactory;

public class MtlsHtttpClient {
	
	public static String getUrlPath(String urlString) {
		try {
			URL url = new URL(urlString);
			return url.getPath();
		} catch (MalformedURLException e) {
			return null;
		}
		
	}
	public static String loadStoreFileToString(String filePath) throws Exception {
		File storeFile = new File(filePath);
		byte[] contentBytes = new byte[(int) storeFile.length()];
		try (InputStream inputStream = new FileInputStream(storeFile)) {
			inputStream.read(contentBytes);
		}
//		byte[] encBytes = Base64.getEncoder().encode(contentBytes);
//		return new String(encBytes, "utf-8");  
		return Base64.getEncoder().encodeToString(contentBytes);
	}
	
	public static void main(String[] args) throws Exception {
		boolean useHostConfig = true;
		System.setProperty("use.test.trust", "y");
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "DEBUG");
        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.content", "DEBUG");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "DEBUG");

		String mtlsTestUrl = "https://localhost:8443";
		SSLContext sslContext = null;
		// javax.net.ssl.SSLHandshakeException: Received fatal alert: bad_certificate
        // String keyStorePath = "d:/ssl-keystore/client1.keystore";
		
		String[] tlsVersions = null;
//		{
//				"TLSv1.0"
//				, "TLSv1.1"
//				, "TLSv1.2"
//				, "TLSv1.3"
//			};
		String[] cipherSuites = null; 
//			{
//				"TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384", 
//				"TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384", 
//				"TLS_RSA_WITH_AES_256_CBC_SHA256", 
//				"TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384", 
//				"TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384", 
//				"TLS_DHE_RSA_WITH_AES_256_CBC_SHA256", 
//				"TLS_DHE_DSS_WITH_AES_256_CBC_SHA256", 
//				"TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA", 
//				"TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA", 
//				"TLS_RSA_WITH_AES_256_CBC_SHA", 
//				"TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA", 
//				"TLS_ECDH_RSA_WITH_AES_256_CBC_SHA", 
//				"TLS_DHE_RSA_WITH_AES_256_CBC_SHA", 
//				"TLS_DHE_DSS_WITH_AES_256_CBC_SHA", 
//				"TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256", 
//				"TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256", 
//				"TLS_RSA_WITH_AES_128_CBC_SHA256", 
//				"TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256", 
//				"TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256", 
//				"TLS_DHE_RSA_WITH_AES_128_CBC_SHA256", 
//				"TLS_DHE_DSS_WITH_AES_128_CBC_SHA256", 
//				"TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA", 
//				"TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA", 
//				"TLS_RSA_WITH_AES_128_CBC_SHA", 
//				"TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA", 
//				"TLS_ECDH_RSA_WITH_AES_128_CBC_SHA", 
//				"TLS_DHE_RSA_WITH_AES_128_CBC_SHA", 
//				"TLS_DHE_DSS_WITH_AES_128_CBC_SHA", 
//				"TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384", 
//				"TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256", 
//				"TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384", 
//				"TLS_RSA_WITH_AES_256_GCM_SHA384", 
//				"TLS_ECDH_ECDSA_WITH_AES_256_GCM_SHA384", 
//				"TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384", 
//				"TLS_DHE_RSA_WITH_AES_256_GCM_SHA384", 
//				"TLS_DHE_DSS_WITH_AES_256_GCM_SHA384", 
//				"TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", 
//				"TLS_RSA_WITH_AES_128_GCM_SHA256", 
//				"TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256", 
//				"TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256", 
//				"TLS_DHE_RSA_WITH_AES_128_GCM_SHA256", 
//				"TLS_DHE_DSS_WITH_AES_128_GCM_SHA256", 
//				"TLS_EMPTY_RENEGOTIATION_INFO_SCSV"
//		};
		
        String type = "P12";
		if("JSK".equals(type)) {
	        String keyStorePath = "d:/ssl-keystore/client.keystore";
	        String keyStorePassword = "changeit";
	        String trustStorePath = "d:/ssl-keystore/client.truststore";
	        String trustStorePassword = "changeit";
	        
	        
			// mTLS를 위한 SSLContext 생성 
	        sslContext = SSLContextFactory.createMTLSContextForJKS(
	        		keyStorePath,
	        		keyStorePassword,
	        		trustStorePath,
	        		trustStorePassword
	        		,tlsVersions, cipherSuites
	        );
		}
		else if("P12".equals(type)) {
	        String keyStorePath = "d:/ssl-keystore/client.p12";
	        String keyStorePassword = "changeit";
	        String trustStorePath = "d:/ssl-keystore/truststore.p12";
	        String trustStorePassword = "changeit";
	        
	        String keyStoreInfo = loadStoreFileToString(keyStorePath);
	        System.out.println("keyStoreInfo["+keyStoreInfo+"]");
	        String trustStoreInfo = loadStoreFileToString(trustStorePath);
	        System.out.println("trustStoreInfo["+trustStoreInfo+"]");
	        
			// mTLS를 위한 SSLContext 생성
	        sslContext = SSLContextFactory.createMTLSContextForP12(
	        		keyStoreInfo,
	        		keyStorePassword,
	        		trustStoreInfo,
	        		trustStorePassword
	        		,tlsVersions, cipherSuites
	        );
		}

        // CustomSSLProtocolSocketFactory 생성
        ProtocolSocketFactory socketFactory = new CustomSSLProtocolSocketFactory(sslContext);

        // HTTPS 프로토콜 등록 (기존 https를 대체)
        Protocol httpsProtocol = new Protocol("https", socketFactory, 443);
        HostConfiguration hostConfig = new HostConfiguration();
        if(useHostConfig) {
            URI uri = new URI(mtlsTestUrl);
            String host = uri.getHost();
            int port = uri.getPort();
            hostConfig.setHost(host, port, new Protocol("https", socketFactory, 443));
        }
        else {
        	Protocol.registerProtocol("https", httpsProtocol);
        }


        // MultiThreadedHttpConnectionManager 생성
        MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();

        // HttpClient 생성
        HttpClient httpClient = new HttpClient(connectionManager);

        // HTTP GET 요청 실행
        HttpMethod method = new GetMethod(mtlsTestUrl);
        int responseCode = -1;
        try {
        	if(useHostConfig) {
        		method = new GetMethod(getUrlPath(mtlsTestUrl));
        		responseCode = httpClient.executeMethod(hostConfig, method);
        	}
        	else {
        		method = new GetMethod(mtlsTestUrl);
        		responseCode = httpClient.executeMethod(method);
        	}
            System.out.println("Response Status: " + responseCode);
            System.out.println("Response Body: " + method.getResponseBodyAsString());
        } catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
            method.releaseConnection();
        }
	}
}
