package com.dhlee.http.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.ssl.SSLSocket;

import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;

public class CustomHttpsSocketFactory implements SecureProtocolSocketFactory
{

   private final SecureProtocolSocketFactory base;

   public CustomHttpsSocketFactory(ProtocolSocketFactory base)
   {
      if(base == null || !(base instanceof SecureProtocolSocketFactory)) throw new IllegalArgumentException();
      this.base = (SecureProtocolSocketFactory) base;
   }

   private Socket acceptOnlyTLS12(Socket socket)
   {
      if(!(socket instanceof SSLSocket)) return socket;
      SSLSocket sslSocket = (SSLSocket) socket;
      sslSocket.setEnabledProtocols(new String[]{"TLSv1", "TLSv1.1", "TLSv1.2"});
      
      for(int i=0; i< sslSocket.getEnabledProtocols().length; i++)
      System.out.println("EnabledProtocols : " + i + " - " + sslSocket.getEnabledProtocols()[i]);
      
      return sslSocket;
   }

   public Socket createSocket(String host, int port) throws IOException
   {
      return acceptOnlyTLS12(base.createSocket(host, port));
   }

   public Socket createSocket(String host, int port, InetAddress localAddress, int localPort) throws IOException
   {
      return acceptOnlyTLS12(base.createSocket(host, port, localAddress, localPort));
   }

   public Socket createSocket(String host, int port, InetAddress localAddress, int localPort, HttpConnectionParams params) throws IOException
   {
      return acceptOnlyTLS12(base.createSocket(host, port, localAddress, localPort, params));
   }

   public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException
   {
      return acceptOnlyTLS12(base.createSocket(socket, host, port, autoClose));
   }

}
