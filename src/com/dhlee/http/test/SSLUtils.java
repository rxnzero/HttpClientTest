package com.dhlee.http.test;

import javax.net.ssl.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public final class SSLUtils {

    public  static void turnOffSslChecking() throws NoSuchAlgorithmException, KeyManagementException {
        // Install the all-trusting trust manager
    	TrustManager[] UNQUESTIONING_TRUST_MANAGER = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers(){
                        return null;
                    }
                    public void checkClientTrusted( X509Certificate[] certs, String authType ){}
                    public void checkServerTrusted( X509Certificate[] certs, String authType ){}
                }
            };
    	
    	HostnameVerifier allHostValid = new javax.net.ssl.HostnameVerifier(){

            public boolean verify(String hostname,
                    javax.net.ssl.SSLSession sslSession) {
//                if (hostname.equals("localhost")) {
                    return true;
//                }
//                return false;
            }
        };
    	
        final SSLContext sc = SSLContext.getInstance("SSL");
        sc.init( null, UNQUESTIONING_TRUST_MANAGER, new SecureRandom() );
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(allHostValid);
    }

    public static void turnOnSslChecking() throws KeyManagementException, NoSuchAlgorithmException {
        // Return it to the initial state (discovered by reflection, now hardcoded)
        SSLContext.getInstance("SSL").init( null, null, null );
    }

    private SSLUtils(){
        throw new UnsupportedOperationException( "Do not instantiate libraries.");
    }
}
