package com.dhlee.http.test;

import org.apache.commons.httpclient.HttpConnectionManager;

public class IdleConnectionMonitorthread extends Thread {
	private final HttpConnectionManager connMgr;
	private volatile boolean shutdown;
	public IdleConnectionMonitorthread(HttpConnectionManager connMgr) {
		super();
		this.connMgr = connMgr;
	}

	@Override
	public void run() {
		try {
			while (!shutdown) {
				synchronized (this) {
					wait(5000);					
					connMgr.closeIdleConnections(5 * 1000); // 5 seconds
					System.out.println(">> IdleConnectionMonitorthread closeIdleConnections");
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
}
