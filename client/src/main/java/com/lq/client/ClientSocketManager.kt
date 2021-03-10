package com.lq.client

import android.util.Log
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.Socket

/**
 *
 *@author : lq
 *@date   : 2021/3/8
 *@desc   : 服务端 通信 管理
 *
 */
private const val TAG = "ClientSocketManager"

object ClientSocketManager {

    fun startConnectServer() {
//        startConnect("127.1.1.1")

        ReceiverBroadcastManager.setBroadcastReceiveCallback(object :
            ReceiverBroadcastManager.BroadcastReceiverCallback {
            override fun onError(errMsg: String?) {
                ReceiverBroadcastManager.stop()
            }

            override fun onReceive(senderIp: String?, message: String?) {

                Log.i(TAG, "收到服务端消息，服务端IP == $senderIp")
                Log.i(TAG, "收到服务端消息，服务端mes == $message")
                //{"type":"server","port":6000,"deviceName":"ONEPLUS A6010"}
                startConnect(senderIp)
            }

        })
        //开启循环广播发送，当建立连接后需关闭
        ReceiverBroadcastManager.start()
    }

    /**
     * 与主机建立udp连接
     */
    private fun startConnect(address: String?) {

        var thread = Thread {
            val ds = DatagramSocket()
//        ds.soTimeout = 1000

            ds.connect(
                InetAddress.getByName(address),
                ControlConstants.SERVER_SOCKET_PORT
            ) // 连接指定服务器和端口
            Log.i(TAG, "udp 连接状态 ds.isConnected ==  "+ds.isConnected)
            //发送
            val data = "Hello".toByteArray()
            var packet = DatagramPacket(data, data.size)
            ds.send(packet)

            Log.i(TAG, "startConnect: 发送数据 Hello")
            // 接收:
//            val buffer = ByteArray(1024)
//            packet = DatagramPacket(buffer, buffer.size)
//            ds.receive(packet)
//            val resp = String(packet.getData(), packet.getOffset(), packet.getLength())
//            Log.i(TAG, "startConnect: 接收数据  " + resp)
        }
        thread.start()


//        ds.disconnect()


    }

}