package com.lq.client

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import kotlinx.android.synthetic.main.activity_main.*

/**
 * 客户端
 */
private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
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

                compoundButton.text = "开启监听端口 ${ControlConstants.MASTER_LISTEN_PORT} 的数据"

                ReceiverBroadcastManager.setBroadcastReceiveCallback(object :
                    ReceiverBroadcastManager.BroadcastReceiverCallback {
                    override fun onError(errMsg: String?) {
                        compoundButton.text = "端口 ${ControlConstants.MASTER_LISTEN_PORT} 的数据 错误 $errMsg"
                        ReceiverBroadcastManager.stop()
                    }

                    override fun onReceive(senderIp: String?, message: String?) {

                        compoundButton.text = "收到端口 ${ControlConstants.MASTER_LISTEN_PORT}数据，主机地址为 $senderIp"
                        Log.i(TAG, "onReceive: senderIp == $senderIp")
                        Log.i(TAG, "onReceive: message == $message")
                        //{"type":"server","port":6000,"deviceName":"ONEPLUS A6010"}

                    }

                })

                //开启循环广播发送，当建立连接后需关闭
                ReceiverBroadcastManager.start()

            } else {
                cb_start.text = "关闭监听端口 ${ControlConstants.MASTER_LISTEN_PORT} 的数据"
                ReceiverBroadcastManager.stop()
            }
        }

    }
}