package com.dhlee.http.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**  
 *  
 */
public class HttpsClientWithDefaultCACert {

	/**
	 * 
	 * @param urlString
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public void getHttps(String urlString) throws IOException, NoSuchAlgorithmException, KeyManagementException {

		// Get HTTPS URL connection
		URL url = new URL(urlString);
		HttpsURLConnection conn = null;
		BufferedReader reader = null;
		
		try {
			conn = (HttpsURLConnection) url.openConnection();
			// Set Hostname verification
			conn.setHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					// Ignore host name verification. It always returns true.
					return true;
				}
	
			});
	
			// SSL setting
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new TrustManager[] { new X509TrustManager() {
	
				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					// client certification check
				}
	
				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	
					// Server certification check
	
					try {
	
						// Get trust store
						KeyStore trustStore = KeyStore.getInstance("JKS");
						String cacertPath = System.getProperty("java.home") + "/lib/security/cacerts"; // Trust store path
																										// should be
																										// different by
																										// system platform.
						trustStore.load(new FileInputStream(cacertPath), "changeit".toCharArray()); // Use default
																									// certification
																									// validation
						// Get Trust Manager
						TrustManagerFactory tmf = TrustManagerFactory
								.getInstance(TrustManagerFactory.getDefaultAlgorithm());
						tmf.init(trustStore);
						TrustManager[] tms = tmf.getTrustManagers();
						((X509TrustManager) tms[0]).checkServerTrusted(chain, authType);
	
					} catch (KeyStoreException e) {
						e.printStackTrace();
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
	
				}
	
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			} }, null);
			conn.setSSLSocketFactory(context.getSocketFactory());
	
			// Connect to host
			conn.connect();
			conn.setInstanceFollowRedirects(true);
	
			// Print response from host
			InputStream in = conn.getInputStream();
			reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) {
				System.out.printf("%s\n", line);
			}
	
			
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		finally {
			if(reader != null) try { reader.close(); reader = null;  } catch(Exception e) { ; }
        	if(conn != null) try { conn.disconnect(); conn = null;  } catch(Exception e) { ; }
		}
	}

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		HttpsClientWithDefaultCACert test = new HttpsClientWithDefaultCACert();
		test.getHttps("https://www.google.com");
//  test.getHttps("https://192.168.10.42");
	}
}
