package com.dhlee.http.test;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;

public class HttpClientPoolTest {

	static IdleConnectionMonitorthread staleMonitor = null;
	public HttpClientPoolTest() {
		// TODO Auto-generated constructor stub
	}
	
	public static HttpClient init() {
		HttpClient client = null;
		MultiThreadedHttpConnectionManager cm = new MultiThreadedHttpConnectionManager();
		String enabled = "true";
		if (enabled == null) {
			enabled = "true";
		}
		try {
			cm.getParams().setStaleCheckingEnabled(new Boolean(enabled).booleanValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
		String connPerHost = null;;
		if (connPerHost == null) {
			connPerHost = "10";			
		}
		try {
			cm.getParams().setDefaultMaxConnectionsPerHost(new Integer(connPerHost).intValue());
		} catch (Exception e) {			
			e.printStackTrace();
		}
		String totalConnections = null;
		if (totalConnections == null) {
			totalConnections = "10";			
		}
		try {
			cm.getParams().setMaxTotalConnections(new Integer(totalConnections).intValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		staleMonitor = new IdleConnectionMonitorthread(cm);
		try {
			staleMonitor.start();
			staleMonitor.join(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		client = new HttpClient(cm);
		return client;
	}
	
	public static void main(String[] args) {
		ConcurrentHashMap<String,HttpClient> h = new ConcurrentHashMap<String,HttpClient>();
		String key = "GET";
		HttpClient client = null;
		GetMethod method = null;
		for(int i=0; i<1000; i++) {
			
			if( h.containsKey(key) ){
//				System.out.println("find - "+ key);
				client = h.get(key);
			}
			else {
				client = init();
//				System.out.println("put - "+ key);
				h.put(key, client);
			}
			
			try {
				method = new GetMethod("http://localhost:10210");
				client.getHttpConnectionManager().getParams().setSoTimeout(1 * 1000);
		        method.getParams().setSoTimeout(1 * 1000);
//		        method.getParams().setParameter("http.protocol.handle-redirects",false);
		        method.setFollowRedirects(false);
		        
//		        method.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_0);
		        int status = client.executeMethod(method);
//	            
//	            byte[] responseMessage = null;
//	            
//	            if(encode != null && encode.length() > 0) {
//	            	byte[] orgResponse = method.getResponseBody();
//	            	String eucString = new String(orgResponse, encode);
//	            	responseMessage = eucString.getBytes(); 
//	            }
//	            else {
//	            	responseMessage = method.getResponseBody();
//	            }
	            
				if(i%100 == 0) {
					printHeap();
					Thread.sleep(2 * 1000);		
//					Runtime.getRuntime().gc();
				}
			} catch (Exception e) {
					e.printStackTrace();
			}
			finally {
				method.releaseConnection();
			}
		}
		try {
			staleMonitor.shutdown();
			Thread.sleep(20 * 1000);
			printHeap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			MultiThreadedHttpConnectionManager mgr = (MultiThreadedHttpConnectionManager)client.getHttpConnectionManager();
			mgr.shutdown();
		}
		
	}
	
	public static void printHeap() {
        long heapSize = Runtime.getRuntime().totalMemory(); 
        long heapMaxSize = Runtime.getRuntime().maxMemory();
        long heapFreeSize = Runtime.getRuntime().freeMemory(); 
        ThreadGroup it = Thread.currentThread().getThreadGroup();
    	int threadCount = it.activeCount() ;
    	
        System.out.println("heapsize"+formatSize(heapSize) 
        + " max="+formatSize(heapMaxSize)
        +" free="+formatSize(heapFreeSize)
        +" threadCount="+threadCount);
    	
    }
    public static String formatSize(long v) {
        if (v < 1024) return v + " B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double)v / (1L << (z*10)), " KMGTPE".charAt(z));
    }
}
