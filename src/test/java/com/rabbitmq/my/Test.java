package com.rabbitmq.my;

import javax.net.SocketFactory;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Test {
    public static void main(String[] args) throws IOException, InterruptedException {
        SocketFactory factory = SocketFactory.getDefault();
        InetAddress localHost = InetAddress.getLocalHost();
        Socket socket = factory.createSocket(localHost, 7015);
//        Socket socket = factory.createSocket();
//        socket.connect(new InetSocketAddress(localHost, 7015));
        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream));

//        while (true) {
//            dataOutputStream.writeBytes("heart");
//            dataOutputStream.flush();
//            int read = bufferedReader.read();
//            if (-1 != read) {
//                System.out.println(read);
//            }
//            TimeUnit.SECONDS.sleep(2L);
//        }
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + ", read s");
            int read = 0;
            try {
                read = bufferedReader.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (-1 != read) {
                System.out.println(Thread.currentThread().getName() + "," + read);
            }
            System.out.println(Thread.currentThread().getName() + ", read e");
        }).start();
        System.out.println(Thread.currentThread().getName() + ", read s");
        int read = bufferedReader.read();
        if (-1 != read) {
            System.out.println(Thread.currentThread().getName() + "," + read);
        }
        System.out.println(Thread.currentThread().getName() + ", read e");
        System.in.read();
    }
}
