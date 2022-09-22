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
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.params.HttpParams;

public class HttpCloseTest {

	private HttpClient client;
	MultiThreadedHttpConnectionManager cm = new MultiThreadedHttpConnectionManager();
	IdleConnectionMonitorthread staleMonitor = null;
	public HttpCloseTest() {
		cm.getParams().setStaleCheckingEnabled(true);

//		cm.getParams().setParameter(HttpMethodParams.PROTOCOL_VERSION, HttpVersion.HTTP_1_0);

		cm.getParams().setDefaultMaxConnectionsPerHost(10);
		cm.getParams().setMaxTotalConnections(100);
		this.client = new HttpClient(cm);
		
		staleMonitor = new IdleConnectionMonitorthread(cm);
		try {
			staleMonitor.start();
			staleMonitor.join(1000);
		} catch (InterruptedException e) {
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
		GetMethod method = new GetMethod(url);
		method.getParams().setSoTimeout(timeout);
		
		// method.setRequestHeader("Content-Type","text/html; charset=euc-kr");
		// add retry Handler - http://hc.apache.org/httpclient-3.x/tutorial.html
		HttpMethodRetryHandler myretryhandler = new HttpMethodRetryHandler() {
		    public boolean retryMethod(
		        final HttpMethod method, 
		        final IOException exception, 
		        int executionCount) {
		    	System.out.println("==> myretryhandler retry =" +executionCount);
		        if (executionCount > 3) {
		            // Do not retry if over max retry count
		            return false;
		        }
		        if (exception instanceof SocketTimeoutException) {
		        	System.out.println("SocketTimeoutException - ");
		        	return true;
		        }
		        
		        if (exception instanceof NoHttpResponseException) {
		            // Retry if the server dropped connection on us
		            return true;
		        }
		        if (!method.isRequestSent()) {
		            // Retry if the request has not been sent fully or
		            // if it's OK to retry methods that have been sent
		            return true;
		        }
		        // otherwise do not retry		        
		        return false;
		    }
		};
		
		
	    
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
				myretryhandler);
		
		// Change Default USER_AGENT value
		method.getParams().setParameter(HttpMethodParams.USER_AGENT, "TestClient");
		
		
//		method.getParams().setParameter("http.protocol.handle-redirects",false);
	    
		String sendData = null;

		HttpClient mclient = this.client;
		mclient.getParams().setSoTimeout(timeout);
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
												// 3.1일 경우
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
			if (status != HttpStatus.SC_OK) {
				System.out.println("Finally http status = " + status);
			}
		}
	}

	public static void main(String[] args) {
		HttpCloseTest test = new HttpCloseTest();

		String url = "http://localhost:8080/SpringREST/hello";
		
		url = "http://localhost:8080/example/test.jsp";
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
