package com.lq.client

import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketException

/**
 * 客户端
 */
private const val TAG = "client"

class MainActivity : AppCompatActivity() {

    /**
     * 与服务器连接
     */
    val datagramSocket = DatagramSocket()
    val stringBuilder = StringBuilder()

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

        cb_start.text = "监听端口 ${ControlConstants.MASTER_LISTEN_PORT} 的数据"

        cb_start.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            if (b) {


//                ClientSocketManager.startConnectServer()


                compoundButton.text = "开启监听端口 ${ControlConstants.MASTER_LISTEN_PORT} 的数据"

                stringBuilder.append("\n开启监听端口 ${ControlConstants.MASTER_LISTEN_PORT} 的数据")
                textView2.text = stringBuilder.toString()

                ReceiverBroadcastManager.setBroadcastReceiveCallback(object :
                    ReceiverBroadcastManager.BroadcastReceiverCallback {
                    override fun onError(errMsg: String?) {
                        compoundButton.text =
                            "端口 ${ControlConstants.MASTER_LISTEN_PORT} 的数据 错误 $errMsg"
                        ReceiverBroadcastManager.stop()
                    }

                    override fun onReceive(senderIp: String?, message: String?) {

                        stringBuilder.append("\n收到端口 ${ControlConstants.MASTER_LISTEN_PORT}数据，消息为 $message")
                        stringBuilder.append("\n收到端口 ${ControlConstants.MASTER_LISTEN_PORT}数据，主机地址为 $senderIp")
                        textView2.text = stringBuilder.toString()

                        compoundButton.text =
                            "收到端口 ${ControlConstants.MASTER_LISTEN_PORT}数据，主机地址为 $senderIp"
                        Log.i(TAG, "onReceive: senderIp == $senderIp")
                        Log.i(TAG, "onReceive: message == $message")
                        //{"type":"server","port":6000,"deviceName":"ONEPLUS A6010"}

                        Thread {
                            datagramSocket.connect(
                                InetAddress.getByName(senderIp),
                                ControlConstants.SERVER_SOCKET_PORT
                            ) // 连接指定服务器和端口
                            Log.i(TAG, "udp 连接状态 ds.isConnected ==  " + datagramSocket.isConnected)
                        }.start()


                        //接收数据线程
                        receiveUDP(datagramSocket)


                    }

                })

                //开启循环广播发送，当建立连接后需关闭
                ReceiverBroadcastManager.start()

            } else {
                cb_start.text = "关闭监听端口 ${ControlConstants.MASTER_LISTEN_PORT} 的数据"
//                ReceiverBroadcastManager.stop()
            }
        }


        btn_send.setOnClickListener {

            if (!et_message.text.toString().isNullOrEmpty()) {
                Log.i(TAG, "数据发送 " + et_message.text.toString())

                //发送
                val data = et_message.text.toString().toByteArray()
                var packet = DatagramPacket(data, data.size)
                datagramSocket.send(packet)

                stringBuilder.append("\n发送了数据：" + et_message.text.toString())
                textView2.setText(stringBuilder.toString())

            } else {
                Log.i(TAG, "数据空")
                Toast.makeText(this, "数据不能为空", Toast.LENGTH_SHORT).show()
            }

        }
        /**
         * 清空数据
         */
        btn_clear.setOnClickListener{
            stringBuilder.delete(0, stringBuilder.length)
            textView2.text = stringBuilder
        }

    }

    /**
     * 接收连接后的消息
     */
    fun receiveUDP(rSocket: DatagramSocket) {
        Thread {
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
                    var mes = String(
                        dp.data,
                        0,
                        dp.length
                    )

                    runOnUiThread {
                        stringBuilder.append("\n收到服务端消息：$mes")
                        textView2.text = stringBuilder.toString()
                    }

                    Log.i(TAG, "receiveUDP: " + mes + "   address = " + dp.address)

                } catch (e: SocketException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }.start()
        Log.i(TAG, "UDP receiver started")
    }
}