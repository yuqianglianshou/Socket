package com.lq.socket

import android.util.Log
import java.net.DatagramPacket
import java.net.DatagramSocket

/**
 *
 *@author : lq
 *@date   : 2021/3/8
 *@desc   : 服务端 通信 管理
 *
 */
private const val TAG = "ServerSocketManager"

object ServerSocketManager {

    var thdReceive: Thread? = null
    val sendBroadcastManager = SendBroadcastManager

    /**
     * 监听指定端口 数据
     */
    private val datagramSocket = DatagramSocket(ControlConstants.SERVER_SOCKET_PORT)

    //监听停止标志
    @Volatile
    private var needListen = false


    interface ServerSocketManagerCallback {

        /**
         * udp连接收到的消息
         */
        fun onReceiverMessage(message: String?)

        /**
         * 关键日志信息
         */
        fun onLog(message: String)
    }

    private var callback: ServerSocketManagerCallback? = null

    fun setServerSocketManagerCallback(callback: ServerSocketManagerCallback?) {
        this.callback = callback
    }

    /**
     * 开启循环广播发送，开启端口号监听
     */
    fun startUDP() {
        callback?.onLog("开启循环广播发送，开启UDP监听连接")
        needListen = true
        Thread {
            receiveUDP(datagramSocket)
            //开启循环广播发送，当建立连接后需关闭
            sendBroadcastManager.start()
        }.start()
    }

    /**
     * 停止广播发送，停止监听固定端口号信息
     */
    fun stopUDP() {
        callback?.onLog("停止循环广播发送，停止UDP监听连接")
        sendBroadcastManager.stop()
        needListen = false
    }


    /**
     * 开启接收信息
     */
    private fun receiveUDP(rSocket: DatagramSocket) {
        if (thdReceive != null) {
            Log.i(TAG, "receiveUDP: UDP receiver is running")
            return
        }
        thdReceive = Thread {

            rSocket.broadcast = true
            while (needListen) {
                val buffer = ByteArray(1024)
                val dp = DatagramPacket(buffer, buffer.size)
                rSocket!!.receive(dp)
                var message = String(dp.data, 0, dp.length)

                //这里放开的话，当客户端取消连接然后再次点击连接，则连接不上了。所以服务端要一直开启客户端连接监听
//                if (message.equals("客户端已经建立连接")) {
//                    //收到确认连接消息，关闭循环广播的发送
//                    sendBroadcastManager.stop()
//                    callback?.onLog("收到确认信息，关闭循环广播发送,等待接收消息中 --- ")
//                }

                Log.i(TAG, "receiveUDP: " + message + "   address = " + dp.address)
                callback?.onReceiverMessage(message)
                callback?.onLog("收到客户端 ${dp.address} 的消息 ："+message)

            }
        }
        thdReceive!!.start()
    }
}