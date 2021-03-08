package com.lq.socket

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * 服务端
 */
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

        cb_start.setText("循环广播控制 端口 ${ControlConstants.MASTER_LISTEN_PORT}")

        cb_start.setOnCheckedChangeListener{ compoundButton: CompoundButton, b: Boolean ->
            if(b){
                compoundButton.text = "循环广播开启 发送至端口 ${ControlConstants.MASTER_LISTEN_PORT}"
                //开启循环广播发送，当建立连接后需关闭
                SendBroadcastManager.start()
            }else{
                compoundButton.text = "循环广播关闭"
                SendBroadcastManager.stop()
            }
        }

    }
}