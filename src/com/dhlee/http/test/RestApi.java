package com.dhlee.http.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;

import javax.net.ssl.HttpsURLConnection;

public class RestApi{
        private final String USER_AGENT = "Mozilla/5.0";
        private final boolean useProxy = false;
        private final String proxyServer = "localhost";
        private final int proxyPort = 80;

        public static void main(String[] args) throws Exception {
                RestApi https = new RestApi();
                //String send_url = "https://aic.kbstar.com:9090/rpmodeler/789/test/irt";
//                String send_url = "https://172.17.209.12:9090/rpmodeler/789/test/irt";
                String send_url = "https://192.168.10.94:8443/example/test.jsp";
//                String send_url = "https://donghoon:8443/example/test.jsp";
                String token_key = "UYCVRBHYRRIPLRKBIFVWTVQXSBVWPOXBPWQBWIZUNNGTQLZVEJNCANOELZINOUUA";
                String dataJson = "{\r\n" +
                                "       \"cmptnIrt\": \"3.2\",\r\n" +
                                "       \"exmtnDstcdC\": \"1\",\r\n" +
                                "       \"exmtnDstcdB\": \"1\",\r\n" +
                                "       \"revisedirt\": \"3.4\",\r\n" +
                                "       \"lnbzTranKndDstcdE\": \"0\",\r\n" +
                                "       \"sbjctCdGCZ\": \"1\",\r\n" +
                                "       \"anticizIrt\": \"1\",\r\n" +
                                "       \"irtModfiTagetDstcdZ\": \"1\",\r\n" +
                                "       \"lnlmtMgivstDstcdA\": \"1\",\r\n" +
                                "       \"captlCstAdirt\": \"1\"\r\n" +
                                "}";

                System.out.println("Https Post");
                https.sendPostHttps(send_url, token_key);
        }

        private void sendPostHttps(String send_url, String token_key) throws Exception {
                URL url = new URL(send_url);
                HttpsURLConnection con = null; 
                DataOutputStream wr = null;
                BufferedReader in = null;
                try {
                	
                	Proxy proxy = Proxy.NO_PROXY;
                	if(useProxy) {
                		proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyServer, proxyPort));
                	}
	                con = (HttpsURLConnection) url.openConnection(proxy);
	                SSLUtils.turnOffSslChecking();
	
	                con.setRequestMethod("POST");
	                //con.setRequestProperty("User-Agent", USER_AGENT);
	                con.setRequestProperty("Accept-Language", "UTF-8");
	                con.setConnectTimeout(1000);       //ÄÁÅØ¼ÇÅ¸ÀÓ¾Æ¿ô 10ÃÊ
	                con.setReadTimeout(5000);           //ÄÁÅÙÃ÷Á¶È¸ Å¸ÀÓ¾Æ¿ô 5ÃÑ
	                System.out.println("1");
	                con.setDoOutput(true);              //Ç×»ó °»½ÅµÈ³»¿ëÀ» °¡Á®¿È.
	                System.out.println("2");
	                con.setRequestProperty("apiKey",token_key);
	                con.setRequestProperty("cmptnIrt","3.2");
	                con.setRequestProperty("exmtnDstcdC","1");
	                con.setRequestProperty("exmtnDstcdB","1");
	                con.setRequestProperty("revisedirt","3.4");
	                con.setRequestProperty("lnbzTranKndDstcdE","0");
	                con.setRequestProperty("sbjctCdGCZ","1");
	                con.setRequestProperty("anticizIrt","1");
	                con.setRequestProperty("irtModfiTagetDstcdZ","1");
				    con.setRequestProperty("lnlmtMgivstDstcdA","1");
	                con.setRequestProperty("captlCstAdirt","1");
	                System.out.println("3");
	                wr = new DataOutputStream(con.getOutputStream());
	                wr.flush();
	                wr.close();
	                System.out.println("4");
	
	                int responseCode = con.getResponseCode();
	                System.out.println("5");
	                System.out.println("\nSending 'POST' request to URL : " + url);
	                System.out.println("API Key : " + token_key);
	                System.out.println("Response Code : " + responseCode);
	
	                Charset charset = Charset.forName("UTF-8");
	                in = new BufferedReader(new InputStreamReader(con.getInputStream(),charset));
	                String inputLine;
	                StringBuffer response = new StringBuffer();
	
	                while ((inputLine = in.readLine()) != null) {
	                    response.append(inputLine);
	                }	                
	                System.out.println(response.toString());
	                SSLUtils.turnOnSslChecking();
                }
                catch(Exception ex) {
                	
                }
                finally {
                	if(wr != null) try { wr.close(); wr = null; } catch(Exception e) { ; }
                	if(in != null) try { in.close(); in = null;  } catch(Exception e) { ; }
                	if(con != null) try { con.disconnect(); con = null;  } catch(Exception e) { ; }
                }
        }
}