package com.dhlee.http.test.mtls;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;

public class CustomSSLProtocolSocketFactory implements SecureProtocolSocketFactory {
    private SSLContext sslContext;

    public CustomSSLProtocolSocketFactory(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(host, port);
    }

    @Override
    public Socket createSocket(String host, int port, java.net.InetAddress localAddress, int localPort) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(host, port, localAddress, localPort);
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

	@Override
	public Socket createSocket(String host, int port, InetAddress localAddress, int localPort,
			HttpConnectionParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
		if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        int timeout = params.getConnectionTimeout();
        SocketFactory socketfactory = sslContext.getSocketFactory();
        if (timeout == 0) {
            return socketfactory.createSocket(host, port, localAddress, localPort);
        } else {
            Socket socket = socketfactory.createSocket();
            SocketAddress localaddr = new InetSocketAddress(localAddress, localPort);
            SocketAddress remoteaddr = new InetSocketAddress(host, port);
            socket.bind(localaddr);
            socket.connect(remoteaddr, timeout);
            return socket;
        }
	}
}
