package com.dhlee.http.test;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;

public class TestHttps {
	public static void main(String[] args) throws HttpException, IOException {

		System.out.println(System.getProperty("java.vendor"));
		 System.out.println(System.getProperty("java.vendor.url"));
		 System.out.println(System.getProperty("java.version"));
		HttpClient httpclient = new HttpClient();

		//		Protocol myhttps = new Protocol("https", new CustomHttpsSocketFactory(new EasySSLProtocolSocketFactory()), 8443);
//		Protocol myhttps = new Protocol("https", new EasySSLProtocolSocketFactory(), 8443);
//		httpclient.getHostConfiguration().setHost("localhost", 8443, myhttps);
//		GetMethod httpget = new GetMethod("/");
		
//		Protocol myhttps = new Protocol("https", new CustomHttpsSocketFactory(new EasySSLProtocolSocketFactory()), 443);
//		Protocol myhttps = new Protocol("https", new EasySSLProtocolSocketFactory(), 443);
//		httpclient.getHostConfiguration().setHost("cryptoreport.websecurity.symantec.com", 443, myhttps);
//		GetMethod httpget = new GetMethod("/checker/");

		 Protocol.registerProtocol("https", 
		new Protocol("https", new CustomHttpsSocketFactory(new EasySSLProtocolSocketFactory()), 443));
//		 new Protocol("https", new EasySSLProtocolSocketFactory(), 443));
		GetMethod httpget = new GetMethod("https://cryptoreport.websecurity.symantec.com/checker/");

		try {
		  httpclient.executeMethod(httpget);
		  System.out.println(httpget.getStatusLine());
		} finally {
			httpget.releaseConnection();
		}
		
		
		

				
	}
}
