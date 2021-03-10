package com.lq.socket

import android.util.Log
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketException
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.concurrent.thread

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

    fun startUDP() {
        val thread = Thread {
            val ds = DatagramSocket(ControlConstants.SERVER_SOCKET_PORT) // 监听指定端口
            Log.i(TAG, "startUDP: 开启UDP监听连接")

            receiveUDP(ds)

        }
        thread.start()

    }

    private fun receiveUDP(rSocket: DatagramSocket) {
        if (thdReceive != null) {
            Log.i(TAG, "receiveUDP: UDP receiver is running")
            return
        }
        thdReceive = Thread {
            try {
                rSocket.broadcast = true
            } catch (e: SocketException) {
                e.printStackTrace()
            }
            while (true) {
                try {
                    val buffer = ByteArray(1024)
                    val dp = DatagramPacket(buffer, buffer.size)
                    rSocket!!.receive(dp)
                    Log.i(
                        TAG, "receiveUDP: " + String(
                            dp.data,
                            0,
                            dp.length
                        ) + "   address = " + dp.address
                    )

                } catch (e: SocketException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        thdReceive!!.start()
        Log.i(TAG, "UDP receiver started")
    }

    private fun sendUDP() {
//        try {
//            val sdf = SimpleDateFormat("HH:mm:ss")
//            var content = sdf.format(Date())
//            content = "[" + Build.MODEL + "]" + content
//            val sendBuffer = content.toByteArray()
//            val bcIP = Inet4Address.getByAddress(getWifiBroadcastIP())
//            Log.i(MainActivity.TAG, "sendUDP: " + bcIP.hostAddress)
//            Log.i(MainActivity.TAG, "sendUDP: " + bcIP.hostName)
//            val udp = DatagramSocket()
//            udp.broadcast = true
//            val dp = DatagramPacket(sendBuffer, sendBuffer.size, bcIP, 6000)
//            udp.send(dp)
//            showMsgLine("发送 [Tx]$content")
//            Log.d("UDP", "[Tx]$content")
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
    }
}