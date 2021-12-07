package com.dhlee.http.test;

import org.apache.commons.httpclient.methods.EntityEnclosingMethod;

/**
 * http method delete에는 body를 보낼 수 없다.
 * 참고 : https://daweini.wordpress.com/2013/12/20/apache-httpclient-send-entity-body-in-a-http-delete-request/
 */
public class HttpDeleteWithBody extends EntityEnclosingMethod {

	public static final String METHOD_NAME = "DELETE";

	public HttpDeleteWithBody() {
		super();
	}

	public HttpDeleteWithBody(String uri) {
		super(uri);
	}

	public String getMethod() {
		return METHOD_NAME;
	}

	@Override
	public String getName() {
		return METHOD_NAME;
	}
}