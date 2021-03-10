package com.lq.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * 发送广播
 */
public class BroadcastSender {

    private static final String BROADCAST_IP = "255.255.255.255";
//    private static final String MSG_TO_SEND = "{\"from\":\"speakin\",\"type\":\"slave\",\"port\":9001}";
//    private static final String MSG_TO_SEND2 = "{\"from\":\"speakin\",\"type\":\"master\"},\"port\":9002";

    private int port = 0;

    public BroadcastSender() {
        port = ControlConstants.MASTER_LISTEN_PORT;
    }

    /**
     * 广播发送
     * @param message
     */
    public void sendBroadcastData(final String message) {
        new Thread(() -> {
            try {
                sendBroadcast(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void sendBroadcast(String message) throws IOException {

        String ipAddr = BROADCAST_IP;

        byte[] msg = message.getBytes();
        /*
         * 在Java UDP中单播与广播的代码是相同的,要实现具有广播功能的程序只需要使用广播地址即可, 例如：这里使用了本地的广播地址
         */
        InetAddress inetAddr = InetAddress.getByName(ipAddr);
        DatagramSocket client = new DatagramSocket();

        DatagramPacket sendPack = new DatagramPacket(msg, msg.length, inetAddr, port);

        client.send(sendPack);
        System.out.println("服务端循环发送udp广播 ==port "+port +"  mes ==  " + message);
        client.close();
    }


}
