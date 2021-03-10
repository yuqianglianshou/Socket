package com.lq.client

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 *
 *@author : lq
 *@date   : 2021/3/8
 *@desc   : 服务端 通信 管理
 *
 */
private const val TAG = "ClientSocketManager"

object ClientSocketManager {

    //广播管理 对象
    val receiverBrodcast = ReceiverBroadcastManager

    /**
     * 与服务器连接
     */
    val datagramSocket = DatagramSocket()

    /**
     * 连接成功与否
     */
    var isConnectSocketSuccess = false


    interface ClientSocketManagerCallback {
        /**
         * 广播接收失败
         */
        fun onReceiverBroadcastError(errMsg: String?)

        /**
         * udp广播收到的消息以及解析的服务器IP地址
         */
        fun onReceiverMessage(senderIp: String?, message: String?)

        /**
         * 连接到 服务器后 的socket 对象
         */
        fun onConnectSocket(socket: DatagramSocket)

        /**
         * 关键日志信息
         */
        fun onLog(message: String)
    }

    private var callback: ClientSocketManagerCallback? = null

    fun setClientSocketManagerCallback(callback: ClientSocketManagerCallback?) {
        this.callback = callback
    }

    fun startConnectServer() {
        receiverBrodcast.setBroadcastReceiveCallback(object :
            ReceiverBroadcastManager.BroadcastReceiverCallback {
            override fun onError(errMsg: String?) {
                callback?.onReceiverBroadcastError(errMsg)
                isConnectSocketSuccess = false
                receiverBrodcast.stop()

            }

            override fun onReceive(senderIp: String?, message: String?) {
                callback?.onReceiverMessage(senderIp, message)
                callback?.onLog("收到服务端消息，服务端IP == $senderIp")
                callback?.onLog("收到服务端消息，服务端mes == $message")

                //{"type":"server","port":6000,"deviceName":"ONEPLUS A6010"}

                //确认是服务端  信息识别
                if (message!!.contains("server")) {

                    if (isConnectSocketSuccess) {
                        return
                    }

                    //  receiveMsg  ==  {"type":"server","port":6000,"deviceName":"ONEPLUS A6010"}
                    //信息识别是否是服务端发送过来的信息，如果是 回调

                    //停止监听
                    receiverBrodcast.stop()

                    Thread {
                        // 连接指定服务器和端口
                        datagramSocket.connect(
                            InetAddress.getByName(senderIp),
                            ControlConstants.SERVER_SOCKET_PORT
                        )

                        callback?.onConnectSocket(datagramSocket)
                        //向服务器发送 确认连接信息
                        val data = "客户端已经建立连接".toByteArray()
                        var packet = DatagramPacket(data, data.size)
                        datagramSocket.send(packet)

                        callback?.onLog("发送了连接确认消息：客户端已经建立连接")

                        isConnectSocketSuccess = true

                    }.start()
                }


            }

        })
        //开启循环广播接收，当建立连接后需关闭
        receiverBrodcast.start()
        callback?.onLog("开启广播监听")
    }

    /**
     * 与主机断开udp连接
     */
    fun disconnectSocket() {
        if (isConnectSocketSuccess) {
            isConnectSocketSuccess = false
            datagramSocket.disconnect()
            callback?.onLog("断开socket连接")
        } else {
            //广播监听关闭
            receiverBrodcast.stop()
            callback?.onLog("socket连接已经断开")
        }

    }

    /**
     * 数据发送
     */
    fun sendMessage(mes: String): Boolean {
        if (isConnectSocketSuccess) {
            //发送
            val data = mes.toByteArray()
            var packet = DatagramPacket(data, data.size)
            datagramSocket.send(packet)

            callback?.onLog("发送了数据：$mes")
            return true

        } else {
            callback?.onLog("socket未连接，数据：$mes 发送失败。")
            return false
        }
    }

}