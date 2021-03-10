package com.lq.socket

import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * 服务端
 */
private const val TAG = "server"

/**
 *
 */
class MainActivity : AppCompatActivity() {

    private val stringBuilder = StringBuilder()

    val serverSocketManager = ServerSocketManager

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

        serverSocketManager.setServerSocketManagerCallback(object :
            ServerSocketManager.ServerSocketManagerCallback {
            override fun onReceiverMessage(message: String?) {

            }

            override fun onLog(message: String) {
                runOnUiThread{
                    stringBuilder.append("\n $message")
                    textView2.text = stringBuilder
                }

            }
        })

        cb_start.text = "循环广播控制 端口 ${ControlConstants.MASTER_LISTEN_PORT}"

        cb_start.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            if (b) {

                serverSocketManager.startUDP()
                Log.i(TAG, "startUDP: 开启UDP监听连接")

                compoundButton.text = "开启UDP监听连接 发送至端口 ${ControlConstants.MASTER_LISTEN_PORT}"

            } else {
                compoundButton.text = "关闭UDP监听连接"
                serverSocketManager.stopUDP()
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