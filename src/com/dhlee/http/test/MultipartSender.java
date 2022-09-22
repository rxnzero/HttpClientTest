package com.dhlee.http.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

public class MultipartSender {
	
	private static String hostURL = "http://localhost:8080";
	
	public MultipartSender() {
		
	}
	
	public static void sendMP() {
		Part[] parts = new Part[3];
		parts[0] = new StringPart("token", "tokenid-test1234");
		parts[1] = new StringPart("documentType", "Picture ID");

		try {
//		    File attachFile = new File("./docs/RFP.pdf");
//		    parts[2] = new FilePart("documentFile", attachFile, "application/pdf", null);
		    File attachFile = new File("./docs/run-options.txt");
		    parts[2] = new FilePart("documentFile", attachFile, "application/text", null);
		} catch (FileNotFoundException ex) {
		    ex.printStackTrace();
		    return; 
		}

		PostMethod httpMethod = new PostMethod(hostURL + "/multipart.jsp");

		MultipartRequestEntity entity = new MultipartRequestEntity(parts, httpMethod.getParams());
		httpMethod.setRequestEntity(entity);

		httpMethod.addRequestHeader("Content-Type", "multipart/form-data");
		httpMethod.addRequestHeader("x-session-key", "rxnzero-internal-key");

		HttpClient httpClient = new HttpClient();
		int statusCode = 0;
		try {
		    statusCode = httpClient.executeMethod(httpMethod);
		} catch (IOException ex) {
		    ex.printStackTrace();
		    return;
		}

		String respond = null;
		try {
		    StringBuilder resultStr = new StringBuilder();
		    try (BufferedReader rd = new BufferedReader(new InputStreamReader(httpMethod.getResponseBodyAsStream()))) {
		        String line;
		        while ((line = rd.readLine()) != null) {
		            resultStr.append(line);
		        }
		    }
		    respond = resultStr.toString();
		    httpMethod.releaseConnection();
		} catch (IOException ex) {
			ex.printStackTrace();
		    return;
		}

		System.out.println("status code: " + statusCode + ", respond: " + respond);
	}
	public static void main(String[] args) {
		sendMP();

	}

}
