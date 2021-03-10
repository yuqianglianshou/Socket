package com.lq.client

import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.net.DatagramSocket

/**
 * 客户端
 */
private const val TAG = "client"

/**
 * 1，UDP监听固定端口号（7022）信息，收到服务端信息，解析出服务端地址
 * 2，拿到服务端地址和服务端指定的端口号（7017），建立udp连接通信，实现客户端给服务端发消息
 */
class MainActivity : AppCompatActivity() {

    /**
     * 与服务器连接
     */
    val datagramSocket = DatagramSocket()
    val stringBuilder = StringBuilder()

    //连接管理 对象
    val clientSocketManager = ClientSocketManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        refreshIP()
        initData()
    }

    private fun refreshIP() {
        val ip = IpUtil.getHostIP()
        tv_ip.text = "本机IP: $ip"
    }

    private fun initData() {
        //设置各个状态监听
        clientSocketManager.setClientSocketManagerCallback(object :
            ClientSocketManager.ClientSocketManagerCallback {
            override fun onReceiverBroadcastError(errMsg: String?) {

            }

            override fun onReceiverMessage(senderIp: String?, message: String?) {
                Log.i(TAG, "收到服务端消息，服务端IP == $senderIp")
                Log.i(TAG, "收到服务端消息，服务端mes == $message")
            }

            override fun onConnectSocket(socket: DatagramSocket) {

            }

            override fun onLog(message: String) {
                runOnUiThread {
                    stringBuilder.append("\n $message")
                    textView2.text = stringBuilder.toString()
                }

            }
        })

        cb_start.text = "监听端口 ${ControlConstants.MASTER_LISTEN_PORT} 的数据"

        cb_start.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            if (b) {

                clientSocketManager.startConnectServer()

                compoundButton.text = "开启监听端口 ${ControlConstants.MASTER_LISTEN_PORT} 的数据"

                stringBuilder.append("\n开启监听端口 ${ControlConstants.MASTER_LISTEN_PORT} 的数据")
                textView2.text = stringBuilder.toString()

            } else {
                cb_start.text = "关闭监听端口 ${ControlConstants.MASTER_LISTEN_PORT} 的数据"
                clientSocketManager.disconnectSocket()
            }
        }


        btn_send.setOnClickListener {

            if (!et_message.text.toString().isNullOrEmpty()) {
                Log.i(TAG, "数据发送 " + et_message.text.toString())

                clientSocketManager.sendMessage(et_message.text.toString())

            } else {
                Log.i(TAG, "数据空")
                Toast.makeText(this, "数据不能为空", Toast.LENGTH_SHORT).show()
            }

        }
        /**
         * 清空数据
         */
        btn_clear.setOnClickListener {
            stringBuilder.delete(0, stringBuilder.length)
            textView2.text = stringBuilder
        }

    }

}