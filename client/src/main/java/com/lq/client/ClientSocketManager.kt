package com.lq.client

import android.util.Log
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
        ReceiverBroadcastManager.setBroadcastReceiveCallback(object :
            ReceiverBroadcastManager.BroadcastReceiverCallback {
            override fun onError(errMsg: String?) {
                ReceiverBroadcastManager.stop()
            }

            override fun onReceive(senderIp: String?, message: String?) {

                Log.i(TAG, "onReceive: senderIp == $senderIp")
                Log.i(TAG, "onReceive: message == $message")
                //{"type":"server","port":6000,"deviceName":"ONEPLUS A6010"}
                startConnect(senderIp)
            }

        })
        //开启循环广播发送，当建立连接后需关闭
        ReceiverBroadcastManager.start()
    }

    /**
     * 尝试连接主机
     */
    private fun startConnect(address: String?) {
        val socket: Socket = Socket(address, ControlConstants.SERVER_SOCKET_PORT)
    }

}