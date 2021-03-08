package com.lq.socket

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import org.json.JSONException
import org.json.JSONObject

/**
 *
 *@author : lq
 *@date   : 2021/3/8
 *@desc   : 发送广播 管理
 *
 */
private const val TAG = "SendBroadcastManager"

object SendBroadcastManager {

    //循环广播停止标志
    @Volatile
    private var stop = false

    private const val BROADCAST_INTERVAL = 5 * 1000

    private var broadcastSender: BroadcastSender? = null

    private val handler = Handler(Looper.getMainLooper())

    /**
     * 循环广播开启
     */
    fun start() {
        stop = false

        //广播发送
        broadcastSender = BroadcastSender()

        //广播发送消息
        broadcastSender!!.sendBroadcastData(getSendMsg())
        //循环广播开启
        handler.postDelayed(
            broadcastRunnable,
            BROADCAST_INTERVAL.toLong()
        )
    }

    /**
     * 循环广播结束
     */
    fun stop() {
        Log.i(TAG, "stop 广播")
        stop = true
        broadcastSender = null
    }

    /**
     * 发送一组 特定的消息
     * port  发送建立连接需要的端口号
     */
    private fun getSendMsg(): String? {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("type", "server")
            jsonObject.put("port", ControlConstants.SERVER_SOCKET_PORT)
            jsonObject.put("deviceName", Build.MODEL)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject.toString()
    }

    /**
     * 不间断发送广播
     */
    private val broadcastRunnable: Runnable = object : Runnable {
        override fun run() {
            if (stop) {
                return
            }
            if (broadcastSender != null) {
                broadcastSender!!.sendBroadcastData(getSendMsg())
                handler.postDelayed(
                    this,
                    BROADCAST_INTERVAL.toLong()
                )
            }
        }
    }
}