package com.lq.client

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.util.*

/**
 *
 *@author : lq
 *@date   : 2021/3/8
 *@desc   : 接收广播 管理
 *
 */
private const val TAG = "SendBroadcastManager"

object ReceiverBroadcastManager {

    //监听停止标志
    @Volatile
    private var needListen = false

    private const val BUFFER_LEN = 1024 * 4

    private var server: DatagramSocket? = null

    private val handler = Handler(Looper.getMainLooper())

    interface BroadcastReceiverCallback {
        fun onError(errMsg: String?)
        fun onReceive(senderIp: String?, message: String?)
    }

    private var callback: BroadcastReceiverCallback? = null

    fun setBroadcastReceiveCallback(callback: BroadcastReceiverCallback?) {
        this.callback = callback
    }

    /**
     * 监听端口数据开启
     */
    fun start() {
        needListen = true
        Thread {
            try {
                startReceive()
            } catch (e: IOException) {
                e.printStackTrace()
                handler.post {
                    if (callback != null) {
                        callback!!.onError(e.localizedMessage)
                    }
                }
                if (server != null) {
                    server!!.close()
                    server = null
                }
            }
        }.start()

    }

    /**
     * 结束
     */
    fun stop() {
        Log.i(TAG, "stop 监听端口")
        needListen = false
    }


    @Throws(IOException::class)
    private fun startReceive() {
        val receive = DatagramPacket(
            ByteArray(BUFFER_LEN),
            BUFFER_LEN
        )

        //避免2次启动端口占用异常
        if (server != null) {
            server!!.close()
            server = null
        }
        if (server == null) {
            server = DatagramSocket(null)
            server!!.setReuseAddress(true)
            server!!.bind(InetSocketAddress(ControlConstants.MASTER_LISTEN_PORT))
        }
        println("---------------------------------")
        println("服务端 start listen ......port == ${ControlConstants.MASTER_LISTEN_PORT}")
        println("---------------------------------")
        while (needListen) {
            server!!.receive(receive)
            val recvByte = Arrays.copyOfRange(receive.data, 0, receive.length)
            val receiveMsg = String(recvByte)

            println("服务端 收到 receive msg:$receiveMsg")
            println("客户端:" + receive.address.toString() + ":" + receive.port)
            //{"type":"server","port":6000,"deviceName":"ONEPLUS A6010"}


            val senderIp = receive.address.hostAddress
            val localIP = IpUtil.getHostIP()
            if (senderIp == localIP) {
                println("服务端收到自己的消息 myself,ignore")
                continue
            }
            if(receiveMsg.contains("server")){
                //  receiveMsg  ==  {"type":"server","port":6000,"deviceName":"ONEPLUS A6010"}
                //信息识别是否是服务端发送过来的信息，如果是 回调
                handler.post {
                    callback?.onReceive(senderIp, receiveMsg)
                }
                //停止监听
                needListen = false
            }

        }
        server!!.disconnect()
        server!!.close()
        println("end listen ......")
    }


}