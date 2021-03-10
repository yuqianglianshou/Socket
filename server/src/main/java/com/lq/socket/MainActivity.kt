package com.lq.socket

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketException

/**
 * 服务端
 */
private const val TAG = "server"
class MainActivity : AppCompatActivity() {
    /**
     * 监听指定端口 数据
     */
    private val datagramSocket = DatagramSocket(ControlConstants.SERVER_SOCKET_PORT)
    private val stringBuilder = StringBuilder()

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

        cb_start.setText("循环广播控制 端口 ${ControlConstants.MASTER_LISTEN_PORT}")

        cb_start.setOnCheckedChangeListener{ compoundButton: CompoundButton, b: Boolean ->
            if(b){

//                ServerSocketManager.startUDP()
                Log.i(TAG, "startUDP: 开启UDP监听连接")

                receiveUDP(datagramSocket)

                compoundButton.text = "循环广播开启 发送至端口 ${ControlConstants.MASTER_LISTEN_PORT}"
                //开启循环广播发送，当建立连接后需关闭
                SendBroadcastManager.start()


            }else{
                compoundButton.text = "循环广播关闭"
                SendBroadcastManager.stop()
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
                        stringBuilder.append("\n收到客户端消息：$mes")
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