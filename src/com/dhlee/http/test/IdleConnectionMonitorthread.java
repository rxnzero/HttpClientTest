package com.dhlee.http.test;

import org.apache.commons.httpclient.HttpConnectionManager;

public class IdleConnectionMonitorthread extends Thread {
	private final HttpConnectionManager connMgr;
	private long idleTimeoutMs = 30 * 1000;
	private volatile boolean shutdown;
	public IdleConnectionMonitorthread(HttpConnectionManager connMgr, long idleTimeoutMs) {
		super();
		this.connMgr = connMgr;
		if(idleTimeoutMs > 1000) {
			this.idleTimeoutMs = idleTimeoutMs;
		}
	}

	@Override
	public void run() {
		try {
			while (!shutdown) {
				synchronized (this) {
					wait(5000);
					connMgr.closeIdleConnections(idleTimeoutMs); // 5 seconds
					System.out.println(">> IdleConnectionMonitorthread closeIdleConnections");
					printHeap();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void shutdown() {
		shutdown = true;
		synchronized (this) {
			notifyAll();
		}
		System.out.println("<< IdleConnectionMonitorthread shutdown");
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
