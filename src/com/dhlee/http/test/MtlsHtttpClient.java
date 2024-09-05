package com.dhlee.http.test;

import java.io.IOException;

import javax.net.ssl.SSLContext;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import com.dhlee.http.test.mtls.CustomSSLProtocolSocketFactory;
import com.dhlee.http.test.mtls.SSLContextFactory;

public class MtlsHtttpClient {
	public static void main(String[] args) {
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
	        
	        
			// mTLS를 위한 SSLContext 생성
	        sslContext = SSLContextFactory.createMTLSContextForP12(
	        		keyStorePath,
	        		keyStorePassword,
	        		trustStorePath,
	        		trustStorePassword
	        		,tlsVersions, cipherSuites
	        );
		}

        // CustomSSLProtocolSocketFactory 생성
        ProtocolSocketFactory socketFactory = new CustomSSLProtocolSocketFactory(sslContext);

        // HTTPS 프로토콜 등록 (기존 https를 대체)
        Protocol httpsProtocol = new Protocol("https", socketFactory, 443);
        Protocol.registerProtocol("https", httpsProtocol);

        // MultiThreadedHttpConnectionManager 생성
        MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();

        // HttpClient 생성
        HttpClient httpClient = new HttpClient(connectionManager);

        // HTTP GET 요청 실행
        HttpMethod method = new GetMethod(mtlsTestUrl);

        try {
            int responseCode = httpClient.executeMethod(method);
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
