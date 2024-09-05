package com.dhlee.http.test;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NoHttpResponseException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class HttpCloseTest {

	private HttpClient client;
	MultiThreadedHttpConnectionManager cm = new MultiThreadedHttpConnectionManager();
	IdleConnectionMonitorthread staleMonitor = null;
	public HttpCloseTest() {
		cm.getParams().setStaleCheckingEnabled(true);
		
//		cm.getParams().setParameter(HttpMethodParams.PROTOCOL_VERSION, HttpVersion.HTTP_1_0);

		cm.getParams().setDefaultMaxConnectionsPerHost(10);
		cm.getParams().setMaxTotalConnections(100);
		cm.getParams().setConnectionTimeout(5 * 1000);
		this.client = new HttpClient(cm);
		
		// 서버에서 연결을 끊기전에 미리 clean 해야 할 경우
		staleMonitor = new IdleConnectionMonitorthread(cm, 10 * 1000);
		try {
			if(staleMonitor != null) {
				staleMonitor.start();
				staleMonitor.join(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// use HTTP 1.0
//		client.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_0);

		// This generate NEW CONNECTION & TIME_WIAT
//		IdleConnectionTimeoutThread idleThread = new IdleConnectionTimeoutThread();
//        idleThread.setTimeoutInterval(20 * 1000);
//        idleThread.addConnectionManager(cm);
//        idleThread.start();

	}

	private byte[] callPostService(String url, String parameterName, byte[] data) throws Exception {
		int timeout = 2 * 1000;
		int soTimeout = 30 * 1000;
		GetMethod method = new GetMethod(url);
		method.getParams().setSoTimeout(timeout);
		
		// method.setRequestHeader("Content-Type","text/html; charset=euc-kr");
		// add retry Handler - http://hc.apache.org/httpclient-3.x/tutorial.html
		HttpMethodRetryHandler myretryhandler = new HttpMethodRetryHandler() {
		    public boolean retryMethod(
		        final HttpMethod method, 
		        final IOException exception, 
		        int executionCount) {
		    	exception.printStackTrace();
		    	System.out.println("==> myretryhandler retry =" +executionCount);
		        if (executionCount > 3) {
		            // Do not retry if over max retry count
		            return false;
		        }
		        if (exception instanceof SocketTimeoutException) {
		        	System.out.println("<== SocketTimeoutException retry =" +executionCount);
		        	return true;
		        }
		        
		        if (exception instanceof NoHttpResponseException) {
		        	System.out.println("<== NoHttpResponseException retry =" +executionCount);
		        	// Retry if the server dropped connection on us
		            return true;
		        }
		        
		        if (exception instanceof ConnectException) {
		        	System.out.println("<== ConnectException retry =" +executionCount);
		        	// Retry if the server dropped connection on us
		            return true;
		        }
		        if (!method.isRequestSent()) {
		        	System.out.println("<== isRequestSent FALSE retry =" +executionCount);
		            // Retry if the request has not been sent fully or
		            // if it's OK to retry methods that have been sent
		            return true;
		        }
		        // otherwise do not retry		        
		        return false;
		    }
		};
		
		// 기본적으로 사용하지 않으나, 재처리가 필요할 경우에만 설정
//		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, myretryhandler);
//		HttpMethodDirector
		// Change Default USER_AGENT value
		method.getParams().setParameter(HttpMethodParams.USER_AGENT, "TestClient");
		method.getParams().setSoTimeout(timeout);
//		method.getParams().setParameter("http.protocol.handle-redirects",false);
	    
		String sendData = null;

		HttpClient mclient = this.client;
		
		// connection timeout
        HttpConnectionParams httpConnectionParams = mclient.getHttpConnectionManager().getParams();
        httpConnectionParams.setConnectionTimeout(timeout);
        // read timeout
        method.getParams().setSoTimeout(soTimeout);
		
		sendData = new String(data);
		
		int status = -1;
		
		try {

			try {
				status = mclient.executeMethod(method);
			} catch (java.net.ConnectException e) {
				throw new Exception("excuteMethod Exceptioin = " + e.getMessage());
			}

			// status 200(정상) 이외에는 error 처리
			if (status != 200) {
				throw new Exception("http receive status fail value= " + status);
			}

			byte[] responseMessage = method.getResponseBody();
			return responseMessage;

		} catch (SocketTimeoutException ste) { // Read Timeout :: HttpClient												
			System.out.println("SocketTimeoutException -" + ste.toString());
			throw ste;
		} catch (ConnectException ce) {
			System.out.println("ConnectException -" + ce.toString());
			throw ce;
		} catch (Exception e) {
			System.out.println("Exception -" + e.toString());
			throw e;
		} finally {
			System.out.println("-----> Finally executed, so releaseConnection");
			method.releaseConnection();
//			cm.deleteClosedConnections();
			System.out.println("Finally http status = " + status);
			if (status == HttpStatus.SC_OK) {
				try {
					Thread.sleep(300 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
//		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
//		System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
//		System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "debug");
//		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "debug");
		
		HttpCloseTest test = new HttpCloseTest();

		String url = "http://localhost:8080/SpringREST/hello";
		
//		url = "http://localhost:8080/example/test.jsp";
		url = "http://localhost:8080/test.jsp";
		
		String parameterName = "name";
		byte[] data = "HttpClient Test".getBytes();		
		
		int iter = 0;
//		while(true) {
			try {
				iter++;
				byte[] response= test.callPostService(url, parameterName, data);
				System.out.println("response = " + new String(response));
			}
			catch(Exception ex) {
				System.out.println("CallError : " + ex.toString());
				ex.printStackTrace();
			}
//			
//			try {
//				if(iter % 3 == 0) {
//					System.out.println("*****>> Sleep 30 secs...");
//					Thread.sleep(30 * 1000);
//				}
//				else {
//					System.out.println(">> Sleep 10 secs...");
//					Thread.sleep(10 * 1000);					
//				}
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}

			if(test.staleMonitor != null) test.staleMonitor.interrupt();
	}
}
