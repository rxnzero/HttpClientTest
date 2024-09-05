package com.dhlee.http.test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TimeoutTestServer {
    public static void main(String[] args) {
        int port = 8888; // ��Ʈ ��ȣ�� ���ϴ� ������ ����

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("������ " + port + " ��Ʈ���� ���� ���Դϴ�.");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Ŭ���̾�Ʈ�� ����Ǿ����ϴ�.");
                
//                Thread.sleep(100 * 1000);
                // ������ ó������ �ʰ� ��� (������ ������ ����)
                // ���� ������ ���� �ð� �ʰ��� �ùķ��̼��մϴ�.

                // Ŭ���̾�Ʈ�� ������ ����
                clientSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
