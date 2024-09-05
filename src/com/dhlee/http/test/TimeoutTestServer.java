package com.dhlee.http.test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TimeoutTestServer {
    public static void main(String[] args) {
        int port = 8888; // 포트 번호를 원하는 값으로 변경

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("서버가 " + port + " 포트에서 실행 중입니다.");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("클라이언트가 연결되었습니다.");
                
//                Thread.sleep(100 * 1000);
                // 연결을 처리하지 않고 대기 (응답을 보내지 않음)
                // 실제 서비스의 연결 시간 초과를 시뮬레이션합니다.

                // 클라이언트와 연결을 닫음
                clientSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
